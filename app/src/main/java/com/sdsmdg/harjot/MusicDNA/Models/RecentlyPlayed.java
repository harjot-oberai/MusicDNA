package com.sdsmdg.harjot.MusicDNA.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harjot on 15-May-16.
 */
public class RecentlyPlayed {
    private List<UnifiedTrack> recentlyPlayed;

    public RecentlyPlayed() {
        recentlyPlayed = new ArrayList<>();
    }

    public List<UnifiedTrack> getRecentlyPlayed() {
        return recentlyPlayed;
    }

    public void setRecentlyPlayed(List<UnifiedTrack> recentlyPlayed) {
        this.recentlyPlayed = recentlyPlayed;
    }

    public void addSong(UnifiedTrack track){
        recentlyPlayed.add(track);
    }
}
