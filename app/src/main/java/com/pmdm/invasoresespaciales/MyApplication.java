package com.pmdm.invasoresespaciales;

import android.app.Application;
import android.graphics.Rect;

//<application android:name="com.pmdm.MyApplication" ...>
//
//MyApplication myApp = (MyApplication) getApplication();
//myApp.doSomething();
//
//MyApplication myApp = (MyApplication) getContext().getApplicationContext();
//myApp.doSomething();
//
public class MyApplication extends Application {

    public int screenWidth = 0;
    public int screenHeight = 0;
    public Rect gameRect = null;
    public int level = 1;
    public int score = 0;

    public int[] people = {5,10,15,20};
    public int[] time = {15000,30000,40000,50000};
}