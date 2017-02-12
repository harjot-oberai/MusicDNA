package com.sdsmdg.harjot.MusicDNA.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harjot on 02-Jun-16.
 */
public class MusicFolder {
    private String folderName;
    private List<LocalTrack> localTracks;

    public MusicFolder(String folderName, List<LocalTrack> localTracks) {
        this.folderName = folderName;
        this.localTracks = localTracks;
    }

    public MusicFolder(String folderName) {
        this.folderName = folderName;
        localTracks = new ArrayList<>();
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<LocalTrack> getLocalTracks() {
        return localTracks;
    }

    public void setLocalTracks(List<LocalTrack> localTracks) {
        this.localTracks = localTracks;
    }
}
