package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVConfig;

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
            return;
        }
        setVisibility(VISIBLE);

        hud_weather_city.setText(config.weather.data.city);

        String icon = config.weather.getWeatherIcon();
        if (icon.isEmpty())
            hud_weather_icon.setImageDrawable(null);
        else {
            Bitmap tmp = config.imageCache.get(icon);
            if (tmp != null) {
                hud_weather_icon.setImageBitmap(tmp);
            } else
                hud_weather_icon.setImageDrawable(null);
        }
        String tmpStr = config.weather.getWeatherType() + " " + config.weather.data.wendu+"℃";
        hud_weather_tq.setText(tmpStr);
        tmpStr = config.weather.data.low + " - " + config.weather.data.high + "   "+config.weather.data.fengxiang+"  "+config.weather.data.fengli;
        hud_weather_highlow.setText(tmpStr);

        tmpStr = "AQI :" + config.weather.data.aqi + "     " + config.weather.data.qlty + "      PM2.5: "+ config.weather.data.pm25 + "      PM10 : " + config.weather.data.pm10;
        hud_weather_aqi.setText(tmpStr);
    }
}
