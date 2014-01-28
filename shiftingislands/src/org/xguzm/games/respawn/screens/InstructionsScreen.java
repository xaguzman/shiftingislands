package org.xguzm.games.respawn.screens;

import org.xguzm.games.respawn.Assets;
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
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class InstructionsScreen implements Screen{

	private Stage stage;
	
	public InstructionsScreen(){		
		float width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		stage = new Stage(width, height, false, Respawn.SPRITE_BATCH);
				
		TextButton back = new TextButton("Back", Assets.uiSkin);
		back.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game)Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
			}
		});
		
		Window window = new Window("Shifting islands", Assets.uiSkin, "instructions");
		window.setFillParent(true);
		window.setMovable(false);
		window.padTop(40);
		
		window.row().expand();
		window.add(back).bottom().expandX();
		window.row().expandY().minHeight(50);		
		window.pack();
		stage.addActor(window);
		
		window.debug();
		
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor( 0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		stage.draw();
		Table.drawDebug(stage);
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
		stage.dispose();
	}

}
