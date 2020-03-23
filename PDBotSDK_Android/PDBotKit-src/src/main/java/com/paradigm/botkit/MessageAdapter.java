package com.paradigm.botkit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.paradigm.botkit.message.MessageItemProvider;
import com.paradigm.botkit.util.DateUtil;
import com.paradigm.botlib.Message;

import java.util.ArrayList;

/**
 * Created by wuyifan on 2018/1/30.
 */

public class MessageAdapter extends BaseAdapter {

    protected Context context;
    protected DateUtil dateUtil;
    protected ArrayList<Message> messageData;
    protected MessageItemProvider[] providers = new MessageItemProvider[16];

    public MessageAdapter(Context context) {
        this.context = context;
        this.dateUtil = new DateUtil(context);
    }

    public void setMessageItemProvider(int messageType, MessageItemProvider provider) {
        providers[messageType] = provider;
    }

    @Override
    public int getCount() {
        return messageData.size();
    }

    @Override
    public Object getItem(int position) {
        return messageData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message message = messageData.get(position);
        MessageItemProvider provider = providers[message.getContentType()];
        if (provider == null) return getUnsupportView();

        String timeString = null;
        if (position == 0 || message.getSendTime().getTime() - messageData.get(position - 1).getSendTime().getTime() > 1000 * 60 * 5)
            timeString = dateUtil.timeStringFromDate(message.getSendTime());

        if (convertView == null)
            convertView = provider.createMessageView(context);
        provider.bindMessageView(convertView, message, timeString);

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return messageData.get(position).getContentType();
    }

    @Override
    public int getViewTypeCount() {
        return providers.length;
    }

    @Override
    public void notifyDataSetChanged() {
        dateUtil.refreash();
        super.notifyDataSetChanged();
    }

    public ArrayList<Message> getMessageData() {
        return messageData;
    }

    public void setMessageData(ArrayList<Message> messageData) {
        this.messageData = messageData;
        this.notifyDataSetChanged();
    }

    private View getUnsupportView() {
        return LayoutInflater.from(context).inflate(R.layout.pd_item_message_unsupport, null);
    }
}
