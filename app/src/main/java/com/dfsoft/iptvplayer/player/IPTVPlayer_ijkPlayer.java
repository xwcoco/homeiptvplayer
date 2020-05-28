package com.dfsoft.iptvplayer.player;

import android.app.Activity;

import com.dfsoft.iptvplayer.player.ijkplayer.IRenderView;
import com.dfsoft.iptvplayer.player.ijkplayer.IjkPlayerView;
import com.dfsoft.iptvplayer.utils.LogUtils;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaMeta;
import tv.danmaku.ijk.media.player.misc.IMediaFormat;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

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
        mPlayerView.setAspectRatio(scaleMode);
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

                IjkTrackInfo[] trackInfos = mPlayerView.mMediaPlayer.getTrackInfo();
                if (trackInfos != null) {
                    setHudInfo(trackInfos,hud);
                    long speed = mPlayerView.mMediaPlayer.getTcpSpeed();
                    LogUtils.i(TAG," tcp speed = " + speed);
                }

                LogUtils.i(TAG,"hud = " + hud.toString());
                mInterface.OnGetHud(hud);
            }
        }
        LogUtils.i(TAG,"video width = "+w+" height = "+h);
    }

    private void setHudInfo(IjkTrackInfo[] trackInfos,IPTVPlayer_HUD hud) {
        for (int i = 0; i < trackInfos.length; i++) {
            IjkTrackInfo ti = trackInfos[i];
            int type = ti.getTrackType();
            if (type == ITrackInfo.MEDIA_TRACK_TYPE_VIDEO) {
                IMediaFormat fm = ti.getFormat();
                LogUtils.i(TAG, "video track : IJKM_KEY_CODEC_NAME =  " + fm.getString(IjkMediaMeta.IJKM_KEY_CODEC_NAME));
                hud.codec = ijkCodecToAndroidCodec(fm.getString(IjkMediaMeta.IJKM_KEY_CODEC_NAME));

//                LogUtils.i(TAG, "video track : IJKM_KEY_BITRATE =  " + fm.getString(IjkMediaMeta.IJKM_KEY_BITRATE));
//                hud.codec = ti.getFormat()
            } else if (type == ITrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                IMediaFormat fm = ti.getFormat();
                hud.audio_codec = fm.getString(IjkMediaMeta.IJKM_KEY_CODEC_NAME);
                hud.audio_rate = fm.getInteger(IjkMediaMeta.IJKM_KEY_SAMPLE_RATE);
                int audio_layout = fm.getInteger(IjkMediaMeta.IJKM_KEY_CHANNEL_LAYOUT);
                if (audio_layout == IjkMediaMeta.AV_CH_LAYOUT_MONO)
                    hud.audio_channels = 1;
                else if (audio_layout == IjkMediaMeta.AV_CH_LAYOUT_STEREO)
                    hud.audio_channels = 2;
                else
                    hud.audio_channels = audio_layout;

                LogUtils.i(TAG, "audio track : IJKM_KEY_CODEC_NAME =  " + fm.getString(IjkMediaMeta.IJKM_KEY_CODEC_NAME));
                LogUtils.i(TAG, "audio track : IJKM_KEY_SAMPLE_RATE =  " + fm.getString(IjkMediaMeta.IJKM_KEY_SAMPLE_RATE));
                LogUtils.i(TAG, "audio track : IJKM_KEY_CHANNEL_LAYOUT =  " + fm.getString(IjkMediaMeta.IJKM_KEY_CHANNEL_LAYOUT));
            }
        }
    }

    private String ijkCodecToAndroidCodec(String name) {
        if (name.contains("h264")) return "video/avc";
        if (name.contains("hevc")) return "video/hevc";
        return name;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        LogUtils.i(TAG,"buffer ... "+percent);
        if (this.mInterface != null) {
//            float percent = event.getBuffering();
            mInterface.onBuffering(percent);
        }
    }

    @Override
    public void close() {
        mPlayerView.release(true);
        super.close();
    }

    @Override
    public void setDisplayMode(int mode) {
        super.setDisplayMode(mode);
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

    @Override
    public int getNeedSpeed() {
        float tmp = (float) mPlayerView.mMediaPlayer.getTcpSpeed() / 1024;
        return Math.round(tmp);
    }
}
