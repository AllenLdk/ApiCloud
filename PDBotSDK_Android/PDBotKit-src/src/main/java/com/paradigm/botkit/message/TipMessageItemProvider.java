package com.paradigm.botkit.message;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paradigm.botkit.R;
import com.paradigm.botlib.Message;
import com.paradigm.botlib.MessageContentTip;

/**
 * Created by wuyifan on 2018/5/24.
 */

public class TipMessageItemProvider extends MessageItemProvider {

    @Override
    public View createContentView(Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.pd_item_message_tip, null);
        return contentView;
    }

    @Override
    public void bindMessageView(View v, Message message, String timeString) {
        MessageItemHolder holder = (MessageItemHolder) v.getTag();

        holder.getTime().setVisibility(View.GONE);
        holder.getPortraitUser().setVisibility(View.GONE);
        holder.getPortraitRobot().setVisibility(View.GONE);
        holder.getFeedbackUp().setVisibility(View.GONE);
        holder.getFeedbackDown().setVisibility(View.GONE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.getMessageBack().getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        holder.getMessageBack().setLayoutParams(layoutParams);

        View contentView = holder.getContentView();
        contentView.setVisibility(View.GONE);
        this.bindContentView(contentView, message);
        contentView.setVisibility(View.VISIBLE);
    }

    @Override
    public void bindContentView(View v, Message message) {
        TextView contentView = (TextView) v;
        MessageContentTip content = (MessageContentTip) message.getContent();
        contentView.setText(content.getTip());
    }
}
