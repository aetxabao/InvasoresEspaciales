package com.pmdm.invasoresespaciales;

import android.content.Intent;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    MyApplication myApp;
    RelativeLayout layout;
    MediaPlayer mp;
    boolean isMusicLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myApp = (MyApplication) getApplication();
        myApp.level = 1;
        myApp.score = 0;

        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        layout = (RelativeLayout) findViewById(R.id.LayoutMain);
        layout.setOnTouchListener(this);

        TextView tv = (TextView) findViewById(R.id.TxtTitle);

        mp = MediaPlayer.create(this, R.raw.start);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setLooping(true);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                isMusicLoaded = true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMusicLoaded){
            if (!mp.isPlaying()) {
                mp.start();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mp.isPlaying()) {
            mp.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
            mp = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        myApp.screenWidth = layout.getWidth();
        myApp.screenHeight = layout.getHeight();
        myApp.gameRect = new Rect(0,(int)(myApp.screenHeight*0.15),myApp.screenWidth,(int)(myApp.screenHeight*0.85));
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
        return false;
    }
}
