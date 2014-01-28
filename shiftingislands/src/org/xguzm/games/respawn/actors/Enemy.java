package org.xguzm.games.respawn.actors;

import java.util.ArrayList;
import java.util.List;

import org.xguzm.games.respawn.Assets;
import org.xguzm.games.respawn.Board;
import org.xguzm.pathfinding.grid.GridCell;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.ObjectMap;

public class Enemy extends Actor {
	/** tiles per second */
	private float velocity;
	public MapChunk parentChunk;
	protected float halfTile;
	
	private static final int DIR_LEFT = 1;
	private static final int DIR_RIGHT = 2;
	private static final int DIR_UP= 3;
	private static final int DIR_DOWN = 4;
	

	private static final ObjectMap<String, ObjectMap<Integer, String>> animations = new ObjectMap<String, ObjectMap<Integer, String>>();
	private List<GridCell> path;
	int currentPathIndex;
	
	static{
		ObjectMap<Integer, String> zombiesAnimations = new ObjectMap<Integer, String>();
		zombiesAnimations.put(DIR_LEFT, "zombieleft");
		zombiesAnimations.put(DIR_RIGHT, "zombieright");
		zombiesAnimations.put(DIR_UP, "zombieup");
		zombiesAnimations.put(DIR_DOWN, "zombiedown");
		
		ObjectMap<Integer, String> skelsAnimations = new ObjectMap<Integer, String>();
		skelsAnimations.put(DIR_LEFT, "skelleft");
		skelsAnimations.put(DIR_RIGHT, "skelright");
		skelsAnimations.put(DIR_UP, "skelup");
		skelsAnimations.put(DIR_DOWN, "skeldown");
		
		animations.put("zombie", zombiesAnimations);
		animations.put("skel", skelsAnimations);
	}
	
	private int dir;
			
	private int col, row;
	float colX, rowY;
	float stateTime;
	
	String spriteName;
	public final Rectangle bounds = new Rectangle();	
	
	public Enemy(MapChunk chunk, int col, int row, String spriteName, float velocity){
		super();
		this.velocity = velocity;
		this.halfTile = chunk.getTileWidth() * 0.5f;
		this.parentChunk = chunk;
		this.col = col;
		this.row = row;
		setPosition(parentChunk.getX(col), parentChunk.getY(row));
		setSize(32, 48);
		
		float boundsSize = 32 * 0.8f;
		float halfBoundsSize = boundsSize * 0.5f;
		bounds.set(getX() - halfBoundsSize, getY() - halfBoundsSize, boundsSize, boundsSize );
		
		this.spriteName = spriteName;
				
		dir = MathUtils.random(DIR_LEFT, DIR_DOWN);
		
		path = new ArrayList<GridCell>(16);
	}
	
	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		
		colX = parentChunk.getX(col);	
		rowY = parentChunk.getY(row);
		
		while (currentPathIndex >= path.size() - 1){
			path.clear();
			path.addAll(parentChunk.findNewPathFor(this));
			currentPathIndex = 0;
		}
		
		//movement
		if (((Board)getStage()).movementAllowed){
			
			GridCell nextCell = path.get(currentPathIndex);
			
			int prevdir = dir;
			chooseMoveDirection(nextCell);
			if (prevdir != dir )
				stateTime = 0;

			stateTime += delta;
			float moveBy = velocity * parentChunk.getTileWidth() * delta;
			
			if (dir ==  DIR_LEFT || dir == DIR_DOWN)
				moveBy *= -1;
			
			if (dir ==  DIR_LEFT || dir == DIR_RIGHT)
				setPosition(getX() + moveBy, rowY);
			if (dir ==  DIR_DOWN || dir == DIR_UP)
				setPosition(colX, getY() + moveBy);
			
			col = parentChunk.getColumnAt(getX());
			row = parentChunk.getRowAt(getY());
			
			boolean inDistance = distance(getX(), parentChunk.getX(nextCell.x)) < 2 && distance(getY(), parentChunk.getY(nextCell.y)) < 2;
			if ( inDistance && col == nextCell.x && row == nextCell.y)
				currentPathIndex++;
		}
		
		bounds.setCenter(getX(), getY());
	}
	
	private void chooseMoveDirection(GridCell nextCell){
		
		if (nextCell.x > getCol())
			dir = DIR_RIGHT;
		else if (nextCell.x < getCol())
			dir =  DIR_LEFT;
		
		if (nextCell.y > getRow())
			dir = DIR_UP;
		else if (nextCell.y < getRow())
			dir = DIR_DOWN;
	}

	private float distance(float from, float to){
		return Math.abs(from - to);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
				
		String animName = animations.get(spriteName).get(dir);
		TextureRegion sprite = Assets.getAnimation(animName).getKeyFrame(stateTime);
		
		float x = getX() - sprite.getRegionWidth() * 0.5f ;
		float y = getY() - sprite.getRegionHeight() * 0.25f;
		batch.draw(sprite, x, y, 0, 0, getWidth(), getHeight(), getScaleX(), getScaleY(), 0);
	}
}
