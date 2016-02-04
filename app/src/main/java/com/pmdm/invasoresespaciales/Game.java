package com.pmdm.invasoresespaciales;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SuppressLint("WrongCall")
public class Game extends SurfaceView implements SurfaceHolder.Callback {

    private MyApplication myApp;
	private GameLoopThread thread;
    private Hero hero;
    private List<Bullet> bullets = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
	private List<TempSprite> temps = new ArrayList<>();
	private long lastClick;
	private Bitmap bmpBlood;
	public Context context;
    private long time = 0;
    private int timeOut = 0;
    private boolean isYouLoose = false;
    private boolean isYouWin = false;
    private SparseArray<PointF> mActivePointers;
    ControlSet controls;
    private long lastFire = 0;
    private long firingInterval = 333;

    private static final int PTS = 20;

    public MediaPlayer mp;
    boolean isMusicLoaded = false;

    SoundPool soundPool;
    int idShoot, idAh;

    public Handler handler;

	public Game(final Context context) {
		super(context);
        this.context = context;
        myApp = (MyApplication) getContext().getApplicationContext();
		getHolder().addCallback(this);

        mActivePointers = new SparseArray<PointF>();
        controls = new ControlSet();
        controls.setActivePointers(mActivePointers);
        int x, y, w, h;
        w = (int)(myApp.screenWidth/3);
        h = myApp.screenHeight - myApp.gameRect.bottom;
        x = myApp.gameRect.left;
        y = myApp.gameRect.bottom;
        controls.setLeftRect(new RectF(x,y,x+w,y+h));
        controls.setRightRect(new RectF(x+2*w,y,x+3*w,y+h));
        controls.setShootRect(new RectF(x + w, y, x + 2 * w, y + h));

        mp = MediaPlayer.create(context, R.raw.game);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setLooping(true);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                isMusicLoaded = true;
            }
        });

        bmpBlood = BitmapFactory.decodeResource(getResources(), R.drawable.blood);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        }else{
            createOldSoundPool();
        }
        idShoot = soundPool.load(context, R.raw.shoot, 0);
        idAh = soundPool.load(context, R.raw.ah, 0);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.arg1==1){
                    Log.d("LOG-GameView", "msg 1");
                    if (isYouLoose){
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                    if (isYouWin){
                        myApp.level++;
                        for(Iterator<TempSprite> it=temps.iterator();it.hasNext();){
                            it.next();
                            it.remove();
                        }
                        for(Iterator<Bullet> it=bullets.iterator();it.hasNext();){
                            it.next();
                            it.remove();
                        }
                        startGame();
                    }
                }
                return false;
            }
        });
	}

    public Rect getGameRect(){
        return myApp.gameRect;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
        Log.d("LOG-GameView", "createNewSoundPool");
    }

    @SuppressWarnings("deprecation")
    protected void createOldSoundPool(){
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        Log.d("LOG-GameView", "createOldSoundPool");
    }

	public void startGame() {
		if (thread == null) {
            Log.d("LOG-GameView", "startGame");
            initiateGameView();
			thread = new GameLoopThread(this);
			thread.startThread();
            try{
                if (isMusicLoaded){
                    if (!mp.isPlaying()) {
                        mp.start();
                        Log.d("LOG-GameView", "mp.start");
                    }
                }
            }catch(Exception ignored){}
		}
	}

    public void initiateGameView(){
        isYouWin = false;
        isYouLoose = false;
        time = 0;
        timeOut = myApp.time[(myApp.level-1)%myApp.time.length];
        createSprites();
    }

    private void createSprites() {
        int ids[] = {R.drawable.stu1,R.drawable.stu2,R.drawable.stu3,R.drawable.stu4};
        int N = myApp.people[(myApp.level-1)%myApp.people.length];
        Random rnd = new Random();
        for(int i=0;i<N;i++){
            enemies.add(createSprite(ids[rnd.nextInt(ids.length)]));
        }

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.stu0);
        hero = new Hero(this, myApp.gameRect, bmp);

    }

    private Enemy createSprite(int resource) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resource);
        return new Enemy(this, myApp.gameRect, bmp);
    }

    public void stopGame() {
        try{
            if (mp.isPlaying()) {
                mp.pause();
            }
        }catch(Exception ignored){}
        soundPool.stop(idShoot);
        soundPool.stop(idAh);
		if (thread != null) {
            Log.d("LOG-GameView", "stopGame");
			thread.stopThread();
			thread = null;
		}
	}

    public void update() {
        if(controls.isLeft()){
            hero.moveLeft();
        }
        if(controls.isRight()){
            hero.moveRight();
        }
        if(!controls.isLeft()&&!controls.isRight()){
            hero.moveNoWhere();
        }
        if(controls.isShoot()){
            if (System.currentTimeMillis() - lastFire >= firingInterval) {
                lastFire = System.currentTimeMillis();
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bullet);
                Bullet bullet = new Bullet(this, myApp.gameRect, bmp);
                bullet.setPos(hero.x+(hero.width-bullet.width)/2,hero.y-bullet.height);
                bullets.add(bullet);
                soundPool.play(idShoot, 1, 1, 1, 0, 1);
            }
        }

