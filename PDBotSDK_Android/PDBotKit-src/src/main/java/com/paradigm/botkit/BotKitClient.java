package com.paradigm.botkit;

import android.graphics.drawable.Drawable;

import com.paradigm.botlib.BotLibClient;

/**
 * Created by wuyifan on 2018/7/3.
 */

public class BotKitClient extends BotLibClient {

    private Drawable portraitUser;
    private Drawable portraitRobot;

    private static BotKitClient instance;

    public static BotKitClient getInstance() {

        if (instance == null) {
            synchronized (BotKitClient.class) {
                if (instance == null) {
                    instance = new BotKitClient();
                }
            }
        }
        return instance;
    }

    public Drawable getPortraitUser() {
        return portraitUser;
    }

    public void setPortraitUser(Drawable portraitUser) {
        this.portraitUser = portraitUser;
    }

    public Drawable getPortraitRobot() {
        return portraitRobot;
    }

    public void setPortraitRobot(Drawable portraitRobot) {
        this.portraitRobot = portraitRobot;
    }
}
