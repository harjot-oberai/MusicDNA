package com.sdsmdg.harjot.MusicDNA;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayListFragment extends Fragment {

    RecyclerView allPlaylistRecycler;
    ViewAllPlaylistsRecyclerAdapter vpAdapter;

    static onPLaylistTouchedListener mCallback;

    public PlayListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (onPLaylistTouchedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public interface onPLaylistTouchedListener{
        public void onPlaylistTouched(int pos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allPlaylistRecycler = (RecyclerView) view.findViewById(R.id.all_playlists_recycler);
        vpAdapter = new ViewAllPlaylistsRecyclerAdapter(HomeActivity.allPlaylists.getPlaylists());
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(HomeActivity.ctx, LinearLayoutManager.VERTICAL, false);
        allPlaylistRecycler.setLayoutManager(mLayoutManager2);
        allPlaylistRecycler.setItemAnimator(new DefaultItemAnimator());
        allPlaylistRecycler.setAdapter(vpAdapter);

        allPlaylistRecycler.addOnItemTouchListener(new ClickItemTouchListener(allPlaylistRecycler) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                mCallback.onPlaylistTouched(position);
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
