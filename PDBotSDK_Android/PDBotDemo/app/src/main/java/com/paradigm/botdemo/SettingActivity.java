package com.paradigm.botdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.paradigm.botkit.BotKitClient;

public class SettingActivity extends AppCompatActivity {

    private final static int RequestCodeForHistory = 1;

    private EditText editAccessKey;
    private Button buttonHist;
    private Button buttonScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editAccessKey = (EditText) findViewById(R.id.editAccessKey);
        buttonHist = (Button) findViewById(R.id.buttonHist);
        buttonScan = (Button) findViewById(R.id.buttonScan);

        editAccessKey.setText(HistoryDataManager.getInstance().getRecentAccessKey(this));

        buttonHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this, HistoryActivity.class);
                startActivityForResult(intent, RequestCodeForHistory);
            }
        });

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(SettingActivity.this);
                integrator.setOrientationLocked(false);
                integrator.setBeepEnabled(true);
                integrator.setCaptureActivity(ScanActivity.class);
                integrator.initiateScan();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.menuConfirm:
                startChat();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                editAccessKey.setText(result.getContents());
            }
        } else if (requestCode == RequestCodeForHistory) {
            if (resultCode == RESULT_OK) {
                editAccessKey.setText(data.getStringExtra("accessKey"));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startChat() {
        String accessKey = editAccessKey.getText().toString();
        if (accessKey == null || accessKey.length() == 0) return;

        String recentAccessKey = HistoryDataManager.getInstance().getRecentAccessKey(this);
        if (accessKey.equals(recentAccessKey)) {
            finish();
            return;
        }

        HistoryDataManager.getInstance().setRecentAccessKey(this, accessKey);

        BotKitClient botClient = BotKitClient.getInstance();
        botClient.init(this, accessKey);

        Intent intent = new Intent();
        intent.setClass(this, DemoChatActivity.class);
        startActivity(intent);
    }
}
