package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.manager.IPTVEpgData;
import com.dfsoft.iptvplayer.utils.LogUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class EPGTimeDetailView extends LinearLayout {
    private Context mContext;

    public IPTVChannel channel = null;

    private int startHour = 0;

    private int lastHightlightIndex = -1;


    public EPGTimeDetailView(Context context,@NonNull IPTVChannel channel,int startHour) {
        super(context);
        mContext = context;
        this.channel = channel;
        this.startHour = startHour;
        showEPG();
    }

    public void hightLightEPG() {
        unHightLightEPG();
        if (epgData == null) return;
        int index = channel.epg.getEPGTimeIndex(epgData);
        if (index == -1) return;
        setHightLightColor(index,R.drawable.epg_timelist_highlight);
        lastHightlightIndex = index;
    }

    public void unHightLightEPG() {
        if (this.lastHightlightIndex != -1) {
            setHightLightColor(lastHightlightIndex,R.drawable.epg_timelist_bg);
            lastHightlightIndex= -1;
        }
    }

    private void setHightLightColor(int index,int color) {
        if (epgData == null) return;
        if (index < 0 || index >= epgData.size()) return;
        LinearLayout layout = (LinearLayout) this.getChildAt(index);
        if (layout == null) return;
        layout.setBackground(mContext.getResources().getDrawable(color));

    }

    private ArrayList<IPTVEpgData> epgData = null;

    private void addEmptyItem() {
        int height = (int) mContext.getResources().getDimension(R.dimen.cate_channel_item_height);
        LinearLayout layout = new LinearLayout(mContext);
        layout.setGravity(Gravity.CENTER);
        layout.setBackground(mContext.getResources().getDrawable(R.drawable.epg_timelist_bg));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        addView(layout,layoutParams);
    }

    public void showEPG() {
        if (channel.epg.isEmpty()) {
            channel.loadEPGData();
            addEmptyItem();
            return;
        }

        epgData = channel.epg.getDataInHours(startHour,startHour+2);

        this.removeAllViews();

        for (int i = 0; i < epgData.size(); i++) {
            IPTVEpgData epg = epgData.get(i);
            LinearLayout layout = new LinearLayout(mContext);
            layout.setGravity(Gravity.CENTER);
            layout.setBackground(mContext.getResources().getDrawable(R.drawable.epg_timelist_bg));
            LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,epg.minutes);
            addView(layout,layoutParams);

            TextView view = new TextView(mContext);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setText(epg.name);
            view.setTextSize(16);
            view.setTextColor(mContext.getResources().getColor(R.color.white));
            view.setEllipsize(TextUtils.TruncateAt.END);
            view.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            view.setGravity(Gravity.CENTER);
            view.setSingleLine();
            view.setMinHeight((int) mContext.getResources().getDimension(R.dimen.cate_channel_item_height));
            layout.addView(view,lp);

        }
    }

}
