package com.pmdm.invasoresespaciales;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Message;
import android.view.SurfaceHolder;

@SuppressLint("WrongCall")
public class GameLoopThread extends Thread {
	static final long FPS = 30;
	private boolean running = false;
	private Game gv;
	private SurfaceHolder surfaceHolder = null;

	public GameLoopThread(Game view) {
		super();
		this.gv = view;
		this.surfaceHolder = view.getHolder();
	}

	public void setRunning(boolean run) {
		running = run;
	}

	public void startThread() {
		running = true;
		super.start();
	}

	public void stopThread() {
		running = false;
	}

	public void run() {
		long ticksPS = 1000 / FPS;
		long startTime;
		long sleepTime;
		Canvas c = null;
		while (running) {
			c = null;
			startTime = System.currentTimeMillis();
			try {
				c = surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					if (c != null) {
                        gv.update();
						gv.onDraw(c);
					}
				}
			} finally {
				if (c != null) {
					surfaceHolder.unlockCanvasAndPost(c);
				}
			}
			sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
			try {
				if (sleepTime > 0)
					sleep(sleepTime);
				else
					sleep(10);
			} catch (Exception e) {}
		}
        try {
            sleep(3000);
        } catch (Exception e) {}
        Message msg = new Message();
        msg.arg1 = 1;
        gv.handler.sendMessage(msg);
	}
}
