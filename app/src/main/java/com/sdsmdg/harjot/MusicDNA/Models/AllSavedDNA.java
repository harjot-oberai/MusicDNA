package com.sdsmdg.harjot.MusicDNA.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harjot on 03-Jun-16.
 */
public class AllSavedDNA {
    List<SavedDNA> savedDNAs;

    public AllSavedDNA() {
        savedDNAs = new ArrayList<>();
    }

    public List<SavedDNA> getSavedDNAs() {
        return savedDNAs;
    }

    public void setSavedDNAs(List<SavedDNA> savedDNAs) {
        this.savedDNAs = savedDNAs;
    }
}

