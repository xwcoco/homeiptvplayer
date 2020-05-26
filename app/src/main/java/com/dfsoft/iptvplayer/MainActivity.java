package com.dfsoft.iptvplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextClock;

import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.manager.IPTVConfig;
import com.dfsoft.iptvplayer.manager.IPTVMessage;
import com.dfsoft.iptvplayer.manager.settings.IptvSettingItem;
import com.dfsoft.iptvplayer.manager.settings.IptvSettings;
import com.dfsoft.iptvplayer.player.IPTVPlayerManager;
import com.dfsoft.iptvplayer.player.IPTVPlayer_HUD;
import com.dfsoft.iptvplayer.utils.AutoHideView;
import com.dfsoft.iptvplayer.utils.LogUtils;
import com.dfsoft.iptvplayer.views.CategoryThreeView;
import com.dfsoft.iptvplayer.views.CategoryTwoView;
import com.dfsoft.iptvplayer.views.CategoryView;
import com.dfsoft.iptvplayer.views.InformationView;
import com.dfsoft.iptvplayer.views.PlayerHUDView;
import com.dfsoft.iptvplayer.views.ProgramDescView;
import com.dfsoft.iptvplayer.views.QuitView;
import com.dfsoft.iptvplayer.views.SettingView;
import com.tvbus.engine.TVCore;
import com.tvbus.engine.TVListener;
import com.tvbus.engine.TVService;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements IPTVConfig.DataEventLister {

    private final String TAG = "MainActivity";
    private IPTVPlayerManager mIPTVManager = null;

    private CategoryView mCategoryView = null;
    private ConstraintLayout.LayoutParams mCategoryViewLayoutParams = null;
    private Boolean mCategoryViewVisible = false;

    private LinearLayout mVideoView;
    private PlayerHUDView mHudView;

    private IPTVConfig config = IPTVConfig.getInstance();

    private AutoHideView mHudHide = null;

    private AutoHideView mInfoHide = null;
    private InformationView mInfoView = null;

    private SettingView mSettingView = null;
    private ConstraintLayout.LayoutParams mCenterLayoutParams = null;

    private TextClock mClockView = null;

    private QuitView mQuitView = null;

    private ProgramDescView mDescView = null;
    private ConstraintLayout.LayoutParams mDescViewLayoutParams = null;

    private ConstraintLayout mViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

//        mCategoryView = findViewById(R.id.main_category_view);

        mViewContainer = findViewById(R.id.main_view_container);

        mVideoView = findViewById(R.id.main_video_view);

        mHudView = findViewById(R.id.main_hud_view);

        mClockView = findViewById(R.id.main_clock);

        config.setDataEventLister(this);
        config.iptvMessage.addMessageListener(this.mHandler);

        mHudHide = new AutoHideView(mHudView, mVideoView);
        mHudHide.setAUTO_HIDE_DELAY_MILLIS(10000);

        mInfoView = findViewById(R.id.main_information);

        mInfoHide = new AutoHideView(mInfoView, mVideoView);

        mCenterLayoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        mCenterLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        mCenterLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        mCenterLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        mCenterLayoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;


//        mSettingView = findViewById(R.id.main_settings_view);

//        mQuitView = findViewById(R.id.main_quit);

        if (config.settings == null) {
            config.settings = new IptvSettings(this);
            config.settings.initAllSettings();
            config.settings.load();
        }

        IptvSettingItem item = config.settings.getItemByTag(IptvSettings.IPTV_SETTING_TAG_SHOWTIME);
        if (item != null && item.getValue() != 0)
            mClockView.setVisibility(View.GONE);


//        this.startTVBusService();
//        this.getDeviceInfo();


        mIPTVManager = new IPTVPlayerManager(this);

        if (config.getPlayingChannal() == null) {
            config.setFirstRunPlayChannel();
        }

        IPTVChannel channel = config.getPlayingChannal();
        if (channel != null) {
            mIPTVManager.play(channel,channel.playIndex);
        }
    }

    public boolean dealWithKeyDown(int keyCode) {


        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_RIGHT) {
            if (programDescViewIsVisible()) {
                hideDescView();
                return true;
            }
            if (mHudHide.isVisble()) {
                showDescView();
                return true;
            }
        }

        if (programDescViewIsVisible()) {
            hideDescView();
        }

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

        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {

            if (isSearchChannelMode && !TextUtils.isEmpty(this.searchChannel)) {
                IPTVChannel channel = config.findChannelByNum(Integer.parseInt(searchChannel));
                if (channel != null) {
                    playChannal(channel);
                } else {
                    mInfoView.updateInfo(searchChannel + " 频道号不存在!");
                    mInfoHide.show();
                }
                isSearchChannelMode = false;
                searchChannel = "";
                return true;
            }

            mHudView.updateHud();
            mHudHide.toggle();
//            consoleHide.toggle();
            return true;
        }
        isSearchChannelMode = false;
        searchChannel = "";

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_LEFT) {
            showCategoryLayout();
//            mCategoryView.toggle();
            return true;
        }


        if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_UP) {
            playNextChannel();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_DOWN) {
            playLastChannel();
        }

        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_DEL) {
            if (isBackPressed) {
                isBackPressed = false;
                mRunHandler.removeCallbacks(mPlayLastChannalRunnable);
                askForQuit();
                return true;
            }
            isBackPressed = true;
            mRunHandler.postDelayed(mPlayLastChannalRunnable, 1000);
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_RIGHT) {
//            this.getDeviceInfo();
            this.playNextSource();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            showSettingView();
            return true;
        }

        return false;
    }

    private boolean isSearchChannelMode = false;
    private String searchChannel = "";

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

