package com.dfsoft.iptvplayer.player;

import android.app.Activity;
import android.widget.FrameLayout;

import com.dfsoft.iptvplayer.R;



public class IPTVPlayer_Base {
    protected Activity main = null;

    public IPTVPlayer_Base(Activity main) {
        this.main = main;
    }

    protected FrameLayout mVideoLayout = null;

    public void initPlayer() {
        mVideoLayout = this.main.findViewById(R.id.main_video_view);
        this.bindView();
    }

    public interface IPTV_HUD_INTERFACE {
        public void onBuffering(float percent);
        public void OnGetHud(IPTVPlayer_HUD hud);
    }

    protected IPTV_HUD_INTERFACE mInterface = null;

    public void setHudInterface(IPTV_HUD_INTERFACE face) {
        this.mInterface = face;
    }


    public void bindView() {

    }


    public void play(String path) {

    }

    public void stop() {

    }

    public void resume() {

    }

    public void pause() {

    }

    public void close() {
        mVideoLayout.removeAllViews();
    }

    protected int scaleMode = 0;

    public void setDisplayMode(int mode) {
        scaleMode = mode;
    }
}
