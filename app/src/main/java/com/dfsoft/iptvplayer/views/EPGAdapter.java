package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.manager.IPTVConfig;
import com.dfsoft.iptvplayer.manager.IPTVEpgData;

public class EPGAdapter extends BaseAdapter {
    private final String TAG = "EPGAdapter";
    private IPTVConfig config = IPTVConfig.getInstance();

    public IPTVChannel channel = null;

    private Context mContext;

    private int currentItem = 0;

    public EPGAdapter(Context context,IPTVChannel channel) {
        mContext = context;
        this.channel = channel;
    }

    @Override
    public int getCount() {
        if (channel.epg.isEmpty())
            return 0;
        return channel.epg.data.size();
    }

    @Override
    public Object getItem(int position) {
        return channel.epg.data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EPGAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_epg_item, null);
            holder = new EPGAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (EPGAdapter.ViewHolder) convertView.getTag();
        }
        //设置文字 内容
        IPTVEpgData data = channel.epg.data.get(position);
        holder.mTextView.setText(data.name);
        holder.mTimeView.setText(data.starttime);

        channel.epg.getCurrentTimer();

//        this.getCurrentTimer();

        if (channel.epg.curTime == position) {
            holder.mLottieAnimationView.setVisibility(View.VISIBLE);
            holder.mLottieAnimationView.playAnimation();
        } else {
            holder.mLottieAnimationView.setVisibility(View.GONE);
        }

//        if (currentItem == position) {
//            //如果被点击，设置当前TextView被选中
//            holder.mTextView.setBackgroundColor(mContext.getResources().getColor(R.color.lightblue));
////            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
//            holder.mTextView.setSelected(true);
//
//            holder.mTimeView.setBackgroundColor(mContext.getResources().getColor(R.color.lightblue));
//            holder.mTimeView.setSelected(true);
//
//        } else {
//            //如果没有被点击，设置当前TextView未被选中
//            holder.mTextView.setSelected(false);
////            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.white));
//            holder.mTextView.setBackgroundColor(mContext.getResources().getColor(R.color.none));
//            holder.mTimeView.setSelected(false);
//            holder.mTimeView.setBackgroundColor(mContext.getResources().getColor(R.color.none));
//        }

        return convertView;
    }

    class ViewHolder {
        TextView mTextView;
        TextView mTimeView;
        LottieAnimationView mLottieAnimationView;

        public ViewHolder(View convertView) {
            mTextView = (TextView) convertView.findViewById(R.id.tv_epg_name);
            mTimeView = (TextView) convertView.findViewById(R.id.tv_epg_time);
            mLottieAnimationView = (LottieAnimationView)convertView.findViewById(R.id.lottieAnimationView);
        }
    }

}
