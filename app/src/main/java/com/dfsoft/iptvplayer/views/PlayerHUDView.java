package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.manager.IPTVConfig;
import com.dfsoft.iptvplayer.manager.IPTVEpgData;
import com.dfsoft.iptvplayer.manager.settings.IptvSettings;
import com.dfsoft.iptvplayer.player.IPTVPlayerManager;
import com.dfsoft.iptvplayer.player.IPTVPlayer_HUD;
import com.dfsoft.iptvplayer.utils.AutoHideInterface;
import com.dfsoft.iptvplayer.utils.ImageCache;

public class PlayerHUDView extends FrameLayout implements AutoHideInterface {

    private final String TAG = "PlayerHUDView";

    private MarqueeTextView hud_channel_num;
    private MarqueeTextView hud_channel_name;
    private TextView hud_current_program_time;
    private MarqueeTextView hud_current_program_name;
    private TextView hud_next_program_time;
    private MarqueeTextView hud_next_program_name;
    private ImageView hud_channel_image;
    private WeatherView mWeatherView;

    private ImageView hud_video_type;
    private ImageView hud_video_player;
    private ProgressBar hud_current_program_percent;

    private ImageView hud_video_hw;

    private ImageView hud_current_program_desc;


    private TextView hud_source_text;

    private TextView hud_net_speed;

    public PlayerHUDView(@NonNull Context context) {
        super(context);
        if (isInEditMode()) {
            return;
        }

        setupLayout(context);
    }

