package com.paradigm.botkit.message;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.paradigm.botkit.R;
import com.paradigm.botkit.util.AudioTools;
import com.paradigm.botlib.Message;
import com.paradigm.botlib.MessageContentAudio;

import java.io.File;

/**
 * Created by wuyifan on 2018/1/31.
 */

public class AudioMessageItemProvider extends MessageItemProvider implements View.OnClickListener {

    public interface OnContentClickListener {
        void onClick(MessageContentAudio content);
    }

    private OnContentClickListener contentClickListener;

    public AudioMessageItemProvider(OnContentClickListener contentClickListener) {
        this.contentClickListener = contentClickListener;
    }

    @Override
    public View createContentView(Context context) {
        AudioMessageItemHolder holder = new AudioMessageItemHolder();
        View contentView = LayoutInflater.from(context).inflate(R.layout.pd_item_message_audio, null);
        holder.setImage((ImageView) contentView.findViewById(R.id.pd_message_item_image));
        holder.setDuration((TextView) contentView.findViewById(R.id.pd_message_item_duration));
        contentView.setTag(holder);
        contentView.setOnClickListener(this);
        return contentView;
    }

    @Override
    public void bindContentView(View v, Message message) {
        AudioMessageItemHolder holder = (AudioMessageItemHolder) v.getTag();
        MessageContentAudio content = (MessageContentAudio) message.getContent();
        if (message.getDirection() == Message.DirectionRecv) {
            holder.getImage().setImageResource(R.drawable.pd_message_audio_left);
            holder.getDuration().setGravity(Gravity.LEFT);
        } else {
            holder.getImage().setImageResource(R.drawable.pd_message_audio_right);
            holder.getDuration().setGravity(Gravity.RIGHT);
        }
        holder.setContent(content);

        File file = new File(v.getContext().getFilesDir(), content.getDataPath());
        long duration = AudioTools.getAudioDuration(file);
        holder.getDuration().setText(String.format("%d''", duration / 1000));
    }

    @Override
    public void onClick(View v) {
        AudioMessageItemHolder holder = (AudioMessageItemHolder) v.getTag();
        if (contentClickListener != null) contentClickListener.onClick(holder.getContent());
    }
}
