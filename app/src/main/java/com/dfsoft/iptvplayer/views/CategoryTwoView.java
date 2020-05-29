package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVCategory;
import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.manager.IPTVMessage;
import com.dfsoft.iptvplayer.utils.LogUtils;

public class CategoryTwoView extends CategoryView implements SubViewKeyEvent {
    public CategoryTwoView(@NonNull Context context) {
        this(context, null);
    }

    public CategoryTwoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    protected MarqueeTextView cate_two_category_name;


    protected IPTVCategory mShowCategory = null;

    @Override
    protected void initLayer() {
        inflate(mContext, R.layout.layout_category_two, this);

        cate_two_category_name = findViewById(R.id.cate_two_category_name);
        mChannelList = findViewById(R.id.cate_two_channel_list);
        mEpgList = findViewById(R.id.cate_two_epg_list);

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

    }

    protected OnKeyListener mOnKeyListener;

    protected void initKeyListener() {

        mOnKeyListener = new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) return true;

                LogUtils.i("CategoryTwoView", "keyCode = " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_LEFT) {
                    IPTVCategory cate = mShowCategory;
                    if (cate == null) return false;
                    int index = config.category.indexOf(cate);
                    if (index == -1) return false;
                    if (index == 0) index = config.category.size() - 1;
                    else
                        index = index - 1;
                    showCategoryChannel(config.category.get(index));

                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_RIGHT) {
                    IPTVCategory cate = mShowCategory;
                    if (cate == null) return false;
                    int index = config.category.indexOf(cate);
                    if (index == -1) return false;
                    if (index == config.category.size() - 1) index = 0;
                    else
                        index = index + 1;
                    showCategoryChannel(config.category.get(index));
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    hide();
                    ChannelAdapter adapter = (ChannelAdapter) mChannelList.getAdapter();
                    if (adapter != null) {
                        IPTVChannel playingChanel = config.getPlayingChannal();
                        IPTVChannel channel = adapter.getChannel();
                        if (channel != playingChanel) {
                            mLastCategory = adapter.getCategory();
                            config.iptvMessage.sendMessage(IPTVMessage.IPTV_CHANNEL_PLAY, channel);
                        }

                    }
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_MENU) {
                    config.iptvMessage.sendMessage(IPTVMessage.IPTV_SWITCH_CATEGORY);
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_ESCAPE || keyCode == KeyEvent.KEYCODE_DEL || keyCode == KeyEvent.KEYCODE_BACK) {
                    hide();
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_UP) {
                    return moveChannelDelta(-1);
//                    return true;
                }
                if (keyCode ==  KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_DOWN) {
                    return moveChannelDelta(1);
//                    return true;
                }
                return false;

            }
        };

        mChannelList.setOnKeyListener(mOnKeyListener);
        cate_two_category_name.setOnKeyListener(mOnKeyListener);
//        mEpgList.setOnKeyListener(mOnKeyListener);

    }

    @Override
    protected void showCurrentChannel() {
        IPTVChannel channel = config.getPlayingChannal();
        if (channel == null) return;
        IPTVCategory cate = this.getLastCategory();
        if (cate == null) return;


        showCategoryChannel(cate);

//        if (cate.data.size() == 0)
//            cate_two_category_name.requestFocus();
//        else
        mChannelList.requestFocus();

    }


    protected void showCategoryChannel(IPTVCategory category) {
        if (category == null) return;

        if (category.channelAdapter == null) {
            category.channelAdapter = new ChannelAdapter(this.mContext, category);
        }
        mShowCategory = category;
        category.channelAdapter.setListViewIsFocused(true);
        mChannelList.setAdapter(category.channelAdapter);
        cate_two_category_name.setText(category.name);
        int cIndex = category.channelAdapter.getCurrentItem();
        if (cIndex == -1) return;
        this.activeChannel(cIndex);
        int h = mChannelList.getMeasuredHeight() / 2;
        mChannelList.setSelectionFromTop(cIndex, h);

    }

    @Override
    public void onKey(int keyCode, KeyEvent event) {
        LogUtils.i("SubViewKeyEvent", "onKeyDown : interface");
        mOnKeyListener.onKey(this, keyCode, event);
    }

    @Override
    protected void afterActiveChannel(IPTVChannel channel) {
        super.afterActiveChannel(channel);
        mChannelList.requestFocus();
    }
}
