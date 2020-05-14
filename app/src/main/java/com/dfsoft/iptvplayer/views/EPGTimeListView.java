package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVCategory;

public class EPGTimeListView extends ConstraintLayout {

    private Context mContext;

    public EPGTimeListView(@NonNull Context context) {
        this(context,null);
    }

    public EPGTimeListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    private ListView mEpgList;
    private EPGTimeHeader mHeader;

    public EPGTimeListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        inflate(context, R.layout.layout_epg_list,this);

        if (isInEditMode()) return;

        mEpgList = findViewById(R.id.epg_list_list);
        mHeader = findViewById(R.id.epg_list_header);


//        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mHeader.getLayoutParams();
//        lp.bottomToBottom =
    }

    public void showCategoryEPG(IPTVCategory category) {
        EpgTimeListAdapter adapter = new EpgTimeListAdapter(mContext,category);
        mEpgList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mHeader.showTime(12);
        adapter.showTime(12);
    }
}
