package com.pmdm.invasoresespaciales;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public abstract class Sprite {
    protected int rows;
    protected int cols;

    public int x = 0;
    public int y = 0;
    public int xSpeed = 0;
    public int ySpeed = 0;
    protected Bitmap bmp;

    protected int currentFrame = 0;
    protected int width = 0;
    protected int height = 0;

    public void setBmpRowsColsWidthHeight(Bitmap bmp, int rows, int cols){
        this.bmp = bmp;
        this.rows = rows;
        this.cols = cols;
        this.width = bmp.getWidth() / cols;
        this.height = bmp.getHeight() / rows;
    }

	public void setPos(int x, int y){
		this.x = x;
		this.y = y;
	}

    public void setXSpeed(int xSpeed){
        this.xSpeed = xSpeed;
    }

    public void setYSpeed(int ySpeed){
        this.ySpeed = ySpeed;
    }

    public Rect getRect(){
        return new Rect(x,y,x+width,y+height);
    }

    public boolean collides(Sprite sprite){
        Rect rA = new Rect(x,y,x+width,y+height);
        Rect rB = new Rect(sprite.x,sprite.y,sprite.x+sprite.width,sprite.y+sprite.height);
        return  Rect.intersects(rA,rB) || rB.contains(rA) || rA.contains(rB);
    }

    @SuppressLint("DrawAllocation")
    public abstract void onDraw(Canvas canvas);


}
