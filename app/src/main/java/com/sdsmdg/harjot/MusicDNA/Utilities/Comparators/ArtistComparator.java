package com.sdsmdg.harjot.MusicDNA.Utilities.Comparators;

import com.sdsmdg.harjot.MusicDNA.Models.Artist;

import java.util.Comparator;

/**
 * Created by Harjot on 18-Jan-17.
 */

public class ArtistComparator implements Comparator<Artist> {

    @Override
    public int compare(Artist lhs, Artist rhs) {
        return lhs.getName().toString().compareTo(rhs.getName().toString());
    }
}
