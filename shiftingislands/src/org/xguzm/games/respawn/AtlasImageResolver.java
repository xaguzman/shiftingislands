package org.xguzm.games.respawn;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;

public class AtlasImageResolver implements ImageResolver {
	private final TextureAtlas atlas;

	public AtlasImageResolver(TextureAtlas atlas) {
		this.atlas = atlas;
	}

	@Override
	public TextureRegion getImage(String name) {
		return atlas.findRegion(name.substring(name.indexOf("/") + 1).replace(".png", ""));
	}
}
