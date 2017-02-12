package com.sdsmdg.harjot.MusicDNA.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harjot on 03-Jun-16.
 */
public class AllMusicFolders {
    List<MusicFolder> musicFolders;

    public AllMusicFolders() {
        musicFolders = new ArrayList<>();
    }

    public List<MusicFolder> getMusicFolders() {
        return musicFolders;
    }

    public void setMusicFolders(List<MusicFolder> musicFolders) {
        this.musicFolders = musicFolders;
    }
}
