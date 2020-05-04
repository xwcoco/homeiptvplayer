package com.dfsoft.iptvplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.manager.IPTVConfig;
import com.dfsoft.iptvplayer.manager.IPTVMessage;
import com.dfsoft.iptvplayer.player.IPTVPlayerManager;
import com.dfsoft.iptvplayer.views.CategoryView;
import com.dfsoft.iptvplayer.views.PlayerHUDView;

public class MainActivity extends AppCompatActivity implements IPTVConfig.DataEventLister {

    private final String TAG = "MainActivity";
    private IPTVPlayerManager mIPTVManager = null;

    private CategoryView mCategoryView;
    private FrameLayout mVideoView;
    private PlayerHUDView mHudView;

    private IPTVConfig config = IPTVConfig.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mCategoryView = findViewById(R.id.main_category_view);

        mVideoView = findViewById(R.id.main_video_view);

        mHudView = findViewById(R.id.main_hud_view);

        config.setDataEventLister(this);
        config.iptvMessage.addMessageListener(this.mHandler);

        mIPTVManager = new IPTVPlayerManager(this);

        if (config.getPlayingChannal() == null) {
            config.setPlayingChannal(config.getFirstCanPlayChannel());
        }
        if (config.getPlayingChannal() != null) {
            mIPTVManager.play(config.getPlayingChannal());
        }
    }

    public boolean dealWithKeyDown(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
//            consoleHide.toggle();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_LEFT) {
            mCategoryView.toggle();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        View view = getCurrentFocus();
        if (view != null && view.getId() != R.id.main_video_view) {
            return super.onKeyDown(keyCode, event);
        }
        Log.d(TAG, "onKeyDown: " + view);
        boolean ret = dealWithKeyDown(keyCode);
        if (ret)
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (this.mCategoryView.isCategoryVisible()) {
            this.mCategoryView.hide();
            return;
        }

//        super.onBackPressed();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            IPTVChannel channel = null;
            switch (msg.what) {
                case IPTVMessage.IPTV_EPG_LOADED:
                    channel = (IPTVChannel) msg.obj;
                    if (channel == config.getPlayingChannal()) {
                        mHudView.updateHud();
                    }
                    mCategoryView.updateEpg(channel);
                    break;
                case IPTVMessage.IPTV_CHANNEL_PLAY:
                    channel = (IPTVChannel) msg.obj;
                    mVideoView.requestFocus();
                    mIPTVManager.play(channel);
                    break;
                case IPTVMessage.IPTV_FULLSCREEN:
//                    consoleHide.hide();
                    break;
            }
        }
    };

    @Override
    public void onInitData(Boolean isOk) {

    }

    @Override
    public void onPlayChannel() {
        mHudView.updateHud();
    }
}
