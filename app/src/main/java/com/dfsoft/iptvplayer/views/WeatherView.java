package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVConfig;
import com.dfsoft.iptvplayer.utils.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class WeatherView extends FrameLayout {
    public WeatherView(@NonNull Context context) {
        this(context,null);
    }

    public WeatherView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }


    private TextView hud_weather_city;
    private ImageView hud_weather_icon;
    private TextView hud_weather_tq;
    private TextView hud_weather_highlow;
    private TextView hud_weather_aqi;

    public WeatherView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) return;

        inflate(context, R.layout.layout_weather,this);

        hud_weather_city = findViewById(R.id.hud_weather_city);
        hud_weather_icon = findViewById(R.id.hud_weather_icon);
        hud_weather_tq = findViewById(R.id.hud_weather_tq);
        hud_weather_highlow = findViewById(R.id.hud_weather_highlow);
        hud_weather_aqi = findViewById(R.id.hud_weather_aqi);

    }

    private IPTVConfig config = IPTVConfig.getInstance();
    public void updateWeather() {
        if (config.weather == null || config.weather.data == null) {
            setVisibility(GONE);
            if (config.weather != null) {
                config.weather.loadWeatherData();
            }
            return;
        }
        setVisibility(VISIBLE);

        hud_weather_city.setText(config.weather.data.city);

        String icon = config.weather.getWeatherIcon();
        LogUtils.i("weather","icon = "+icon);
        if (icon.isEmpty())
            hud_weather_icon.setImageDrawable(null);
        else {
            Bitmap tmp = config.imageCache.get(icon);
            if (tmp != null) {
                hud_weather_icon.setImageBitmap(tmp);
            } else
                hud_weather_icon.setImageDrawable(null);
        }

        ArrayList<MyColor> list = new ArrayList<>();
        list.add(new MyColor(config.weather.getWeatherType()+" ",0));
        list.add(new MyColor(config.weather.data.wendu+"℃",getWenduColor(config.weather.data.wendu)));

        SpannableStringBuilder span = this.getColorString(list);
        hud_weather_tq.setText(span);

        String tmpStr = config.weather.data.low + " - " + config.weather.data.high + "   "+config.weather.data.fengxiang+"  "+config.weather.data.fengli;
        hud_weather_highlow.setText(tmpStr);

        list.clear();
        list.add(new MyColor("AQI :"));
        list.add(new MyColor(config.weather.data.aqi,getAQIColor(config.weather.data.aqi)));
        list.add(new MyColor("     "));
        list.add(new MyColor(config.weather.data.qlty,getAQIColor(config.weather.data.aqi)));
        list.add(new MyColor("      PM2.5: "));
        list.add(new MyColor(config.weather.data.pm25,getAQIColor(config.weather.data.pm25)));
        list.add(new MyColor("      PM10 : "));
        list.add(new MyColor(config.weather.data.pm10,getAQIColor(config.weather.data.pm10)));

        SpannableStringBuilder span1 =getColorString(list);

//        tmpStr = "AQI :" + config.weather.data.aqi + "     " + config.weather.data.qlty + "      PM2.5: "+ config.weather.data.pm25 + "      PM10 : " + config.weather.data.pm10;
        hud_weather_aqi.setText(span1);
    }

    private int getWenduColor(String wd) {
        float d = Float.parseFloat(wd);
        if (d < 10) return Color.WHITE;
        if (d < 30) return Color.GREEN;
        return Color.RED;
    }

    private int getAQIColor(String aqi) {
        int d = Integer.parseInt(aqi);
        if (d < 50) return Color.GREEN;
        if (d < 100) return Color.WHITE;
        if (d < 300) return Color.MAGENTA;
        return Color.RED;
    }

    private SpannableStringBuilder getColorString(ArrayList<MyColor> list) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            text.append(list.get(i).text);
        }
        SpannableStringBuilder span = new SpannableStringBuilder(text.toString());
        int start = 0;
        for (int i = 0; i < list.size(); i++) {
            int len = list.get(i).text.length();
            if (list.get(i).color != 0) {
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(list.get(i).color);
                span.setSpan(colorSpan, start, start + len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            start = start + len;
        }
        return span;
    }


    private class MyColor {
        String text;
        int color;

        public MyColor(String text, int color) {
            this.text = text;
            this.color = color;
        }

        public MyColor(String text) {
            this.text = text;
            this.color = 0;
        }
    }
}
