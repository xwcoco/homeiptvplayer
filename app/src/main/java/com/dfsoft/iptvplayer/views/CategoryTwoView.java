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

public class CategoryTwoView extends CategoryView {
    public CategoryTwoView(@NonNull Context context) {
        this(context,null);
    }

    public CategoryTwoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    protected MarqueeTextView cate_two_category_name;


    protected IPTVCategory mShowCategory = null;

    @Override
    protected void initLayer() {
        inflate(mContext, R.layout.layout_category_two,this);

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

    protected void initKeyListener() {
        mChannelList.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) return true;

                LogUtils.i("CategoryTwoView","keyCode = "+keyCode);
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
                            config.iptvMessage.sendMessage(IPTVMessage.IPTV_CHANNEL_PLAY,channel);
                        }

                    }
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_MENU) {
                    config.iptvMessage.sendMessage(IPTVMessage.IPTV_SWITCH_CATEGORY);
                    return true;
                }

//                if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_UP) {
//                    ChannelAdapter adapter = (ChannelAdapter) mChannelList.getAdapter();
//                    if (adapter != null) {
//                        int index = adapter.getCurrentItem();
//                        if (index == 0) index = adapter.getCount() - 1;
//                        else
//                            index = index - 1;
//                        mChannelList.setSelection(index);
//                        activeChannel(index);
////                        mChannelList.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
//                    }
//                }

                if (keyCode == KeyEvent.KEYCODE_ESCAPE || keyCode == KeyEvent.KEYCODE_DEL || keyCode == KeyEvent.KEYCODE_BACK ) {
                    hide();
                    return true;
                }
                return false;

            }
        });

    }

    @Override
    protected void showCurrentChannel() {
        IPTVChannel channel = config.getPlayingChannal();
        if (channel == null) return;
        IPTVCategory cate = this.getLastCategory();
        if (cate == null) return;


        showCategoryChannel(cate);
        mChannelList.requestFocus();

    }


//    @Override
//    protected void afterActiveChannel(IPTVChannel channel) {
//        if (mode == 1)
//            super.afterActiveChannel(channel);
//        else {
//            mEpgTimeList.afterOnActiveChannel(channel,mChannelList);
//        }
//
//    }

    protected void showCategoryChannel(IPTVCategory category) {
        if (category == null) return;

        if (category.channelAdapter == null) {
            category.channelAdapter = new ChannelAdapter(this.mContext,category);
        }
        mShowCategory = category;
        mChannelList.setAdapter(category.channelAdapter);
        cate_two_category_name.setText(category.name);
//        category.channelAdapter.notifyDataSetChanged();
        int cIndex = category.channelAdapter.getCurrentItem();
        this.activeChannel(cIndex);
        int h = mChannelList.getMeasuredHeight() / 2;
        mChannelList.setSelectionFromTop(cIndex,h);

//        mEpgTimeList.showCategoryEPG(mShowCategory);

//        EpgTimeListAdapter adapter = new EpgTimeListAdapter(mContext,category);
//        mEpgTimeList.setAdapter(adapter);
    }

//    @Override
//    protected void onEPGLoaded(IPTVChannel channel) {
//        if (mode == 1) {
//            super.onEPGLoaded(channel);
//            return;
//        }
//
//        mEpgTimeList.doEPGLoaded(channel);
//
//    }

//    private void doShowEpgListTime() {
//        mEpgTimeList.showCategoryEPG(mShowCategory,mEpgTimeListWidth);
//    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        if (mEpgTimeListWidth == 0) {
//            int width = mEpgTimeList.getWidth();
//            if (width != 0) {
//                mEpgTimeListWidth = width;
//                doShowEpgListTime();
//            }
//        }
//        LogUtils.i("CategoryTwoView","on Measure width = "+mEpgTimeList.getWidth());
//    }
}
