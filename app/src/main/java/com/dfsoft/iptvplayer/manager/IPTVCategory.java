package com.dfsoft.iptvplayer.manager;


import com.dfsoft.iptvplayer.views.ChannelAdapter;

import java.util.ArrayList;
import java.util.List;

public class IPTVCategory {
    public IPTVCategory() {
    }

    public String name = "";

    public String psw = "";

    public List<IPTVChannel> data = new ArrayList<IPTVChannel>();

    public ChannelAdapter channelAdapter = null;
}
