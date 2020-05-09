package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.graphics.Bitmap;
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

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.manager.IPTVConfig;
import com.dfsoft.iptvplayer.manager.IPTVEpgData;
import com.dfsoft.iptvplayer.player.IPTVPlayer_HUD;
import com.dfsoft.iptvplayer.utils.ImageCache;

public class PlayerHUDView extends FrameLayout {

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
    private ProgressBar hud_current_program_percent;


    private TextView hud_source_text;

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
        inflate(context, R.layout.layout_hud,this);
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
        } else {
            channel.epg.getCurrentTimer();
            if (channel.epg.curTime != -1) {
                IPTVEpgData data = channel.epg.data.get(channel.epg.curTime);
                curTime = data.starttime;
                curEPG = data.name;
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

        String tmp = String.valueOf(channel.playIndex+1) + " / " + String.valueOf(channel.source.size());
        hud_source_text.setText(tmp);


        mWeatherView.updateWeather();
//        this.hideVideoHud();
    }

    private boolean mVisible = false;
    public void show() {

    }

    public void hide() {

    }

    public void toggle() {

    }

    private void hideVideoHud() {
        hud_video_type.setVisibility(View.GONE);
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
    }
}
