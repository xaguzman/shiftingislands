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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MenuScreen implements Screen {
	
	Stage stage;
	
	public MenuScreen(){
		
		float width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		stage = new Stage(width, height, false, Respawn.SPRITE_BATCH);
			
		
		TextButton start = new TextButton("Play", Assets.uiSkin);
		start.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen());
			}
		});
		TextButton instructions = new TextButton("Instructions", Assets.uiSkin);
		instructions.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game)Gdx.app.getApplicationListener()).setScreen(new InstructionsScreen());
			}
		});
		Label legend = new Label("You crashed in an island with shifting landscape! \nGather wood to"
				+ " build your escape boat while \navoiding undead pirates", Assets.uiSkin);
		
		legend.setWrap(true);
		legend.setAlignment(Align.center);
		legend.setFontScale(0.8f);
		legend.pack();
		
		Window window = new Window("Shifting islands", Assets.uiSkin);
		window.setFillParent(true);
		window.setMovable(false);
		window.padTop(40);
		
		window.add(legend).expandX().colspan(2).padBottom(60);
		window.row();
		window.add(start).bottom();
		window.add(instructions).bottom();
		
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
		//Table.drawDebug(stage);
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
