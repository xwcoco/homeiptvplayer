package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVConfig;
import com.dfsoft.iptvplayer.manager.IPTVMessage;
import com.dfsoft.iptvplayer.manager.settings.IptvSettingItem;

public class QuitView extends FrameLayout {
    public QuitView(@NonNull Context context) {
        this(context,null);
    }

    public QuitView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    private IptvSettingItem mItem = null;
    private ListView mListView;

    public QuitView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) return;

        inflate(context, R.layout.layout_quit,this);

        mItem = new IptvSettingItem();
        mItem.name = "quit";
        mItem.options.add(context.getResources().getString(R.string.quit_ok));
        mItem.options.add(context.getResources().getString(R.string.Canel));

        mItem.noSetValue = true;
        mItem.noImage = true;
        mItem.adapter = new SettingItemAdapter(context,mItem);

        mListView = findViewById(R.id.quit_list);
        mListView.setAdapter(mItem.adapter);

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mItem.adapter.setSelectedPosotion(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private IPTVConfig config = IPTVConfig.getInstance();

    public void show() {
//        setVisibility(VISIBLE);
        mListView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_ESCAPE) {
                    hide();
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (mItem.adapter.getSelectedPosotion() == 1) {
                        hide();
                        return true;
                    }

                    config.iptvMessage.sendMessage(IPTVMessage.IPTV_QUIT);
                }
                return false;
            }
        });
        mListView.setSelection(0);
        mListView.requestFocus();
    }

    public void hide() {
//        setVisibility(GONE);
        mListView.setOnKeyListener(null);
        config.iptvMessage.sendMessage(IPTVMessage.IPTV_QUIT_QUITASK);
    }


}
