package com.dfsoft.iptvplayer.manager;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IPTVConfig {
    private final String TAG = "IPTVConfig";
    private static IPTVConfig _instance = new IPTVConfig();

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private IPTVChannel playingChannal = null;

    private OkHttpClient client = new OkHttpClient();

    public ArrayList<IPTVCategory> category = new ArrayList<>();

    public IPTVMessage iptvMessage = new IPTVMessage();


    private IPTVConfig() {
    }

    public interface DataEventLister {

        public void onInitData(Boolean isOk);
        public void onPlayChannel();
    }

    public IPTVChannel getPlayingChannal() {
        return playingChannal;
    }

    public void setPlayingChannal(IPTVChannel playingChannal) {
        this.playingChannal = playingChannal;
        if (this.dataEventLister != null)
            this.dataEventLister.onPlayChannel();
    }

    public DataEventLister dataEventLister = null;

    public void setDataEventLister(DataEventLister dataEventLister) {
        this.dataEventLister = dataEventLister;
    }

    public static IPTVConfig getInstance() {
        return _instance;
    }

    public String host = "http://192.168.50.8:8080";

    public String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }

    }

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static String uncompress(String str) {
        if (str.length() <= 0) {
            return str;
        }
        try {
            byte[] tmpData = new Base64().decode(str.getBytes());

            byte[] output = new byte[0];
            Inflater decompresser = new Inflater();
            decompresser.reset();
            decompresser.setInput(tmpData);

            ByteArrayOutputStream o = new ByteArrayOutputStream(tmpData.length);
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
            decompresser.end();
            return new String(output);

        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    public IPTVChannel getFirstCanPlayChannel() {
        if (this.category == null) return null;
        for (int i = 0; i < category.size(); i++) {
            IPTVCategory cate = category.get(i);
            if (cate.data.size() == 0)
                continue;
            for (int j = 0; j < cate.data.size(); j++) {
                IPTVChannel channel = cate.data.get(j);
                if (channel.source.size() == 0 )
                    continue;

                return channel;

            }
        }
        return  null;
    }


    public void LoadDataFromString(String dataString) {
        Gson json = new Gson();
//        Log.d(TAG, "LoadDataFromString: "+dataString);
        Type collectionType = new TypeToken<ArrayList<IPTVCategory>>(){}.getType();

        category = json.fromJson(dataString, collectionType);

        Boolean ok = (category != null) && (!category.isEmpty());

        if (this.dataEventLister != null)
            this.dataEventLister.onInitData(ok);
    }


    public void initConfig() {

        String url = this.host + "/data.php";

        HashMap<String, String> keys = new HashMap<String, String>();
        keys.put("rand", "");
        String jsonStr = new Gson().toJson(keys);

        HashMap<String, String> json = new HashMap<>();
        json.put("data", jsonStr);

        jsonStr = new Gson().toJson(json);

        Log.d(TAG, "initConfig: jsonstr = " + jsonStr);

        RequestBody body = RequestBody.create(jsonStr, JSON);
        // 创建请求实例
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("IPTVConfig", "接口调用失败");
                if (dataEventLister != null) {
                    dataEventLister.onInitData(false);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String strByNet = response.body().string();
                String retStr = uncompress(strByNet);
                LoadDataFromString(retStr);

            }
        });
    }

    public IPTVCategory getCategoryByChannel(IPTVChannel channel) {
        for (int i = 0; i < this.category.size(); i++) {
            IPTVCategory cate = category.get(i);
            if (cate.data.indexOf(channel) != -1)
                return cate;
        }
        return null;
    }
}