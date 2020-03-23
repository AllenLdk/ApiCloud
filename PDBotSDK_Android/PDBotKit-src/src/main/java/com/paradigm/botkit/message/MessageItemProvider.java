package com.paradigm.botkit.message;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paradigm.botkit.BotKitClient;
import com.paradigm.botkit.R;
import com.paradigm.botlib.Message;

/**
 * Created by wuyifan on 2018/1/30.
 */

public abstract class MessageItemProvider {

    public View createMessageView(Context context) {

        MessageItemHolder holder = new MessageItemHolder();

        View messageView = LayoutInflater.from(context).inflate(R.layout.pd_item_message, null);
        holder.setTime((TextView) messageView.findViewById(R.id.pd_message_time));
        holder.setMessageBack((LinearLayout) messageView.findViewById(R.id.pd_message_back));
        holder.setContentBack((FrameLayout) messageView.findViewById(R.id.pd_message_content_back));
        holder.setPortraitUser((ImageView) messageView.findViewById(R.id.pd_message_portrait_l));
        holder.setPortraitRobot((ImageView) messageView.findViewById(R.id.pd_message_portrait_r));
        holder.setFeedbackUp((ImageView) messageView.findViewById(R.id.pd_message_feedback_up));
        holder.setFeedbackDown((ImageView) messageView.findViewById(R.id.pd_message_feedback_down));
        messageView.setTag(holder);

        View contentView = this.createContentView(context);
        holder.setContentView(contentView);
        holder.getContentBack().addView(contentView);

        Drawable portraitUser = BotKitClient.getInstance().getPortraitUser();
        Drawable portraitRobot = BotKitClient.getInstance().getPortraitRobot();
        if (portraitUser != null) holder.getPortraitUser().setImageDrawable(portraitUser);
        if (portraitRobot != null) holder.getPortraitRobot().setImageDrawable(portraitRobot);

        holder.getFeedbackUp().setTag(holder);
        holder.getFeedbackUp().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageItemHolder holder = (MessageItemHolder) v.getTag();
                if (holder.getMessage() != null && holder.getMessage().getFeedbackResult() == null) {
                    BotKitClient.getInstance().sendFeedback(holder.getMessage().getFeedbackId(), true);
                    holder.getMessage().setFeedbackResult(true);
                    holder.getFeedbackUp().setSelected(true);
                }
            }
        });

        holder.getFeedbackDown().setTag(holder);
        holder.getFeedbackDown().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageItemHolder holder = (MessageItemHolder) v.getTag();
                if (holder.getMessage() != null && holder.getMessage().getFeedbackResult() == null) {
                    BotKitClient.getInstance().sendFeedback(holder.getMessage().getFeedbackId(), false);
                    holder.getMessage().setFeedbackResult(true);
                    holder.getFeedbackDown().setSelected(true);
                }
            }
        });

        return messageView;
    }

    public void bindMessageView(View v, Message message, String timeString) {

        MessageItemHolder holder = (MessageItemHolder) v.getTag();
        holder.setMessage(message);

        if (timeString == null) {
            holder.getTime().setVisibility(View.GONE);
        } else {
            holder.getTime().setVisibility(View.VISIBLE);
            holder.getTime().setText(timeString);
        }

        if (message.getDirection() == Message.DirectionRecv) {
            if (isShowContentBubble())
                holder.getContentBack().setBackgroundResource(R.drawable.pd_message_bubble_left);
            holder.getPortraitUser().setVisibility(View.VISIBLE);
            holder.getPortraitRobot().setVisibility(View.INVISIBLE);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.getMessageBack().getLayoutParams();
            layoutParams.gravity = Gravity.START;
            holder.getMessageBack().setLayoutParams(layoutParams);
        } else {
            if (isShowContentBubble())
                holder.getContentBack().setBackgroundResource(R.drawable.pd_message_bubble_right);
            holder.getPortraitUser().setVisibility(View.INVISIBLE);
            holder.getPortraitRobot().setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.getMessageBack().getLayoutParams();
            layoutParams.gravity = Gravity.END;
            holder.getMessageBack().setLayoutParams(layoutParams);
        }

        if (message.getFeedbackId() != null) {
            holder.getFeedbackUp().setVisibility(View.VISIBLE);
            holder.getFeedbackDown().setVisibility(View.VISIBLE);
            if (message.getFeedbackResult() != null) {
                if (message.getFeedbackResult()) {
                    holder.getFeedbackUp().setSelected(true);
                    holder.getFeedbackDown().setSelected(false);
                } else {
                    holder.getFeedbackUp().setSelected(false);
                    holder.getFeedbackDown().setSelected(true);
                }
            } else {
                holder.getFeedbackUp().setSelected(false);
                holder.getFeedbackDown().setSelected(false);
            }
        } else {
            holder.getFeedbackUp().setVisibility(View.GONE);
            holder.getFeedbackDown().setVisibility(View.GONE);
        }

        View contentView = holder.getContentView();
        contentView.setVisibility(View.GONE);
        this.bindContentView(contentView, message);
        contentView.setVisibility(View.VISIBLE);
    }

    public abstract View createContentView(Context context);

    public abstract void bindContentView(View v, Message message);

    public boolean isShowContentBubble() {
        return true;
    }
}
