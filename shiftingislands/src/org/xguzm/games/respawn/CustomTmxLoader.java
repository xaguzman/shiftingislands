package org.xguzm.games.respawn;

import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.ImageResolver.DirectImageResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader.Element;

public class CustomTmxLoader extends TmxMapLoader {

	public TiledMap load(String fileName, TextureAtlas atlas) {
		Parameters parameters = new Parameters();
		try {
			this.yUp = parameters.yUp;
			this.convertObjectToTileSpace = parameters.convertObjectToTileSpace;
			FileHandle tmxFile = resolve(fileName);
			root = xml.parse(tmxFile);
			ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();
			for (FileHandle textureFile : loadTilesets(root, tmxFile)) {
				Texture texture = new Texture(textureFile, parameters.generateMipMaps);
				texture.setFilter(parameters.textureMinFilter, parameters.textureMagFilter);
				textures.put(textureFile.path(), texture);
			}
//			ImageResolver imageResolver = new AtlasImageResolver(atlas);
			ImageResolver imageResolver = new DirectImageResolver(textures);
			TiledMap map = loadTilemap(root, tmxFile, imageResolver);
			map.setOwnedResources(textures.values().toArray());
			return map;
		} catch (IOException e) {
			throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
		}
	}
	
	/** Load one layer (a 'layer' tag).
	 * @param map
	 * @param element */
	protected void loadTileLayer (TiledMap map, Element element) {
		String layerName = element.getAttribute("name");
		if (layerName.equalsIgnoreCase("data") || layerName.equalsIgnoreCase("tiles")){
			element.setAttribute("visible", "0");
		}
		super.loadTileLayer(map, element);
	}
}
