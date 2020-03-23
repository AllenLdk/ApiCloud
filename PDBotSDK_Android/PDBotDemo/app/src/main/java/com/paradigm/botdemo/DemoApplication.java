package com.paradigm.botdemo;

import android.app.Application;

import com.paradigm.botkit.BotKitClient;

/**
 * Created by wuyifan on 2018/7/3.
 */

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String accessKey = HistoryDataManager.getInstance().getRecentAccessKey(this);
        BotKitClient.getInstance().enableDebugLog();
        BotKitClient.getInstance().init(this, accessKey);
    }
}
