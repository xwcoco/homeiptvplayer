package com.dfsoft.iptvplayer.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.settings.IptvSettingItem;

public class SettingItemAdapter extends BaseAdapter {

    private Context mContext;
    private IptvSettingItem mSettingItem;

    public SettingItemAdapter(Context mContext, IptvSettingItem mSettingItem) {
        this.mContext = mContext;
        this.mSettingItem = mSettingItem;

        mSettingItem.adapter = this;
    }

    public IptvSettingItem getSettingItem() {
        return mSettingItem;
    }

    private int mSelectedPosotion = 0;

    public int getSelectedPosotion() {
        return mSelectedPosotion;
    }

    public void setSelectedPosotion(int mSelectedPosotion) {
        this.mSelectedPosotion = mSelectedPosotion;
    }

    @Override
    public int getCount() {
        return mSettingItem.options.size();
    }

    @Override
    public Object getItem(int position) {
        return mSettingItem.options.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SettingItemAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_setting_options_item, null);
            holder = new SettingItemAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SettingItemAdapter.ViewHolder) convertView.getTag();
        }
        //设置文字 内容

        String name = mSettingItem.options.get(position);
        int resid = mContext.getResources().getIdentifier(name,"string",mContext.getPackageName());
        if (resid != 0)
            name = mContext.getResources().getString(resid);
        holder.mTextView.setText(name);

        if (mSettingItem.noImage) {
            holder.mImageView.setVisibility(View.GONE);
        } else {
            if (mSettingItem.getValue() == position) {
                holder.mImageView.setVisibility(View.VISIBLE);
            } else
                holder.mImageView.setVisibility(View.GONE);
        }

        if (mSettingItem.noImage) {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) holder.mTextView.getLayoutParams();
            lp.setMarginStart(0);
        }

        if (mSettingItem.centerText) {
            holder.mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

//        IptvSettingItem item = iptvSettings.settings.get(position);
//        String chname = item.name;
//        int resid = getResId(item.name);
//        if (resid != 0)
//            chname = mContext.getResources().getString(resid);
//        holder.mTextView.setText(chname);
//
//        if (currentItem == position) {
//            holder.mTextView.setSelected(true);
//            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
//        } else {
//            holder.mTextView.setSelected(false);
//            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.white));
//        }

        return convertView;
    }

    static class ViewHolder {
        TextView mTextView;
        ImageView mImageView;

        ViewHolder(View convertView) {
            mTextView = (TextView) convertView.findViewById(R.id.setting_options_text);
            mImageView = convertView.findViewById(R.id.setting_options_image);
        }
    }
}
