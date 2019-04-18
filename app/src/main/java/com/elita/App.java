package com.elita;

import android.app.Application;

import com.elita.studydemo.ElitaSdkApi;

/**
 * Created by SDC on 2019/4/18.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ElitaSdkApi.get(this);
    }
}
