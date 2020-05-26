package com.dfsoft.iptvplayer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dfsoft.iptvplayer.manager.IPTVConfig;
import com.forcetech.android.P2PManager;

public class SplashActivity extends AppCompatActivity implements IPTVConfig.DataEventLister {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_splash);

        P2PManager p2PManager = P2PManager.getInstance();
        p2PManager.setContext(this.getApplicationContext());
        p2PManager.initServices();

        IPTVConfig config = IPTVConfig.getInstance();
        config.setDataEventLister(this);
        config.initConfig();
    }

    /**
     * 跳转至MainActivity
     */
    private void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);

        //singTask
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        //屏蔽back键
    }


    @Override
    public void onInitData(Boolean isOk) {
        if (isOk) {
            this.toMainActivity();
        } else {
            this.finish();
        }
    }

    @Override
    public void onPlayChannel() {

    }

}
