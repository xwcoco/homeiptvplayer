package com.dfsoft.iptvplayer.player;

import android.app.Activity;

import com.dfsoft.iptvplayer.player.ijkplayer.IjkPlayerView;
import com.dfsoft.iptvplayer.utils.LogUtils;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class IPTVPlayer_ijkPlayer extends IPTVPlayer_Base implements IMediaPlayer.OnVideoSizeChangedListener {
    private final String TAG = "IPTVPlayer_ijkPlayer";
    public IPTVPlayer_ijkPlayer(Activity main) {
        super(main);
    }

    private IjkPlayerView mPlayerView = new IjkPlayerView();

    @Override
    public void bindView() {
        mPlayerView.setOnVideoSizeChangedListener(this);
        mPlayerView.bindView(this.mVideoLayout);
    }

    @Override
    public void play(String path) {
        mPlayerView.setVideoPath(path);
        mPlayerView.start();
    }

    private int last_video_width = -1;
    private int last_video_height = -1;

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
        int w = mp.getVideoWidth();
        int h = mp.getVideoHeight();
        if (w != last_video_height || h != last_video_height) {
            last_video_height = h;
            last_video_width = w;
            if (mInterface != null) {
                IPTVPlayer_HUD hud = new IPTVPlayer_HUD();
                hud.width = w;
                hud.height = h;
                mInterface.OnGetHud(hud);
            }
        }
        LogUtils.i(TAG,"video width = "+w+" height = "+h);
    }
}
