package com.paradigm.botkit.message;

import android.widget.ImageView;
import android.widget.TextView;

import com.paradigm.botlib.MessageContentAudio;

/**
 * Created by wuyifan on 2018/9/13.
 */

public class AudioMessageItemHolder {

    private ImageView image;
    private TextView duration;
    private MessageContentAudio content;

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public TextView getDuration() {
        return duration;
    }

    public void setDuration(TextView duration) {
        this.duration = duration;
    }

    public MessageContentAudio getContent() {
        return content;
    }

    public void setContent(MessageContentAudio content) {
        this.content = content;
    }
}
