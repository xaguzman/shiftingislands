package org.xguzm.games.respawn;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Respawn";
		cfg.useGL20 = false;
		cfg.width = 800;
		cfg.height = 480;
		
		//packTextures(args);
		new LwjglApplication(new Respawn(), cfg);
	}
}
