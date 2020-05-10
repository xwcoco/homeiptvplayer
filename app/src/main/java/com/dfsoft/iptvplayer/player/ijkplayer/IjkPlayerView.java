package com.dfsoft.iptvplayer.player.ijkplayer;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkPlayerView implements MediaController.MediaPlayerControl {
    private final String TAG = "IjkPlayerView";

    private Uri mUri;
    private Map<String, String> mHeaders;

    private boolean mRenderWithTextureView = false;
    private IRenderView mRenderView;
    private IjkMediaPlayer mMediaPlayer = null;

    private Context mAppContext;
    // 用于界面恢复后展示上一次播放的最后一帧画面
    private ImageView mImageView;
    private int mVideoSarNum;
    private int mVideoSarDen;

    private boolean isFullState;
    private ViewGroup.LayoutParams mRawParams;

    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;

    private IRenderView.ISurfaceHolder mSurfaceHolder = null;


//    private MonitorRecorder monitorRecorder;
    private float playSpeed = .0f;

    private long bufferTime;
    private long startbufferTime;
    private static int PURSUETIME = 10 * 1000;
    private boolean isAutoPursue = false;


    private static final int[] s_allAspectRatio = {
            IRenderView.AR_ASPECT_FIT_PARENT,
            IRenderView.AR_ASPECT_FILL_PARENT,
            IRenderView.AR_ASPECT_WRAP_CONTENT,
            IRenderView.AR_MATCH_PARENT,
            IRenderView.AR_16_9_FIT_PARENT,
            IRenderView.AR_4_3_FIT_PARENT};
    private int mCurrentAspectRatioIndex = 0;
    private int mCurrentAspectRatio = s_allAspectRatio[0];

    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mVideoRotationDegree;
    private MediaController mMediaController;
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
    private int mCurrentBufferPercentage;
    private IMediaPlayer.OnErrorListener mOnErrorListener;
    private IMediaPlayer.OnInfoListener mOnInfoListener;

    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener;

    public void setOnVideoSizeChangedListener(IMediaPlayer.OnVideoSizeChangedListener listener) {
        this.mOnVideoSizeChangedListener = listener;
    }



    private IjkMediaPlayer.OnNativeInvokeListener mOnNativeInvokeListener;
    private int mSeekWhenPrepared;  // recording the seek position while preparing
    private boolean mCanPause = true;
    private boolean mCanSeekBack = true;
    private boolean mCanSeekForward = true;

    private int bufferSize = -1;
    private boolean isAutoPlay = true;

    private static final int MSG_CACHE_DRU = 20160101;

    private static final int CACHE_WATER = 1 * 1000;

//    private InfoHudViewHolder mHudViewHolder;
    private long mPrepareStartTime = 0;
    private long mPrepareEndTime = 0;

    private long mSeekStartTime = 0;
    private long mSeekEndTime = 0;


    // OnNativeInvokeListener what
    public static final int AVAPP_EVENT_WILL_HTTP_OPEN = 1; //AVAppHttpEvent
    public static final int AVAPP_EVENT_DID_HTTP_OPEN = 2; //AVAppHttpEvent
    public static final int AVAPP_EVENT_WILL_HTTP_SEEK = 3; //AVAppHttpEvent
    public static final int AVAPP_EVENT_DID_HTTP_SEEK = 4; //AVAppHttpEvent

    public static final int AVAPP_EVENT_ASYNC_STATISTIC = 0x11000; //AVAppAsyncStatistic
    public static final int AVAPP_EVENT_ASYNC_READ_SPEED = 0x11001; //AVAppAsyncReadSpeed
    public static final int AVAPP_EVENT_IO_TRAFFIC = 0x12204; //AVAppIOTraffic

    public static final int AVAPP_CTRL_WILL_TCP_OPEN = 0x20001; //AVAppTcpIOControl
    public static final int AVAPP_CTRL_DID_TCP_OPEN = 0x20002; //AVAppTcpIOControl

    public static final int AVAPP_CTRL_WILL_HTTP_OPEN = 0x20003; //AVAppIOControl
    public static final int AVAPP_CTRL_WILL_LIVE_OPEN = 0x20005; //AVAppIOControl

    public static final int AVAPP_CTRL_WILL_CONCAT_SEGMENT_OPEN = 0x20007; //AVAppIOControl
    // OnNativeInvokeListener bundle key
    public static final String AVAPP_EVENT_URL = "url";
    public static final String AVAPP_EVENT_ERROR = "error";
    public static final String AVAPP_EVENT_HTTP_CODE = "http_code";

    private ViewGroup mBindView;

    public void bindView(ViewGroup view) {
        if (view == null) return;
        mBindView = view;
        mAppContext = view.getContext().getApplicationContext();
        IRenderView renderView;
        if (mRenderWithTextureView) {
            Log.i(TAG, "RenderWithTextureView true");
            renderView = new TextureRenderView(view.getContext());
        } else {
            Log.i(TAG, "RenderWithTextureView false");
            renderView = new SurfaceRenderView(view.getContext());
        }
        setRenderView(renderView);

        mVideoWidth = 0;
        mVideoHeight = 0;
        // REMOVED: getHolder().addCallback(mSHCallback);
        // REMOVED: getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mBindView.setFocusable(true);
        mBindView.setFocusableInTouchMode(true);
        mBindView.requestFocus();
        // REMOVED: mPendingSubtitleTracks = new Vector<Pair<InputStream, MediaFormat>>();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;

        addImageView();
    }

    private void addImageView() {
        // 用于界面恢复后展示上一次播放的最后一帧画面
        if (mImageView != null) {
            return;
        }
        mImageView = new ImageView(mBindView.getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        mImageView.setLayoutParams(lp);
        mBindView.addView(mImageView);
        mImageView.setVisibility(View.GONE);
    }
    public void setRenderView(IRenderView renderView) {
        if (mRenderView != null) {
            if (mMediaPlayer != null)
                mMediaPlayer.setSurface(null);

            View renderUIView = mRenderView.getView();
            mRenderView.removeRenderCallback(mSHCallback);
            mRenderView = null;
            mBindView.removeView(renderUIView);
        }

        if (renderView == null)
            return;

        mRenderView = renderView;
        renderView.setAspectRatio(mCurrentAspectRatio);
        if (mVideoWidth > 0 && mVideoHeight > 0)
            renderView.setVideoSize(mVideoWidth, mVideoHeight);
        if (mVideoSarNum > 0 && mVideoSarDen > 0)
            renderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);

        View renderUIView = mRenderView.getView();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        renderUIView.setLayoutParams(lp);
        mBindView.addView(renderUIView);

        mRenderView.addRenderCallback(mSHCallback);
        mRenderView.setVideoRotation(mVideoRotationDegree);
    }

    private IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int w, int h) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceChanged: unmatched render callback\n");
                return;
            }
            Log.i(TAG, "onSurfaceChanged");
            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = !mRenderView.shouldWaitForResize() || (mVideoWidth == w && mVideoHeight == h);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                start();
            }
        }

        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceCreated: unmatched render callback\n");
                return;
            }
            mSurfaceHolder = holder;
            if (mMediaPlayer != null) {
                bindSurfaceHolder(mMediaPlayer, holder);
            } else {
                // onSurfaceCreated在调用过start之后触发
                if (mTargetState == STATE_PLAYING) {
                    openVideo();
                    if (mMediaPlayer != null) {
                        bindSurfaceHolder(mMediaPlayer, holder);
                        mCurrentState = STATE_PLAYING;
                        start();
                    }
                }
            }

            Bitmap lastFrame = mRenderView.getLastFrame();
            if (lastFrame != null) {
                mImageView.setImageBitmap(lastFrame);
                mImageView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceDestroyed: unmatched render callback\n");
                return;
            }
            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            // REMOVED: if (mMediaController != null) mMediaController.hide();
            // REMOVED: release(true);
