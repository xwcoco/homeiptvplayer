package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dfsoft.iptvplayer.R;

public class EPGTimeHeader extends LinearLayout {
    private Context mContext;
    public EPGTimeHeader(Context context) {
        this(context,null);
    }

    public EPGTimeHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EPGTimeHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setOrientation(LinearLayout.HORIZONTAL);

        buildLayout();
    }

    private int dpPerHour = 200;

    public void buildLayout() {
        for (int i = 0; i < 24; i++) {
            LinearLayout tmp = new LinearLayout(mContext);
            LinearLayout.LayoutParams lp = new LayoutParams(dpPerHour, ViewGroup.LayoutParams.WRAP_CONTENT);
            addView(tmp,lp);

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
}
