package org.xguzm.games.respawn.screens;

import org.xguzm.games.respawn.Board;
import org.xguzm.games.respawn.Respawn;
import org.xguzm.games.respawn.actors.ProgressBar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameScreen implements Screen {

	Stage gameStage;
	Stage uiStage;
	Music bgMusic;
	
	public GameScreen(){
		float width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		
		gameStage = new Board(width, height, false, "data/map1.tmx");
		
		ProgressBar pBar = new ProgressBar("Progress", (Board)gameStage);
		pBar.setPosition(width - pBar.getWidth(), height - pBar.getHeight() - 15);
		
		uiStage = new Stage(width, height, false, Respawn.SPRITE_BATCH);
		uiStage.addActor(pBar);
		
		bgMusic = Gdx.audio.newMusic(Gdx.files.internal("data/island_0.ogg"));
		bgMusic.setLooping(true);
		//Gdx.input.setInputProcessor( new InputMultiplexer(gameStage, debuggerInput));
		Gdx.input.setInputProcessor( gameStage );
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor( 21f / 255f , 108f / 255f , 153f / 255f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		delta = Math.min(delta, 1f / 60f);
		gameStage.act(delta);
		uiStage.act(delta);
		
		if (Gdx.gl11 != null && Gdx.gl10 != null)
			gameStage.getCamera().apply(Gdx.gl10);
		
		gameStage.draw();
		uiStage.draw();
	}
	
	

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		bgMusic.play();
	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		uiStage.dispose();
		gameStage.dispose();
		bgMusic.dispose();
	}
	
	
	InputAdapter debuggerInput = new InputAdapter(){
		int lastTouchX;
		int lastTouchY;
		
		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {		
			lastTouchX = x;
			lastTouchY = y;
			return true;
		}
		
		@Override public boolean touchDragged(int x, int y, int pointer) {
			OrthographicCamera camera = (OrthographicCamera)gameStage.getCamera();
			
			gameStage.getCamera().position.add((lastTouchX - x) * camera.zoom, (y - lastTouchY) * camera.zoom, 0);
			lastTouchX = x;
			lastTouchY = y;
			return true;
		}
		
		@Override public boolean scrolled(int amount) {
			OrthographicCamera camera = (OrthographicCamera)gameStage.getCamera();
			camera.zoom *= amount > 0 ? 1.05f : 0.95f;
			return true;
		}
	};

}
