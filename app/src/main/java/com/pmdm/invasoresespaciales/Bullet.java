package com.pmdm.invasoresespaciales;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Bullet extends Sprite {
    int ROWS = 1;
    int COLS = 1;
    private static final int MAX_SPEED = 10;

    private Game gv;

    public Bullet(Game gv, Rect gameRect, Bitmap bmp) {
        this.gv = gv;
        this.setBmpRowsColsWidthHeight(bmp, ROWS, COLS);
        xSpeed = 0;
        ySpeed = MAX_SPEED;
    }

    public void update() {
        x = x + xSpeed;
        y = y - ySpeed;
    }

    @SuppressLint("DrawAllocation")
    public void onDraw(Canvas canvas) {
        int srcX = 0;
        int srcY = 0;
        Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
        Rect dst = new Rect(x, y, x+width, y+height);
        canvas.drawBitmap(bmp, src, dst, null);
    }

}
