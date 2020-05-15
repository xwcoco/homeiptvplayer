package com.dfsoft.iptvplayer.manager;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

public class IPTVMessage {
    private final String TAG = "IPTVMessage";

    private List<Handler> mHandlerList = new ArrayList<>();

    public final static int IPTV_EPG_LOADED = 1;

    public final static int IPTV_CHANNEL_PLAY = 10;
    public final static int IPTV_CHANNEL_PLAY_INDEX = 11;

    public final static int IPTV_FULLSCREEN = 20;

    public final static int IPTV_QUIT_CATEGORY = 21;

    public final static int IPTV_QUIT_SETTING = 22;

    public final static int IPTV_QUIT_QUITASK = 23;

    public final static int IPTV_HUD_CHANGED = 30;

    public final static int IPTV_BUFFERING = 31;

    public final static int IPTV_CONFIG_CHANGED = 32;

    public final static int IPTV_IMAGECACHE_UPDATE = 33;

    public final static int IPTV_SHOWMESSAGE= 34;

    public final static int IPTV_SWITCH_CATEGORY = 35;

    public final static int IPTV_QUIT = 50;

    public void addMessageListener(Handler handler) {
        this.mHandlerList.add(handler);
    }

    public void removeMessageListener(Handler handler) {
        this.mHandlerList.remove(handler);
    }

    public void sendMessage(int msgId, Object msgData) {
        for (int i = 0; i < this.mHandlerList.size(); i++) {
            Message msg = new Message();
            msg.what = msgId;
            msg.obj = msgData;
            Handler handler = mHandlerList.get(i);
            handler.sendMessage(msg);
        }
    }

    public void sendMessage(int msgId) {
        sendMessage(msgId,null);
    }
}
