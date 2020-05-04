package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.manager.IPTVConfig;
import com.dfsoft.iptvplayer.manager.IPTVEpgData;
import com.dfsoft.iptvplayer.player.IPTVPlayer_HUD;

public class PlayerHUDView extends FrameLayout {

    private final String TAG = "PlayerHUDView";

    private MarqueeTextView hud_channel_num;
    private MarqueeTextView hud_channel_name;
    private TextView hud_current_program_time;
    private MarqueeTextView hud_current_program_name;
    private TextView hud_next_program_time;
    private MarqueeTextView hud_next_program_name;


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

        hud_source_text = findViewById(R.id.hud_source_text);
    }

    private IPTVConfig config = IPTVConfig.getInstance();
    public void updateHud() {
        IPTVChannel channel = config.getPlayingChannal();
        if (channel == null) return;
        hud_channel_num.setText(String.valueOf(channel.num));
        hud_channel_name.setText(channel.name);

        String curEPG = "";
        String nextEPG = "";

        String curTime = "";
        String nextTime = "";

        if (channel.epg.isEmpty()) {
            Log.d(TAG, "showPlayingChannelConsole: empty epg");
            channel.loadEPGData();
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
        }

        hud_current_program_time.setText(curTime);
        hud_current_program_name.setText(curEPG);
        hud_next_program_time.setText(nextTime);
        hud_next_program_name.setText(nextEPG);

        String tmp = String.valueOf(channel.playIndex+1) + " / " + String.valueOf(channel.source.size());
        hud_source_text.setText(tmp);
    }

    public void updateHUD(IPTVPlayer_HUD hud) {

    }
}
