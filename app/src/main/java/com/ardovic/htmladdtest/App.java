package com.ardovic.htmladdtest;

import android.app.Application;

public class App extends Application {

    private static App instance;
    private AddData addData;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }

    public AddData getAppData(){
        return addData;
    }

    public void setAppData(AddData appData){
        this.addData = appData;
    }
}
