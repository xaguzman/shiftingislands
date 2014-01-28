package org.xguzm.games.respawn.client;

import org.xguzm.games.respawn.Respawn;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class GwtLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig () {
		GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(800, 480);
		return cfg;
	}

	private Respawn game;
	@Override
	public ApplicationListener getApplicationListener () {
		if (game == null)
			game = new Respawn();
		return game;
	}
	
	
}