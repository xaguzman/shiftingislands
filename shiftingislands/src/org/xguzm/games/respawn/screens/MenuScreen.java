package org.xguzm.games.respawn.screens;

import org.xguzm.games.respawn.Respawn;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MenuScreen implements Screen {
	
	Stage stage;
	TextureAtlas atlas;
	Skin skin;
	
	public MenuScreen(){
		float width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		stage = new Stage(width, height, false, Respawn.SPRITE_BATCH);
		atlas = new TextureAtlas(Gdx.files.internal("data/ui.atlas"));
		skin = new Skin(Gdx.files.internal("data/ui-skin.json"), atlas);
		
		Table layout = new Table(skin);
		layout.setBackground("menu-bg");
		layout.setFillParent(true);
		
		TextButton start = new TextButton("Play", skin);
		start.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen());
			}
		});
		TextButton instructions = new TextButton("Instructions", skin);
		instructions.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game)Gdx.app.getApplicationListener()).setScreen(new InstructionsScreen());
			}
		});
		
		layout.add(start).bottom();
		layout.add(instructions).bottom();
		
		stage.addActor(layout);
		
		
		Gdx.input.setInputProcessor(stage);
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
	}

	@Override
	public void dispose() {	
		atlas.dispose();
		stage.dispose();
		skin.dispose();
	}

}
