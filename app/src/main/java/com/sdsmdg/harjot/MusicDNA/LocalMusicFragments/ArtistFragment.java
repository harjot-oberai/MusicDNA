package com.sdsmdg.harjot.MusicDNA.LocalMusicFragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.sdsmdg.harjot.MusicDNA.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment {

    public static ArtistRecyclerAdapter arAdapter;
    public static RecyclerView rv;

    public static onArtistClickListener mCallback;

    public ArtistFragment() {
        // Required empty public constructor
    }

    public interface onArtistClickListener {
        public void onArtistClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (onArtistClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = (RecyclerView) view.findViewById(R.id.artists_recycler);
        arAdapter = new ArtistRecyclerAdapter(HomeActivity.finalArtists);
        LinearLayoutManager llManager = new LinearLayoutManager(HomeActivity.ctx, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(arAdapter);

        rv.addOnItemTouchListener(new ClickItemTouchListener(rv) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                HomeActivity.tempArtist = HomeActivity.finalArtists.get(position);
                mCallback.onArtistClick();
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
