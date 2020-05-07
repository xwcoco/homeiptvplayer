package com.dfsoft.iptvplayer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageCache {
    private final String TAG = "ImageCache";

    private LruCache<String, Bitmap> mMemoryCache;

    private static ImageCache _instance = null;

    public String host = "";

    public static ImageCache getInstance() {
        if (_instance == null) {
            _instance = new ImageCache();

        }
        return _instance;
    }

    private ImageCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    public void put(String key, Bitmap bitmap) {
        if (get(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap get(String key) {
        return mMemoryCache.get(key);
    }


//    private IPTVConfig config = IPTVConfig.getInstance();

    public void loadImage(String url,String key) {
        String tmpUrl = "";
        if (!url.startsWith("/"))
            tmpUrl = host + "/"+url;
        else
            tmpUrl = host+url;

        DownloadTask task = new DownloadTask();
        task.downloadUrl = tmpUrl;
        task.putkey = key;

        mHandler.post(task);
    }

    private Handler mHandler = new Handler();

    class DownloadTask implements Runnable {

        private String downloadUrl;
        private String putkey;
        private OkHttpClient client = new OkHttpClient();

        @Override
        public void run() {
            Request request = new Request.Builder()
                    .url(downloadUrl)
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
                    byte[] bytes = response.body().bytes();
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bitmap != null) {
                        Log.e(TAG, "保存图片成功");
                        put(putkey,bitmap);
                    }
                }
            });
        }
    };



////    private Runnable downloadTask = new Runnable() {
//
//        private OkHttpClient client = new OkHttpClient();
//        private String downloadUrl = ""
//
//        @Override
//        public void run() {
//
//        }
//    };

}
