package com.sdsmdg.harjot.MusicDNA.models;

import java.util.List;

/**
 * Created by Harjot on 15-Jun-16.
 */
public class Album {
    private String Name;
    private List<LocalTrack> albumSongs;

    public Album(String name, List<LocalTrack> albumSongs) {
        Name = name;
        this.albumSongs = albumSongs;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<LocalTrack> getAlbumSongs() {
        return albumSongs;
    }

    public void setAlbumSongs(List<LocalTrack> albumSongs) {
        this.albumSongs = albumSongs;
    }
}
