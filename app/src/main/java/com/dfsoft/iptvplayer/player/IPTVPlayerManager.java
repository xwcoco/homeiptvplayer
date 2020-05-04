package com.dfsoft.iptvplayer.player;

import android.app.Activity;

import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.manager.IPTVConfig;
import com.dfsoft.iptvplayer.manager.IPTVMessage;

public class IPTVPlayerManager implements IPTVPlayer_Base.IPTV_HUD_INTERFACE {
    private Activity main = null;

    private int playerid = 1;

    private IPTVPlayer_Base mPlayer = null;
    private IPTVConfig config = IPTVConfig.getInstance();

    public IPTVPlayer_HUD hud = new IPTVPlayer_HUD();

    public IPTVPlayerManager(Activity main) {
        this.main = main;
        this.createPlayer();
    }

    private void createPlayer() {
        switch (playerid) {
            case 0:
                mPlayer = new IPTVPlayer_ijkPlayer(main);
                break;
            case 1:
                mPlayer = new IPTVPlayer_VLCPlayer(main);
                break;
        }
        if (mPlayer == null) return;
        mPlayer.setHudInterface(this);
        mPlayer.initPlayer();
    }

    public void play(IPTVChannel channel,int index) {
        if (mPlayer == null || channel == null) return;
        if (index < 0 || channel.source.size() == 0 || index >= channel.source.size()) return;

        this.hud = new IPTVPlayer_HUD();

        mPlayer.play(channel.source.get(index));

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
}
