package com.news.android.newssystem;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Alan on 2018-03-16.
 * 初始化资源
 */

public class MyApplication extends Application {

    /**
     * 初始化Fresco图片处理类
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
