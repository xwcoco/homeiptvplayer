package com.dfsoft.iptvplayer.player;

import android.app.Activity;

import com.dfsoft.iptvplayer.player.ijkplayer.IjkPlayerView;

public class IPTVPlayer_ijkPlayer extends IPTVPlayer_Base {
    public IPTVPlayer_ijkPlayer(Activity main) {
        super(main);
    }

    private IjkPlayerView mPlayerView = new IjkPlayerView();

    @Override
    public void bindView() {
        mPlayerView.bindView(this.mVideoLayout);
    }

    @Override
    public void play(String path) {
        mPlayerView.setVideoPath(path);
        mPlayerView.start();
    }
}
