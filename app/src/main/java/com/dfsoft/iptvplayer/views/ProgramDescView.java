package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.manager.IPTVConfig;
import com.dfsoft.iptvplayer.manager.IPTVEpgData;

public class ProgramDescView extends FrameLayout {
    public ProgramDescView(@NonNull Context context) {
        this(context,null);
    }

    public ProgramDescView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }


    TextView mTitle = null;
    TextView mDesc = null;

    public ProgramDescView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_program_desc,this);
        if (isInEditMode()) return;

        mTitle = findViewById(R.id.hud_program_desc_title);
        mDesc = findViewById(R.id.hud_program_desc_desc);

    }

    private IPTVConfig config = IPTVConfig.getInstance();

    public void show() {
        IPTVChannel channel = config.getPlayingChannal();
        if (channel == null) return;
        if (!channel.epg.isEmpty()) {
            if (channel.epg.curTime != -1) {
                IPTVEpgData data = channel.epg.data.get(channel.epg.curTime);
                mTitle.setText(data.name);
                mDesc.setText(data.desc);
            }
        }

    }

    public boolean canShow() {
        IPTVChannel channel = config.getPlayingChannal();
        if (channel == null) return false;
        if (channel.epg.isEmpty()) return false;
        channel.epg.getCurrentTimer();
        if (channel.epg.curTime == -1) return false;
        return !channel.epg.data.get(channel.epg.curTime).desc.isEmpty();

    }

    public void hide() {

    }
}
