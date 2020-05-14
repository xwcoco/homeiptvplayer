package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVChannel;

public class EPGTimeDetailView extends LinearLayout {
    private Context mContext;
    public EPGTimeDetailView(Context context) {
        super(context);
        mContext = context;
    }

    private int dpPerHour = 200;

    public void showEPG(IPTVChannel channel) {
        if (channel.epg.isEmpty()) return;

        int totalWidth = 0;
        int width = 0;
        for (int i = 0; i < channel.epg.data.size(); i++) {
            TextView view = new TextView(mContext);
            width = (int) Math.ceil(channel.epg.getProgramHours(i) * dpPerHour);
            LayoutParams lp = new LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setText(channel.epg.data.get(i).name);
            view.setTextSize(18);
            view.setTextColor(mContext.getResources().getColor(R.color.white));
            view.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            view.setSingleLine();
            view.setMinHeight(49);
            addView(view,lp);

        }
    }


}
