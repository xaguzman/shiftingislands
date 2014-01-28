package org.xguzm.games.respawn.actions;

import org.xguzm.games.respawn.Board;
import org.xguzm.games.respawn.actors.MapChunk;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;

public class ChunkReAssigner extends Action{
	MapChunk _chunk;
	Board board;
	
	public ChunkReAssigner(){}
	
	public void setChunk(MapChunk chunk) { this._chunk = chunk; }
	public void setBoard(Board board) {this.board = board;}
	
	@Override
	public boolean act(float delta) {
		Vector2 pos = board.findChunkPos(_chunk);
		board.rooms[(int)pos.x][(int)pos.y] = _chunk;
		return true;
	}
	
}