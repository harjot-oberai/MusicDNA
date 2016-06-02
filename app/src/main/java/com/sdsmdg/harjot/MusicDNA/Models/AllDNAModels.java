package com.sdsmdg.harjot.MusicDNA.Models;

import android.widget.Toast;

import com.sdsmdg.harjot.MusicDNA.HomeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harjot on 02-Jun-16.
 */
public class AllDNAModels {
    private List<DNAModel> allDNAs;

    public AllDNAModels() {
        allDNAs = new ArrayList<>();
    }

    public List<DNAModel> getAllDNAs() {
//        Toast.makeText(HomeActivity.ctx, "SAVING...", Toast.LENGTH_SHORT).show();
        return allDNAs;
    }

    public void setAllDNAs(List<DNAModel> allDNAs) {
        this.allDNAs = allDNAs;
    }
}
