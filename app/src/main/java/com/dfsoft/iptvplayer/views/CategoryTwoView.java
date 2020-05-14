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

    MarqueeTextView cate_two_category_name;

    private EPGTimeListView mEpgTimeList;

    private IPTVCategory mShowCategory = null;

    @Override
    protected void initLayer() {
        inflate(mContext, R.layout.layout_category_two,this);

        cate_two_category_name = findViewById(R.id.cate_two_category_name);
        mChannelList = findViewById(R.id.cate_two_channel_list);
        mEpgList = findViewById(R.id.cate_two_epg_list);

        mEpgTimeList = findViewById(R.id.cate_two_epg_time);

        mChannelList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activeChannel(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

                if (keyCode == KeyEvent.KEYCODE_ESCAPE || keyCode == KeyEvent.KEYCODE_DEL || keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
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
//        int index = cate.data.indexOf(channel);
//        this.activeChannel(index);
//        int h = mChannelList.getMeasuredHeight() / 2;
//        mChannelList.setSelectionFromTop(index,h);
//        mChannelList.setFocusable(true);
        mChannelList.requestFocus();

    }

    private void showCategoryChannel(IPTVCategory category) {
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

        mEpgTimeList.showCategoryEPG(category);

//        EpgTimeListAdapter adapter = new EpgTimeListAdapter(mContext,category);
//        mEpgTimeList.setAdapter(adapter);
    }

}
