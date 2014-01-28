package org.xguzm.games.respawn.actors;

import static com.badlogic.gdx.graphics.g2d.Batch.C1;
import static com.badlogic.gdx.graphics.g2d.Batch.C2;
import static com.badlogic.gdx.graphics.g2d.Batch.C3;
import static com.badlogic.gdx.graphics.g2d.Batch.C4;
import static com.badlogic.gdx.graphics.g2d.Batch.U1;
import static com.badlogic.gdx.graphics.g2d.Batch.U2;
import static com.badlogic.gdx.graphics.g2d.Batch.U3;
import static com.badlogic.gdx.graphics.g2d.Batch.U4;
import static com.badlogic.gdx.graphics.g2d.Batch.V1;
import static com.badlogic.gdx.graphics.g2d.Batch.V2;
import static com.badlogic.gdx.graphics.g2d.Batch.V3;
import static com.badlogic.gdx.graphics.g2d.Batch.V4;
import static com.badlogic.gdx.graphics.g2d.Batch.X1;
import static com.badlogic.gdx.graphics.g2d.Batch.X2;
import static com.badlogic.gdx.graphics.g2d.Batch.X3;
import static com.badlogic.gdx.graphics.g2d.Batch.X4;
import static com.badlogic.gdx.graphics.g2d.Batch.Y1;
import static com.badlogic.gdx.graphics.g2d.Batch.Y2;
import static com.badlogic.gdx.graphics.g2d.Batch.Y3;
import static com.badlogic.gdx.graphics.g2d.Batch.Y4;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import java.util.List;

import org.xguzm.games.respawn.Assets;
import org.xguzm.games.respawn.events.PlayerEvent;
import org.xguzm.games.respawn.events.PlayerEvent.Type;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.Heuristic;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarFinder;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
 
public class MapChunk extends Group {
	
	NavigationGrid<GridCell> navigation;
	private static AStarFinder<GridCell> pathFinder = new AStarFinder<GridCell>(new GridFinderOptions(false, true, Heuristic.Manhattan, false));
	
	
	private MapLayers layers;
	private int col, row, cwidth, cheight;
	private float[] vertices = new float[20];
	private Rectangle viewBounds;
	ObjectMap<Vector2, Float> simplelogs = new ObjectMap<Vector2, Float>(64);
	ObjectMap<Vector2, Float> triplelogs = new ObjectMap<Vector2, Float>(64);
	
	/** Array for anchors used for pathfinding*/
	Array<Vector2> anchorPoints;
	
	private final Vector2 tmp = new Vector2();
	 
	
	public int maxScore;
	public int score;
	
	public static int simpleLogPoints = 1;
	public static int tripleLogPoints = 3;
	
	private static float simpleLogChance = 0.75f;
	private static float tripleLogChance = 0.525f;
	
	int respawnX = -1, respawnY = -1;
	private Integer tileWidth;
	private Integer tileHeight;
	
	public static final String TILE_RESPAWN = "respawn";
	//public static final String TILE_ACTIVERESPAWN = "activeRespawn";
	
	private final Array<Enemy> enemies;
	Image respawnMarker;
	private boolean isCurrentRespawn;
 
	/**
	 * 	
	 * @param col The x of the tile at the bottom left of this chunk
	 * @param row The x of the tile at the bottom left of this chunk
	 * @param width The width (in tiles) of this chunk
	 * @param height The height (in tiles) of this chunk
	 */
	public MapChunk(int col, int row, int width, int height){
		this.col = col; this.row = row;
		this.cwidth = width; this.cheight = height;
		this.viewBounds = new Rectangle();
		enemies = new Array<Enemy>();
		anchorPoints = new Array<Vector2>();
		addCaptureListener(new EventListener() {
			
			@Override
			public boolean handle(Event event) {
				for (Actor child : getChildren()){
					for ( EventListener listener : child.getListeners()){
						if  (listener.handle(event))
							return true;
					}
					
					for ( EventListener listener : child.getCaptureListeners()){
						if  (listener.handle(event))
							return true;
					}
				}
				return false;
			}
		});
	}
	
