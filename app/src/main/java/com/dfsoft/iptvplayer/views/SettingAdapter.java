package com.dfsoft.iptvplayer.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.settings.IptvSettingItem;
import com.dfsoft.iptvplayer.manager.settings.IptvSettings;

public class SettingAdapter extends BaseAdapter {
    private IptvSettings iptvSettings = null;
    private Context mContext;

    public SettingAdapter(@NonNull Context context, @NonNull IptvSettings settings) {
        mContext = context;
        this.iptvSettings = settings;
    }

    @Override
    public int getCount() {
        return iptvSettings.settings.size();
    }

    @Override
    public Object getItem(int position) {
        return iptvSettings.settings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int currentItem = 0;

    private int getResId(String resName) {
        Resources res = mContext.getResources();
        return res.getIdentifier(resName,"string",mContext.getPackageName());
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SettingAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_cate_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SettingAdapter.ViewHolder) convertView.getTag();
        }
        //设置文字 内容
        IptvSettingItem item = iptvSettings.settings.get(position);
        String chname = item.name;
        int resid = getResId(item.name);
        if (resid != 0)
            chname = mContext.getResources().getString(resid);
        holder.mTextView.setText(chname);

        if (currentItem == position) {
            holder.mTextView.setSelected(true);
            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        } else {
            holder.mTextView.setSelected(false);
            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.white));
        }

        return convertView;

    }

    static class ViewHolder {
        TextView mTextView;

        ViewHolder(View convertView) {
            mTextView = (TextView) convertView.findViewById(R.id.cate_item_id);
        }
    }
}
