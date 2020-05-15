package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVCategory;
import com.dfsoft.iptvplayer.manager.IPTVChannel;

import java.util.HashMap;

public class ChannelWithEPGAdapter extends ChannelAdapter {

    public ChannelWithEPGAdapter(Context mContext, IPTVCategory mCategory) {
        super(mContext,mCategory);
    }

    private HashMap<Integer,EPGTimeDetailView> mDetailViewList = new HashMap<>();

    public int startHour;

    public void onLoadEPG(IPTVChannel channel) {
        EPGTimeDetailView view = getViewByChannel(channel);
        if (view == null) return;
        view.showEPG();
    }

    private EPGTimeDetailView getViewByChannel(IPTVChannel channel) {
        for (int i = 0; i < this.getCount(); i++) {
            EPGTimeDetailView view = mDetailViewList.get(i);
            if (view != null && view.channel == channel) return view;
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EPGViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_epg_timelist_item, null);
            holder = new EPGViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (EPGViewHolder) convertView.getTag();
        }

        EPGTimeDetailView view = mDetailViewList.get(position);
        if (view == null) {
            view = new EPGTimeDetailView(mContext,mCategory.data.get(position),startHour);
            mDetailViewList.put(position,view);
        }

//        if (view.channel == curHightLightChannel) {
//            showChannelCurrentEPG(curHightLightChannel);
//        }

        holder.root.removeAllViews();
        ViewGroup pview = (ViewGroup) view.getParent();

        if (pview != null)
            pview.removeView(view);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        holder.root.addView(view,lp);


        IPTVChannel channel = mCategory.data.get(position);
        String chname = String.valueOf(channel.num) + "  "+channel.name;
        holder.channel_name.setText(chname);

        if (position == getCurrentItem()) {
//            holder.channel_name.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            holder.channel_name.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.channel_name.setBackground(mContext.getResources().getDrawable(R.drawable.item_left_bg));
            view.hightLightEPG();
        } else {
            holder.channel_name.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.channel_name.setBackground(null);
            view.unHightLightEPG();
        }

        return convertView;
    }

    class EPGViewHolder {
        TextView channel_name;
        LinearLayout root;
        public EPGViewHolder(View convertView) {
            channel_name = convertView.findViewById(R.id.epg_timelist_channel);
            root = convertView.findViewById(R.id.epg_timelist_root);
        }
    }
}
