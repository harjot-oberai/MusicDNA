package com.sdsmdg.harjot.MusicDNA.models;

/**
 * Created by Harjot on 03-Jun-16.
 */
public class SavedDNA {
    private String name;
    private boolean type;
    private String localPath;
    private String trackArtworkURL;
    private String artist;
    private String base64encodedBitmap;

    public SavedDNA(String name, boolean type, String localPath, String trackArtworkURL, String artist, String base64encodedBitmap) {
        this.name = name;
        this.type = type;
        this.localPath = localPath;
        this.trackArtworkURL = trackArtworkURL;
        this.artist = artist;
        this.base64encodedBitmap = base64encodedBitmap;
    }
    public Boolean getType() {
        return type;
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getTrackArtworkURL() {
        return trackArtworkURL;
    }

    public String getArtist() {
        return artist;
    }

    public String getBase64encodedBitmap() {
        return base64encodedBitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
