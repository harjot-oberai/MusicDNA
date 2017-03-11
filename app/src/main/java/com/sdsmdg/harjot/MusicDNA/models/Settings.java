package com.sdsmdg.harjot.MusicDNA.models;

import android.graphics.Color;

/**
 * Created by Harjot on 02-Aug-16.
 */
public class Settings {
    public static final int DEF_THEME_COLOR = Color.parseColor("#B24242");
    public static boolean DEF_ALBUM_ART_BG = true;
    public static boolean DEF_STREAM_ONLY_ON_WIFI = false;
    public static float DEF_DNA_DENSITY = 60f;

    private int themeColor = DEF_THEME_COLOR;
    private float minAudioStrength;
    private boolean albumArtBackgroundEnabled = DEF_ALBUM_ART_BG;
    private boolean streamOnlyOnWifiEnabled = DEF_STREAM_ONLY_ON_WIFI;

    public Settings() {
        this.minAudioStrength = 0.40f;
        albumArtBackgroundEnabled = true;
    }

    public int getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(int themeColor) {
        this.themeColor = themeColor;
    }

    public float getMinAudioStrength() {
        return minAudioStrength;
    }

    public void setMinAudioStrength(float minAudioStrength) {
        this.minAudioStrength = minAudioStrength;
    }

    public boolean isAlbumArtBackgroundEnabled() {
        return albumArtBackgroundEnabled;
    }

    public void setAlbumArtBackgroundEnabled(boolean albumArtBackgroundEnabled) {
        this.albumArtBackgroundEnabled = albumArtBackgroundEnabled;
    }

    public void setStreamOnlyOnWifiEnabled(boolean streamOnlyOnWifiEnabled) {
        this.streamOnlyOnWifiEnabled = streamOnlyOnWifiEnabled;
    }

    public boolean isStreamOnlyOnWifiEnabled() {
        return streamOnlyOnWifiEnabled;
    }
}
