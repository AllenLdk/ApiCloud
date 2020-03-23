package com.paradigm.botkit.message;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.paradigm.botkit.R;
import com.paradigm.botkit.util.ImageUtil;
import com.paradigm.botlib.Message;
import com.paradigm.botlib.MessageContentRichText;

import java.io.File;
import java.net.URL;

/**
 * Created by wuyifan on 2018/1/31.
 */

public class RichtextMessageItemProvider extends MessageItemProvider implements View.OnClickListener, Html.ImageGetter {

    public interface OnContentClickListener {
        void onClick(MessageContentRichText content);
    }

    private OnContentClickListener contentClickListener;
    private Handler handler;

    public RichtextMessageItemProvider(OnContentClickListener contentClickListener) {
        this.contentClickListener = contentClickListener;
        this.handler = new Handler();
    }

    @Override
    public View createContentView(Context context) {
        RichtextMessageItemHolder holder = new RichtextMessageItemHolder();
        View contentView = LayoutInflater.from(context).inflate(R.layout.pd_item_message_richtext, null);
        holder.setCover((ImageView) contentView.findViewById(R.id.pd_message_item_cover));
        holder.setTitle((TextView) contentView.findViewById(R.id.pd_message_item_title));
        holder.setDigest((TextView) contentView.findViewById(R.id.pd_message_item_digest));
        contentView.setTag(holder);
        contentView.setOnClickListener(this);
        return contentView;
    }

    @Override
    public void bindContentView(View v, Message message) {
        final RichtextMessageItemHolder holder = (RichtextMessageItemHolder) v.getTag();
        final MessageContentRichText content = (MessageContentRichText) message.getContent();
        holder.setContent(content);

        if (content.isDirectShow()) {
            holder.getCover().setVisibility(View.GONE);
            holder.getDigest().setVisibility(View.GONE);

            holder.getTitle().setText(Html.fromHtml(content.getDigest()));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Spanned text = Html.fromHtml(content.getDigest(), RichtextMessageItemProvider.this, null);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.getTitle().setText(text);
                        }
                    });
                }
            }).start();
        } else {
            if (content.getCoverPath() != null) {
                File file = new File(v.getContext().getFilesDir(), content.getCoverPath());
                holder.getCover().setImageURI(Uri.fromFile(file));
                holder.getCover().setVisibility(View.VISIBLE);
            } else {
                holder.getCover().setVisibility(View.GONE);
            }

            holder.getTitle().setText(content.getTitle());
            holder.getDigest().setText(content.getDigest());
            holder.getDigest().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        RichtextMessageItemHolder holder = (RichtextMessageItemHolder) v.getTag();
        if (contentClickListener != null) contentClickListener.onClick(holder.getContent());
    }

    @Override
    public Drawable getDrawable(String source) {
        Drawable drawable = null;
        try {
            URL url = new URL(source);
            drawable = Drawable.createFromStream(url.openStream(), "img");
            drawable.setBounds(0, 0, ImageUtil.dp2px(drawable.getIntrinsicWidth()), ImageUtil.dp2px(drawable.getIntrinsicHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;
    }
}
