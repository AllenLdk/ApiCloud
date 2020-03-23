package com.paradigm.botkit.message;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.paradigm.botkit.R;
import com.paradigm.botlib.Message;
import com.paradigm.botlib.MessageContentWorkorder;

/**
 * Created by wuyifan on 2018/9/10.
 */

public class WorkorderMessageItemProvider extends MessageItemProvider implements View.OnClickListener {

    public interface OnContentClickListener {
        void onClick(MessageContentWorkorder content);
    }

    private OnContentClickListener contentClickListener;

    public WorkorderMessageItemProvider(OnContentClickListener contentClickListener) {
        this.contentClickListener = contentClickListener;
    }

    @Override
    public View createContentView(Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.pd_item_message_workorder, null);
        contentView.setOnClickListener(this);
        return contentView;
    }

    @Override
    public void bindContentView(View v, Message message) {
        TextView contentView = (TextView) v;

        Context context = v.getContext();
        MessageContentWorkorder content = (MessageContentWorkorder) message.getContent();

        SpannableStringBuilder builder = new SpannableStringBuilder(content.getMessage());
        int color = context.getResources().getColor(R.color.pd_hyperlink_text);
        builder.append(context.getString(R.string.pd_click_here));
        builder.setSpan(new ForegroundColorSpan(color), content.getMessage().length(), builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        contentView.setText(builder);
        contentView.setTag(content);
    }

    @Override
    public void onClick(View v) {
        MessageContentWorkorder content = (MessageContentWorkorder) v.getTag();
        if (contentClickListener != null) contentClickListener.onClick(content);
    }
}
