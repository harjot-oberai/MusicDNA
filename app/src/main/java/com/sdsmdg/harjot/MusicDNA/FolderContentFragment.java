package com.sdsmdg.harjot.MusicDNA;


import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.Models.UnifiedTrack;
import com.squareup.leakcanary.RefWatcher;


/**
 * A simple {@link Fragment} subclass.
 */
public class FolderContentFragment extends Fragment {

    RecyclerView folderContentRecycler;
    LocalTrackListAdapter fContentAdapter;
    LinearLayoutManager mLayoutManager2;

    FloatingActionButton playAllFAB;

    onFolderContentPlayAllListener mCallback;
    onFolderContentItemClickListener mCallback2;
    folderContentAddToPlaylistListener mCallback3;

    public FolderContentFragment() {
        // Required empty public constructor
    }

    public interface onFolderContentPlayAllListener {
        void onFolderContentPlayAll();
    }

    public interface onFolderContentItemClickListener {
        void onFolderContentItemClick(int position);
    }

    public interface folderContentAddToPlaylistListener {
        public void addToPlaylist(UnifiedTrack ut);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (onFolderContentPlayAllListener) context;
            mCallback2 = (onFolderContentItemClickListener) context;
            mCallback3 = (folderContentAddToPlaylistListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_folder_content, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        folderContentRecycler = (RecyclerView) view.findViewById(R.id.folder_content_recycler);
        fContentAdapter = new LocalTrackListAdapter(HomeActivity.tempFolderContent, getContext());
        mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        folderContentRecycler.setLayoutManager(mLayoutManager2);
        folderContentRecycler.setItemAnimator(new DefaultItemAnimator());
        folderContentRecycler.setAdapter(fContentAdapter);

        folderContentRecycler.addOnItemTouchListener(new ClickItemTouchListener(folderContentRecycler) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                LocalTrack track = HomeActivity.tempMusicFolder.getLocalTracks().get(position);
                if (HomeActivity.queue.getQueue().size() == 0) {
                    HomeActivity.queueCurrentIndex = 0;
                    HomeActivity.queue.getQueue().add(new UnifiedTrack(true, track, null));
                } else if (HomeActivity.queueCurrentIndex == HomeActivity.queue.getQueue().size() - 1) {
                    HomeActivity.queueCurrentIndex++;
                    HomeActivity.queue.getQueue().add(new UnifiedTrack(true, track, null));
                } else if (HomeActivity.isReloaded) {
                    HomeActivity.isReloaded = false;
                    HomeActivity.queueCurrentIndex = HomeActivity.queue.getQueue().size();
                    HomeActivity.queue.getQueue().add(new UnifiedTrack(true, track, null));
                } else {
                    HomeActivity.queue.getQueue().add(++HomeActivity.queueCurrentIndex, new UnifiedTrack(true, track, null));
                }
                HomeActivity.localSelectedTrack = track;
                HomeActivity.streamSelected = false;
                HomeActivity.localSelected = true;
                HomeActivity.queueCall = false;
                HomeActivity.isReloaded = false;
                mCallback2.onFolderContentItemClick(position);
                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                PopupMenu popup = new PopupMenu(getContext(), view);
                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Add to Playlist")) {
                            mCallback3.addToPlaylist(new UnifiedTrack(true, HomeActivity.tempMusicFolder.getLocalTracks().get(position), null));
                            HomeActivity.pAdapter.notifyDataSetChanged();
                        }
                        if (item.getTitle().equals("Add to Queue")) {
                            Log.d("QUEUE", "CALLED");
                            HomeActivity.queue.getQueue().add(new UnifiedTrack(true, HomeActivity.tempMusicFolder.getLocalTracks().get(position), null));
                        }
                        if (item.getTitle().equals("Play")) {
                            LocalTrack track = HomeActivity.tempMusicFolder.getLocalTracks().get(position);
                            if (HomeActivity.queue.getQueue().size() == 0) {
                                HomeActivity.queueCurrentIndex = 0;
                                HomeActivity.queue.getQueue().add(new UnifiedTrack(true, track, null));
                            } else if (HomeActivity.queueCurrentIndex == HomeActivity.queue.getQueue().size() - 1) {
                                HomeActivity.queueCurrentIndex++;
                                HomeActivity.queue.getQueue().add(new UnifiedTrack(true, track, null));
                            } else if (HomeActivity.isReloaded) {
                                HomeActivity.isReloaded = false;
                                HomeActivity.queueCurrentIndex = HomeActivity.queue.getQueue().size();
                                HomeActivity.queue.getQueue().add(new UnifiedTrack(true, track, null));
                            } else {
                                HomeActivity.queue.getQueue().add(++HomeActivity.queueCurrentIndex, new UnifiedTrack(true, track, null));
                            }
                            HomeActivity.localSelectedTrack = track;
                            HomeActivity.streamSelected = false;
                            HomeActivity.localSelected = true;
                            HomeActivity.queueCall = false;
                            HomeActivity.isReloaded = false;
                            mCallback2.onFolderContentItemClick(position);
                        }
                        if (item.getTitle().equals("Play Next")) {
                            LocalTrack track = HomeActivity.tempMusicFolder.getLocalTracks().get(position);
                            if (HomeActivity.queue.getQueue().size() == 0) {
                                HomeActivity.queueCurrentIndex = 0;
                                HomeActivity.queue.getQueue().add(new UnifiedTrack(true, track, null));
                                HomeActivity.localSelectedTrack = track;
                                HomeActivity.streamSelected = false;
                                HomeActivity.localSelected = true;
                                HomeActivity.queueCall = false;
                                HomeActivity.isReloaded = false;
                                mCallback2.onFolderContentItemClick(position);
                            } else if (HomeActivity.queueCurrentIndex == HomeActivity.queue.getQueue().size() - 1) {
                                HomeActivity.queue.getQueue().add(new UnifiedTrack(true, track, null));
                            } else if (HomeActivity.isReloaded) {
                                HomeActivity.isReloaded = false;
                                HomeActivity.queueCurrentIndex = HomeActivity.queue.getQueue().size();
                                HomeActivity.queue.getQueue().add(new UnifiedTrack(true, track, null));
                                HomeActivity.localSelectedTrack = track;
                                HomeActivity.streamSelected = false;
                                HomeActivity.localSelected = true;
                                HomeActivity.queueCall = false;
                                HomeActivity.isReloaded = false;
                                mCallback2.onFolderContentItemClick(position);
                            } else {
                                HomeActivity.queue.getQueue().add(HomeActivity.queueCurrentIndex + 1, new UnifiedTrack(true, track, null));
                            }
                        }
                        if (item.getTitle().equals("Add to Favourites")) {
                            UnifiedTrack ut = new UnifiedTrack(true, HomeActivity.tempMusicFolder.getLocalTracks().get(position), null);
                            HomeActivity.addToFavourites(ut);
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

        playAllFAB = (FloatingActionButton) view.findViewById(R.id.folder_play_all_fab);
        playAllFAB.setBackgroundTintList(ColorStateList.valueOf(HomeActivity.themeColor));
        playAllFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFolderContentPlayAll();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mLayoutManager2.scrollToPositionWithOffset(0, 0);
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
}