//        if (event.getAction() == KeyEvent.ACTION_DOWN) return super.onKeyDown(keyCode, event);

//        mInfoView.updateInfo(" pressed : " + String.valueOf(keyCode));
//        mInfoHide.show();

        View view = getCurrentFocus();
//        if (view != null && view.getId() != R.id.main_video_view && !ViewIsGone(view)) {
//            return super.onKeyDown(keyCode, event);
//        }
        Log.d(TAG, "onKeyDown: " + view);
        if (hasOtherNeedFocusView()) {
//            return false;
            return super.onKeyDown(keyCode, event);
        }
        boolean ret = dealWithKeyDown(keyCode);
        if (ret)
            return true;
//        return false;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        LogUtils.i(TAG, "Back Pressed!");
//        if (this.mCategoryView.isCategoryVisible()) {
//            this.mCategoryView.hide();
//            return;
//        }

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
//                    mCategoryView.updateEpg(channel);
                    break;
                case IPTVMessage.IPTV_CHANNEL_PLAY:
                    channel = (IPTVChannel) msg.obj;
                    playChannal(channel);

                    break;
                case IPTVMessage.IPTV_CHANNEL_PLAY_INDEX:
                    HashMap hashMap = (HashMap) msg.obj;
                    channel = (IPTVChannel) hashMap.get("channel");
                    if (channel == null) return;
                    int newIndex = (int) hashMap.get("index");
                    playChannal(channel, newIndex);
                    break;
                case IPTVMessage.IPTV_FULLSCREEN:
                    mRunHandler.postDelayed(mFocusVideoRunnable, UI_ANIMATION_DELAY);