	/**
	 * Copy the section defined by this chunk from the given map
	 * @param map the map to create the chunk from
	 */
	public void loadFromMap(TiledMap map){
		this.tileWidth = map.getProperties().get("tilewidth", Integer.class);
		this.tileHeight = map.getProperties().get("tileheight", Integer.class);
		
		layers = new MapLayers();
		GridCell[][] cells = new GridCell[cheight][cwidth];
		GridCell node = null;
		
		for(MapLayer l : map.getLayers()){
			if (l instanceof TiledMapTileLayer){
				TiledMapTileLayer layer = (TiledMapTileLayer) l;
				TiledMapTileLayer slicedLayer = new TiledMapTileLayer(this.cwidth, this.cheight, tileWidth, tileHeight);
				copyLayerSettings(layer, slicedLayer);
				for (int r = 0; r < this.cheight; r++){
					//cells[r] = new GridCell[]
					for (int c = 0; c < this.cwidth; c++){
						Cell cell = layer.getCell(c + col, r + row);
						slicedLayer.setCell(c, r , cell);
						
						if (layer.getName().equalsIgnoreCase("tiles")){
							node = new GridCell(r, c, false);
							cells[r][c] = node;
						}
						
						if (cell != null ){
							String type = checkCellType(cell, c, r);
							if(type.equalsIgnoreCase("path")){
								node.setWalkable(true);
							}
						}
						
					}
				}
				layers.add(slicedLayer);
			}else{
				MapLayer slicedLayer = new MapLayer();
				copyLayerSettings(l, slicedLayer);
				
				layers.add(slicedLayer);
			}
		}
		
		int logMax = simplelogs.size * simpleLogPoints;
		int log3Max = triplelogs.size * tripleLogPoints;
		this.maxScore = logMax + log3Max;
				
		navigation = new NavigationGrid<GridCell>(cwidth, cheight, cells);
		setSize(tileWidth * cwidth, tileHeight * cheight);
		if (respawnMarker != null)
			addActor(respawnMarker);
	}
	
	private String checkCellType(Cell cell, int x, int y){
		String type = cell.getTile().getProperties().get("type", String.class);
		if (type == null) return "";
		
		if (type.equals("enemy")){
			String enemyKind = cell.getTile().getProperties().get("kind", String.class);
			float vel = Float.valueOf( cell.getTile().getProperties().get("vel", String.class) );
			Enemy enemy = new Enemy(this, x, y, enemyKind, vel);
			addActor(enemy);
			enemies.add(enemy);
		}
		else if(type.equals("respawn")){
			respawnX = x;
			respawnY = y;
			respawnMarker = new Image(Assets.getImage("hand"));
			respawnMarker.setPosition(getX(x) - 25, getY(y) + 34);
			respawnMarker.pack();
		}
		else if(type.equals("player")){
			addActor(new Player(this));
			setActiveSpawn(true);
		}else if(type.equalsIgnoreCase("log")){
			Vector2 v = new Vector2(x,y);
			anchorPoints.add(v);
			
			if (MathUtils.random() <= simpleLogChance)
				simplelogs.put(v, 0f);
		}else if(type.equalsIgnoreCase("log3")){
			Vector2 v = new Vector2(x,y);
			anchorPoints.add(v);
			if (MathUtils.random() <= tripleLogChance)
				triplelogs.put(v, 0f);
		}else if(type.equalsIgnoreCase("goal")){
			Flag finishLine = new Flag();
			
			float offsetY = tileHeight * 0.75f;
			finishLine.setPosition(x * tileWidth , y * tileHeight + offsetY);
			
			addActor(finishLine);
		}
		
		return type;
	}
	
	public Array<Enemy> getEnemies(){
		return enemies;
	}
	
	private void copyLayerSettings(MapLayer from, MapLayer to){
		to.setVisible(from.isVisible());
		to.setOpacity(from.getOpacity());
		to.setName(from.getName());
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		
		//update the viewbounds
		OrthographicCamera cam = (OrthographicCamera) getStage().getCamera();
		float width = cam.viewportWidth * cam.zoom;
		float height = cam.viewportHeight * cam.zoom;
		viewBounds.set(cam.position.x - width / 2, cam.position.y - height / 2, width, height);
		
		
		//draw the layers
		for (MapLayer layer : layers) {
			if (!layer.isVisible()) continue; 
			
			if (layer instanceof TiledMapTileLayer) {
				drawLayer((TiledMapTileLayer)layer, batch, parentAlpha);
			} else {
				for (MapObject object : layer.getObjects()) {
					drawObject(object);
				}
			}
			
		}
		drawLogs(batch);
		setUpSpawnMarker();
		super.draw(batch, parentAlpha);
	}
	
	private void setUpSpawnMarker() {
		if (respawnMarker == null) return;
		
		
		if (isCurrentRespawn)
			respawnMarker.setColor(Color.WHITE);
//		else
//			respawnMarker.setColor(Color.GRAY);
		
	}

