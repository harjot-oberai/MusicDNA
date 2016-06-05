package com.sdsmdg.harjot.MusicDNA.Models;

import android.graphics.Bitmap;

/**
 * Created by Harjot on 03-Jun-16.
 */
public class SavedDNA {
    private String name;
    private DNAModel model;

    public SavedDNA(String name, DNAModel model) {
        this.name = name;
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DNAModel getModel() {
        return model;
    }

    public void setModel(DNAModel model) {
        this.model = model;
    }
}
