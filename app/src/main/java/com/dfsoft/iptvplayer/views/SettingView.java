package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.manager.IPTVConfig;
import com.dfsoft.iptvplayer.manager.IPTVMessage;
import com.dfsoft.iptvplayer.manager.settings.IptvSettingItem;
import com.dfsoft.iptvplayer.manager.settings.IptvSettings;

public class SettingView extends FrameLayout {

    private Context mContext;

    private ListView mSettingList;
    private ListView mOptions;

    public SettingView(@NonNull Context context) {
        this(context,null);
    }

    public SettingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) return;

        mContext = context;

        LayoutInflater.from(context).inflate(R.layout.layout_setting, this);

        mSettingList = findViewById(R.id.setting_list);
        mOptions = findViewById(R.id.setting_options);

        mSettingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activeSettingItem(position);
            }
        });

        mSettingList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activeSettingItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSettingList.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ESCAPE || keyCode == KeyEvent.KEYCODE_DEL  || keyCode == KeyEvent.KEYCODE_BACK ) {
                    hide();
                    return true;
                }
                return false;
            }
        });

        mOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SettingItemAdapter adapter = (SettingItemAdapter) mOptions.getAdapter();
                adapter.setSelectedPosotion(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mOptions.setOnKeyListener(mKeyListener);

    }


    public void show() {
        SettingAdapter adapter = (SettingAdapter) mSettingList.getAdapter();
        if (adapter == null) {
            adapter = new SettingAdapter(mContext,config.settings);
            mSettingList.setAdapter(adapter);
        } else
            adapter.notifyDataSetChanged();

        mSettingList.setSelection(0);



        this.setVisibility(VISIBLE);
        this.requestFocus();


    }

    private void activeSettingItem(int index) {
        IptvSettingItem item = config.settings.settings.get(index);
        if (item.adapter == null) {
            item.adapter = new SettingItemAdapter(mContext,item);
        }
        mOptions.setAdapter(item.adapter);
    }

    private IPTVConfig config = IPTVConfig.getInstance();
    public void hide() {
        setVisibility(GONE);

        config.iptvMessage.sendMessage(IPTVMessage.IPTV_FULLSCREEN);

    }

    private OnKeyListener mKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                SettingItemAdapter adapter = (SettingItemAdapter) mOptions.getAdapter();
                if (adapter != null) {
                    IptvSettingItem item = adapter.getSettingItem();
                    int value = adapter.getSelectedPosotion();
                    item.setValue(value);
                    adapter.notifyDataSetChanged();
                    item.apply();

                }

//
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_ESCAPE || keyCode == KeyEvent.KEYCODE_DEL || keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
                hide();
                return true;
            }
            return false;
        }
    };

    public void afterApplySetting() {
        this.mOptions.requestFocus();
    }

}