	protected void drawLogs(Batch batch){	
		for (Vector2 logpos : simplelogs.keys()){
			float x = getX() + getX((int)logpos.x);
			float y = getY() + getY((int)logpos.y);
			TextureRegion frame = Assets.getImage("simplelog");
			batch.draw(frame, x - frame.getRegionWidth() * 0.5f, y - frame.getRegionHeight() * 0.5f);
		}
		
		for (Vector2 logpos : triplelogs.keys()){
			float x = getX() + getX((int)logpos.x);
			float y = getY() + getY((int)logpos.y);
			TextureRegion frame = Assets.getImage("triplelog");
			batch.draw(frame, x - frame.getRegionWidth() * 0.5f, y - frame.getRegionHeight() * 0.5f);
		}
	}
	
	protected void drawLayer(TiledMapTileLayer layer, Batch batch, float parentAlpha){
		final Color batchColor = batch.getColor();
		final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.getOpacity() * parentAlpha);
 
		final int layerWidth = layer.getWidth();
		final int layerHeight = layer.getHeight();
 
		final float layerTileWidth = layer.getTileWidth();// * unitScale;
		final float layerTileHeight = layer.getTileHeight();// * unitScale;
 
		final int col1 = Math.max(0, (int) ( (viewBounds.x - getX()) / layerTileWidth));
		final int col2 = Math.min(layerWidth, (int) ((viewBounds.x + viewBounds.width + layerTileWidth) / layerTileWidth));
 
		final int row1 = Math.max(0, (int) ((viewBounds.y - getY()) / layerTileHeight));
		final int row2 = Math.min(layerHeight, (int) ((viewBounds.y + viewBounds.height + layerTileHeight) / layerTileHeight));				
 
		float y = getY() + (row1 * layerTileHeight);
		float xStart = getX() + (col1 * layerTileWidth);
		final float[] vertices = this.vertices;
 
