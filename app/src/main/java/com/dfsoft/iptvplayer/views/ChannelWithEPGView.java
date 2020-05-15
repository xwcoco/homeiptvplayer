package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.widget.LinearLayout;

import com.dfsoft.iptvplayer.manager.IPTVChannel;

public class ChannelWithEPGView extends LinearLayout {

    private Context mContext;

    private IPTVChannel mChannel;

    public ChannelWithEPGView(Context context,IPTVChannel channel) {
        super(context);
        mContext = context;
        this.mChannel = channel;
    }




}
