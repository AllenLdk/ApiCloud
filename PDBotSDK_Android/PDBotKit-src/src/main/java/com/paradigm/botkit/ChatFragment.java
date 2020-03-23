package com.paradigm.botkit;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.PermissionChecker;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import com.paradigm.botkit.message.AudioMessageItemProvider;
import com.paradigm.botkit.message.ImageMessageItemProvider;
import com.paradigm.botkit.message.MenuMessageItemProvider;
import com.paradigm.botkit.message.RichtextMessageItemProvider;
import com.paradigm.botkit.message.TextMessageItemProvider;
import com.paradigm.botkit.message.TipMessageItemProvider;
import com.paradigm.botkit.message.WorkorderMessageItemProvider;
import com.paradigm.botkit.util.AudioTools;
import com.paradigm.botkit.util.ImageUtil;
import com.paradigm.botkit.util.StreamUtil;
import com.paradigm.botlib.BotLibClient;
import com.paradigm.botlib.MenuItem;
import com.paradigm.botlib.Message;
import com.paradigm.botlib.MessageContentAudio;
import com.paradigm.botlib.MessageContentImage;
import com.paradigm.botlib.MessageContentRichText;
import com.paradigm.botlib.MessageContentWorkorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ChatFragment extends Fragment implements BotLibClient.ConnectionListener, BotLibClient.MessageListener {

    private static final String TAG = "ChatActivity";

    private static final int RequestCodeCamera = 1;
    private static final int RequestCodeAlbum = 2;

    private static final int PluginPhoto = 1001;
    private static final int PluginCamera = 1002;
    private static final int PluginHuman = 1003;
    private static final int PluginMessage = 1004;

    private static final long MaxImageSize = 1024 * 1024;

    protected ListView messageList;
    protected ListView suggestionList;
    protected EditText inputText;
    protected Button inputRecord;
    protected ImageButton inputKey;
    protected ImageButton inputAudio;
    protected ImageButton inputPlugin;
    protected Button inputSend;

    private GridLayout pluginBack;
    private PluginViewHolder pluginViewHolder;

    protected View recordBack;
    protected RecordViewHolder recordViewHolder;
    protected Timer recordTimer;

    private AudioTools audioTools;
    private MessageContentAudio audioPlayingMessage;
    private Uri uriCapture;

    private MessageAdapter messageAdapter;
    private SuggestionAdapter suggestionAdapter;
    private ArrayList<Message> messageData;

    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pd_fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageUtil.init(getActivity());
        audioTools = new AudioTools(getActivity());

        initViews();
        initBotClient();
    }

    @Override
    public void onStop() {
        super.onStop();
        audioTools.stopPlay();
        audioTools.cancelRecord();
    }

    private void initViews() {
        messageList = getActivity().findViewById(R.id.pd_message_list);
        suggestionList = getActivity().findViewById(R.id.pd_suggestion_list);
        inputText = getActivity().findViewById(R.id.pd_input_text);
        inputRecord = getActivity().findViewById(R.id.pd_input_record);
        inputKey = getActivity().findViewById(R.id.pd_input_key);
        inputAudio = getActivity().findViewById(R.id.pd_input_audio);
        inputPlugin = getActivity().findViewById(R.id.pd_input_plugin);
        inputSend = getActivity().findViewById(R.id.pd_input_send);

        pluginBack = getActivity().findViewById(R.id.pd_plgin_back);
        pluginViewHolder = new PluginViewHolder(pluginBack);

        recordBack = getActivity().findViewById(R.id.pd_record_back);
        recordViewHolder = new RecordViewHolder(recordBack);

        inputKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputKey.setVisibility(View.GONE);
                inputAudio.setVisibility(View.VISIBLE);
                inputText.setVisibility(View.VISIBLE);
                inputRecord.setVisibility(View.GONE);
                showKeyboard();
                setSendButton();
            }
        });

        inputAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputKey.setVisibility(View.VISIBLE);
                inputAudio.setVisibility(View.GONE);
                inputText.setVisibility(View.GONE);
                inputRecord.setVisibility(View.VISIBLE);
                pluginBack.setVisibility(View.GONE);
                suggestionList.setVisibility(View.GONE);
                hideKeyboard();
                setSendButton();
            }
        });

        inputPlugin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pluginBack.getVisibility() == View.GONE) {
                    pluginBack.setVisibility(View.VISIBLE);
                    hideKeyboard();
                    if (inputRecord.getVisibility() == View.VISIBLE) {
                        inputKey.setVisibility(View.GONE);
                        inputAudio.setVisibility(View.VISIBLE);
                        inputText.setVisibility(View.VISIBLE);
                        inputRecord.setVisibility(View.GONE);
                        setSendButton();
                    }
                } else {
                    pluginBack.setVisibility(View.GONE);
                }
            }
        });

        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setSendButton();
                String text = s.toString().trim();
                if (text.length() > 0)
                    BotKitClient.getInstance().askSuggestion(text);
                else
                    suggestionList.setVisibility(View.GONE);
            }
        });

        inputText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pluginBack.setVisibility(View.GONE);
                showKeyboard();
                return false;
            }
        });

        inputRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        inputRecord.setText(R.string.pd_release_to_send);
                        startRecord();
                        break;
                    case MotionEvent.ACTION_UP:
                        inputRecord.setText(R.string.pd_hold_to_talk);
                        if (recordViewHolder.isCancel())
                            cancelRecord();
                        else
                            finishRecord();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getY() < 10) {
                            inputRecord.setText(R.string.pd_release_to_cancel);
                            recordViewHolder.setCancel(true);
                        } else {
                            inputRecord.setText(R.string.pd_release_to_send);
                            recordViewHolder.setCancel(false);
                        }
                        break;
                }
                return true;
            }
        });

        pluginViewHolder.insertItem(R.drawable.pd_plugin_photo, R.string.pd_photo, PluginPhoto);
        pluginViewHolder.insertItem(R.drawable.pd_plugin_camera, R.string.pd_camera, PluginCamera);
        pluginViewHolder.insertItem(R.drawable.pd_plugin_human, R.string.pd_human, PluginHuman);
        pluginViewHolder.insertItem(R.drawable.pd_plugin_message, R.string.pd_message, PluginMessage);
        pluginViewHolder.setOnPluginClickListener(new PluginViewHolder.OnPluginClickListener() {
            @Override
            public void onClick(int tag) {
                pluginBack.setVisibility(View.GONE);
                if (tag == PluginPhoto) {
                    getPhotoFromAlbum();
                } else if (tag == PluginCamera) {
                    getPhotoFromCamera();
                } else if (tag == PluginHuman) {
                    BotKitClient.getInstance().transferToHumanServices();
                } else if (tag == PluginMessage) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WebActivity.class);
                    intent.putExtra("url", BotKitClient.getInstance().getLeaveMessageUrl());
                    startActivity(intent);
                }
            }
        });

        inputSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputText.getText().toString().trim();
                if (text.length() > 0) {
                    BotKitClient.getInstance().askQuestion(text);
                    inputText.setText("");
                }
            }
        });
    }

    public void initBotClient() {

        BotKitClient botClient = BotKitClient.getInstance();
        botClient.setConnectionListener(this);
        botClient.setMessageListener(this);

        messageAdapter = new MessageAdapter(getActivity());
        messageAdapter.setMessageItemProvider(Message.ContentTypeTip, new TipMessageItemProvider());
        messageAdapter.setMessageItemProvider(Message.ContentTypeText, new TextMessageItemProvider());
        messageAdapter.setMessageItemProvider(Message.ContentTypeMenu, new MenuMessageItemProvider(new MenuMessageItemProvider.OnContentClickListener() {
            @Override
            public void onItemClick(MenuItem item, int type) {
                BotKitClient.getInstance().askQuestion(item, type);
            }
        }));
        messageAdapter.setMessageItemProvider(Message.ContentTypeImage, new ImageMessageItemProvider(new ImageMessageItemProvider.OnContentClickListener() {
            @Override
            public void onClick(MessageContentImage content) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ImageActivity.class);
                intent.putExtra("path", content.getDataPath());
                startActivity(intent);
            }
        }));
        messageAdapter.setMessageItemProvider(Message.ContentTypeAudio, new AudioMessageItemProvider(new AudioMessageItemProvider.OnContentClickListener() {
            @Override
            public void onClick(MessageContentAudio content) {
                File file = new File(getActivity().getFilesDir(), content.getDataPath());
                if (audioTools.getPlayingFile() != null && audioPlayingMessage == content) {
                    audioTools.stopPlay();
                    audioPlayingMessage = null;
                } else {
                    audioTools.startPlay(file);
                    audioPlayingMessage = content;
                }
            }
        }));
        messageAdapter.setMessageItemProvider(Message.ContentTypeRichText, new RichtextMessageItemProvider(new RichtextMessageItemProvider.OnContentClickListener() {
            @Override
            public void onClick(MessageContentRichText content) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), WebActivity.class);
                intent.putExtra("url", content.getUrl());
                startActivity(intent);
            }
        }));
        messageAdapter.setMessageItemProvider(Message.ContentTypeWorkorder, new WorkorderMessageItemProvider(new WorkorderMessageItemProvider.OnContentClickListener() {
            @Override
            public void onClick(MessageContentWorkorder content) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), WebActivity.class);
                intent.putExtra("url", content.getUrl());
                startActivity(intent);
            }
        }));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -30);
        botClient.removeMessage(cal.getTime());

        messageData = new ArrayList<>();
        messageAdapter.setMessageData(messageData);
        messageList.setAdapter(messageAdapter);

        suggestionAdapter = new SuggestionAdapter(getActivity());
        suggestionList.setAdapter(suggestionAdapter);
        suggestionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuItem item = (MenuItem) suggestionList.getItemAtPosition(position);
                BotKitClient.getInstance().askQuestion(item.getContent());
                inputText.setText("");
            }
        });

        reloadMessageList();
        botClient.connect();
    }

    public void reloadMessageList() {
        messageData.clear();
        messageData.addAll(BotKitClient.getInstance().getMessageList());
        messageAdapter.notifyDataSetChanged();
        if (messageData.size() > 0) messageList.setSelection(messageData.size() - 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeCamera) {
            if (resultCode == Activity.RESULT_OK) {
                File imgFile = null;
                try {
                    imgFile = File.createTempFile("photo", ".jpg", getActivity().getCacheDir());
                    FileOutputStream os = new FileOutputStream(imgFile);
                    InputStream is = getActivity().getContentResolver().openInputStream(uriCapture);
                    long fileSize = StreamUtil.StreamTransfer(is, os);
                    os.close();
                    is.close();

                    if (fileSize > MaxImageSize) {
                        File tmpFile = File.createTempFile("photo", ".jpg", getActivity().getCacheDir());
                        ImageUtil.compressImage(imgFile, 4000, 4000, Bitmap.CompressFormat.JPEG, 80, tmpFile.getPath());
                        imgFile.delete();
                        imgFile = tmpFile;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                getActivity().getContentResolver().delete(uriCapture, null, null);
                if (imgFile != null) BotKitClient.getInstance().askQuestionImage(imgFile);
            }
        } else if (requestCode == RequestCodeAlbum) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imgUri = data.getData();
                File imgFile = null;
                try {
                    imgFile = File.createTempFile("photo", ".jpg", getActivity().getCacheDir());
                    FileOutputStream os = new FileOutputStream(imgFile);
                    InputStream is = getActivity().getContentResolver().openInputStream(imgUri);
                    long fileSize = StreamUtil.StreamTransfer(is, os);
                    os.close();
                    is.close();

                    if (fileSize > MaxImageSize) {
                        File tmpFile = File.createTempFile("photo", ".jpg", getActivity().getCacheDir());
                        ImageUtil.compressImage(imgFile, 4000, 4000, Bitmap.CompressFormat.JPEG, 80, tmpFile.getPath());
                        imgFile.delete();
                        imgFile = tmpFile;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (imgFile != null) BotKitClient.getInstance().askQuestionImage(imgFile);
            }
        }
    }

    private void showKeyboard() {
        inputText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(inputText, 0);
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(inputText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setSendButton() {
        boolean showSend = inputText.getVisibility() == View.VISIBLE && inputText.getText().length() > 0;
        inputSend.setVisibility(showSend ? View.VISIBLE : View.GONE);
        inputPlugin.setVisibility(showSend ? View.GONE : View.VISIBLE);
    }

    private void startRecord() {
        if (!selfPermissionGranted(Manifest.permission.RECORD_AUDIO)) return;
        audioTools.startRecord();
        recordBack.setVisibility(View.VISIBLE);
        recordViewHolder.setCancel(false);
        recordViewHolder.setVolume(0.0f);

        recordTimer = new Timer();
        recordTimer.schedule(new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        recordViewHolder.setVolume(audioTools.getRecordVolume());
                        if (audioTools.getRecordDuration() > 60000) finishRecord();
                    }
                });
            }
        }, 500, 500);
    }

    private void finishRecord() {
        if (recordTimer != null) {
            recordTimer.cancel();
            recordTimer = null;
        }

        File file = audioTools.finishRecord();
        recordBack.setVisibility(View.GONE);

        if (file != null) BotKitClient.getInstance().askQuestionAudio(file);
    }

    private void cancelRecord() {
        if (recordTimer != null) {
            recordTimer.cancel();
            recordTimer = null;
        }

        audioTools.cancelRecord();
        recordBack.setVisibility(View.GONE);
    }

    private void getPhotoFromCamera() {
        if (!selfPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) return;

        String name = "photo" + System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, name);
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpeg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        uriCapture = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCapture);
        startActivityForResult(intent, RequestCodeCamera);
    }

    private void getPhotoFromAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RequestCodeAlbum);
    }

    public boolean selfPermissionGranted(String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        int targetSdkVersion = 0;
        try {
            final PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = getContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
                if (!result) requestPermissions(new String[]{permission}, 0);
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(getContext(), permission) == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }

    public ListView getMessageListView() {
        return messageList;
    }

    public MessageAdapter getMessageAdapter() {
        return messageAdapter;
    }

    public ArrayList<Message> getMessageData() {
        return messageData;
    }

    @Override
    public void onConnectionStateChanged(int state) {
        BotKitClient botClient = BotKitClient.getInstance();

        Activity activity = getActivity();
        if (activity != null) {
            switch (state) {
                case BotLibClient.ConnectionIdel:
                    activity.setTitle(R.string.pd_connection_closed);
                    break;
                case BotLibClient.ConnectionConnecting:
                    activity.setTitle(R.string.pd_connecting);
                    break;
                case BotLibClient.ConnectionConnectedRobot:
                    activity.setTitle(botClient.getRobotName());
                    break;
                case BotLibClient.ConnectionConnectedHuman:
                    activity.setTitle(botClient.getRobotName());
                    break;
                default:
                    activity.setTitle(R.string.pd_connection_failed);
            }
        }

        boolean isEnableHuman = state == BotLibClient.ConnectionConnectedRobot && botClient.isEnableHuman();
        boolean isEnableMessage = state == BotLibClient.ConnectionConnectedRobot;
        this.pluginViewHolder.hideItemWithTag(PluginHuman, !isEnableHuman);
        this.pluginViewHolder.hideItemWithTag(PluginMessage, !isEnableMessage);

        if (activity instanceof BotLibClient.ConnectionListener) {
            ((BotLibClient.ConnectionListener) activity).onConnectionStateChanged(state);
        }
    }

    @Override
    public void onReceivedSuggestion(ArrayList<MenuItem> suggestions) {
        suggestionAdapter.setSuggestionList(suggestions);
        suggestionList.setVisibility(suggestions.isEmpty() ? View.GONE : View.VISIBLE);

        if (getActivity() instanceof BotLibClient.MessageListener) {
            ((BotLibClient.MessageListener) getActivity()).onReceivedSuggestion(suggestions);
        }
    }

    @Override
    public void onAppendMessage(Message message) {
        messageData.add(message);
        messageAdapter.notifyDataSetChanged();

        if (getActivity() instanceof BotLibClient.MessageListener) {
            ((BotLibClient.MessageListener) getActivity()).onAppendMessage(message);
        }
    }
}