		for (int row = row1; row < row2; row++) {
			float x = xStart;
			for (int col = col1; col < col2; col++) {
				final TiledMapTileLayer.Cell cell = layer.getCell(col, row);
				if(cell == null) {
					x += layerTileWidth;
					continue;
				}
				final TiledMapTile tile = cell.getTile();
				if (tile == null) continue; 
					
				final boolean flipX = cell.getFlipHorizontally();
				final boolean flipY = cell.getFlipVertically();
				final int rotations = cell.getRotation();
 
				TextureRegion region = tile.getTextureRegion();
 
				float x1 = x;
				float y1 = y;
				float x2 = x1 + region.getRegionWidth();// * unitScale;
				float y2 = y1 + region.getRegionHeight();// * unitScale;
 
				float u1 = region.getU();
				float v1 = region.getV2();
				float u2 = region.getU2();
				float v2 = region.getV();
 
				vertices[X1] = x1;
				vertices[Y1] = y1;
				vertices[C1] = color;
				vertices[U1] = u1;
				vertices[V1] = v1;
 
				vertices[X2] = x1;
				vertices[Y2] = y2;
				vertices[C2] = color;
				vertices[U2] = u1;
				vertices[V2] = v2;
 
				vertices[X3] = x2;
				vertices[Y3] = y2;
				vertices[C3] = color;
				vertices[U3] = u2;
				vertices[V3] = v2;
 
				vertices[X4] = x2;
				vertices[Y4] = y1;
				vertices[C4] = color;
				vertices[U4] = u2;
				vertices[V4] = v1;
 
				if (flipX) {
					float temp = vertices[U1];
					vertices[U1] = vertices[U3];
					vertices[U3] = temp;
					temp = vertices[U2];
					vertices[U2] = vertices[U4];
					vertices[U4] = temp;
				}
				if (flipY) {
					float temp = vertices[V1];
					vertices[V1] = vertices[V3];
					vertices[V3] = temp;
					temp = vertices[V2];
					vertices[V2] = vertices[V4];
					vertices[V4] = temp;
				}
				if (rotations != 0) {
					switch (rotations) {
						case Cell.ROTATE_90: {
							float tempV = vertices[V1];
							vertices[V1] = vertices[V2];
							vertices[V2] = vertices[V3];
							vertices[V3] = vertices[V4];
							vertices[V4] = tempV;
 
							float tempU = vertices[U1];
							vertices[U1] = vertices[U2];
							vertices[U2] = vertices[U3];
							vertices[U3] = vertices[U4];
							vertices[U4] = tempU;
							break;
						}
						case Cell.ROTATE_180: {
							float tempU = vertices[U1];
							vertices[U1] = vertices[U3];
							vertices[U3] = tempU;
							tempU = vertices[U2];
							vertices[U2] = vertices[U4];
							vertices[U4] = tempU;
							float tempV = vertices[V1];
							vertices[V1] = vertices[V3];
							vertices[V3] = tempV;
							tempV = vertices[V2];
							vertices[V2] = vertices[V4];
							vertices[V4] = tempV;
							break;
						}
						case Cell.ROTATE_270: {
							float tempV = vertices[V1];
							vertices[V1] = vertices[V4];
							vertices[V4] = vertices[V3];
							vertices[V3] = vertices[V2];
							vertices[V2] = tempV;
 
							float tempU = vertices[U1];
							vertices[U1] = vertices[U4];
							vertices[U4] = vertices[U3];
							vertices[U3] = vertices[U2];
							vertices[U2] = tempU;
							break;
						}
					}
				}
				batch.draw(region.getTexture(), vertices, 0, 20);
				x += layerTileWidth;
			}
			y += layerTileHeight;
		}
	}
	
	protected void drawObject(MapObject object){
		
	}
	
	public int getColumn() {
		return col;
	}

	public int getRow() {
		return row;
	}
	
	public int getRespawnX(){
		return respawnX;
	}
	
	public int getRespawnY(){
		return respawnX;
	}
	
	public float getX(int col){
		return tileWidth * col + (tileWidth * 0.5f);
	}
	
	public float getY(int row){
		return tileHeight * row + (tileHeight * 0.5f);
	}
	
	public int getChunkWidth(){
		return cwidth;
	}
	
	public int getChunkHeight(){
		return cheight;
	}
	
	public int getTileWidth(){
		return tileWidth;
	}
	
	public int getTileHeight(){
		return tileHeight;
	}
	
	public boolean isWalkable(int x, int y){
		return navigation.isWalkable(x, y);
	}
	
	public boolean isPlayerWalkable(int x, int y){
		return isWalkable(x, y) || isSpawn(x, y) || isGoal(x, y);
	}
	
	public boolean isSpawn(int x, int y){
		return x == respawnX && y == respawnY;
	}
	
	RepeatAction markerAction; 
	public void setActiveSpawn(boolean isCurrentSpawn){
		if (markerAction != null){
			respawnMarker.removeAction(markerAction);
			Pools.get(RepeatAction.class).free(markerAction);
		}
		if (isCurrentSpawn){
			markerAction = forever(sequence ( moveBy(0, -15, 0.25f), moveBy(0, 15, 0.25f) ));
			respawnMarker.addAction(markerAction);
		}else{
			markerAction = null;
		}
		
		this.isCurrentRespawn = isCurrentSpawn;
	}

	public int getColumnAt(float x) {
		if (x < 0 ) 
			return -1;
		if ( x > getX() + getWidth())
			return -2;
		
		return (int) x / tileWidth;
	}

	public int getRowAt(float y) {
		if (y < 0 )
			return -1;
		if (y > getY() + getHeight())
			return -2;
		
		return (int) y / tileHeight;
	}

	
	public void tryPickCoin(int col2, int row2) {
		tmp.set(col2, row2);
		
		PlayerEvent event = Pools.get(PlayerEvent.class).obtain();
		event.setType(Type.LogPicked);
		
		if (simplelogs.containsKey(tmp)){
			simplelogs.remove(tmp);
			event.isTripleLog(false);
			this.fire(event);
		}
		else if (triplelogs.containsKey(tmp)){
			triplelogs.remove(tmp);
			event.isTripleLog(true);
			this.fire(event);
		}
		
		
		Pools.get(PlayerEvent.class).free(event);
	}
	
	public List<GridCell> findNewPathFor(Enemy enemy){
		int idx = MathUtils.random(anchorPoints.size - 1);
		return pathFinder.findPath(enemy.getCol(), enemy.getRow(), (int)anchorPoints.get(idx).x, (int)anchorPoints.get(idx).y, navigation);
	}

	public boolean isGoal(int col2, int row2) {
		Cell c = ((TiledMapTileLayer)layers.get("tiles") ).getCell(col2, row2);
		String type = c == null ? "" : c.getTile().getProperties().get("type", "", String.class);
		return type.equalsIgnoreCase("goal");
	}

	
	
}
