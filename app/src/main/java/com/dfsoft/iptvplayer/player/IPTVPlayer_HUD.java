package com.dfsoft.iptvplayer.player;

public class IPTVPlayer_HUD {
    public String codec = "";
    public int height = 0;
    public int width = 0;

    public String audio_codec = "";
    public int audio_channels = 0;
    public int audio_rate = 0;
    public int audio_bitrate = 0;

    public void init() {
        codec = "";
        height = 0;
        width = 0;
        audio_codec = "";
        audio_channels = 0;
        audio_rate = 0;
        audio_bitrate = 0;
    }

    public String toString() {
        return "Video codec = " + codec + " height = "+String.valueOf(height) + " width = "+String.valueOf(width) + " Audio codec = " + audio_codec +
                " channels = "+ String.valueOf(audio_channels)+" rate = "+String.valueOf(audio_rate);
    }

}
