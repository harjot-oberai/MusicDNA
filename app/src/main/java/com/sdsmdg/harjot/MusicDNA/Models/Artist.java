package com.sdsmdg.harjot.MusicDNA.models;

import java.util.List;

/**
 * Created by Harjot on 23-Jul-16.
 */
public class Artist {
    private String Name;
    private List<LocalTrack> artistSongs;

    public Artist(String name, List<LocalTrack> artistSongs) {
        Name = name;
        this.artistSongs = artistSongs;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<LocalTrack> getArtistSongs() {
        return artistSongs;
    }

    public void setArtistSongs(List<LocalTrack> artistSongs) {
        this.artistSongs = artistSongs;
    }
}
