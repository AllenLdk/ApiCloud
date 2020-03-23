package com.paradigm.botkit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.paradigm.botlib.BotLibClient;
import com.paradigm.botlib.BotLibClient.ConnectionListener;
import com.paradigm.botlib.MenuItem;
import com.paradigm.botlib.Message;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements ConnectionListener, BotLibClient.MessageListener {

    protected ChatFragment chatFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pd_activity_chat);

        chatFragment = (ChatFragment) getFragmentManager().findFragmentById(R.id.pd_char_fragment);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        chatFragment.initBotClient();
    }

    protected void reloadMessageList() {
        chatFragment.reloadMessageList();
    }

    protected ListView getMessageListView() {
        return chatFragment.getMessageListView();
    }

    protected MessageAdapter getMessageAdapter() {
        return chatFragment.getMessageAdapter();
    }

    protected ArrayList<Message> getMessageData() {
        return chatFragment.getMessageData();
    }

    @Override
    public void onConnectionStateChanged(int state) {
    }

    @Override
    public void onReceivedSuggestion(ArrayList<MenuItem> suggestions) {
    }

    @Override
    public void onAppendMessage(Message message) {
    }
}
