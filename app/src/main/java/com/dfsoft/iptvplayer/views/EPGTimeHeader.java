package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.utils.LogUtils;

public class EPGTimeHeader extends LinearLayout {

    private final static String TAG = "EPGTimeHeader";

    private Context mContext;
    public EPGTimeHeader(Context context) {
        this(context,null);
    }

    public EPGTimeHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }


    private LinearLayout mContainer;

    HorizontalScrollView mScrollView;

    public EPGTimeHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        inflate(context,R.layout.layout_hscroll,this);

        mContainer = findViewById(R.id.hscroll_container);

        mScrollView = findViewById(R.id.hscroll_scroll);

        setOrientation(LinearLayout.HORIZONTAL);
        dpPerHour = (int) mContext.getResources().getDimension(R.dimen.cate_epg_timelist_width_perhour);
        buildLayout();
    }

    private int dpPerHour = 200;

    public void buildLayout() {
        for (int i = 0; i < 24; i++) {
            LinearLayout tmp = new LinearLayout(mContext);
            LinearLayout.LayoutParams lp = new LayoutParams(dpPerHour, ViewGroup.LayoutParams.WRAP_CONTENT);
            mContainer.addView(tmp,lp);

            LinearLayout line = new LinearLayout(mContext);
            LayoutParams lineLp = new LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
            tmp.addView(line,lineLp);

            TextView textView = new TextView(mContext);
            LayoutParams textlp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setTextColor(mContext.getResources().getColor(R.color.white));
            textView.setTextSize(20);
            String time = String.valueOf(i)+":00";
            textView.setText(time);
            tmp.addView(textView,textlp);

        }
    }

    public void showTime(int time) {
        int hsvWidth = mScrollView.getWidth();

        LogUtils.i(TAG,"scroll width = "+hsvWidth);

        mScrollView.smoothScrollTo(dpPerHour * time,0);

    }
}
