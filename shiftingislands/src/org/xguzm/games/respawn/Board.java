package org.xguzm.games.respawn;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.removeAction;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import org.xguzm.games.respawn.actions.ChunkReAssigner;
import org.xguzm.games.respawn.actors.MapChunk;
import org.xguzm.games.respawn.actors.Player;
import org.xguzm.games.respawn.events.PlayerEvent;
import org.xguzm.games.respawn.events.PlayerEvent.Type;
import org.xguzm.games.respawn.events.PlayerEventListener;
import org.xguzm.games.respawn.tweens.CameraAccesor;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class Board extends Stage {
	
	TiledMap map;
	private int roomw, roomh;
	public MapChunk[][] rooms;
	MapChunk currentPlayerChunk;
	TweenManager tweenManager;	
	private OrthographicCamera cam;
	private final Array<MapChunk> neighbors = new Array<MapChunk>();
	private static final Vector2 temp = new Vector2();
	public boolean movementAllowed = false;
	public Player player;
	private final Array<MapChunk> rotatingChunks = new Array<MapChunk>();
	static final int ROTATE_BOTRIGHT = 1, ROTATE_BOTLEFT = 2, ROTATE_TOPLEFT = 3 , ROTATE_TOPRIGHT = 4, ROTATE_ALL = 5;
	final Vector2 stageMiddle = new Vector2();
	private int totalWidth;
	private int totalHeight;
	
	Label msg;
	Image boat;
	
	
	private static final float MAX_ZOOM = 3f, MIN_ZOOM = 1f, CAMERA_ZOOM_TIME = 0.75f;
	
	public int currentScore, maxScore;
	
	
	public Board(float width, float height, boolean keepAspectRatio, String tmxPath){
		super(width, height, true, Respawn.SPRITE_BATCH);
		map = new CustomTmxLoader().load(tmxPath, Assets.getAtlas());
		this.totalWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
		this.totalHeight = map.getProperties().get("height", Integer.class)* map.getProperties().get("tileheight", Integer.class);
		rooms = createChunks(3, 3);
		currentPlayerChunk = rooms[1][1];
		tweenManager = new TweenManager();
		
		Tween.registerAccessor(OrthographicCamera.class, new CameraAccesor());
		
		cam = (OrthographicCamera)getCamera();
		cam.position.set(totalWidth * 0.5f, totalHeight * 0.5f, 0);
		cam.zoom = MAX_ZOOM;
		
		msg = new Label("Not enough wood", new LabelStyle(Assets.getFont("default"), Color.WHITE));
		msg.setVisible(false);
		
		boat = new Image(Assets.getImage("boat"));
		//boat.setSize(128, 64);
		boat.setVisible(false);
		
		addListener(new PlayerEventListener() {
			
			@Override
			public void movedtoChunk(MapChunk mapChunk) {	
				float targetX = mapChunk.getX() + mapChunk.getWidth() * 0.5f;
				float targetY = mapChunk.getY() + mapChunk.getHeight() * 0.5f;
				movementAllowed = false;
				currentPlayerChunk = mapChunk;
				
				Timeline.createParallel()
				.push(
					Tween
						.to(cam, CameraAccesor.POSITION, CAMERA_ZOOM_TIME)
						.target(targetX, targetY)
				).push(
					Tween
						.to(cam, CameraAccesor.ZOOM, CAMERA_ZOOM_TIME)
						.target(MIN_ZOOM)
				)
				.delay(0.15f)
				.setCallback(enableInput)
				.start(tweenManager);
			}

			@Override
			public void logPicked(boolean tripleLog) {
				int delta = tripleLog ? MapChunk.tripleLogPoints : MapChunk.simpleLogPoints; 
				currentScore += delta;
				Assets.getSound("coin1").play();
			}
			
			@Override
			public void endReached() {
				if (currentScore == maxScore){
					endGame();
				}else{
					displayNotEnoughMessage();
				}
			}

			
		});
		
		addAction(mapChanger);
		addActor(boat);
		addActor(msg);
				
		PlayerEvent event = Pools.get(PlayerEvent.class).obtain();
		event.setType(Type.ChunkChanged);
		player.fire(event);
		Pools.get(PlayerEvent.class).free(event);
	}
	
	private MapChunk[][] createChunks(int w, int h) {
		MapChunk[][] createdRooms = new MapChunk[h][w];
		int tilewidth = map.getProperties().get("tilewidth", Integer.class);
		int tileheight = map.getProperties().get("tileheight", Integer.class);
		int mapWidth = map.getProperties().get("width", Integer.class);
		int mapHeight = map.getProperties().get("height", Integer.class);
		
		roomw = (int) mapWidth / w;
		roomh = (int) mapHeight / h;
		boolean lookForPlayer = true;
		
		stageMiddle.set( tilewidth * mapWidth * 0.5f, tileheight * mapHeight * 0.5f);
		
		for ( int x = 0; x < w; x++)
			for (int y = 0; y < h; y++){
				MapChunk chunk = new MapChunk(x * roomw , y * roomh, roomw, roomh);
				addActor(chunk);
				chunk.loadFromMap(map);
				chunk.setPosition( chunk.getColumn() * tilewidth , chunk.getRow() * tileheight);
				createdRooms[x][y] = chunk;
				maxScore += chunk.maxScore;
				if (lookForPlayer){
					for (Actor a : chunk.getChildren()){
						if ( a instanceof Player){
							player = (Player)a;
							lookForPlayer = false;
							break;
						}
					}
				}
			}
		return createdRooms;
	}

	
	@Override
	public void act(float delta) {
		tweenManager.update(delta);
		super.act(delta);
	}
	
	public Array<MapChunk> getNeighbors(MapChunk chunk){
		neighbors.clear();
		
		MapChunk neighbor = getLeftNeighbor(chunk);
		if (neighbor != null)
			neighbors.add(neighbor);
		
		neighbor = getAboveNeighbor(chunk);
		if (neighbor != null)
			neighbors.add(neighbor);
		
		neighbor = getRightNeighbor(chunk);
		if (neighbor != null)
			neighbors.add(neighbor);
		
		neighbor = getBelowNeighbor(chunk);
		if (neighbor != null)
			neighbors.add(neighbor);
		
		return neighbors;
	}
	
	public MapChunk getLeftNeighbor(MapChunk chunk){
		Vector2 chunkPos = findChunkPos(chunk);
		if ( chunkPos.x > 0)
			return rooms[(int)chunkPos.x - 1][(int)chunkPos.y];
		
		return null;
	}
	
	public MapChunk getRightNeighbor(MapChunk chunk){
		Vector2 chunkPos = findChunkPos(chunk);
		if ( chunkPos.x < rooms.length - 1 )
			return rooms[(int)chunkPos.x + 1][(int)chunkPos.y];
		
		return null;
	}
	
	public MapChunk getBelowNeighbor(MapChunk chunk){
		Vector2 chunkPos = findChunkPos(chunk);
		if ( chunkPos.y > 0)
			return rooms[(int)chunkPos.x][(int)chunkPos.y - 1];
		
		return null;
	}
	
	public MapChunk getAboveNeighbor(MapChunk chunk){
		Vector2 chunkPos = findChunkPos(chunk);
		if ( chunkPos.y < rooms[0].length - 1 )
			return rooms[(int)chunkPos.x][(int)chunkPos.y + 1];
		
		return null;
	}
	
	public Vector2 findChunkPos(MapChunk chunk){
		float x = chunk.getX() == 0 ? 0 : chunk.getX() / ( totalWidth / rooms.length ) ;
		float y = chunk.getY() == 0 ? 0 : chunk.getY() / (totalHeight / rooms[0].length );
		temp.set(x,y);
		return temp;
	}
	
	private void rotate(int left, int top, int right, int bottom, boolean clockWise){
		//MapChunk tmp = rooms[left][bottom];
		final float rotationDuration = 0.66f;
		movementAllowed = false;
		
		if (clockWise){
			
			for (int y = bottom; y < top; y++){
				rotatingChunks.add(rooms[left][y]);
			}
			
			for (int x = left; x < right; x++){
				rotatingChunks.add(rooms[x][top]);
			}
			
			for (int y = top; y > bottom; y--){
				rotatingChunks.add(rooms[right][y]);
			}
			
			for (int x = right; x > left; x--){
				rotatingChunks.add(rooms[x][bottom]);
			}
		}else{
			
			for (int x = left; x < right; x++){
				rotatingChunks.add(rooms[x][bottom]);
			}
			
			for (int y = bottom; y < top; y++){
				rotatingChunks.add(rooms[right][y]);
			}
			
			for (int x = right; x > left; x--){
				rotatingChunks.add(rooms[x][top]);
			}
			
			for (int y = top; y > bottom; y--){
				rotatingChunks.add(rooms[left][y]);
			}
		}
		
		Timeline.createSequence()
		.beginParallel()
			.push(
				Tween.to(cam, CameraAccesor.ZOOM, CAMERA_ZOOM_TIME).target(MAX_ZOOM)
				.setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {	
						MapChunk current = rotatingChunks.get(rotatingChunks.size - 1), next = rotatingChunks.get(0);
						
						current
							.addAction(
							sequence(		
									moveTo(next.getX(), next.getY(), rotationDuration),
									reIndex(current)
							));
						
						for(int i = 0; i < rotatingChunks.size - 1; i++){
							current = rotatingChunks.get(i);
							next = rotatingChunks.get(i+1);
							
							current.addAction(
								sequence(
									moveTo(next.getX(), next.getY(), rotationDuration),
									reIndex(current)
								));
						}
						rotatingChunks.clear();
					}
				}))
			.push(
					Tween
					.to(cam, CameraAccesor.POSITION, CAMERA_ZOOM_TIME)
					.target(stageMiddle.x, stageMiddle.y)
			)
		.end()
		.pushPause(rotationDuration)
			.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					
					PlayerEvent event = Pools.get(PlayerEvent.class).obtain();
					event.setType(Type.ChunkChanged);
					player.fire(event);
					Pools.get(PlayerEvent.class).free(event);
				}
			})
		.start(tweenManager);
	}
	
	static Action reIndex(MapChunk chunk){
		Pool<ChunkReAssigner> pool = Pools.get(ChunkReAssigner.class);
		ChunkReAssigner action = pool.obtain();
		action.setChunk(chunk);
		action.setPool(pool);
		action.setBoard((Board)chunk.getStage());
		return action;
	}
	
	public void requestRotation(){
		
		int chunkToRotate = MathUtils.random(ROTATE_BOTRIGHT, ROTATE_ALL);
		boolean clockWise = MathUtils.randomBoolean(); 
				
		switch (chunkToRotate){
		case ROTATE_BOTRIGHT:
			Gdx.app.log("Map","rotating bottom right");
			rotate(1, 1, 2, 0, clockWise);
			break;
		case ROTATE_BOTLEFT:
			Gdx.app.log("Map","rotating bottom left");
			rotate(0, 1, 1 , 0, clockWise);
			break;
		case ROTATE_TOPLEFT:
			Gdx.app.log("Map","rotating top left");
			rotate(0, 2, 1, 1, clockWise);
			break;
		case ROTATE_TOPRIGHT:
			Gdx.app.log("Map","rotating top right");
			rotate(1, 2, 2, 1, clockWise);
			break;
		case ROTATE_ALL:
			Gdx.app.log("","rotating all");
			rotate(0, 2, 2, 0, clockWise);
			break;
		}
	}
	
	private void displayNotEnoughMessage() {
		temp.set(player.getX(), player.getY());
		temp.set(player.currentMapChunk.localToStageCoordinates(temp));
		msg.setPosition(temp.x - msg.getWidth() * 0.5f, temp.y + 40);
		msg.setVisible(true);
		removeAction(messageHidder);
		messageHidder.reset();
		addAction(messageHidder);
	}

	private void endGame() {
		removeAction(mapChanger);
		movementAllowed = false;
		
		temp.set(player.getX(), player.getY());
		temp.set(player.currentMapChunk.localToStageCoordinates(temp));
		
		msg.setText("Level Complete! Press Any Key to go back");
		msg.setPosition(temp.x - msg.getWidth() * 0.5f, temp.y + 40);
		boat.setPosition(temp.x - boat.getWidth() * 0.5f, temp.y + 50);
		msg.setVisible(true);
		boat.setVisible(true);
		
		//Gdx.input
	}
	
	// tween callbacks
	private TweenCallback enableInput = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			if (type == TweenCallback.COMPLETE){
				movementAllowed = true;
			}
		}
	};
	
	Action mapChanger = new Action(){
		float accum;
		
		@Override
		public boolean act(float delta) {
			if (movementAllowed)
				accum += delta;
			if (accum >= 15f){
				requestRotation();
				accum = 0;
			}
			return false;
		}
	};
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	MessageHidder messageHidder = new MessageHidder();
	
	class MessageHidder extends Action {
		float accum;
		
		public void reset(){
			accum = 0;
		}
		
		@Override
		public boolean act(float delta) {
			accum += delta;
			if (accum >= 3f){
				msg.setVisible(false);
				reset();
			}
			return false;
		}
	};
	
	
	
}
