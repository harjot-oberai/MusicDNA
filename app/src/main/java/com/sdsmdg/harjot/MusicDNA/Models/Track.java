package com.sdsmdg.harjot.MusicDNA.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Harjot on 30-Apr-16.
 */
public class Track {
    @SerializedName("title")
    private String mTitle;

    @SerializedName("id")
    private int mID;

    @SerializedName("duration")
    private int mDuration;

    @SerializedName("stream_url")
    private String mStreamURL;

    @SerializedName("artwork_url")
    private String mArtworkURL;

    public String getTitle() {
        return mTitle;
    }

    public int getID() {
        return mID;
    }

    public int getDuration() {
        return mDuration;
    }

    public String getStreamURL() {
        return mStreamURL;
    }

    public String getArtworkURL() {
        return mArtworkURL;
    }

}
