package org.xguzm.games.respawn.events;

import com.badlogic.gdx.scenes.scene2d.Event;

public class PlayerEvent extends Event {
	
	public static enum Type{
		ChunkChanged, LogPicked, EndReached
	}
	
	public static enum Dir{
		Left, Right, Up, Down
	}
	
	private Type type = Type.ChunkChanged;
	private Dir dir;
	private boolean isTripleLog;
	
	public Type getType() { return type; }
	public void setType(Type type) { this.type = type; }
	
	public Dir getDir() { return dir; }
	public void setDir(Dir dir) { this.dir = dir; }
	
	public boolean isTripleLog() { return isTripleLog; }
	public void isTripleLog(boolean triplelog){ isTripleLog = triplelog;  }
	
}
