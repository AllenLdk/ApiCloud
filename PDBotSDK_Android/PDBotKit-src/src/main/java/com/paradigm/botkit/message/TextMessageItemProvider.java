package com.paradigm.botkit.message;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.paradigm.botkit.R;
import com.paradigm.botlib.Message;
import com.paradigm.botlib.MessageContentText;

/**
 * Created by wuyifan on 2018/1/31.
 */

public class TextMessageItemProvider extends MessageItemProvider implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener, View.OnTouchListener {

    private boolean isLongClick;

    @Override
    public View createContentView(Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.pd_item_message_text, null);
        contentView.setOnCreateContextMenuListener(this);
        contentView.setOnTouchListener(this);
        return contentView;
    }

    @Override
    public void bindContentView(View v, Message message) {
        TextView contentView = (TextView) v;
        MessageContentText content = (MessageContentText) message.getContent();
        contentView.setText(content.getText());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(android.R.string.copy).setActionView(v).setOnMenuItemClickListener(this);
        isLongClick = true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        TextView contentView = (TextView) item.getActionView();
        ClipData clipData = ClipData.newPlainText("Text", contentView.getText());
        ClipboardManager clipboardManager = (ClipboardManager) contentView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(clipData);
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isLongClick = false;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            return isLongClick;
        }
        return false;
    }
}
