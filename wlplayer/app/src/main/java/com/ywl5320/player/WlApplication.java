package com.ywl5320.player;

import android.app.Application;

/**
 * Created by ywl on 2017-12-30.
 */

public class WlApplication extends Application{

    private static WlApplication instance;
    public static WlApplication getInstance()
    {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
