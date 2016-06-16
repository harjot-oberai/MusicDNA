package com.sdsmdg.harjot.MusicDNA.LocalMusicFragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdsmdg.harjot.MusicDNA.Helpers.GridSpacingItemDecoration;
import com.sdsmdg.harjot.MusicDNA.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.Models.RecentlyPlayed;
import com.sdsmdg.harjot.MusicDNA.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends Fragment {

    static AlbumRecyclerAdapter abAdapter;

    static RecyclerView rv;

    public static onAlbumClickListener mCallback;

    public AlbumFragment() {
        // Required empty public constructor
    }

    public interface onAlbumClickListener {
        public void onAlbumClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (onAlbumClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = (RecyclerView) view.findViewById(R.id.albums_recycler);
        abAdapter = new AlbumRecyclerAdapter(HomeActivity.albums);
        GridLayoutManager glManager = new GridLayoutManager(HomeActivity.ctx, 2);
        rv.setLayoutManager(glManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(abAdapter);

        rv.addOnItemTouchListener(new ClickItemTouchListener(rv) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                HomeActivity.tempAlbum = HomeActivity.albums.get(position);
                mCallback.onAlbumClick();
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