//            releaseWithoutStop();
            if (mMediaController != null) mMediaController.hide();
//            release(true);
            releaseWithoutStop();
        }
    };

    // REMOVED: mSHCallback
    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
        if (mp == null)
            return;

        if (holder == null) {
            mp.setSurface(null);
            return;
        }

        holder.bindToMediaPlayer(mp);
    }

    public void releaseWithoutStop() {
        if (mMediaPlayer != null){
            mMediaPlayer.setSurface(null);
        }
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            dismissLastFrame();
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            mRenderView.getView().setBackgroundDrawable(null);
        } else {
            Log.i(TAG, "start isInPlaybackState false mMediaPlayer == null:" + (mMediaPlayer == null) + " mCurrentState" + mCurrentState);
        }
        mTargetState = STATE_PLAYING;

    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;

    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getDuration();
        }

        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            if (mCurrentState == STATE_PLAYBACK_COMPLETED) {
                return (int) mMediaPlayer.getDuration();
            }
            return (int) mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (isInPlaybackState()) {
            mSeekStartTime = System.currentTimeMillis();
            mMediaPlayer.seekTo(pos);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = pos;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;

    }

    @Override
    public boolean canPause() {
        return mCanPause;
    }

    @Override
    public boolean canSeekBackward() {
        return mCanSeekBack;
    }

    @Override
    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    @Override
    public int getAudioSessionId() {
        return mMediaPlayer.getAudioSessionId();
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    private void dismissLastFrame() {
        mImageView.setVisibility(View.GONE);
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);

        //开始播放时间
//        monitorRecorder.start();
//        monitorRecorder.setPlayUrl(mUri.toString());

//        mHandler.removeMessages(MSG_CACHE_DRU);
//        mHandler.sendEmptyMessageDelayed(MSG_CACHE_DRU, 500);

        AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        try {
            mMediaPlayer = new IjkMediaPlayer();
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", isAutoPlay ? 1 : 0);
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 1024 * 400);
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 0);
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", "128000");
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec_mpeg4", 1);
//            mMediaPlayer.setLooping(true);
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "sync", "ext");
//            mMediaPlayer.setSpeed(1.03f);

            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 8);
            mMediaPlayer.setOption(1, "analyzemaxduration", 100L);
