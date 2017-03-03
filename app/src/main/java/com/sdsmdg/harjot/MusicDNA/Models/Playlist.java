package com.sdsmdg.harjot.MusicDNA.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harjot on 15-May-16.
 */
public class Playlist {
    private List<UnifiedTrack> songList;
    private String playlistName;

    public Playlist(String name) {
        playlistName = name;
        songList = new ArrayList<UnifiedTrack>();
    }

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

    public void addSong(UnifiedTrack track) {
        songList.add(track);
    }
    
    public void deleteSong(UnifiedTrack track){

        int index;

        if(track.getType()){

            long idd = track.getLocalTrack().getId();

            for(UnifiedTrack songs : songList){

                if(songs.getLocalTrack().getId() == idd){
                    
                    index = songs.get();
                    break;

                }
            }

            songList.remove(index);

        }else{
            
            int id = track.getStreamTrack().getID();

            for(UnifiedTrack otherSongs : songList){

                if(otherSongs.getStreamTrack().getID()){

                    index = otherSongs.get();
                    break;

                }

            }

            songList.remove(index);

        }

    }

}
