package org.xguzm.games.respawn.tweens;

import com.badlogic.gdx.graphics.OrthographicCamera;

import aurelienribon.tweenengine.TweenAccessor;

public class CameraAccesor  implements  TweenAccessor<OrthographicCamera> {

	public static final int POSITION = 1;
	public static final int ZOOM = 2;
	
	@Override
	public int getValues(OrthographicCamera target, int tweenType,
			float[] returnValues) {
		
		switch(tweenType){
		case POSITION:
			returnValues[0] = target.position.x;
			returnValues[1] = target.position.y;
			return 2;
		case ZOOM:
			returnValues[0] = target.zoom;
			return 1;
		}
		
		return 0;
	}
	@Override
	public void setValues(OrthographicCamera target, int tweenType,
			float[] newValues) {
		switch(tweenType){
		case POSITION:
			target.position.x = newValues[0];
			target.position.y = newValues[1];
			break;
		case ZOOM:
			target.zoom = newValues[0];
			break;
		}
	}
	


}
