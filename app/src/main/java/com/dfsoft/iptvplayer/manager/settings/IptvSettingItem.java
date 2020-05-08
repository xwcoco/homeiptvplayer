package com.dfsoft.iptvplayer.manager.settings;

import com.dfsoft.iptvplayer.manager.IPTVConfig;
import com.dfsoft.iptvplayer.manager.IPTVMessage;
import com.dfsoft.iptvplayer.views.SettingItemAdapter;

import java.util.ArrayList;

public class IptvSettingItem {
    public String name = "";
    public ArrayList<String> options = new ArrayList<>();
    private int value = -1;

    public int tag = -1;

    public IptvSettings mOwner = null;

    public int getValue() {
        return value;
    }

    public void setValue(int newValue) {
        if (noSetValue) return;
        value = newValue;
    }

    public boolean noSetValue = false;
    public boolean noImage = false;

    public SettingItemAdapter adapter = null;

    protected IPTVConfig config = IPTVConfig.getInstance();

    public void apply() {
        mOwner.save();
        config.iptvMessage.sendMessage(IPTVMessage.IPTV_CONFIG_CHANGED,this);
    }
}
