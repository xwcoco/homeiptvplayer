package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVCategory;
import com.dfsoft.iptvplayer.utils.LogUtils;

import java.util.HashMap;

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

    private HashMap<Integer,EPGTimeDetailView> mViewList = new HashMap();

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
            view.invalidate();
            view.showTime(12);
            mViewList.put(position,view);
//            }
        }
        return convertView;
    }
    
    public void showTime(int time) {

        LogUtils.i("EPGTimeDetailView","lenth = "+this.getCount());
        for (int i = 0; i < this.getCount(); i++) {
            EPGTimeDetailView view = mViewList.get(i);
            LogUtils.i("EPGTimeDetailView","view = "+view);
            if (view != null)
                view.showTime(time);
        }
    }
}
