package com.sdsmdg.harjot.MusicDNA.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harjot on 15-May-16.
 */
public class Favourite {
    private List<UnifiedTrack> favourite;

    public Favourite() {
        favourite = new ArrayList<>();
    }

    public List<UnifiedTrack> getFavourite() {
        return favourite;
    }

    public void setFavourite(List<UnifiedTrack> favourite) {
        this.favourite = favourite;
    }
}
