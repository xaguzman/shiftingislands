package org.xguzm.games.respawn.actors;

import org.xguzm.games.respawn.Assets;
import org.xguzm.games.respawn.Board;
import org.xguzm.games.respawn.events.PlayerEvent;
import org.xguzm.games.respawn.events.PlayerEvent.Type;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pools;


public class Player extends Actor {

	/** tiles per second */
	private float velocity;
	public MapChunk currentMapChunk;
	protected float halfTile;
	
	/** last respawn activated by the player */
	private MapChunk respawnChunk;
	
	public static final int MAP_CHUNK_CHANGE = 1;
	
	private int col, row;
	int dir = 1;
	
	String spriteName;
	public final Rectangle bounds = new Rectangle();
	private float stateTime;	
	
	public Player(MapChunk chunk){
		super();
		this.velocity = 3.5f;
		this.halfTile = chunk.getTileWidth() * 0.5f;
		
		setRespawn(chunk);
		respawn();
		setSize(64, 64);
		
		float boundsSize = 32 * 0.8f;
		float halfBoundsSize = boundsSize * 0.5f;
		bounds.set(getX() - halfBoundsSize, getY() - halfBoundsSize, boundsSize, boundsSize );
		
		this.spriteName = "playerdown";
		
		setScale(0.85f);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		//check input
		if (((Board)getStage()).movementAllowed ){
			if (Gdx.input.isKeyPressed(Keys.ANY_KEY) ){
				int prevDir = dir;
				
				checkInput(delta);
				if (prevDir != dir)
					stateTime = 0f;
					
				//update col / row 
				int prevCol = col, prevRow = row;
				col = currentMapChunk.getColumnAt(getX());
				row = currentMapChunk.getRowAt(getY());
				
				currentMapChunk.tryPickCoin(col, row);
				
				if ( currentMapChunk != respawnChunk && currentMapChunk.respawnX == col && currentMapChunk.respawnY == row){
					setRespawn(currentMapChunk);
					Assets.getSound("newSpawn").play();
				}
				
				if (currentMapChunk.isGoal(col, row) && (prevCol != col || prevRow != row)){
					PlayerEvent event = Pools.get(PlayerEvent.class).obtain();
					event.setType(Type.EndReached);
					fire(event);
				}
				
				bounds.setCenter(getX(), getY());
				stateTime += delta;
			}else
				stateTime = 0;
			
		}
		
		//check for collisions on the current map chunk
		for (Enemy enemy : currentMapChunk.getEnemies()){
			boolean couldCollide = Math.abs(enemy.getCol() - col) < 2 && Math.abs(enemy.getRow() - row) < 2;
			if (couldCollide){
				if (enemy.bounds.overlaps(bounds)){
					boolean notifyNeeded = respawnChunk != currentMapChunk;
					respawn();
					Assets.getSound("hit").play();
					if (notifyNeeded)
						notifyChunkChanged();
					break;
				}
			}
		} 
	}

