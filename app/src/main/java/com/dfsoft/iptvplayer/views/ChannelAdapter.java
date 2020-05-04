package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVCategory;
import com.dfsoft.iptvplayer.manager.IPTVChannel;

import java.util.List;

public class ChannelAdapter extends BaseAdapter {
    private List<IPTVCategory> mCategory = null;
    private Context mContext;
    private Integer cateIndex = 0;

    private int currentItem = 0;

    public ChannelAdapter(Context context, List<IPTVCategory> mCategory , Integer cateIndex) {
        this.mContext = context;
        this.mCategory = mCategory;
        this.cateIndex = cateIndex;
    }

    public IPTVChannel getChannel() {
        return this.mCategory.get(cateIndex).data.get(currentItem);
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    public int getCurrentItem() {
        return this.currentItem;
    }

    @Override
    public int getCount() {
        return mCategory.get(cateIndex).data.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategory.get(cateIndex).data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChannelAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_channel_item, null);
            holder = new ChannelAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChannelAdapter.ViewHolder) convertView.getTag();
        }
        //设置文字 内容
        IPTVCategory cate = mCategory.get(this.cateIndex);
        IPTVChannel channel = cate.data.get(position);
        String chname = String.valueOf(channel.num) + "  "+channel.name;
        holder.mTextView.setText(chname);

        if (currentItem == position) {

            //如果被点击，设置当前TextView被选中
//            holder.mTextView.setBackgroundColor(mContext.getResources().getColor(R.color.colorBlue));
//            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.lightblue));
            holder.mTextView.setSelected(true);
            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        } else {
            //如果没有被点击，设置当前TextView未被选中
            holder.mTextView.setSelected(false);
            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.white));
//            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.white));
//            holder.mTextView.setBackgroundColor(mContext.getResources().getColor(R.color.none));
        }

        return convertView;
    }

    class ViewHolder {
        TextView mTextView;

        public ViewHolder(View convertView) {
            mTextView = (TextView) convertView.findViewById(R.id.cate_channel_id);
        }
    }

}

