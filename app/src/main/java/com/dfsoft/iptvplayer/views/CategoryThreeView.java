package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVCategory;
import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.utils.LogUtils;

import java.util.Calendar;
import java.util.HashMap;

public class CategoryThreeView extends CategoryTwoView {
    private final static String TAG = "CategoryThreeView";

    public CategoryThreeView(@NonNull Context context) {
        this(context, null);
    }

    public CategoryThreeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    EPGTimeHeader mHeader;
    LinearLayout mTimeLine;

    @Override
    protected void initLayer() {
        inflate(mContext, R.layout.layout_category_three, this);

        cate_two_category_name = findViewById(R.id.cate_three_category_name);
        mChannelList = findViewById(R.id.cate_three_channel_list);

        mHeader = findViewById(R.id.cate_three_epg_header);
        mTimeLine = findViewById(R.id.cate_three_epg_timeline);

        mChannelList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activeChannel(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initKeyListener();

        mAdapterList = new HashMap<>();
    }

    private HashMap<IPTVCategory, ChannelWithEPGAdapter> mAdapterList;

    @Override
    protected void showCategoryChannel(IPTVCategory category) {
        if (category == null) return;

        mHeader.buildTimeLayout();

        ChannelWithEPGAdapter adapter = mAdapterList.get(category);

        if (adapter == null) {
            adapter = new ChannelWithEPGAdapter(mContext, category);
            mAdapterList.put(category, adapter);
        }

        adapter.startHour = mHeader.startHour;

        setTimeLinePosition();

        mShowCategory = category;
        mChannelList.setAdapter(adapter);
        cate_two_category_name.setText(category.name);
//        category.channelAdapter.notifyDataSetChanged();
        int cIndex = adapter.getCurrentItem();
        this.activeChannel(cIndex);
        int h = mChannelList.getMeasuredHeight() / 2;
        mChannelList.setSelectionFromTop(cIndex, h);
    }

    @Override
    public void activeChannel(int index) {
        ChannelWithEPGAdapter adapter = (ChannelWithEPGAdapter) mChannelList.getAdapter();
        if (adapter == null) return;

        adapter.setCurrentItem(index);

        IPTVChannel channel = adapter.getChannel();
        if (channel == null)
            return;
        if (channel.epg.isEmpty()) {
            channel.loadEPGData();
        }

        adapter.notifyDataSetChanged();

//        afterActiveChannel(channel);
    }

    @Override
    protected void onEPGLoaded(IPTVChannel channel) {
        ChannelWithEPGAdapter adapter = (ChannelWithEPGAdapter) mChannelList.getAdapter();
        if (adapter == null) return;
        adapter.onLoadEPG(channel);
    }


    private void setTimeLinePosition() {
        int hour = mHeader.startHour;
        Calendar now = Calendar.getInstance();

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);

        int timeleft = (int) ((now.getTimeInMillis() - startTime.getTimeInMillis()) / 1000 / 60);

        int left = timeleft * 1432 / 120;

//        LogUtils.i(TAG," timeline left = "+left + " timeleft = "+timeleft);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mTimeLine.getLayoutParams();
//        int old = params.getMarginStart();
//        if (old != left) {
        params.setMarginStart(left);
        mTimeLine.setLayoutParams(params);
//            LogUtils.i(TAG," timeline set new margin");
//        }
    }
}