//                    mVideoView.requestFocus();
//                    consoleHide.hide();
                    break;
                case IPTVMessage.IPTV_HUD_CHANGED:
                    mHudView.updateVideoWidthAndHeight((IPTVPlayer_HUD) msg.obj);
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
                case IPTVMessage.IPTV_CONFIG_CHANGED:
                    IptvSettingItem item = (IptvSettingItem) msg.obj;
                    applySetting(item);
                    break;
                case IPTVMessage.IPTV_IMAGECACHE_UPDATE:
                    mHudView.updateHud();
                    break;
                case IPTVMessage.IPTV_QUIT_CATEGORY:
                    hideCategoryLayout();
                    break;
                case IPTVMessage.IPTV_QUIT_SETTING:
                    hideSettingView();
                    break;
                case IPTVMessage.IPTV_QUIT_QUITASK:
                    hideQuitView();
                    break;
                case IPTVMessage.IPTV_SHOWMESSAGE:
                    mInfoView.updateInfo((String) msg.obj);
                    mInfoHide.show();
                    break;
                case IPTVMessage.IPTV_SWITCH_CATEGORY:
                    hideCategoryLayout();
                    mCategoryView = null;
                    IptvSettingItem set = config.settings.getItemByTag(IptvSettings.IPtV_SETTING_TAG_CATEGORY);
                    set.nextValue();
                    showCategoryLayout();
                    break;
                case IPTVMessage.IPTV_QUIT:
                    exit();
                    break;
            }
        }
    };

    public void playChannal(IPTVChannel channel) {
        if (channel.playIndex == -1)
            playChannal(channel, 0);
        else
            playChannal(channel,channel.playIndex);
    }

    public void playChannal(IPTVChannel channel, int index) {
        mVideoView.requestFocus();
//        config.setPlayingChannal(channel);
        mIPTVManager.play(channel, index);
        mHudHide.show();
    }

    @Override
    public void onInitData(Boolean isOk) {
        String msg = "";
        if (isOk) {
            msg = "本次更新：" + config.getCategoryInfo();
        } else {
            msg = "本次更新失败！";
        }
        config.iptvMessage.sendMessage(IPTVMessage.IPTV_SHOWMESSAGE, msg);
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
        if (channel != null && channel != config.getPlayingChannal())
            playChannal(channel);
    }

    public void playNextSource() {
        IPTVChannel channel = config.getPlayingChannal();
        if (channel == null) return;

        if (channel.source.size() == 1) return;
        int index = channel.playIndex;
        if (index == channel.source.size() - 1)
            index = 0;
        else
            index = index + 1;

        this.playChannal(channel, index);

    }


    private void applySetting(IptvSettingItem item) {
        if (item == null) return;
        LogUtils.i(TAG, "change config " + item.tag + " -> " + item.getValue());
        switch (item.tag) {
            case IptvSettings.IPTV_SETTING_TAG_CHANNEL_SOUCE:
                playChannal(config.getPlayingChannal(), item.getValue());
                break;
            case IptvSettings.IPTV_SETTING_TAG_PLAYER:
                mIPTVManager.changePlayer(item.getValue());
                hideSettingView();
                break;
            case IptvSettings.IPTV_SETTING_TAG_DISPLAY_MODE:
                mIPTVManager.setDisplayMode();
                break;
            case IptvSettings.IPTV_SETTING_TAG_SHOWTIME:
                int vi = item.getValue();
                if (vi == 0)
                    mClockView.setVisibility(View.VISIBLE);
                else
                    mClockView.setVisibility(View.GONE);
                break;
            case IptvSettings.IPTV_SETTING_TAG_UPDATEDATA:
                config.initConfig();
                hideSettingView();
                break;
            case IptvSettings.IPTV_SETTING_TAG_HARDWARE:
                mIPTVManager.setHardwareMode();
                break;
            case IptvSettings.IPTV_SETTING_TAG_FAVORITE:
                String msg = config.favortyModify();
                hideSettingView();
                if (!msg.equals(""))
                    this.showInfo(msg);
                break;
            case IptvSettings.IPtV_SETTING_TAG_CATEGORY:
                if (this.mCategoryView != null)
                    this.mCategoryView = null;
                break;

        }
        mSettingView.afterApplySetting();
    }

    private final Handler mRunHandler = new Handler();
    private final int UI_ANIMATION_DELAY = 300;
    private final Runnable mFocusVideoRunnable = new Runnable() {
        @Override
        public void run() {
            mVideoView.requestFocus();
        }
    };

    private void exit() {
        this.finish();
        System.exit(0);
    }

    private Boolean isBackPressed = false;
    private final Runnable mPlayLastChannalRunnable = new Runnable() {
        @Override
        public void run() {
            backToLastPlayChannel();
            isBackPressed = false;
        }
    };

    private void askForQuit() {
        if (mQuitView == null) {
            mQuitView = new QuitView(this);
        }
        if (mViewContainer.indexOfChild(mQuitView) == -1)
            mViewContainer.addView(mQuitView, mCenterLayoutParams);
        mQuitView.show();
    }

    private void hideQuitView() {
        if (mQuitView == null || mViewContainer.indexOfChild(mQuitView) == -1)
            return;
        mViewContainer.removeView(mQuitView);
        mVideoView.requestFocus();
    }

    private boolean ViewIsGone(View view) {
        if (view.getVisibility() != View.VISIBLE) return true;

        ViewParent parent = view.getParent();
        if (parent == null) return false;
        if (parent instanceof View)
            return ViewIsGone((View) parent);
        return false;
    }

    public void showInfo(String info) {
        this.mInfoView.updateInfo(info);
        this.mInfoHide.show();
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        View view = getCurrentFocus();
//        if (view != null && view.getId() != R.id.main_video_view && !ViewIsGone(view)) {
//            return super.dispatchKeyEvent(event);
//        }
//        boolean ret = dealWithKeyDown(event.getKeyCode());
//        if (ret)
//            return  true;
//        return super.dispatchKeyEvent(event);
//    }

    private void getDeviceInfo() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        float density = metric.density;
        int densityDpi = metric.densityDpi;
        String info = "机顶盒型号: " + android.os.Build.MODEL + ",\nSDK版本:"
                + android.os.Build.VERSION.SDK + ",\n系统版本:"
                + android.os.Build.VERSION.RELEASE + "\n屏幕宽度（像素）: " + width + "\n屏幕高度（像素）: " + height + "\n屏幕密度:  " + density + "\n屏幕密度DPI: " + densityDpi;

        showInfo(info);
        Log.d(TAG, info);
    }

    private void showCategoryLayout() {
        if (this.mCategoryView == null)
            this.createCategoryLayout();
        if (mCategoryView == null) return;
        if (mViewContainer.indexOfChild(mCategoryView) == -1) {
            mViewContainer.addView(mCategoryView, mCategoryViewLayoutParams);
        }
        mCategoryView.show();
//        mCategoryView.requestFocus();
    }

    private void hideCategoryLayout() {
        if (mCategoryView != null && mViewContainer.indexOfChild(mCategoryView) != -1) {
            mViewContainer.removeView(mCategoryView);
        }
        mVideoView.requestFocus();
    }

    private boolean hasOtherNeedFocusView() {
        if (mCategoryView != null && mViewContainer.indexOfChild(mCategoryView) != -1) {
            return true;
        }
        if (mSettingView != null && mViewContainer.indexOfChild(mSettingView) != -1) {
            return true;
        }
        if (mQuitView != null && mViewContainer.indexOfChild(mQuitView) != -1) {
            return true;
        }

        return false;
    }

    private void createCategoryLayout() {

        int mode = config.settings.getSettingValue(IptvSettings.IPtV_SETTING_TAG_CATEGORY);
        if (mode == 0) {
            mCategoryView = new CategoryView(this);

        } else if (mode == 1) {
            mCategoryView = new CategoryTwoView(this);
        } else
            mCategoryView = new CategoryThreeView(this);

        if (mCategoryViewLayoutParams == null) {
            mCategoryViewLayoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            mCategoryViewLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            mCategoryViewLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        }
    }

    private void showSettingView() {
        this.createSettingView();
        if (mSettingView == null || this.mViewContainer.indexOfChild(mSettingView) != -1) return;
        mViewContainer.addView(mSettingView, mCenterLayoutParams);
        config.settings.addPlayingChannelSetting();
        mSettingView.show();
    }

    private void hideSettingView() {
        if (mSettingView == null) return;
        mViewContainer.removeView(mSettingView);
        mVideoView.requestFocus();
    }

    private void createSettingView() {
        if (this.mSettingView != null) return;
        mSettingView = new SettingView(this);
    }

    private void showDescView() {
        if (this.mDescView == null) {
            mDescView = new ProgramDescView(this);
            mDescViewLayoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            mDescViewLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            mDescViewLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        }
        if (!mDescView.canShow()) return;
        if (this.mViewContainer.indexOfChild(mDescView) == -1) {
            mViewContainer.addView(mDescView, mDescViewLayoutParams);
            mDescView.show();
        }
    }

    private boolean programDescViewIsVisible() {
        return (this.mDescView != null && mViewContainer.indexOfChild(mDescView) != -1);
    }


    private void hideDescView() {
        if (programDescViewIsVisible())
            mViewContainer.removeView(mDescView);
        mVideoView.requestFocus();
    }


}
