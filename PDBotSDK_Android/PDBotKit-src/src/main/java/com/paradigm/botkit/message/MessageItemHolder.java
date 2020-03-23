package com.paradigm.botkit.message;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paradigm.botlib.Message;

/**
 * Created by wuyifan on 2018/1/30.
 */

public class MessageItemHolder {

    private TextView time;
    private LinearLayout messageBack;
    private FrameLayout contentBack;
    private ImageView portraitUser;
    private ImageView portraitRobot;
    private ImageView feedbackUp;
    private ImageView feedbackDown;
    private View contentView;
    private Message message;

    public TextView getTime() {
        return time;
    }

    public void setTime(TextView time) {
        this.time = time;
    }

    public LinearLayout getMessageBack() {
        return messageBack;
    }

    public void setMessageBack(LinearLayout messageBack) {
        this.messageBack = messageBack;
    }

    public FrameLayout getContentBack() {
        return contentBack;
    }

    public void setContentBack(FrameLayout contentBack) {
        this.contentBack = contentBack;
    }

    public ImageView getPortraitUser() {
        return portraitUser;
    }

    public void setPortraitUser(ImageView portraitUser) {
        this.portraitUser = portraitUser;
    }

    public ImageView getPortraitRobot() {
        return portraitRobot;
    }

    public void setPortraitRobot(ImageView portraitRobot) {
        this.portraitRobot = portraitRobot;
    }

    public ImageView getFeedbackUp() {
        return feedbackUp;
    }

    public void setFeedbackUp(ImageView feedbackUp) {
        this.feedbackUp = feedbackUp;
    }

    public ImageView getFeedbackDown() {
        return feedbackDown;
    }

    public void setFeedbackDown(ImageView feedbackDown) {
        this.feedbackDown = feedbackDown;
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
