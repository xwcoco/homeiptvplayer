package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dfsoft.iptvplayer.R;

import java.util.Calendar;

public class EPGTimeHeader extends LinearLayout {

    private final static String TAG = "EPGTimeHeader";

    private Context mContext;
    private int parentWidth;
    public EPGTimeHeader(Context context) {
        this(context,null);
    }

    public EPGTimeHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }
//
//
//    private LinearLayout mContainer;
//
//    HorizontalScrollView mScrollView;
//
    public EPGTimeHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        setOrientation(LinearLayout.HORIZONTAL);
        buildTimeLayout();
    }

//    private int dpPerMinute = 200;

    public int startHour = 0;

    public void buildTimeLayout() {
        Calendar n = Calendar.getInstance();
        int hour = n.get(Calendar.HOUR_OF_DAY);
        if (hour + 2 > 24) {
            hour = 22;
        }

        startHour = hour;

        removeAllViews();


        for (int i = 0; i < 4; i++) {
            LinearLayout tmp = new LinearLayout(mContext);
            LinearLayout.LayoutParams lp = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);

            addView(tmp,lp);

            LinearLayout line = new LinearLayout(mContext);
            LayoutParams lineLp = new LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
            line.setBackgroundColor(mContext.getResources().getColor(R.color.black_overlay));
            tmp.addView(line,lineLp);

            TextView textView = new TextView(mContext);
            LayoutParams textlp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setTextColor(mContext.getResources().getColor(R.color.white));
            textView.setTextSize(20);
            String time = "";
            if (i % 2 == 0) {
                time = String.valueOf(hour + i / 2) + ":00";
            } else
                time = String.valueOf(hour + i / 2) + ":30";

            textView.setText(time);
            tmp.addView(textView,textlp);

        }
    }
}