	private static final Vector2 worldPos = new Vector2();
	private void checkInput(float delta){
		float moveBy = velocity * currentMapChunk.getTileWidth() * delta;
		float range = 8;
		float rowY = currentMapChunk.getY(row);
		float colX = currentMapChunk.getX(col);
		Board board = (Board)getStage();
		
		if (Gdx.input.isKeyPressed(Keys.A)){
			if ( distance(rowY, getY()) <= range && 
				(currentMapChunk.isPlayerWalkable(col - 1, row) || colX < getX() ) ){
				//inside chunk
				setX(getX() - moveBy);
				setY(rowY);
			}else if (col == 0 ){
				MapChunk nextChunk = board.getLeftNeighbor(currentMapChunk);
				if (nextChunk != null){
					worldPos.set(getX() - moveBy, rowY);
					moveToChunk( nextChunk );
				}
			}
			spriteName = "playerleft";
			dir = 1;
		}
		else if (Gdx.input.isKeyPressed(Keys.D)){
			if (distance(rowY, getY()) <= range && 
				(currentMapChunk.isPlayerWalkable(col + 1, row) || colX > getX() ) ){
				setX(getX() + moveBy);
				setY(rowY);
			}else if (col == currentMapChunk.getChunkWidth() - 1 ){
				MapChunk nextChunk = board.getRightNeighbor(currentMapChunk);
				if (nextChunk != null){
					worldPos.set(getX() + moveBy, rowY);
					moveToChunk( nextChunk );
				}
			}
			spriteName = "playerright"; 
			dir = 3;
		}else if (Gdx.input.isKeyPressed(Keys.S)){
			if ( distance( colX,getX()) <= range && 
					(currentMapChunk.isPlayerWalkable(col, row - 1) || rowY < getY() ) ){
				//inside chunk
				setY(getY() -moveBy);
				setX(colX);
			}else if (row == 0 ){
				MapChunk nextChunk = board.getBelowNeighbor(currentMapChunk);
				if (nextChunk != null){
					worldPos.set(colX, getY() - moveBy);
					moveToChunk( nextChunk );
				}
			}	
			spriteName = "playerdown"; 
			dir = 4;
		}else if (Gdx.input.isKeyPressed(Keys.W)){
			if ( distance(colX, getX()) <= range && 
					(currentMapChunk.isPlayerWalkable(col , row + 1) || rowY > getY() ) ){
				//inside chunk
				setY(getY() + moveBy);
				setX(colX);
			}else if (row == currentMapChunk.getChunkHeight() - 1 ){
				MapChunk nextChunk = board.getAboveNeighbor(currentMapChunk);
				if (nextChunk != null){
					worldPos.set(colX, getY() + moveBy);
					moveToChunk( nextChunk );
				}
			}
			spriteName = "playerup"; 
			dir = 2;
		}
	}
	
	public void moveToChunk(MapChunk chunk){
		
		currentMapChunk.localToStageCoordinates(worldPos);
		chunk.stageToLocalCoordinates(worldPos);
		
		worldPos.x = MathUtils.clamp(worldPos.x, 0, chunk.getWidth() - 1 );
		worldPos.y = MathUtils.clamp(worldPos.y, 0, chunk.getHeight() - 1 );
		
		int col = chunk.getColumnAt(worldPos.x);
		int row = chunk.getRowAt(worldPos.y);
		
		if (!chunk.isPlayerWalkable(col, row))
			return;
		
		setPosition(chunk.getX(col), chunk.getY(row));
		chunk.addActor(this);
		currentMapChunk = chunk;
		
		notifyChunkChanged();
	}
	
	private void notifyChunkChanged(){
		PlayerEvent event = Pools.get(PlayerEvent.class).obtain();
		event.setType(Type.ChunkChanged);
		event.setTarget(this);
		fire(event);
		Pools.get(PlayerEvent.class).free(event);
	}
	
	private float distance(float from, float to){
		return Math.abs(from - to);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		TextureRegion sprite = Assets.getAnimation(spriteName).getKeyFrame(stateTime); 
		
		float x = getX() - sprite.getRegionWidth() * 0.5f * getScaleX();
		float y = getY() - halfTile;
		batch.draw(sprite, x, y, 0, 0, sprite.getRegionWidth(), sprite.getRegionHeight(), getScaleX(), getScaleY(), 0);
	}
	
	public void respawn(){
		col = respawnChunk.respawnX;
		row = respawnChunk.respawnY;
		respawnChunk.addActor(this);
		this.currentMapChunk = respawnChunk;
		setPosition(currentMapChunk.getX(col), currentMapChunk.getY(row));
		
	}
	
	public void setRespawn(MapChunk chunk){
		if (respawnChunk != null){
			respawnChunk.setActiveSpawn(false);
			chunk.setActiveSpawn(true);
		}
		this.respawnChunk = chunk;
	}
}
