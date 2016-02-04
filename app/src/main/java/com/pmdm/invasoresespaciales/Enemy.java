package com.pmdm.invasoresespaciales;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;

public class Enemy extends Sprite {
	// direction = 0 up, 1 left, 2 down, 3 right
	// animation = 3 back, 1 left, 0 front, 2 right
	int[] DIR = {3, 1, 0, 2};
	int ROWS = 4;
	int COLS = 3;
	private static final int MAX_SPEED = 5;

	private Game gv;

	public Enemy(Game gv, Rect gameRect, Bitmap bmp) {
		this.gv = gv;
		this.setBmpRowsColsWidthHeight(bmp, ROWS, COLS);

		Random rnd = new Random();
		xSpeed = rnd.nextInt(MAX_SPEED*2) - MAX_SPEED;
		ySpeed = rnd.nextInt(MAX_SPEED*2) - MAX_SPEED;
        x = gameRect.left+rnd.nextInt(gameRect.width()-2*width)+width/2;
        y = gameRect.top+rnd.nextInt(gameRect.height()-2*height)+height/2;
	}

	public void update() {
		if (x > gv.getGameRect().right - width - xSpeed || x + xSpeed < gv.getGameRect().left) {
			xSpeed = -xSpeed;
		}
		x = x + xSpeed;
		if (y > gv.getGameRect().bottom - height - ySpeed || y + ySpeed < gv.getGameRect().top) {
			ySpeed = -ySpeed;
		}
		y = y + ySpeed;
		updateFrame();
	}

    public void updateFrame(){
        currentFrame = ++currentFrame%cols;
    }

	@SuppressLint("DrawAllocation")
	public void onDraw(Canvas canvas) {
		//update();
		int srcX = currentFrame * width;
		int srcY = getAnimationRow() * height;
		Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
		Rect dst = new Rect(x, y, x+width, y+height);
		canvas.drawBitmap(bmp, src, dst, null);
	}
	
	// direction = 0 up, 1 left, 2 down, 3 right
	// animation = 3 back, 1 left, 0 front, 2 right
	private int getAnimationRow(){
		double dirDouble = (Math.atan2(xSpeed, ySpeed) / (Math.PI / 2) + 2);
		int dir = (int) Math.round(dirDouble) % rows;
		return DIR[dir];
	}

    public void bounce(){
        xSpeed = -1*++xSpeed;
        ySpeed = -1*++ySpeed;
        update();
    }

}
