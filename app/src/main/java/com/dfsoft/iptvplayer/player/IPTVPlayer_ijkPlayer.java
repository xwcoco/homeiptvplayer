package com.dfsoft.iptvplayer.player;

import android.app.Activity;

import com.dfsoft.iptvplayer.player.ijkplayer.IRenderView;
import com.dfsoft.iptvplayer.player.ijkplayer.IjkPlayerView;
import com.dfsoft.iptvplayer.utils.LogUtils;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class IPTVPlayer_ijkPlayer extends IPTVPlayer_Base implements IMediaPlayer.OnVideoSizeChangedListener,IMediaPlayer.OnBufferingUpdateListener {
    private final String TAG = "IPTVPlayer_ijkPlayer";
    public IPTVPlayer_ijkPlayer(Activity main) {
        super(main);
    }

    private IjkPlayerView mPlayerView = new IjkPlayerView();

    @Override
    public void bindView() {
        mPlayerView.setOnVideoSizeChangedListener(this);
        mPlayerView.setPlayerBufferingUpdateListener(this);
        mPlayerView.bindView(this.mVideoLayout);
    }

    @Override
    public void play(String path) {
        mPlayerView.setUseHardwardDecoder(getIJKHardwareCode());
        mPlayerView.setVideoPath(path);
        mPlayerView.start();
    }

    private int getIJKHardwareCode() {
        if (hardwareMode == 0 || hardwareMode == 1) return 1;
        return 0;
    }

    @Override
    public void stop() {
        mPlayerView.stop();
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

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        LogUtils.i(TAG,"buffer ... "+percent);
    }

    @Override
    public void close() {
        mPlayerView.release(true);
        super.close();
    }

    @Override
    public void setDisplayMode(int mode) {
        int tmode = mode;
        switch (mode) {
            case 0:
                tmode = IRenderView.AR_ASPECT_FIT_PARENT;
                break;
            case 1:
                tmode = IRenderView.AR_MATCH_PARENT;
                break;
            case 2:
                tmode = IRenderView.AR_ASPECT_FILL_PARENT;
                break;
            case 3:
                tmode = IRenderView.AR_16_9_FIT_PARENT;
                break;
            case 4:
                tmode = IRenderView.AR_4_3_FIT_PARENT;
                break;
            default:
                tmode = IRenderView.AR_ASPECT_WRAP_CONTENT;
        }
        mPlayerView.setAspectRatio(tmode);
    }

}
