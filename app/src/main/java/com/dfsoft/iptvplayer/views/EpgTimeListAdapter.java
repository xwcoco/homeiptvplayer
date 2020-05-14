package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVCategory;

public class EpgTimeListAdapter extends BaseAdapter {

    Context mContext;
    IPTVCategory category;

    public EpgTimeListAdapter(Context mContext,@NonNull IPTVCategory category) {
        this.mContext = mContext;
        this.category = category;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
//            if (position == 0) {
//                if (mHeader == null) {
//                    mHeader = new EPGTimeHeader(mContext);
//                }
//                convertView = mHeader;
//            } else {
            EPGTimeDetailView view = new EPGTimeDetailView(mContext);
            view.showEPG(category.data.get(position));
            convertView = view;
//            }
        }
        return convertView;
    }
}
