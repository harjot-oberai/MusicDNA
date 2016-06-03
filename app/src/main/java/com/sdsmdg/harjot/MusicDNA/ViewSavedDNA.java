package com.sdsmdg.harjot.MusicDNA;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sdsmdg.harjot.MusicDNA.Models.SavedDNA;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewSavedDNA extends Fragment {

    static RecyclerView viewDnaRecycler;
    static ViewSavedDnaRecyclerAdapter vdAdapter;

    static VisualizerView2 mVisualizerView2;

    public ViewSavedDNA() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_saved_dn, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewDnaRecycler = (RecyclerView) view.findViewById(R.id.saved_dna_recycler);
        vdAdapter = new ViewSavedDnaRecyclerAdapter(HomeActivity.savedDNAs.getSavedDNAs());
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(HomeActivity.ctx, LinearLayoutManager.HORIZONTAL, false);
        viewDnaRecycler.setLayoutManager(mLayoutManager2);
        viewDnaRecycler.setItemAnimator(new DefaultItemAnimator());
        viewDnaRecycler.setAdapter(vdAdapter);

        mVisualizerView2 = (VisualizerView2) view.findViewById(R.id.saved_dna_visualizer);

        viewDnaRecycler.addOnItemTouchListener(new ClickItemTouchListener(viewDnaRecycler) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                SavedDNA dna = HomeActivity.savedDNAs.getSavedDNAs().get(position);
                mVisualizerView2.setPts(dna.getModel().getPts());
                mVisualizerView2.setPtPaint(dna.getModel().getPtPaint());
                mVisualizerView2.update();
                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


    }
}
