package com.sdsmdg.harjot.MusicDNA;


import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdsmdg.harjot.MusicDNA.Helpers.SimpleItemTouchHelperCallback;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPlaylistFragment extends Fragment implements PlaylistTrackAdapter.OnDragStartListener {

    RecyclerView playlistRecyler;
    static PlaylistTrackAdapter plAdapter;
    FloatingActionButton playAll;

    static ItemTouchHelper mItemTouchHelper;

    static onPLaylistItemClickedListener mCallback;
    static onPlaylistPlayAllListener mCallback2;

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public interface onPLaylistItemClickedListener {
        public void onPLaylistItemClicked(int position);
    }

    public interface onPlaylistPlayAllListener {
        public void onPlaylistPLayAll();
    }

    public ViewPlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (onPLaylistItemClickedListener) context;
            mCallback2 = (onPlaylistPlayAllListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_playlist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playlistRecyler = (RecyclerView) view.findViewById(R.id.view_playlist_recycler);

        plAdapter = new PlaylistTrackAdapter(HomeActivity.tempPlaylist.getSongList(), this);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(HomeActivity.ctx, LinearLayoutManager.VERTICAL, false);
        playlistRecyler.setLayoutManager(mLayoutManager2);
        playlistRecyler.setItemAnimator(new DefaultItemAnimator());
        playlistRecyler.setAdapter(plAdapter);

        playlistRecyler.addOnItemTouchListener(new ClickItemTouchListener(playlistRecyler) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                mCallback.onPLaylistItemClicked(position);
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

        playAll = (FloatingActionButton) view.findViewById(R.id.play_all_fab);
        playAll.setBackgroundTintList(ColorStateList.valueOf(HomeActivity.themeColor));
        playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = HomeActivity.tempPlaylist.getSongList().size();
                HomeActivity.queue.getQueue().clear();
                for (int i = 0; i < size; i++) {
                    HomeActivity.queue.addToQueue(HomeActivity.tempPlaylist.getSongList().get(i));
                }
                HomeActivity.queueCurrentIndex = 0;
                mCallback2.onPlaylistPLayAll();
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(plAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(playlistRecyler);

    }

}
