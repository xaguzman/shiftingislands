package org.xguzm.games.respawn.events;

import org.xguzm.games.respawn.actors.MapChunk;
import org.xguzm.games.respawn.actors.Player;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public abstract class PlayerEventListener implements EventListener {

	@Override
	public boolean handle (Event event) {
		if (!(event instanceof PlayerEvent)) return false;
		PlayerEvent playerEvent = (PlayerEvent)event;
		switch (playerEvent.getType()) {
			case ChunkChanged:
				movedtoChunk( ((Player)playerEvent.getTarget()).currentMapChunk );
				break;
			case LogPicked:
				logPicked(playerEvent.isTripleLog());
				break;
			case EndReached:
				endReached();
		}
		return true;
	}

	public abstract void logPicked(boolean tripleLog);

	public abstract void movedtoChunk(MapChunk mapChunk);
	
	public abstract void endReached();

}
