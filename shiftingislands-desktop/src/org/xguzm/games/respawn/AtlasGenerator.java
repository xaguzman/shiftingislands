package org.xguzm.games.respawn;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

public class AtlasGenerator {
	public static void main(String[] args) {
		packTextures(args);
		
		System.out.println("Refresh Android's project 'data' folder to see changes");
	}
	
	private static void packTextures(String[] args) {
		TexturePacker2.processIfModified(args[0], args[1], "images");	
	}
}
