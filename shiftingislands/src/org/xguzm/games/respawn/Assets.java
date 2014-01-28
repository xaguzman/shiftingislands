package org.xguzm.games.respawn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class Assets {

	private static ObjectMap<String, TextureRegion> images = new ObjectMap<String, TextureRegion>();
	private static ObjectMap<String, Animation> animations = new ObjectMap<String, Animation>();
	private static ObjectMap<String, BitmapFont> fonts = new ObjectMap<String, BitmapFont>();
	private static ObjectMap<String, Sound> sounds = new ObjectMap<String, Sound>();
	private static TextureAtlas atlas;
	
	public static void load(){
		loadTempGraphics();
		fonts.put("default", new BitmapFont( Gdx.files.internal("data/font.fnt"), atlas.findRegion("font") ));
		sounds.put("coin1", Gdx.audio.newSound(Gdx.files.internal("data/picked-coin.ogg")));
		sounds.put("hit", Gdx.audio.newSound(Gdx.files.internal("data/hit.ogg")));
		sounds.put("newSpawn", Gdx.audio.newSound(Gdx.files.internal("data/newSpawn.ogg")));
	}
	
	private static void loadTempGraphics(){
		images.put("simplelog", getAtlas().findRegion("simplelog"));
		images.put("triplelog", getAtlas().findRegion("triplelog"));
		images.put("hand", getAtlas().findRegion("hand"));
		images.put("boat", getAtlas().findRegion("boat"));
		//animations.put("coin", new Animation( 1f / 5f, getAtlas().findRegions("goldCoin"), Animation.LOOP_PINGPONG ) );
		
		TextureRegion[][] enemySprites = getAtlas().findRegion("enemy-sprites").split(32, 48);
		Animation zombieUp = new Animation(0.18f, enemySprites[3][0], enemySprites[3][1], enemySprites[3][2]);
		zombieUp.setPlayMode(Animation.LOOP);
		
		Animation zombieDown = new Animation(0.18f, enemySprites[0][0], enemySprites[0][1], enemySprites[0][2]);
		zombieDown.setPlayMode(Animation.LOOP);
		
		Animation zombieLeft = new Animation(0.18f, enemySprites[1][0], enemySprites[1][1], enemySprites[1][2]);
		zombieLeft.setPlayMode(Animation.LOOP);
		
		Animation zombieRight = new Animation(0.18f, enemySprites[2][0], enemySprites[2][1], enemySprites[2][2]);
		zombieRight.setPlayMode(Animation.LOOP);
		
		Animation skelUp = new Animation(0.12f, enemySprites[3][3], enemySprites[3][3], enemySprites[3][5]);
		skelUp.setPlayMode(Animation.LOOP);
		
		Animation skelDown = new Animation(0.12f, enemySprites[0][3], enemySprites[0][4], enemySprites[0][5]);
		skelDown.setPlayMode(Animation.LOOP);
		
		Animation skelLeft = new Animation(0.12f, enemySprites[1][3], enemySprites[1][4], enemySprites[1][5]);
		skelLeft.setPlayMode(Animation.LOOP);
		
		Animation skelRight = new Animation(0.12f, enemySprites[2][3], enemySprites[2][4], enemySprites[2][5]);
		skelRight.setPlayMode(Animation.LOOP);
		
		TextureRegion[][] playerSprites = getAtlas().findRegion("player-sprites").split(64, 64);
		Animation playerUp = new Animation(0.09f, 
				playerSprites[0][0], playerSprites[0][1], playerSprites[0][2], playerSprites[0][3],
				playerSprites[0][4], playerSprites[0][5], playerSprites[0][6], playerSprites[0][7], playerSprites[0][8]);
		playerUp.setPlayMode(Animation.LOOP);
		
		Animation playerLeft = new Animation(0.09f, 
				playerSprites[1][0], playerSprites[1][1], playerSprites[1][2], playerSprites[1][3],
				playerSprites[1][4], playerSprites[1][5], playerSprites[1][6], playerSprites[1][7], playerSprites[1][8]);
		playerLeft.setPlayMode(Animation.LOOP);
		
		Animation playerDown = new Animation(0.09f, 
				playerSprites[2][0], playerSprites[2][1], playerSprites[2][2], playerSprites[2][3],
				playerSprites[2][4], playerSprites[2][5], playerSprites[2][6], playerSprites[2][7], playerSprites[2][8]);
		playerDown.setPlayMode(Animation.LOOP);
		
		Animation playerRight = new Animation(0.09f, 
				playerSprites[3][0], playerSprites[3][1], playerSprites[3][2], playerSprites[3][3],
				playerSprites[3][4], playerSprites[3][5], playerSprites[3][6], playerSprites[3][7], playerSprites[3][8]);
		playerRight.setPlayMode(Animation.LOOP);
		
		animations.put("zombieup", zombieUp);
		animations.put("zombiedown", zombieDown);
		animations.put("zombieleft", zombieLeft);
		animations.put("zombieright", zombieRight);
		
		animations.put("skelup", skelUp);
		animations.put("skeldown", skelDown);
		animations.put("skelleft", skelLeft);
		animations.put("skelright", skelRight);
		
		animations.put("playerup", playerUp);
		animations.put("playerdown", playerDown);
		animations.put("playerleft", playerLeft);
		animations.put("playerright", playerRight);
		
		animations.put("flag", new Animation(0.1f, getAtlas().findRegions("flag"), Animation.LOOP));
	}
	
	public static TextureRegion getImage(String name){
		return images.get(name);
	}
	
	public static Animation getAnimation(String name){
		return animations.get(name);
	}
	
	public static TextureAtlas getAtlas(){
		if (atlas == null){
			atlas = new TextureAtlas("data/images.atlas");
		}
		return atlas;
	}
	
	public static void dispose() {
		
		atlas.dispose();
		for(Sound s : sounds.values())
			s.dispose();
		
		for( BitmapFont f : fonts.values())
			f.dispose();
		sounds.clear();
		images.clear();
		animations.clear();
	}

	public static BitmapFont getFont(String name) {
		return fonts.get(name);
	}

	public static Sound getSound(String name) {
		return sounds.get(name);
	}
}
