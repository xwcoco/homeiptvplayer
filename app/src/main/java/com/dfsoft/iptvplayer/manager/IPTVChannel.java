package com.dfsoft.iptvplayer.manager;

import android.graphics.Bitmap;
import android.util.Log;

import com.dfsoft.iptvplayer.utils.ImageCache;
import com.dfsoft.iptvplayer.utils.LogUtils;
import com.dfsoft.iptvplayer.views.EPGAdapter;
import com.google.gson.Gson;

import org.apache.commons.codec.binary.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IPTVChannel {
    private final String TAG = "IPTVChannel";
    public IPTVChannel() {
    }
    private OkHttpClient client = new OkHttpClient();

    private IPTVConfig config = IPTVConfig.getInstance();

    public Integer num = -1;

    public String name = "";

    public List<String> source = new ArrayList<String>();

    public IPTVEPG epg = new IPTVEPG();

    public int playIndex = 0;

    public EPGAdapter epgAdapter = null;

    public String icon = "";

    public void loadEPGData() {
        String url = config.host + "/epg/live_proxy_epg.php/out_epg?id="+name;

        // 创建请求实例
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "接口调用失败");
                epg.code = 0;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "接口调用成功");
                final String strByNet = response.body().string();
                load(strByNet);
            }
        });
    }


    public void load(String dataString) {
        Gson json = new Gson();
        epg = json.fromJson(dataString, IPTVEPG.class);
        if (!this.epg.isEmpty()) {
            config.iptvMessage.sendMessage(IPTVMessage.IPTV_EPG_LOADED,this);
        }

    }


    public void doLoadChannelIcon() {
        if (this.icon.isEmpty()) {
            this.loadChannelIcon();
        } else {
            Bitmap tmp = config.imageCache.get(this.name);
            if (tmp == null)
                config.imageCache.loadImage(this.icon,this.name);
        }
    }
//
    private void loadChannelIcon() {
        String url = config.host + "/icon/getnameicon?name="+name;

        // 创建请求实例
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "接口调用失败");
                epg.code = 0;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String strByNet1 = response.body().string();
                getIconName(strByNet1);
            }
        });
    }

    private void getIconName(String dataString) {
        Gson json = new Gson();
        IconRespJSON obj = json.fromJson(dataString,IconRespJSON.class);
        if (obj.code == 200) {
            this.icon = obj.data;

            LogUtils.i(TAG,"icon = "+icon);
            if (!icon.isEmpty()) {
                Bitmap tmp = config.imageCache.get(this.name);
                if (tmp == null)
                    config.imageCache.loadImage(this.icon,this.name);
            }
        }
    }

    private class IconRespJSON {
        private int code;
        private String data;

        public IconRespJSON() {
        }
    }

}
