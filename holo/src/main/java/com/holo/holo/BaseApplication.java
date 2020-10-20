package com.holo.holo;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Author: wangchengge
 * Date: 2020/10/20
 * Version: 1.0.0
 * Description:description
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
