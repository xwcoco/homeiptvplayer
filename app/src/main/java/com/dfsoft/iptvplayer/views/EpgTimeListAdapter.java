package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.icu.util.Freezable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVCategory;
import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.utils.LogUtils;

import java.util.HashMap;

public class EpgTimeListAdapter extends BaseAdapter {

    private Context mContext;
    private IPTVCategory category;

    private int startHour = 0;


    public EpgTimeListAdapter(Context mContext, @NonNull IPTVCategory category,int startHour) {
        this.mContext = mContext;
        this.category = category;
        this.startHour = startHour;
    }

    EPGTimeHeader mHeader = null;

    @Override
    public int getCount() {
        return category.data.size();
    }

    @Override
    public Object getItem(int position) {
//        return null;
        return category.data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private HashMap<Integer,EPGTimeDetailView> mTimeViewList = new HashMap<>();

    private IPTVChannel lastHighLightChannel = null;

    public IPTVChannel curHightLightChannel = null;

    public void showChannelCurrentEPG(IPTVChannel channel) {
        EPGTimeDetailView view = null;
        if (lastHighLightChannel != null) {
            if (lastHighLightChannel == channel) return;
            view = getViewByChannel(lastHighLightChannel);
            if (view != null)
                view.unHightLightEPG();

        }
        view = getViewByChannel(channel);
        if (view != null)
            view.hightLightEPG();
        lastHighLightChannel = channel;
    }

    private EPGTimeDetailView getViewByChannel(IPTVChannel channel) {
        for (int i = 0; i < this.getCount(); i++) {
            EPGTimeDetailView view = mTimeViewList.get(i);
            if (view != null && view.channel == channel) return view;
        }
        return null;
    }

    public void onEPGLoaded(IPTVChannel channel) {
        EPGTimeDetailView view = getViewByChannel(channel);
        if (view == null) return;
        view.showEPG();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EPGTimeDetailView view = mTimeViewList.get(position);
        if (view == null) {
            view = new EPGTimeDetailView(mContext,category.data.get(position),startHour);
            mTimeViewList.put(position,view);
//            LogUtils.i("EpgTimeListAdapter","create EPGTimeDetailView");
        }

        if (view.channel == curHightLightChannel) {
            showChannelCurrentEPG(curHightLightChannel);
        }

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_epg_timelist_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.root.removeAllViews();
        ViewGroup pview = (ViewGroup) view.getParent();

        if (pview != null)
            pview.removeView(view);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        holder.root.addView(view,lp);

        return convertView;

    }

    class ViewHolder {
        public LinearLayout root;
        public ViewHolder(View convertView) {
            root = convertView.findViewById(R.id.epg_timelist_root);
        }
    }

}
