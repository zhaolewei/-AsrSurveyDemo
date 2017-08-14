package com.zlw.main.asrsurveydemo;

import android.app.Application;

import com.zlw.main.asrsurveydemo.utils.Logger;


public class MyApp extends Application {
    private static final String TAG = MyApp.class.getSimpleName();
    private static MyApp ins;

    public static MyApp getInstance() {
        return ins;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "=========>>MyApp<<========= ");
        ins = this;
    }


}