OuterLoop: for (Iterator<Bullet> it = bullets.iterator(); it.hasNext(); ) {
            Bullet bullet = it.next();
            for (Iterator<Enemy> iter = enemies.iterator(); iter.hasNext(); ) {
                Enemy enemy = iter.next();
                if (bullet.collides(enemy)){
                    temps.add(new TempSprite(temps, this, enemy.x + enemy.width/2, enemy.y + enemy.height/2, bmpBlood));
                    soundPool.play(idAh, 1, 1, 1, 0, 1);
                    myApp.score += PTS;
                    iter.remove();
                    it.remove();
                    continue OuterLoop;
                }
            }
            if (myApp.gameRect.contains(bullet.getRect())){
                bullet.update();
            }else{
                it.remove();
            }
        }
        hero.update();
        for (int i=temps.size()-1;i>=0;i--) {
            temps.get(i).update();
        }
        int i,j;
        int N = enemies.size();
        for(i=0;i<N;i++){
            for(j=i+1;j<N; j++){
                if(enemies.get(i).collides(enemies.get(j))){
                    enemies.get(i).bounce();
                    enemies.get(i).update();
                    enemies.get(j).bounce();
                }
            }
        }
        for (Enemy enemy : enemies) {
            enemy.update();
        }
        if ((timeOut>0)&&(enemies.size()==0)&&(temps.size()==0)){
            Log.d("LOG-Game", "You win!");
            isYouWin = true;
            stopGame();
        }
        if (timeOut==0){
            Log.d("LOG-GameView", "You loose!");
            isYouLoose = true;
            stopGame();
        }
    }

	public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(getGameRect(), paint);
        drawControls(canvas);
        hero.onDraw(canvas);
		for (int i=temps.size()-1;i>=0;i--) {
			temps.get(i).onDraw(canvas);
		}
		for (Enemy sprite : enemies) {
			sprite.onDraw(canvas);
		}
        for (Bullet bullet : bullets) {
            bullet.onDraw(canvas);
        }
        drawScore(canvas);
        drawTime(canvas);
        drawIfYouLoose(canvas);
        drawIfYouWin(canvas);
	}

    private void drawControls(Canvas canvas){
        Paint paint = new Paint();
        if (controls.isLeft()){
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
        }else{
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
        }
        canvas.drawOval(controls.rectLeft, paint);
        if (controls.isRight()){
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
        }else{
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
        }
        canvas.drawOval(controls.rectRigth, paint);
        if (controls.isShoot()){
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
        }else{
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
        }
        canvas.drawRect(controls.rectShoot,paint);
    }

    private void drawScore(Canvas canvas){
        String str = getResources().getString(R.string.points) + ": " + myApp.score;
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(40);
        paint.setTypeface(Typeface.MONOSPACE);
        canvas.drawText(str, 10, 100, paint);
    }

    private void drawTime(Canvas canvas){
        int d;
        long t;
        if (time==0) {
            time = System.currentTimeMillis();
        }else{
            t = System.currentTimeMillis();
            d = (int)(t - time);
            timeOut = timeOut - d;
            timeOut = timeOut>0?timeOut:0;
            String str = getResources().getString(R.string.time) + ": "
                       + String.format("%.2f", ((double) timeOut) / 1000);
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setTextSize(40);
            paint.setTypeface(Typeface.MONOSPACE);
            canvas.drawText(str, 10, 50, paint);
            time = t;
        }
    }

    private void drawIfYouLoose(Canvas canvas){
        if (isYouLoose){
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setTextSize(80);
            paint.setTypeface(Typeface.MONOSPACE);
            canvas.drawText("You loose!", 40, getHeight()/2, paint);
        }
    }

    private void drawIfYouWin(Canvas canvas){
        if (isYouWin){
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setTextSize(80);
            paint.setTypeface(Typeface.MONOSPACE);
            canvas.drawText("You win!", 40, getHeight()/2, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();
        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);
        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();
        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                // We have a new pointer. Lets add it to the list of pointers
                PointF f = new PointF();
                f.x = event.getX(pointerIndex);
                f.y = event.getY(pointerIndex);
                mActivePointers.put(pointerId, f);
                break;
            }
            case MotionEvent.ACTION_MOVE: { // a pointer was moved
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    PointF point = mActivePointers.get(event.getPointerId(i));
                    if (point != null) {
                        point.x = event.getX(i);
                        point.y = event.getY(i);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                mActivePointers.remove(pointerId);
                break;
            }
        }
        return true;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

	public void surfaceCreated(SurfaceHolder holder) {
		startGame();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		stopGame();
	}

    public void destroy(){
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
            mp = null;
        }
    }
}
