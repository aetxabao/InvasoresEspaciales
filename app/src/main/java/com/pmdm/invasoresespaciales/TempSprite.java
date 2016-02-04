package com.pmdm.invasoresespaciales;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.List;

public class TempSprite {

	private float x;
	private float y;
	private Bitmap bmp;
    private static int N = 16;
	private int life = N;
	private List<TempSprite> temps;
    Paint paint;

	public TempSprite(List<TempSprite> temps, Game game, float x, float y, Bitmap bmp) {
		this.x = x - bmp.getWidth()/2;
		this.y = y - bmp.getHeight()/2;
		this.bmp = bmp;
		this.temps = temps;
        paint = new Paint();
	}

	public void onDraw(Canvas canvas) {
        paint.setAlpha(255*life/N);
		canvas.drawBitmap(bmp, x, y, paint);
	}

	public void update() {
		if (--life < 1) {
			temps.remove(this);
		}
	}

	public void remove() {
			temps.remove(this);
	}

}
