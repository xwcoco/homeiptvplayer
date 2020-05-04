package com.dfsoft.iptvplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
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
import com.dfsoft.iptvplayer.player.IPTVPlayer_HUD;
import com.dfsoft.iptvplayer.utils.AutoHideView;
import com.dfsoft.iptvplayer.views.CategoryView;
import com.dfsoft.iptvplayer.views.InformationView;
import com.dfsoft.iptvplayer.views.PlayerHUDView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IPTVConfig.DataEventLister {

    private final String TAG = "MainActivity";
    private IPTVPlayerManager mIPTVManager = null;

    private CategoryView mCategoryView;
    private FrameLayout mVideoView;
    private PlayerHUDView mHudView;

    private IPTVConfig config = IPTVConfig.getInstance();

    private AutoHideView mHudHide = null;

    private AutoHideView mInfoHide = null;
    private InformationView mInfoView = null;

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

        mHudHide = new AutoHideView(mHudView, mVideoView);

        mInfoView = findViewById(R.id.main_information);

        mInfoHide = new AutoHideView(mInfoView,mVideoView);

        mIPTVManager = new IPTVPlayerManager(this);

        if (config.getPlayingChannal() == null) {
            config.setPlayingChannal(config.getFirstCanPlayChannel());
        }
        if (config.getPlayingChannal() != null) {
            mIPTVManager.play(config.getPlayingChannal());
        }
    }

    public boolean dealWithKeyDown(int keyCode) {
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            ArrayList<String> numList = new ArrayList<>();
            for (int i = 0; i <= 9; i++) {
                numList.add(String.valueOf(i));
            }
            if (isSearchChannelMode) {
                this.searchChannel = this.searchChannel + numList.get(keyCode - KeyEvent.KEYCODE_0);
            } else {
                this.isSearchChannelMode = true;
                this.searchChannel = numList.get(keyCode - KeyEvent.KEYCODE_0);
            }
            mInfoView.updateChannelNum(this.searchChannel);
            mInfoHide.show();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_ENTER) {

            if (isSearchChannelMode && !TextUtils.isEmpty(this.searchChannel)) {
                IPTVChannel channel = config.findChannelByNum(Integer.parseInt(searchChannel));
                if (channel != null) {
                    playChannal(channel);
                } else {
                    mInfoView.updateInfo(searchChannel+" 频道号不存在!");
                    mInfoHide.show();
                }
                isSearchChannelMode = false;
                searchChannel = "";
                return true;
            }

            mHudHide.toggle();
//            consoleHide.toggle();
            return true;
        }
        isSearchChannelMode = false;
        searchChannel = "";

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_LEFT) {
            mCategoryView.toggle();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_UP) {
            playLastChannel();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_DOWN) {
            playNextChannel();
        }

        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_DEL) {
            this.backToLastPlayChannel();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_RIGHT) {
            this.playNextSource();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }

        return false;
    }

    private boolean isSearchChannelMode = false;
    private String searchChannel = "";

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
                    playChannal(channel);

                    break;
                case IPTVMessage.IPTV_FULLSCREEN:
//                    consoleHide.hide();
                    break;
                case IPTVMessage.IPTV_HUD_CHANGED:
                    mHudView.updateHUD((IPTVPlayer_HUD) msg.obj);
                    break;
                case IPTVMessage.IPTV_BUFFERING:

                    if (isSearchChannelMode)
                        return;

                    int tmp = Math.round((float) msg.obj);
                    String info = "loading " + String.valueOf(tmp) + " %";
                    mInfoView.updateInfo(info);

                    if (tmp >= 99)
                        mInfoHide.hide();
                    else
                        mInfoHide.show();
                    break;
            }
        }
    };

    public void playChannal(IPTVChannel channel) {
        playChannal(channel,0);
    }

    public void playChannal(IPTVChannel channel,int index) {
        mVideoView.requestFocus();
//        config.setPlayingChannal(channel);
        mIPTVManager.play(channel,index);
        mHudHide.show();
    }

    @Override
    public void onInitData(Boolean isOk) {

    }

    @Override
    public void onPlayChannel() {
        mHudView.updateHud();
    }

    public void playLastChannel() {
        IPTVChannel channel = config.getCategoryPirorChannel();
        if (channel != null) {
            this.playChannal(channel);
        }
    }

    public void playNextChannel() {
        IPTVChannel channel = config.getCategoryNextChannel();
        if (channel != null) {
            this.playChannal(channel);
        }
    }

    public void backToLastPlayChannel() {
        IPTVChannel channel = config.getLastPlayingChannel();
        if (channel != null)
            playChannal(channel);
    }

    public void playNextSource() {
        IPTVChannel channel = config.getPlayingChannal();
        if (channel == null) return;

        if (channel.source.size() == 1) return;
        int index = this.mIPTVManager.getCurrentSourceIndex();
        if (index == channel.source.size() - 1)
            index = 0;
        else
            index = index + 1;

        this.playChannal(channel,index);

    }
}
