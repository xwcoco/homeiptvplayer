package com.dfsoft.iptvplayer.manager.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.dfsoft.iptvplayer.manager.IPTVChannel;
import com.dfsoft.iptvplayer.manager.IPTVConfig;

import java.util.ArrayList;

public class IptvSettings {
    public ArrayList<IptvSettingItem> settings = new ArrayList<>();

    public final static int IPTV_SETTING_TAG_PLAYER = 1;
    public final static int IPTV_SETTING_TAG_CHANNEL_SOUCE = 2;
    public final static int IPTV_SETTING_TAG_DISPLAY_MODE = 3;
    public final static int IPTV_SETTING_TAG_SHOWTIME = 4;
    public final static int IPTV_SETTING_TAG_UPDATEDATA = 5;

    private Context mContext;

    public IptvSettings(Context mContext) {
        this.mContext = mContext;
    }


    private void addSetting(String name,ArrayList<String> list,int tag) {
        addSetting(name,list,false,0,tag);
    }

    private void addSetting(String name,ArrayList<String> list,boolean noValue,int value,int tag) {
        IptvSettingItem item = new IptvSettingItem();
        item.name = name;
        item.options.addAll(list);
        item.noSetValue = noValue;
        item.setValue(value);
        item.tag = tag;
        addToList(item);
    }

    private IPTVConfig config = IPTVConfig.getInstance();

    private ArrayList<String> getDisplayModeList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("DM_BEST_FIT");
        list.add("DM_FIT_SCREEN");
        list.add("DM_FILL");
        list.add("DM_16_9");
        list.add("DM_4_3");
        list.add("DM_ORIGINAL");
        return list;
    }

    private void addToList(IptvSettingItem item) {
        settings.add(item);
        item.mOwner = this;
    }

    private ArrayList<String> getBooleanList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("boolean_yes");
        list.add("boolean_no");
        return list;
    }

    public void initAllSettings() {
        settings.clear();
        ArrayList<String> list = new ArrayList<>();

        list.clear();
        list.add("vlcplayer");
        list.add("ijkplayer");


        addSetting("setting_player", list,IPTV_SETTING_TAG_PLAYER);
        addSetting("setting_displaymode", getDisplayModeList(),IPTV_SETTING_TAG_DISPLAY_MODE);
        addSetting("setting_showtime",getBooleanList(),IPTV_SETTING_TAG_SHOWTIME);

        list.clear();
        list.add("set_updatedata");
        addSetting("set_datamanager", list,true,0,IPTV_SETTING_TAG_UPDATEDATA);

        load();
    }

    public IptvSettingItem getItemByTag(int tag) {
        for (int i = 0; i < settings.size(); i++) {
            IptvSettingItem item = settings.get(i);
            if (item.tag == tag) return item;
        }
        return null;
    }

    public void addPlayingChannelSetting() {
        IPTVChannel channel = config.getPlayingChannal();
        ArrayList<String> list = new ArrayList<>();
        if (channel != null) {
            for (int i = 0; i < channel.source.size(); i++) {
                list.add("源 "+String.valueOf(i+1));
            }
            IptvSettingItem item = getItemByTag(IPTV_SETTING_TAG_CHANNEL_SOUCE);
            if (item == null) {
                item = new IptvSettingItem();
                item.tag = IPTV_SETTING_TAG_CHANNEL_SOUCE;
                item.name = "set_source";
                item.mOwner = this;
                settings.add(0,item);
            }
            item.options.clear();
            item.options.addAll(list);
            item.setValue(channel.playIndex);
        }

    }

    public void saveLastPlayedChannel() {
        IPTVChannel channel = config.getPlayingChannal();
        if (channel == null) return;
        SharedPreferences sp = mContext.getSharedPreferences("user",Context.MODE_PRIVATE);
        sp.edit().putInt("lastPlayedChannel",channel.num).apply();
    }

    public int getLastPlayedChannel() {
        SharedPreferences sp = mContext.getSharedPreferences("user",Context.MODE_PRIVATE);
        return sp.getInt("lastPlayedChannel",-1);
    }

    public void load() {
        SharedPreferences sp = mContext.getSharedPreferences("user",Context.MODE_PRIVATE);
        for (int i = 0; i < settings.size(); i++) {
            IptvSettingItem item = settings.get(i);
            if (item.name.startsWith("setting_")) {
                int value = sp.getInt(item.name,0);
                item.setValue(value);
            }
        }
    }

    public void save() {
        SharedPreferences sp = mContext.getSharedPreferences("user",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for (int i = 0; i < settings.size(); i++) {
            IptvSettingItem item = settings.get(i);
            if (item.name.startsWith("setting_")) {
                editor.putInt(item.name,item.getValue());
            }
        }
        editor.apply();
    }


}
