package org.xguzm.games.respawn.screens;

import org.xguzm.games.respawn.Respawn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;


public class MenuScreen implements Screen {
	
	Stage stage;
	
	public MenuScreen(){
		float width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		stage = new Stage(width, height, false, Respawn.SPRITE_BATCH);
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor( 0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		
		
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {		
	}

	@Override
	public void show() {		
	}

	@Override
	public void hide() {		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
