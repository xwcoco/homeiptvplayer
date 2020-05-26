package com.dfsoft.iptvplayer.player;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;

import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.manager.IPTVConfig;
import com.dfsoft.iptvplayer.manager.IPTVMessage;
import com.dfsoft.iptvplayer.manager.settings.IptvSettingItem;
import com.dfsoft.iptvplayer.manager.settings.IptvSettings;
import com.dfsoft.iptvplayer.utils.LogUtils;
import com.forcetech.android.P2PManager;
import com.tvbus.engine.TVCore;
import com.tvbus.engine.TVListener;
import com.tvbus.engine.TVService;

import org.json.JSONException;
import org.json.JSONObject;

public class IPTVPlayerManager implements IPTVPlayer_Base.IPTV_HUD_INTERFACE {
    private Activity main = null;

    private final static String TAG = "IPTVPlayerManager";

    private int playerid = 0;

    private static TVCore mTVCore = null;

    private IPTVPlayer_Base mPlayer = null;
    private IPTVConfig config = IPTVConfig.getInstance();

    public IPTVPlayer_HUD hud = new IPTVPlayer_HUD();

    private P2PManager p2PManager = P2PManager.getInstance();

    public IPTVPlayerManager(Activity main) {
        this.main = main;
        playerid = config.settings.getSettingValue(IptvSettings.IPTV_SETTING_TAG_PLAYER,playerid);

//        p2PManager.setContext(main);

        this.createPlayer();
        displayDecoders();
    }

    private void createPlayer() {
        switch (playerid) {
            case 0:
                mPlayer = new IPTVPlayer_VLCPlayer(main);
                break;
            case 1:
                mPlayer = new IPTVPlayer_ijkPlayer(main);
                break;
            case 2:
                mPlayer = new IPTVPlayer_ExoPlayer(main);
                break;
        }
        if (mPlayer == null) return;
        mPlayer.setHudInterface(this);
        mPlayer.initPlayer();
        setDisplayMode();
    }

    public void play(IPTVChannel channel,int index) {
        if (mPlayer == null || channel == null) return;
        if (channel.source.size() == 0) return;
        if (index < 0 || index >= channel.source.size())
            index = 0;

        this.hud = new IPTVPlayer_HUD();

        channel.playIndex = index;

        String source = channel.source.get(index);
        if (source.startsWith("tvbus")) {
            if (mTVCore == null)
                this.startTVBusService();

            mTVCore.start(source);

        } else if (source.startsWith("P2p") || source.startsWith("p2p")) {
            String tmpUrl = p2PManager.getUrl(source,9001);
            mPlayer.play(tmpUrl);
        } else if (source.startsWith("p8p")) {
            String tmpUrl = p2PManager.getUrl(source,9002);
            mPlayer.play(tmpUrl);
        }
        else {
            if (mTVCore != null) {
                mTVCore.stop();
            }
            mPlayer.play(channel.source.get(index));
        }



        config.setPlayingChannal(channel);

//        mPlayer.play("http://live.hcs.cmvideo.cn:8088/migu/kailu/cctv1hd265/57/20191230/index.m3u8?&encrypt=");
//        mPlayer.play("http://192.168.50.4:8888/udp/225.1.4.162:1204");
    }


    public void play(IPTVChannel channel) {
        play(channel,0);
    }

    public void onResume() {
        mPlayer.resume();
    }

    public void onPause() {
        mPlayer.pause();
    }

    public void onStop() {
        mPlayer.stop();
    }

    public void onDestory() {
        mPlayer.close();
    }

    @Override
    public void onBuffering(float percent) {
        config.iptvMessage.sendMessage(IPTVMessage.IPTV_BUFFERING,percent);
    }

    @Override
    public void OnGetHud(IPTVPlayer_HUD hud) {
        this.hud = hud;
        config.iptvMessage.sendMessage(IPTVMessage.IPTV_HUD_CHANGED,hud);
    }

