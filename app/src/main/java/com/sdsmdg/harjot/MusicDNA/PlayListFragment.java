package com.sdsmdg.harjot.MusicDNA;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayListFragment extends Fragment {

    RecyclerView allPlaylistRecycler;
    static ViewAllPlaylistsRecyclerAdapter vpAdapter;

    LinearLayout noPlaylistContent;

    static onPLaylistTouchedListener mCallback;
    static onPlaylistMenuPlayAllListener mCallback2;

    public PlayListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (onPLaylistTouchedListener) context;
            mCallback2 = (onPlaylistMenuPlayAllListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public interface onPLaylistTouchedListener {
        public void onPlaylistTouched(int pos);
    }

    public interface onPlaylistMenuPlayAllListener {
        public void onPlaylistMenuPLayAll();
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

        noPlaylistContent = (LinearLayout) view.findViewById(R.id.noPlaylistContent);

        allPlaylistRecycler = (RecyclerView) view.findViewById(R.id.all_playlists_recycler);

        ((TextView)view.findViewById(R.id.noPlaylistContentText)).setTypeface(SplashActivity.tf2);

        if (HomeActivity.allPlaylists.getPlaylists().size() == 0) {
            allPlaylistRecycler.setVisibility(View.INVISIBLE);
            noPlaylistContent.setVisibility(View.VISIBLE);
        } else {
            allPlaylistRecycler.setVisibility(View.VISIBLE);
            noPlaylistContent.setVisibility(View.INVISIBLE);
        }
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
            boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                PopupMenu popup = new PopupMenu(HomeActivity.ctx, view);
                popup.getMenuInflater().inflate(R.menu.playlist_popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Play")) {
                            HomeActivity.tempPlaylist = HomeActivity.allPlaylists.getPlaylists().get(position);
                            HomeActivity.queue.setQueue(HomeActivity.tempPlaylist.getSongList());
                            HomeActivity.queueCurrentIndex = 0;
                            mCallback2.onPlaylistMenuPLayAll();
                        } else if (item.getTitle().equals("Delete")) {
                            HomeActivity.allPlaylists.getPlaylists().remove(position);
                            if (PlayListFragment.vpAdapter != null) {
                                PlayListFragment.vpAdapter.notifyItemRemoved(position);
                            }
                            HomeActivity.pAdapter.notifyItemRemoved(position);
                        }
                        return true;
                    }
                });
                popup.show();
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

    }
}
