package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.dfsoft.iptvplayer.R;

public class InformationView extends FrameLayout {

    private TextView mInfo = null;

    public InformationView(@NonNull Context context) {
        this(context,null);
    }

    public InformationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public InformationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            return;
        }

        inflate(context, R.layout.layout_information,this);

        mInfo = findViewById(R.id.info_text);
    }


    public void updateInfo(String info) {
        mInfo.setTextColor(getContext().getResources().getColor(R.color.white));
        mInfo.setTextSize(20);
        mInfo.setText(info);
    }

    public void updateChannelNum(String info) {
        mInfo.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
        mInfo.setTextSize(40);
        mInfo.setText(info);
    }


}