//            mMediaPlayer.setOption(1, "probesize", 10240L);
            mMediaPlayer.setOption(1, "flush_packets", 0L);
            mMediaPlayer.setOption(4, "packet-buffering", 1L);
            mMediaPlayer.setOption(4, "framedrop", 1L);


            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", mUseHardwardDecoder);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", mUseHardwardDecoder);



            if (bufferSize != -1) {
                mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", bufferSize);
            }

            // TODO: create SubtitleController in MediaPlayer, but we need
            // a context for the subtitle renderers
            final Context context = mBindView.getContext();
            // REMOVED: SubtitleController

            // REMOVED: mAudioSession
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnNativeInvokeListener(mNativeInvokeListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mMediaPlayer.setDataSource(mAppContext, mUri, mHeaders);
            } else {
                mMediaPlayer.setDataSource(mUri.toString());
            }
            bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mPrepareStartTime = System.currentTimeMillis();
            mMediaPlayer.prepareAsync();
//            if (mHudViewHolder != null)
//                mHudViewHolder.setMediaPlayer(mMediaPlayer);

            if (playSpeed != .0f) {
                mMediaPlayer.setSpeed(playSpeed);
            }

            // REMOVED: mPendingSubtitleTracks

            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING;
            attachMediaController();
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        } finally {
            // REMOVED: mPendingSubtitleTracks.clear();
        }
    }
    /*
     * release the media player in any state
     */
    public void release(boolean cleartargetstate) {

        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
//            mHandler.removeMessages(MSG_CACHE_DRU);
//            monitorRecorder.endRecode();
            mMediaPlayer = null;
            // REMOVED: mPendingSubtitleTracks.clear();
            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                mTargetState = STATE_IDLE;
            }
            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }
    private IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {
            mPrepareEndTime = System.currentTimeMillis();
//            if (mHudViewHolder != null) {
//                mHudViewHolder.updateLoadCost(mPrepareEndTime - mPrepareStartTime);
//            }
//            monitorRecorder.firstPacket();
            mCurrentState = STATE_PREPARED;
            mMediaPlayer.pause();

            // Get the capabilities of the player for this stream
            // REMOVED: Metadata

            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
            if (mMediaController != null) {
                mMediaController.setEnabled(true);
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

//            monitorRecorder.setVideoSize(mVideoHeight, mVideoWidth);
//            monitorRecorder.setFirstPlayState(0);
//            monitorRecorder.getMetaData(mMediaPlayer._getMetaData());

            int seekToPosition = mSeekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                //Log.i("@@@@", "video size: " + mVideoWidth +"/"+ mVideoHeight);
                // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                if (mRenderView != null) {
                    mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                    mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                    if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                        // We didn't actually change the size (it was already at the size
                        // we need), so we won't get a "surface changed" callback, so
                        // start the video here instead of in the callback.
                        if (mTargetState == STATE_PLAYING) {
                            start();
                            if (mMediaController != null) {
                                mMediaController.show();
                            }
                        } else if (!isPlaying() &&
                                (seekToPosition != 0 || getCurrentPosition() > 0)) {
                            if (mMediaController != null) {
                                // Show the media controls when we're paused into a video and make 'em stick.
                                mMediaController.show(0);
                            }
                        }
                    }
                }
            } else {
                // We don't know the video size yet, but should start anyway.
                // The video size might be reported to us later.
                if (mTargetState == STATE_PLAYING) {
                    start();
                }
            }
        }
    };

    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new IMediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
                    if (mOnVideoSizeChangedListener != null) {
                        mOnVideoSizeChangedListener.onVideoSizeChanged(mp, width, height, sarNum, sarDen);
                    }
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();
                    mVideoSarNum = mp.getVideoSarNum();
                    mVideoSarDen = mp.getVideoSarDen();
                    if (mVideoWidth != 0 && mVideoHeight != 0) {
                        if (mRenderView != null) {
                            mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                            mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                        }
                        // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                        mBindView.requestLayout();
                    }
                }
            };

    private IMediaPlayer.OnCompletionListener mCompletionListener =
            new IMediaPlayer.OnCompletionListener() {
                public void onCompletion(IMediaPlayer mp) {
                    mCurrentState = STATE_PLAYBACK_COMPLETED;
                    mTargetState = STATE_PLAYBACK_COMPLETED;
                    if (mMediaController != null) {
                        mMediaController.hide();
                    }
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                }
            };

    private IMediaPlayer.OnErrorListener mErrorListener =
            new IMediaPlayer.OnErrorListener() {
                public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
                    Log.e(TAG, "Error: " + framework_err + "," + impl_err);

//                    monitorRecorder.errorDate("Error: " + framework_err + "," + impl_err);

                    mCurrentState = STATE_ERROR;
                    mTargetState = STATE_ERROR;
                    if (mMediaController != null) {
                        mMediaController.hide();
                    }

                    /* If an error handler has been supplied, use it and finish. */
                    if (mOnErrorListener != null) {
                        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                            return true;
                        }
                    }

                    return true;
                }
            };

    private IjkMediaPlayer.OnNativeInvokeListener mNativeInvokeListener = new IjkMediaPlayer.OnNativeInvokeListener() {
        @Override
        public boolean onNativeInvoke(int what, Bundle args) {
            Log.i(TAG, "onNativeInvoke:" + what);
            if (mOnNativeInvokeListener != null) {
                mOnNativeInvokeListener.onNativeInvoke(what, args);
            }
            return false;
        }
    };

    private IMediaPlayer.OnInfoListener mInfoListener =
            new IMediaPlayer.OnInfoListener() {
                public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) {
                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(mp, arg1, arg2);
                    }
                    switch (arg1) {
                        case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
                            dismissLastFrame();
                            break;
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                            Log.e(TAG, "卡顿时间：" + bufferTime);
                            startbufferTime = System.currentTimeMillis();
                            if (bufferTime > PURSUETIME && isAutoPursue) {
                                bufferTime = 0;
                                resume();
                                Log.i(TAG, "卡顿重连追帧");
                                break;
                            }
                            if (isAutoPursue) {
                                reportError();
                            }

                            Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                            Log.i(TAG, "结束缓冲：" + bufferTime);
                            if (startbufferTime != 0 && isAutoPursue) {
                                bufferTime = System.currentTimeMillis() - startbufferTime;
                                if (bufferTime > 2000) {
                                    bufferTime = 0;
                                    resume();
                                }
                            }
                            cancelReport();
//                            monitorRecorder.BufferEnd();
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
//                            recorder.setBandwidth(arg2);
                            Log.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
                            break;
                        case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                            Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                            Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                            Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                            Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                            mVideoRotationDegree = arg2;
                            Log.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2);
                            if (mRenderView != null)
                                mRenderView.setVideoRotation(arg2);
                            break;
                        case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                            Log.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                            break;
                    }
                    return true;
                }
            };

    private IMediaPlayer.OnBufferingUpdateListener mPlayerBufferingUpdateListener = null;

    public void setPlayerBufferingUpdateListener(IMediaPlayer.OnBufferingUpdateListener listener) {
        mPlayerBufferingUpdateListener = listener;
    }

    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new IMediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;
                    if (mPlayerBufferingUpdateListener != null)
                        mPlayerBufferingUpdateListener.onBufferingUpdate(mp,percent);
                }
            };

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            View anchorView = this.mBindView.getParent() instanceof View ?
                    (View) this.mBindView.getParent() : this.mBindView;
            mMediaController.setAnchorView(anchorView);
            mMediaController.setEnabled(isInPlaybackState());
        }
    }

    public void resume() {
        openVideo();
    }

    private void reportError() {
        Log.e(TAG, "reportError");
        this.mBindView.postDelayed(reconnection, 20000);
    }

    private Runnable reconnection = new Runnable() {
        @Override
        public void run() {
            release(true);
            mErrorListener.onError(mMediaPlayer, -1001, 0);
        }
    };

    private void cancelReport() {
        Log.e(TAG, "cancelReport");
        this.mBindView.removeCallbacks(reconnection);
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    private void setVideoURI(Uri uri, Map<String, String> headers) {
        mUri = uri;

        if (uri.toString().startsWith("http")) {
            if (headers == null) {
                headers = new HashMap<>();
            }
            headers.put("X-Accept-Video-Encoding", "h265");
        }

        mHeaders = headers;
        mSeekWhenPrepared = 0;
        openVideo();
        mBindView.requestLayout();
        mBindView.invalidate();
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    public void setAspectRatio(int aspectRatio) {
        mCurrentAspectRatio = aspectRatio;
        if (mRenderView != null)
            mRenderView.setAspectRatio(mCurrentAspectRatio);
    }

    private int mUseHardwardDecoder = 1;


    public int getUseHardwardDecoder() {
        return mUseHardwardDecoder;
    }

    public void setUseHardwardDecoder(int useHardwardDecoder) {
        this.mUseHardwardDecoder = useHardwardDecoder;
    }
}