package com.dfsoft.iptvplayer.manager;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.dfsoft.iptvplayer.utils.ImageCache;
import com.dfsoft.iptvplayer.utils.LogUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Weather {
    private final String TAG = "Weather";

    public String host = "";

    public WeatherData data = null;

    private OkHttpClient client = new OkHttpClient();



    private Handler mHander = new Handler();

    private Runnable mDownloadData = new Runnable() {
        @Override
        public void run() {
            String downloadUrl = host+"/weather";
            Request request = new Request.Builder()
                    .url(downloadUrl)
                    .get()
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "接口调用失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG, "接口调用成功");
                    final String strByNet1 = response.body().string();
                    loadDataFromString(strByNet1);
                }
            });

        }
    };

    private void loadDataFromString(String dataString) {
        Gson json = new Gson();
        data = json.fromJson(dataString,WeatherData.class);
        if (data != null && data.code != 200) {
            LogUtils.i(TAG,"数据不合法!");
            data = null;
        } else
            loadWeatherIcon();
    }

    private ImageCache cache = ImageCache.getInstance();
    private void loadWeatherIcon() {
        if (data == null) return;
        if (!data.day_icon.isEmpty()) {
            Bitmap tmp = cache.get(data.day_icon);
            if (tmp == null) {
                cache.loadImage(data.day_icon,data.day_icon);
            }
        }
        if (!data.night_icon.isEmpty()) {
            Bitmap tmp = cache.get(data.night_icon);
            if (tmp == null) {
                cache.loadImage(data.night_icon,data.night_icon);
            }
        }
    }

    public String getWeatherIcon() {
        if (data==null) return "";
        Calendar c = Calendar.getInstance();
//        Date date = c.getTime();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour < 8 || hour >= 20)
            return data.night_icon;
        else
            return data.day_icon;
    }

    public String getWeatherType() {
        if (data==null) return "";
        Calendar c = Calendar.getInstance();
//        Date date = c.getTime();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour < 8 || hour >= 20)
            return data.night_type;
        else
            return data.day_type;
    }

    public void loadWeatherData() {
        mHander.post(mDownloadData);
    }

    public void addTwoHourTask() {
        mHander.postDelayed(mDownloadData,1000 * 60 * 60 * 2);
    }



}


