package com.sdsmdg.harjot.MusicDNA.Models;

import android.util.Pair;

import java.util.List;

/**
 * Created by Harjot on 02-Jun-16.
 */
public class DNAModel {
    private boolean type;
    private String title;
    private List<Pair<Float, Float>> pts;
    private List<Pair<Float, Pair<Integer, Integer>>> ptPaint;

    public DNAModel(Boolean type, String title, List<Pair<Float, Float>> pts, List<Pair<Float, Pair<Integer, Integer>>> ptPaint) {
        this.type = type;
        this.title = title;
        this.pts = pts;
        this.ptPaint = ptPaint;
    }

    public boolean getType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Pair<Float, Float>> getPts() {
        return pts;
    }

    public void setPts(List<Pair<Float, Float>> pts) {
        this.pts = pts;
    }

    public List<Pair<Float, Pair<Integer, Integer>>> getPtPaint() {
        return ptPaint;
    }

    public void setPtPaint(List<Pair<Float, Pair<Integer, Integer>>> ptPaint) {
        this.ptPaint = ptPaint;
    }
}
