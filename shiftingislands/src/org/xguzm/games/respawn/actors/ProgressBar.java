package org.xguzm.games.respawn.actors;

import org.xguzm.games.respawn.Assets;
import org.xguzm.games.respawn.Board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.StringBuilder;

public class ProgressBar extends Table{
	private BitmapFont font;
	private Label score;
	private StringBuilder strBuilder = new StringBuilder(8);
	private Board board;
	

	public ProgressBar(String caption, Board board) {
	
		this.board = board;
		
		//font = skin.getFont("buxton");
		font = Assets.getFont("default");
		font.setScale(0.6f);
		
		
		Image logImg = new Image(Assets.getImage("simplelog"));
		
		logImg.setSize(32, 32);
		score = new Label("00 / 100", new LabelStyle(font, Color.WHITE));
		
		add(logImg).padRight(3).fillX();
		add();
		add(score);
		
		pack();
	}
	
	@Override
	public void act(float delta) {
		strBuilder.length = 0;
		strBuilder.append(board.currentScore).append(" / ").append(board.maxScore);
		
		score.setText(strBuilder.toString());
		
		setPosition(getStage().getWidth() - getWidth() - 10, getStage().getHeight() - getHeight() - 15);
		
		if (board.currentScore == board.maxScore)
			score.setColor(Color.GREEN);
		super.act(delta);
	}
}