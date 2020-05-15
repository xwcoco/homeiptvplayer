package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dfsoft.iptvplayer.manager.IPTVCategory;
import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.manager.IPTVConfig;
import com.dfsoft.iptvplayer.manager.IPTVMessage;
import com.dfsoft.iptvplayer.R;

public class CategoryView extends FrameLayout {

    private final String TAG = "CategoryView";

    private Boolean mVisible = false;

    private CategoryAdapter mCategoryAdapter = null;

    public IPTVConfig config = IPTVConfig.getInstance();

    private ListView mCateList = null;

    protected ListView mChannelList = null;

    protected ListView mEpgList = null;

    protected Context mContext;

    public CategoryView(@NonNull Context context) {
        this(context, null);
    }

    public CategoryView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;

        if (isInEditMode()) return;

        this.initLayer();

    }

    protected void initLayer() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_category, this);

        mCateList = findViewById(R.id.categorylistView);

        mChannelList = findViewById(R.id.category_channel_list);

        mEpgList = findViewById(R.id.category_epg_list);

        mCategoryAdapter = new CategoryAdapter(mContext, this.config.category);

        mCateList.setAdapter(mCategoryAdapter);
        mCateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activeCategory(position);
            }
        });
        mCateList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: " + position);
                activeCategory(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCateList.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU) {
                    config.iptvMessage.sendMessage(IPTVMessage.IPTV_SWITCH_CATEGORY);
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    hide();
                    return true;
                }
//                if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_UP) {
//                    CategoryAdapter adapter = (CategoryAdapter) mCateList.getAdapter();
//                    int index = adapter.getCurrentItem();
//                    if (index == 0)
//                        index = adapter.getCount() - 1;
//                    else
//                        index = index - 1;
//                    mCateList.setSelection(index);
//                    activeCategory(index);
//                    adapter.notifyDataSetChanged();
//                    return true;
//                }

                return false;
            }
        });

        mChannelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activeChannel(position);
            }
        });

        mChannelList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activeChannel(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mCateList.setOnFocusChangeListener(this.mCateFocusChangeListener);

        mChannelList.setOnKeyListener(mKeyListener);

        mEpgList.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU) {
                    config.iptvMessage.sendMessage(IPTVMessage.IPTV_SWITCH_CATEGORY);
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    hide();
                    return true;
                }
                return false;
            }
        });

    }

    public void activeCategory(int index) {
        mCategoryAdapter.setCurrentItem(index);
        IPTVCategory cate = this.config.category.get(index);
        if (cate.channelAdapter == null) {
            cate.channelAdapter = new ChannelAdapter(this.mContext, cate);
        }
        mChannelList.setAdapter(cate.channelAdapter);
        mCategoryAdapter.notifyDataSetChanged();
        int cIndex = cate.channelAdapter.getCurrentItem();
//        this.activeChannel(cIndex);
        int h = mChannelList.getMeasuredHeight() / 2;
        mChannelList.setSelectionFromTop(cIndex, h);
    }

    public void activeChannel(int index) {
        ChannelAdapter adapter = (ChannelAdapter) mChannelList.getAdapter();

        adapter.setCurrentItem(index);

        IPTVChannel channel = adapter.getChannel();
        if (channel == null)
            return;
        if (channel.epg.isEmpty()) {
            channel.loadEPGData();
        }
        if (channel.epgAdapter == null) {
            channel.epgAdapter = new EPGAdapter(this.mContext, channel);
        }

        adapter.notifyDataSetChanged();

        afterActiveChannel(channel);

        mChannelList.requestFocus();
    }

    protected void afterActiveChannel(IPTVChannel channel) {
        mEpgList.setAdapter(channel.epgAdapter);
        setEPGCurrentProgramToCenter(channel);
    }

    protected IPTVCategory mLastCategory = null;

    protected IPTVCategory getLastCategory() {
        if (mLastCategory != null) return mLastCategory;
        IPTVChannel channel = config.getPlayingChannal();
        if (channel == null) return null;
        return config.getCategoryByChannel(channel);
    }

    protected void showCurrentChannel() {
        IPTVChannel channel = config.getPlayingChannal();
        if (channel == null) return;
//        IPTVCategory cate = config.getCategoryByChannel(channel);
        IPTVCategory cate = this.getLastCategory();
        if (cate == null) return;

        int index = config.category.indexOf(cate);
        int h1 = mCateList.getMeasuredHeight() / 2;
        mCateList.setSelectionFromTop(index, h1);
        this.activeCategory(index);
//        index = cate.data.indexOf(channel);
//        this.activeChannel(index);
//        int h = mChannelList.getMeasuredHeight() / 2;
//        mChannelList.setSelectionFromTop(index,h);
////        mChannelList.setFocusable(true);
//        mChannelList.requestFocus();
    }

    public void show() {
//        this.setVisibility(View.VISIBLE);
        config.iptvMessage.addMessageListener(mMessageHandler);
        this.showCurrentChannel();
        this.mVisible = true;
    }

    public void hide() {
//        this.setVisibility(View.GONE);
//        mChannelList.setFocusable(false);
        config.iptvMessage.removeMessageListener(mMessageHandler);
        this.mVisible = false;
        config.iptvMessage.sendMessage(IPTVMessage.IPTV_QUIT_CATEGORY);
    }

    public boolean isCategoryVisible() {
        return this.mVisible;
    }

//    public void toggle() {
//        if (this.mVisible) {
//            this.hide();
//        } else
//            this.show();
//    }

    public void updateEpg(IPTVChannel channel) {
        if (channel.epgAdapter != null)
            channel.epgAdapter.notifyDataSetChanged();

        EPGAdapter adapter = (EPGAdapter) mEpgList.getAdapter();
        if (adapter != null && adapter.channel == channel) {
            channel.epg.getCurrentTimer();
            int curtime = channel.epg.curTime;
            if (curtime != -1) {
                int h = mEpgList.getMeasuredHeight() / 2;
                mEpgList.setSelectionFromTop(curtime, h);
            }

        }

    }

    private void setEPGCurrentProgramToCenter(IPTVChannel channel) {
        channel.epg.getCurrentTimer();
        int curtime = channel.epg.curTime;
        if (curtime != -1) {
            int h = mEpgList.getMeasuredHeight() / 2;
            mEpgList.setSelectionFromTop(curtime, h);
        }

    }

    protected Handler mMessageHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IPTVMessage.IPTV_EPG_LOADED:
                    IPTVChannel channel = (IPTVChannel) msg.obj;
                    onEPGLoaded(channel);
                    break;
            }
        }
    };

    protected void onEPGLoaded(IPTVChannel channel) {
        ChannelAdapter adapter = (ChannelAdapter) mChannelList.getAdapter();
        if (adapter != null && adapter.getChannel() == channel) {
            setEPGCurrentProgramToCenter(channel);
        }
    }

    private OnKeyListener mKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (!mVisible) return false;
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

//
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
            return false;
        }
    };


    private OnFocusChangeListener mCateFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mEpgList.setVisibility(View.GONE);
            } else {
                mEpgList.setVisibility(View.VISIBLE);
            }
        }
    };


}
