package org.xguzm.games.respawn;

import org.xguzm.games.respawn.screens.MenuScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Respawn extends Game{

	public static SpriteBatch SPRITE_BATCH;

	@Override
	public void create() {
		Respawn.SPRITE_BATCH = new SpriteBatch();
		Assets.load();
		
		setScreen(new MenuScreen());
	}
	
	@Override
	public void dispose() {
		super.dispose();
		Assets.dispose();
		SPRITE_BATCH.dispose();
	}
}
