package com.sdsmdg.harjot.MusicDNA.Utilities.Comparators;

import com.sdsmdg.harjot.MusicDNA.Models.Album;

import java.util.Comparator;

/**
 * Created by Harjot on 18-Jan-17.
 */

public class AlbumComparator implements Comparator<Album> {

    @Override
    public int compare(Album lhs, Album rhs) {
        return lhs.getName().toString().compareTo(rhs.getName().toString());
    }
}
