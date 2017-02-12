package com.sdsmdg.harjot.MusicDNA.models;

/**
 * Created by Harjot on 14-May-16.
 */
public class UnifiedTrack {
    boolean type;                       // true->localTrack         false->streamTrack
    LocalTrack localTrack;
    Track streamTrack;

    public UnifiedTrack(boolean type, LocalTrack localTrack, Track streamTrack) {
        this.type = type;
        this.localTrack = localTrack;
        this.streamTrack = streamTrack;
    }

    public boolean getType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public LocalTrack getLocalTrack() {
        return localTrack;
    }

    public void setLocalTrack(LocalTrack localTrack) {
        this.localTrack = localTrack;
    }

    public Track getStreamTrack() {
        return streamTrack;
    }

    public void setStreamTrack(Track streamTrack) {
        this.streamTrack = streamTrack;
    }
}
