package com.sdsmdg.harjot.MusicDNA;


import android.content.Context;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

import com.squareup.leakcanary.RefWatcher;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayListFragment extends Fragment {

    RecyclerView allPlaylistRecycler;
    ViewAllPlaylistsRecyclerAdapter vpAdapter;

    LinearLayout noPlaylistContent;

    onPlaylistTouchedListener mCallback;
    onPlaylistMenuPlayAllListener mCallback2;
    onPlaylistRenameListener mCallback3;
    newPlaylistListerner mCallback4;

    FloatingActionButton addPlaylistFAB;

    public PlayListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (onPlaylistTouchedListener) context;
            mCallback2 = (onPlaylistMenuPlayAllListener) context;
            mCallback3 = (onPlaylistRenameListener) context;
            mCallback4 = (newPlaylistListerner) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public interface onPlaylistTouchedListener {
        public void onPlaylistTouched(int pos);
    }

    public interface onPlaylistMenuPlayAllListener {
        public void onPlaylistMenuPLayAll();
    }

    public interface onPlaylistRenameListener {
        public void onPlaylsitRename();
    }


    public interface newPlaylistListerner {
        public void newPlaylistListener();
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

        addPlaylistFAB = (FloatingActionButton) view.findViewById(R.id.new_playlist_fab);
        addPlaylistFAB.setBackgroundTintList(ColorStateList.valueOf(HomeActivity.themeColor));
        addPlaylistFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback4.newPlaylistListener();
            }
        });

        if (SplashActivity.tf2 != null)
            ((TextView) view.findViewById(R.id.noPlaylistContentText)).setTypeface(SplashActivity.tf2);

        if (HomeActivity.allPlaylists.getPlaylists().size() == 0) {
            allPlaylistRecycler.setVisibility(View.INVISIBLE);
            noPlaylistContent.setVisibility(View.VISIBLE);
        } else {
            allPlaylistRecycler.setVisibility(View.VISIBLE);
            noPlaylistContent.setVisibility(View.INVISIBLE);
        }
        vpAdapter = new ViewAllPlaylistsRecyclerAdapter(HomeActivity.allPlaylists.getPlaylists(), getContext());
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
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
                PopupMenu popup = new PopupMenu(getContext(), view);
                popup.getMenuInflater().inflate(R.menu.playlist_popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Play")) {
                            HomeActivity.tempPlaylist = HomeActivity.allPlaylists.getPlaylists().get(position);

                            int size = HomeActivity.tempPlaylist.getSongList().size();
                            HomeActivity.queue.getQueue().clear();
                            for (int i = 0; i < size; i++) {
                                HomeActivity.queue.addToQueue(HomeActivity.tempPlaylist.getSongList().get(i));
                            }
                            HomeActivity.queueCurrentIndex = 0;

                            mCallback2.onPlaylistMenuPLayAll();
                        } else if (item.getTitle().equals("Delete")) {
                            HomeActivity.allPlaylists.getPlaylists().remove(position);
                            if (vpAdapter != null) {
                                vpAdapter.notifyItemRemoved(position);
                            }
                            if (HomeActivity.allPlaylists.getPlaylists().size() == 0) {
                                noPlaylistContent.setVisibility(View.VISIBLE);
                            }
                            new HomeActivity.SavePlaylists().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            HomeActivity.pAdapter.notifyItemRemoved(position);
                        } else if (item.getTitle().equals("Rename")) {
                            HomeActivity.renamePlaylistNumber = position;
                            mCallback3.onPlaylsitRename();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }

    public void itemChanged(int position) {
        if (vpAdapter != null) {
            vpAdapter.notifyItemChanged(position);
        }
    }

    public void dataChanged() {
        if (vpAdapter != null) {
            vpAdapter.notifyDataSetChanged();
            if (HomeActivity.allPlaylists.getPlaylists().size() > 0) {
                noPlaylistContent.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void itemRemoved(int position) {
        if (vpAdapter != null) {
            vpAdapter.notifyItemRemoved(position);
        }
    }

}
