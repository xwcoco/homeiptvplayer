package com.forcetech.android;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;

import com.dfsoft.iptvplayer.utils.LogUtils;
import com.forcetech.service.P2p_AService;
import com.forcetech.service.P8PService;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class P2PManager {

    private final static String TAG = "P2PManager";

    private static P2PManager _instance = null;

    public static P2PManager getInstance() {
        if (_instance == null)
            _instance = new P2PManager();
        return _instance;
    }

    private Context mContext = null;

    public void setContext(Context context) {

        this.mContext = context;
    }

    public void initServices() {
        mContext.bindService(new Intent(mContext,P2p_AService.class),mConnection,Context.BIND_AUTO_CREATE);
        mContext.bindService(new Intent(mContext, P8PService.class),mConnection,Context.BIND_AUTO_CREATE);
    }

//    public void initServices() {
//
//    }
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.i(TAG,"Service " + name + " connected.");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private HashMap<Integer, Intent> mServiceList = new HashMap<>();

    public String getUrl(String source,int port) {
//        Intent intent = mServiceList.get(port);
//        if (intent == null) {
//            switch (port) {
//                case 9001:
//                    intent = new Intent(mContext,P2p_AService.class);
//                    break;
//            }
//            mServiceList.put(port,intent);
//        }
//        mContext.bindService(intent,mConnection,Context.BIND_AUTO_CREATE);

        Uri uri = Uri.parse(source);

        String cmd = "http://127.0.0.1:"+port+"/cmd.xml?cmd=switch_chan&server=" + uri.getHost() + ":" + uri.getPort() + "&id=";

        String tmp = uri.getLastPathSegment();
        int index = tmp.lastIndexOf(".");

        if (index == -1)
            cmd = cmd + tmp;
        else
            cmd = cmd + tmp.substring(0,index);

        cmd = cmd + "&" + uri.getQuery();

//        StringBuilder builder = new StringBuilder();
//        builder.append("http://127.0.0.1:");
//        builder.append(port);
//        builder.append("/");
//        builder.append(uri.getLastPathSegment());
//
//        String retValue = builder.toString();
//
//        builder.append("/cmd.xml?cmd=switch_chan&server=");
//        builder.append(uri.getHost());
//        builder.append(":");
//        builder.append(uri.getPort());
//
//        if (index == -1) {
//            builder.append(tmp);
//            retValue = retValue + ".ts";
//        } else {
//            builder.append(tmp.substring(0,index));
//        }
//
//        if (uri.getQuery() != null) {
//            builder.append("&");
//            builder.append(uri.getQuery());
//        }
//
//        LogUtils.i(TAG," url = " + builder.toString());
//        LogUtils.i(TAG," ret = " + retValue);

        doGetP2PUrl(cmd);

        return "http://127.0.0.1:"+port+uri.getPath();

    }

    private OkHttpClient client = new OkHttpClient();

    private void doGetP2PUrl(String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                doGetHttp(path);
            }
        }).start();
    }

    private void doGetHttp(String path) {
        try {
            URL url = new URL(path);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "MTV");
            conn.connect();
            InputStreamReader in = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
            String retValue = "";
            BufferedReader br = new BufferedReader(in);
            while (true) {
                String s = br.readLine();
                if (s != null) {
                    retValue = retValue + s + "\n";
                } else
                    break;
            }
            LogUtils.i(TAG,"doGetP2PUrl = "+retValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void doGetP2PUrl(String path) {
//        Request request = new Request.Builder()
//                .url(path)
//                .get()
//                .build();
//
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                final String str = response.body().string();
//                LogUtils.i(TAG,"local ret = "+str);
//            }
//
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }


}
