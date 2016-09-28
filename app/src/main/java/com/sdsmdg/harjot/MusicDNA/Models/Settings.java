package com.sdsmdg.harjot.MusicDNA.Models;

import android.graphics.Color;

/**
 * Created by Harjot on 02-Aug-16.
 */
public class Settings {
    private int themeColor = Color.parseColor("#B24242");
    private float minAudioStrength;
    private boolean albumArtBackgroundEnabled = false;

    public Settings() {
        this.minAudioStrength = 0.40f;
        albumArtBackgroundEnabled = false;
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
}
