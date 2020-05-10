package com.dfsoft.iptvplayer.player.exoplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;

public class ExoPlayerView extends SurfaceView {

    public static final int SURFACE_BEST_FIT = 0;
    public static final int SURFACE_FIT_SCREEN = 1;
    public static final int SURFACE_FILL = 2;
    public static final int SURFACE_16_9 = 3;
    public static final int SURFACE_4_3 = 4;
    public static final int SURFACE_ORIGINAL = 5;

//    public  int SURFACE_BEST_FIT = 0;
//    public  int SURFACE_FIT_SCREEN = 1;
//    public  int SURFACE_FILL = 2;
//    public  int SURFACE_16_9 = 3;
//    public  int SURFACE_4_3 = 4;
//    public  int SURFACE_ORIGINAL = 5;

//    public enum ScaleType {
//        SURFACE_BEST_FIT,
//        SURFACE_FIT_SCREEN,
//        SURFACE_FILL,
//        SURFACE_16_9,
//        SURFACE_4_3,
//        SURFACE_ORIGINAL
//    }

    public ExoPlayerView(Context context) {
        this(context, null);
    }

    public ExoPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoSarNum;
    private int mVideoSarDen;

    private int mMeasuredWidth;
    private int mMeasuredHeight;

    private int mScaleMode = SURFACE_BEST_FIT;

    public int getScaleMode() {
        return mScaleMode;
    }

    public void setScaleMode(int mode) {
        if (mode != mScaleMode) {
            mScaleMode = mode;
            requestLayout();
        }
    }

    public void setPlayer(@NonNull ExoPlayer player) {
        @Nullable Player.VideoComponent newVideoComponent = player.getVideoComponent();
        newVideoComponent.setVideoSurfaceView(this);
    }

    public void setVideoSize(int videoWidth, int videoHeight) {
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;

        requestLayout();
    }

    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        mVideoSarNum = videoSarNum;
        mVideoSarDen = videoSarDen;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int mVideoRotationDegree = (int) getRotation();
        // 如果旋转了90°或270°则宽高测量值进行互换
        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
            int tempMeasureSpec = widthMeasureSpec;
            widthMeasureSpec = heightMeasureSpec;
            heightMeasureSpec = tempMeasureSpec;
        }

        // 获取默认的测量宽高值
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);

        if (mScaleMode == SURFACE_FIT_SCREEN || mScaleMode == SURFACE_BEST_FIT) {
            // 在 AR_MATCH_PARENT 模式下直接用原始测量值
            width = widthMeasureSpec;
            height = heightMeasureSpec;
        } else if (mVideoWidth > 0 && mVideoHeight > 0) {
            int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
            // modify，把&&操作符换为||
            if (widthSpecMode == View.MeasureSpec.AT_MOST || heightSpecMode == View.MeasureSpec.AT_MOST) {
                // 测量宽高比，对应的视图的宽高比
                float specAspectRatio = (float) widthSpecSize / (float) heightSpecSize;
                // 显示宽高比，要显示的视频宽高比
                float displayAspectRatio;
                // 这里计算显示宽高比
                switch (mScaleMode) {
                    case SURFACE_16_9:
                        // 16：9
                        displayAspectRatio = 16.0f / 9.0f;
                        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270)
                            displayAspectRatio = 1.0f / displayAspectRatio;
                        break;
                    case SURFACE_4_3:
                        // 4：3
                        displayAspectRatio = 4.0f / 3.0f;
                        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270)
                            displayAspectRatio = 1.0f / displayAspectRatio;
                        break;
                    case SURFACE_FILL:
                    case SURFACE_FIT_SCREEN:
                    default:
                        // 按视频来源宽高比
                        displayAspectRatio = (float) mVideoWidth / (float) mVideoHeight;
                        if (mVideoSarNum > 0 && mVideoSarDen > 0)
                            displayAspectRatio = displayAspectRatio * mVideoSarNum / mVideoSarDen;
                        break;
                }
                // 是否要显示视频宽度比例较大
                boolean shouldBeWider = displayAspectRatio > specAspectRatio;
                // 这里确定最终宽高
                switch (mScaleMode) {
                    case SURFACE_FIT_SCREEN:
                    case SURFACE_16_9:
                    case SURFACE_4_3:
                        if (shouldBeWider) {
                            // too wide, fix width；宽度比较大，固定宽度，使用测量宽度，按显示比例缩放高度
                            width = widthSpecSize;
                            height = (int) (width / displayAspectRatio);
                        } else {
                            // too high, fix height；高度比较大，固定高度，使用测量高度，按显示比例缩放宽度
                            height = heightSpecSize;
                            width = (int) (height * displayAspectRatio);
                        }
                        break;
                    case SURFACE_FILL: // 填充满控件模式
                        if (shouldBeWider) {
                            // not high enough, fix height；宽度比较大，固定高度，缩放宽度
                            height = heightSpecSize;
                            width = (int) (height * displayAspectRatio);
                        } else {
                            // not wide enough, fix width；高度比较大，固定宽度，缩放高度
                            width = widthSpecSize;
                            height = (int) (width / displayAspectRatio);
                        }
                        break;
                    case SURFACE_ORIGINAL:
                    default:
                        if (shouldBeWider) {
                            // too wide, fix width；和第一个类似，这里取 (mVideoWidth, widthSpecSize) 最小的值
                            width = Math.min(mVideoWidth, widthSpecSize);
                            height = (int) (width / displayAspectRatio);
                        } else {
                            // too high, fix height
                            height = Math.min(mVideoHeight, heightSpecSize);
                            width = (int) (height * displayAspectRatio);
                        }
                        break;
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View
                    .MeasureSpec.EXACTLY) {
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;

                // for compatibility, we adjust size based on aspect ratio
                // 这里做的是缩小某一边的大小以达到和视频原始尺寸的比例
                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints，不让高度超出测量高度
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints，不让宽度超出测量宽度
                    width = widthSpecSize;
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }

        setMeasuredDimension(width, height);

    }
}