    public PlayerHUDView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }

        setupLayout(context);
    }

    public PlayerHUDView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }

        setupLayout(context);
    }

    private void setupLayout(@NonNull Context context) {
        inflate(context, R.layout.layout_hud, this);
        hud_channel_num = findViewById(R.id.hud_channel_number);
        hud_channel_name = findViewById(R.id.hud_channel_name);
        hud_current_program_time = findViewById(R.id.hud_current_program_time);
        hud_current_program_name = findViewById(R.id.hud_current_program_name);
        hud_next_program_time = findViewById(R.id.hud_next_program_time);
        hud_next_program_name = findViewById(R.id.hud_next_program_name);

        hud_channel_image = findViewById(R.id.hud_channel_image);

        hud_source_text = findViewById(R.id.hud_source_text);

        hud_video_type = findViewById(R.id.hud_video_type);

        mWeatherView = findViewById(R.id.hud_weather_view);

        hud_current_program_percent = findViewById(R.id.hud_current_program_percent);

        hud_video_player = findViewById(R.id.hud_video_player);
        hud_video_hw = findViewById(R.id.hud_video_hw);

        hud_net_speed = findViewById(R.id.hud_net_speed);

        hud_current_program_desc = findViewById(R.id.hud_current_program_desc);
    }

    private IPTVConfig config = IPTVConfig.getInstance();

    private ImageCache cache = ImageCache.getInstance();

    public void updateHud() {
        IPTVChannel channel = config.getPlayingChannal();
        if (channel == null) return;
        hud_channel_num.setText(String.valueOf(channel.num));
        hud_channel_name.setText(channel.name);

        Bitmap image = cache.get(channel.name);
        if (image != null) {
            hud_channel_image.setImageBitmap(image);
        } else {
            hud_channel_image.setImageDrawable(null);
        }

        String curEPG = "";
        String nextEPG = "";

        String curTime = "";
        String nextTime = "";

        if (channel.epg.isEmpty()) {
            Log.d(TAG, "showPlayingChannelConsole: empty epg");
            channel.loadEPGData();
            hud_current_program_percent.setProgress(0);
            hud_current_program_desc.setVisibility(GONE);

        } else {
            channel.epg.getCurrentTimer();
            if (channel.epg.curTime != -1) {
                IPTVEpgData data = channel.epg.data.get(channel.epg.curTime);
                curTime = data.starttime;
                curEPG = data.name;
                if (!data.desc.equals("")) {
                    hud_current_program_desc.setVisibility(VISIBLE);
                } else
                    hud_current_program_desc.setVisibility(GONE);

                if (channel.epg.curTime + 1 < channel.epg.data.size()) {
                    data = channel.epg.data.get(channel.epg.curTime + 1);
                    nextTime = data.starttime;
                    nextEPG = data.name;
                }
            }
            int percent = channel.epg.getCurrenPercent();
            hud_current_program_percent.setProgress(percent);
        }
        hud_current_program_time.setText(curTime);
        hud_current_program_name.setText(curEPG);
        hud_next_program_time.setText(nextTime);
        hud_next_program_name.setText(nextEPG);

        String tmp = String.valueOf(channel.playIndex + 1) + " / " + String.valueOf(channel.source.size());
        hud_source_text.setText(tmp);

        int playerId = IPTVConfig.getInstance().settings.getSettingValue(IptvSettings.IPTV_SETTING_TAG_PLAYER);
        switch (playerId) {
            case 0:
                hud_video_player.setImageResource(R.mipmap.icon_vlc);
                break;
            case 1:
                hud_video_player.setImageResource(R.mipmap.icon_ijk);
                break;
            default:
                hud_video_player.setImageResource(R.mipmap.icon_exoplayer);
        }


        mWeatherView.updateWeather();

        showNetSpeed();
//        this.hideVideoHud();
    }

    private boolean mVisible = false;

    public void show() {
        mHander.postDelayed(mShowSpeedRun,1000);
    }

    public void hide() {
        mHander.removeCallbacks(mShowSpeedRun);
    }

    public void toggle() {

    }

    private void hideVideoHud() {
        hud_video_type.setVisibility(View.GONE);
        hud_video_hw.setVisibility(View.GONE);
    }

    public void updateVideoWidthAndHeight(IPTVPlayer_HUD hud) {
        this.hideVideoHud();

        if (hud.width * hud.height != 0) {
            int resid = -1;
            if (hud.width == 1920 || hud.height == 1080) {
                resid = R.mipmap.infobar_1080p;
            } else if (hud.width == 1280 || hud.height == 720) {
                resid = R.mipmap.infobar_720p;
            } else if (hud.width == 3840 || hud.height == 2160) {
                resid = R.mipmap.infobar_uhd;
            } else if (hud.width == 720 || hud.height == 576) {
                resid = R.mipmap.infobar_sd;
            }
            if (resid != -1) {
                hud_video_type.setVisibility(VISIBLE);
                hud_video_type.setImageResource(resid);
            }
        }

        if (this.checkHWDecoder(hud)) {
            hud_video_hw.setVisibility(VISIBLE);
        }


    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    private boolean checkHWDecoder(IPTVPlayer_HUD hud) {
        int v = config.settings.getSettingValue(IptvSettings.IPTV_SETTING_TAG_HARDWARE);
        if (v == 2) return false;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaFormat mf = MediaFormat.createVideoFormat(hud.codec, hud.width, hud.height);
            MediaCodecList mcl = new MediaCodecList(MediaCodecList.ALL_CODECS);
            String codecName = mcl.findDecoderForFormat(mf);
            if (codecName == null) return false;
            if (codecName.startsWith("OMX.google")) return false;
            if (codecName.startsWith("OMX.")) return true;
        }
        return false;
    }

    private Handler mHander = new Handler();

    public IPTVPlayerManager playerManager = null;

    private void showNetSpeed() {
        int speed = playerManager.getNetSpeed();
        String tmp = speed + " KB/S";
        hud_net_speed.setText(tmp);
    }

    private Runnable mShowSpeedRun = new Runnable() {
        @Override
        public void run() {
            showNetSpeed();
            mHander.removeCallbacks(mShowSpeedRun);
            mHander.postDelayed(mShowSpeedRun,1000);
        }
    };
}
