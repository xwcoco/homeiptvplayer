package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.utils.LogUtils;

public class EPGTimeDetailView extends LinearLayout {
    private Context mContext;

    private LinearLayout mContainer;

    HorizontalScrollView mScrollView;

    public EPGTimeDetailView(Context context) {
        super(context);
        mContext = context;

        inflate(context,R.layout.layout_hscroll,this);

        dpPerHour = (int) mContext.getResources().getDimension(R.dimen.cate_epg_timelist_width_perhour);

        mContainer = findViewById(R.id.hscroll_container);

        mScrollView = findViewById(R.id.hscroll_scroll);


    }

    private int dpPerHour = 200;

    public void showEPG(IPTVChannel channel) {
        if (channel.epg.isEmpty()) {
            channel.loadEPGData();
            return;
        }

        int totalWidth = 0;
        int width = 0;
        for (int i = 0; i < channel.epg.data.size(); i++) {
            width = (int) Math.ceil(channel.epg.getProgramHours(i) * dpPerHour);

            LinearLayout layout = new LinearLayout(mContext);
            layout.setGravity(Gravity.CENTER);
            layout.setBackground(mContext.getResources().getDrawable(R.drawable.epg_timelist_bg));
            LayoutParams layoutParams = new LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
            mContainer.addView(layout,layoutParams);

            TextView view = new TextView(mContext);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setText(channel.epg.data.get(i).name);
            view.setTextSize(16);
            view.setTextColor(mContext.getResources().getColor(R.color.white));
            view.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            view.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            view.setGravity(Gravity.CENTER);
            view.setSingleLine();
            view.setMinHeight((int) mContext.getResources().getDimension(R.dimen.cate_channel_item_height));
            layout.addView(view,lp);

        }
    }

    public void showTime(int time) {
        int hsvWidth = mScrollView.getWidth();
        LogUtils.i("EPGTimeDetailView","scroll width = "+hsvWidth);
        mScrollView.scrollTo(dpPerHour * time,0);

    }

}
