package com.example.harjot.musicstreamer.Models;

import java.util.List;

/**
 * Created by Harjot on 15-May-16.
 */
public class Playlist {
    private List<UnifiedTrack> songList;
    private String playlistName;

    public Playlist(List<UnifiedTrack> songList, String playlistName) {
        this.songList = songList;
        this.playlistName = playlistName;
    }

    public List<UnifiedTrack> getSongList() {
        return songList;
    }

    public void setSongList(List<UnifiedTrack> songList) {
        this.songList = songList;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public void addSong(UnifiedTrack track){
        songList.add(track);
    }

}
