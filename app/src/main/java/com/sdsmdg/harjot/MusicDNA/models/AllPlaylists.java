package com.sdsmdg.harjot.MusicDNA.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harjot on 15-May-16.
 */
public class AllPlaylists {
    private List<Playlist> allPlaylists;

    public AllPlaylists() {
        allPlaylists = new ArrayList<>();
    }

    public List<Playlist> getPlaylists() {
        return allPlaylists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.allPlaylists = playlists;
    }

    public void addPlaylist(Playlist pl){
        allPlaylists.add(pl);
    }

}
