package org.xguzm.games.respawn.actors;

import org.xguzm.games.respawn.Assets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Flag extends Image {

	float accum;
	
	TextureRegionDrawable drawable = new TextureRegionDrawable();
	
	public Flag() {
		setDrawable(drawable);
		TextureRegion f = Assets.getAnimation("flag").getKeyFrame(accum);
		setSize(f.getRegionWidth(), f.getRegionHeight());
	}
	
	@Override
	public void act(float delta) {
		accum += delta;
		
		drawable.setRegion(Assets.getAnimation("flag").getKeyFrame(accum));
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}
}
