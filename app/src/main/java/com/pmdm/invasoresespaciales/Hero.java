package com.pmdm.invasoresespaciales;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public class Hero extends Sprite {
    int ROWS = 4;
    int COLS = 3;
    private static final int MAX_SPEED = 5;
    static final int LEFT = 1;
    static final int RIGHT = 2;
    static final int UP = 3;
    int side;

    private Game gv;

    public Hero(Game gv, Rect gameRect, Bitmap bmp) {
        this.gv = gv;
        this.setBmpRowsColsWidthHeight(bmp, ROWS, COLS);
        xSpeed = MAX_SPEED;
        ySpeed = 0;
        x = gameRect.centerX()-width/2;
        y = (int)(gameRect.bottom-1.2*height);
    }

    public void update() {
        if (x <= gv.getGameRect().right - width - xSpeed && x + xSpeed >= gv.getGameRect().left) {
            x = x + xSpeed;
        }
    }

    public void updateFrame(){
        currentFrame = ++currentFrame%cols;
    }

    public void moveRight(){
        xSpeed = MAX_SPEED;
        side = RIGHT;
        updateFrame();
    }

    public void moveLeft(){
        xSpeed = -MAX_SPEED;
        side = LEFT;
        updateFrame();
    }

    public void moveNoWhere(){
        xSpeed = 0;
        side = UP;
    }

    @SuppressLint("DrawAllocation")
    public void onDraw(Canvas canvas) {
        int srcX = currentFrame * width;
        int srcY = side * height;
        Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
        Rect dst = new Rect(x, y, x+width, y+height);
        canvas.drawBitmap(bmp, src, dst, null);
    }

    public boolean intersects(Sprite sprite){
        RectF rA = new RectF(x,y,x+width,y+height);
        RectF rB = new RectF(sprite.x,sprite.y,sprite.x+width,sprite.y+height);
        return rA.intersect(rB);
    }

}
