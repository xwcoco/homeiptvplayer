package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVCategory;
import com.dfsoft.iptvplayer.manager.IPTVChannel;

import java.util.Calendar;

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
    private LinearLayout mTimeLine;

    public EPGTimeListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        inflate(context, R.layout.layout_epg_list,this);

        if (isInEditMode()) return;

        mEpgList = findViewById(R.id.epg_list_list);

        mTimeLine = findViewById(R.id.epg_list_timeline);
        mHeader = findViewById(R.id.epg_list_header);


//        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mHeader.getLayoutParams();
//        lp.bottomToBottom =
    }

    public void showCategoryEPG(IPTVCategory category) {
        EpgTimeListAdapter adapter = new EpgTimeListAdapter(mContext,category,mHeader.startHour);
        mEpgList.setAdapter(adapter);

//        int hour = mHeader.startHour;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (this.getWidth() != 0) {
            int hour = mHeader.startHour;
            Calendar now = Calendar.getInstance();

            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY,hour);
            startTime.set(Calendar.MINUTE,0);
            startTime.set(Calendar.SECOND,0);

            int timeleft = (int) ((now.getTimeInMillis() - startTime.getTimeInMillis()) / 1000 / 60);

            int left =  timeleft  * this.getWidth() / 120;

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mTimeLine.getLayoutParams();
            params.setMarginStart(left);
            mTimeLine.setLayoutParams(params);
        }


    }

    public void afterOnActiveChannel(IPTVChannel channel,ListView channelView) {

        if (channelView.getChildCount() > 0) {
            int postion = channelView.getFirstVisiblePosition();
            int top = channelView.getChildAt(0).getTop();
            mEpgList.setSelectionFromTop(postion,top);
        }

        showChannelCurrentEPG(channel);
    }

    public void showChannelCurrentEPG(IPTVChannel channel) {
        EpgTimeListAdapter adapter = (EpgTimeListAdapter) mEpgList.getAdapter();
        if (adapter == null) return;
        adapter.curHightLightChannel = channel;
        adapter.notifyDataSetChanged();
    }

    public void doEPGLoaded(IPTVChannel channel) {
        EpgTimeListAdapter adapter = (EpgTimeListAdapter) mEpgList.getAdapter();
        if (adapter == null) return;
        adapter.onEPGLoaded(channel);
        adapter.notifyDataSetChanged();
    }
}
