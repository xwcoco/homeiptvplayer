package com.dfsoft.iptvplayer.player;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.dfsoft.iptvplayer.player.exoplayer.ExoPlayerView;
import com.dfsoft.iptvplayer.utils.LogUtils;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Util;

public class IPTVPlayer_ExoPlayer extends IPTVPlayer_Base implements Player.EventListener, AnalyticsListener {

    private static final String TAG = "IPTVPlayer_ExoPlayer";

    public IPTVPlayer_ExoPlayer(Activity main) {
        super(main);
    }

    private SimpleExoPlayer mPlayer = null;
    protected DefaultRenderersFactory mRendererFactory;
    protected Context mAppContext;
    private MappingTrackSelector mTrackSelector;

//    private AspectRatioFrameLayout mAspectRatioFrameLayout;

    private ExoPlayerView mView = null;

    @Override
    public void bindView() {
        mAppContext = mVideoLayout.getContext().getApplicationContext();

//        mAspectRatioFrameLayout = new AspectRatioFrameLayout(mVideoLayout.getContext());
//
//        SurfaceView mView = new SurfaceView(mVideoLayout.getContext());
////        PlayerView mView = new PlayerView(mVideoLayout.getContext());
////        PlayerControlView mView = new PlayerControlView(mVideoLayout.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
//
//        mAspectRatioFrameLayout.addView(mView,lp);
//        mAspectRatioFrameLayout.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
//        mVideoLayout.addView(mAspectRatioFrameLayout, lp);

        mView = new ExoPlayerView(mVideoLayout.getContext());
        mView.setScaleMode(scaleMode);
        mVideoLayout.addView(mView,lp);

        if (mPlayer == null) {
//            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//            mTrackSelector = new DefaultTrackSelector();
            mRendererFactory = new DefaultRenderersFactory(mAppContext);
            mRendererFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
            mPlayer = new SimpleExoPlayer.Builder(mAppContext, mRendererFactory).build();
            mPlayer.addAnalyticsListener(this);

            mPlayer.addListener(this);
        }
//        @Nullable Player.VideoComponent newVideoComponent = mPlayer.getVideoComponent();
//        newVideoComponent.setVideoSurfaceView(mView);
//        mPlayer.setVideoSurface(mView);

        mView.setPlayer(mPlayer);

    }

    @Override
    public void play(String path) {
        MediaSource mediaSource = getMediaSource(path);
        if (mediaSource == null) return;
        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(true);
    }

    public static final int TYPE_RTMP = 4;

    private MediaSource getMediaSource(String path) {
        Uri contentUri = Uri.parse(path);
        int contentType = inferContentType(path);
        DataSource.Factory dataSourceFactory =
                new DefaultHttpDataSourceFactory(Util.getUserAgent(mAppContext, "homeiptvplayer"));
        MediaSource mediaSource = null;
        switch (contentType) {
            case TYPE_RTMP:
                RtmpDataSourceFactory rtmpDataSourceFactory = new RtmpDataSourceFactory(null);
                mediaSource = new ProgressiveMediaSource.Factory(rtmpDataSourceFactory,
                        new DefaultExtractorsFactory())
                        .createMediaSource(contentUri);
                break;
            case C.TYPE_DASH:
                mediaSource = new DashMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(contentUri);
                break;
            case C.TYPE_HLS:
                mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(contentUri);
                break;
            case C.TYPE_SS:
                mediaSource = new SsMediaSource.Factory(dataSourceFactory).createMediaSource(contentUri);
                break;
            default:
                mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(path));
        }
        return mediaSource;
    }

    private int inferContentType(String dataSource) {
        String fileName = Util.toLowerInvariant(dataSource);
        if (fileName.startsWith("rtmp:")) {
            return TYPE_RTMP;
        } else {
            return Util.inferContentType(Uri.parse(dataSource), "");
        }

    }


    @Override
    public void stop() {
        mPlayer.stop();
    }

    @Override
    public void close() {
        mPlayer.release();
    }


    private boolean isBuffing = false;
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        LogUtils.i(TAG, "playWhenReady = " + playWhenReady);
        LogUtils.i(TAG, "playbackState = " + playbackState);
    }

    @Override
    public void onVideoSizeChanged(EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        LogUtils.i(TAG, "onVideoSizeChanged width = " + width +" height = "+height);
        if (mView != null)
            mView.setVideoSize(width,height);

        if (this.mInterface != null) {

            IPTVPlayer_HUD hud = new IPTVPlayer_HUD();
            hud.height = height;
            hud.width = width;
            mInterface.OnGetHud(hud);
        }
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
//        if (isLoading) {
//
//        }
        if (isLoading && this.mInterface != null) {
            mInterface.onBuffering(mPlayer.getBufferedPercentage());
        }
        LogUtils.i(TAG,"loading = "+ isLoading);
        LogUtils.i(TAG,"loading .... getBufferedPosition = " +  String.valueOf(mPlayer.getBufferedPosition()));
        LogUtils.i(TAG,"loading .... getBufferedPercentage = " +  String.valueOf(mPlayer.getBufferedPercentage()));
    }

    @Override
    public void setDisplayMode(int mode) {
        super.setDisplayMode(mode);
        if (mView != null)
            mView.setScaleMode(mode);
    }
}
