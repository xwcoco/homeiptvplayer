package com.dfsoft.iptvplayer.player;

import android.app.Activity;
import android.net.Uri;
import android.nfc.Tag;
import android.util.Log;

import com.dfsoft.iptvplayer.utils.LogUtils;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.VideoHelperHacker;
import org.videolan.libvlc.interfaces.IMedia;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class IPTVPlayer_VLCPlayer extends IPTVPlayer_Base implements MediaPlayer.EventListener {

    private static final String TAG = "IPTVPlayer_VLCPlayer";

    public IPTVPlayer_VLCPlayer(Activity main) {
        super(main);
    }

    private LibVLC mLibVLC = null;
    public MediaPlayer mMediaPlayer;

    private VLCVideoLayout mVLCVideoLayout;
    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = false;

    @Override
    public void bindView() {
        if (mLibVLC != null) return;

        ArrayList<String> options= new ArrayList<>();
        options.add("-vvv");
        options.add("--http-reconnect");
        options.add("--vout=android-display");
//        options.add("-vvv") // verbosity

//        options.add("--aout=opensles");
        options.add("--android-display-chroma=RV32");
//        options.add("--audio-time-stretch"); // time stretching
//        options.add("--deinterlace=-1");
//        options.add("--deinterlace-mode=yadif");
//        options.add("--sout-deinterlace-mode=yadif");
//        options.add("--video-filter=deinterlace");
//        options.add("--no-hdtv-fix");
        options.add("--network-caching=8192");
        options.add("--udp-buffer=8192");
//        options.add("--clock-jitter=0");
        options.add("--clock-synchro=0");

//        options.add("--h264-fps=2");
//        options.add("--hevc-fps=20");
        mLibVLC = new LibVLC(mVideoLayout.getContext(), options);
        mMediaPlayer = new MediaPlayer(mLibVLC);

        mVLCVideoLayout = new VLCVideoLayout(mVideoLayout.getContext());

        mVideoLayout.addView(mVLCVideoLayout);
//        mMediaPlayer.getVLCVout().setVideoView();
        mMediaPlayer.attachViews(mVLCVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);
//        doHackVideoHelper();
        mMediaPlayer.setEventListener(this);
    }

    private void doHackVideoHelper() {
        Class hacker = mMediaPlayer.getClass();
        Field field = null;
        try {
            field = hacker.getDeclaredField("mVideoHelper");
            field.setAccessible(true);
            VideoHelperHacker newHelper = new VideoHelperHacker(mMediaPlayer,mVLCVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);
            newHelper.attachViews();
//            Object tmpObj = field.get(mMediaPlayer);
            field.set(mMediaPlayer,newHelper);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void play(String path) {
        Media media = new Media(mLibVLC, Uri.parse(path));
        media.addOption(":network-caching=1000");

        boolean hwEnabled = true;
        boolean hwForce = false;
        switch (hardwareMode) {
            case 1:
                hwForce = true;
                break;
            case 2:
                hwEnabled = false;
                break;
        }

        media.setHWDecoderEnabled(hwEnabled,hwForce);
//        media.addOption(":codec=mediacodec_ndk,mediacodec_jni,none");
        mMediaPlayer.setMedia(media);
        mMediaPlayer.setVideoScale(MediaPlayer.ScaleType.values()[scaleMode]);
        media.release();

        mMediaPlayer.play();
    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
    }

    @Override
    public void setDisplayMode(int mode) {
        super.setDisplayMode(mode);
        MediaPlayer.ScaleType oldmode = mMediaPlayer.getVideoScale();
        if (oldmode.ordinal() == mode) return;
        mMediaPlayer.setVideoScale(MediaPlayer.ScaleType.values()[mode]);
    }

    @Override
    public void setHardwareMode(int hardwareMode) {
        super.setHardwareMode(hardwareMode);
        boolean hwEnabled = true;
        boolean hwForce = false;
        switch (hardwareMode) {
            case 1:
                hwForce = true;
                break;
            case 2:
                hwEnabled = false;
                break;
        }

        Media media = (Media) mMediaPlayer.getMedia();
        if (media != null) {
            media.setHWDecoderEnabled(hwEnabled,hwForce);
        }
    }

    @Override
    public void close() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        if (mLibVLC != null) {
            mLibVLC.release();
        }
        super.close();
    }

    @Override
    public void onEvent(MediaPlayer.Event event) {
        switch (event.type) {
            case MediaPlayer.Event.Buffering:

                if (this.mInterface != null) {
                    float percent = event.getBuffering();
                    mInterface.onBuffering(percent);
                }
                break;
            case MediaPlayer.Event.Vout:
                if (this.mInterface != null) {

                    IPTVPlayer_HUD hud = new IPTVPlayer_HUD();

                    IMedia media = mMediaPlayer.getMedia();
                    final int trackCount = media.getTrackCount();
                    for (int i = 0; i < trackCount; ++i) {
                        final IMedia.Track  track = media.getTrack(i);
                        if (track.type == IMedia.Track.Type.Video) {
                            IMedia.VideoTrack vt = (IMedia.VideoTrack) track;
                            hud.codec = vt.codec;
                            hud.width = vt.width;
                            hud.height = vt.height;
                            Log.d(TAG, "onVout: v codec = "+vt.codec);
                            Log.d(TAG, "onVout: v height = "+vt.height);
                            Log.d(TAG, "onVout: v width = "+vt.width);
                        } else if (track.type == IMedia.Track.Type.Audio) {
                            IMedia.AudioTrack at = (IMedia.AudioTrack) track;
                            hud.audio_codec = at.codec;
                            hud.audio_bitrate = at.bitrate;
                            hud.audio_channels = at.channels;
                            hud.audio_rate = at.rate;
                            LogUtils.i(TAG,"auudio codec = "+at.codec);
                            LogUtils.i(TAG,"auudio description = "+at.description);
                            LogUtils.i(TAG,"auudio bitrate = "+at.bitrate);
                            LogUtils.i(TAG,"auudio channels = "+at.channels);
                            LogUtils.i(TAG,"auudio rate = "+at.rate);
                        }

                    }

                    if (hud.width == 0 || hud.height == 0) {
                        MediaPlayerHack hack = new MediaPlayerHack();
                        hack.getVideoOut(mMediaPlayer);
                        if (hack.width != 0 && hack.height != 0) {
                            hud.width = hack.width;
                            hud.height = hack.height;
                        }
                    }

                    mInterface.OnGetHud(hud);
                }
                break;
//            default:
//                LogUtils.i(TAG,"MediaPlayer Event 0x"+Integer.toHexString(event.type));
        }
    }

    private class MediaPlayerHack {
        int width = 0;
        int height = 0;

        void getVideoOut(MediaPlayer player) {
            Class hacker = player.getClass();
            try {
                Field field = hacker.getDeclaredField("mVideoHelper");
                field.setAccessible(true);
                LogUtils.i(IPTVPlayer_VLCPlayer.TAG,"field : " + field);
                Class videoHelperClass = field.getType();
                Object tmpObj = field.get(player);
                Field field_width = videoHelperClass.getDeclaredField("mVideoWidth");
                field_width.setAccessible(true);
                width = field_width.getInt(tmpObj);
                LogUtils.i(IPTVPlayer_VLCPlayer.TAG,"mVideoHelper field width: " + width);

                Field field_height = videoHelperClass.getDeclaredField("mVideoHeight");
                field_height.setAccessible(true);
                height = field_height.getInt(tmpObj);
                LogUtils.i(IPTVPlayer_VLCPlayer.TAG,"mVideoHelper field height: " + height);
//                LogUtils.i(IPTVPlayer_VLCPlayer.TAG,"field : " + field.getType());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