    public void changePlayer(int newIndex) {
        if (playerid == newIndex) return;
        playerid = newIndex;
        mPlayer.stop();
        mPlayer.close();
        this.createPlayer();
        if (mPlayer != null) {
            IPTVChannel channel = config.getPlayingChannal();
            if (channel != null)
                play(channel,channel.playIndex);
        }
    }

    public void setDisplayMode() {
        IptvSettingItem item = config.settings.getItemByTag(IptvSettings.IPTV_SETTING_TAG_DISPLAY_MODE);
        if (item == null) return;
        int mode = item.getValue();
        mPlayer.setDisplayMode(mode);
    }

    public void setHardwareMode() {
        int mode = config.settings.getSettingValue(IptvSettings.IPTV_SETTING_TAG_HARDWARE);
        mPlayer.setHardwareMode(mode);
    }

    private void displayDecoders() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaCodecList list = new MediaCodecList(MediaCodecList.REGULAR_CODECS);//REGULAR_CODECS参考api说明
            MediaCodecInfo[] codecs = list.getCodecInfos();
            for (MediaCodecInfo codec : codecs) {
                if (codec.isEncoder())
                    continue;
                LogUtils.i("IPTVPlayerManager", "displayDecoders: " + codec.getName());
            }
        }
    }

    // tvbus p2p module related
    private void startTVBusService() {
        mTVCore = TVCore.getInstance();
        assert mTVCore != null;

        // start tvcore
        mTVCore.setTVListener(new TVListener() {
            @Override
            public void onInited(String result) {
                parseCallbackInfo("onInited", result);
            }

            @Override
            public void onStart(String result) {
                parseCallbackInfo("onStart", result);
            }

            @Override
            public void onPrepared(String result) {
                if(parseCallbackInfo("onPrepared", result)) {
                    startTvBusPlayback();
//                    startPlayback();
                }
            }

            @Override
            public void onInfo(String result) {
                parseCallbackInfo("onInfo", result);
//                checkPlayer();
            }

            @Override
            public void onStop(String result) {
                parseCallbackInfo("onStop", result);
            }

            @Override
            public void onQuit(String result) {
                parseCallbackInfo("onQuit", result);
            }
        });

        this.main.startService(new Intent(this.main, TVService.class));
    }

    private void stopTVBusService() {
        this.main.stopService(new Intent(this.main, TVService.class));
        mTVCore = null;
    }

    private String mTvBusPlayBackUrl = "";

    private boolean parseCallbackInfo(String event, String result) {
        JSONObject jsonObj = null;
        String statusMessage = null;

        try {
            jsonObj = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//
        if(jsonObj == null) {
            return false;
        }
        switch (event) {
            case "onInited":
                if ((jsonObj.optInt("tvcore", 1)) == 0) {
                    statusMessage = "Ready to go!";
                }
                else {
                    statusMessage = "Init error!";
                }
                break;

            case "onStart":
                break;

            case "onPrepared": // use http-mpegts as source
                if(jsonObj.optString("http", null) != null) {
                    mTvBusPlayBackUrl = jsonObj.optString("http", null);
                    LogUtils.i(TAG,"play url :"+mTvBusPlayBackUrl);
//                    mPlayer.play(playbackUrl);
                    break;
                }

                return false;
            case "onInfo":
//                mTmPlayerConn = jsonObj.optInt("hls_last_conn", 0);
//                mBuffer = jsonObj.optInt("buffer", 0);
//
//                statusMessage = "" + mBuffer + "  "
//                        + jsonObj.optInt("download_rate", 0) * 8 / 1000 +"K";
                break;

            case "onStop":
//                if(jsonObj.optInt("errno", 1) < 0) {
//                    statusMessage = "stop: " + jsonObj.optInt("errno", 1);
//                }
//                break;
//
//            case "onQut":
//                break;
        }
//        if(statusMessage != null) {
//            updateStatusView(statusMessage);
//        }
        return true;
    }

    private void startTvBusPlayback() {
        this.main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPlayer.play(mTvBusPlayBackUrl);
            }
        });
    };
}
