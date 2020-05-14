package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVCategory;

public class EPGTimeListView extends FrameLayout {

    private Context mContext;

    public EPGTimeListView(@NonNull Context context) {
        this(context,null);
    }

    public EPGTimeListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    private ListView mEpgList;

    public EPGTimeListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        inflate(context, R.layout.layout_epg_list,this);

        if (isInEditMode()) return;

        mEpgList = findViewById(R.id.epg_list_list);
    }

    public void showCategoryEPG(IPTVCategory category) {
        EpgTimeListAdapter adapter = new EpgTimeListAdapter(mContext,category);
        mEpgList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
