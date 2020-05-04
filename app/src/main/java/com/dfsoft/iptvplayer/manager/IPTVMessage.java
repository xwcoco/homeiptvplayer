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

    public final static int IPTV_FULLSCREEN = 20;

    public final static int IPTV_HUD_CHANGED = 30;

    public final static int IPTV_BUFFERING = 31;

    public void addMessageListener(Handler handler) {
        this.mHandlerList.add(handler);
    }

    public void removeMessageListener(Handler handler) {
        this.mHandlerList.remove(handler);
    }

    public void sendMessage(int msgId, Object msgData) {
        Message msg = new Message();
        msg.what = msgId;
        msg.obj = msgData;
        for (int i = 0; i < this.mHandlerList.size(); i++) {
            Handler handler = mHandlerList.get(i);
            handler.sendMessage(msg);
        }
    }

    public void sendMessage(int msgId) {
        sendMessage(msgId,null);
    }
}
