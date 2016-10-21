package com.sdsmdg.harjot.MusicDNA;


import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sdsmdg.harjot.MusicDNA.CustomBottomSheetDialogs.CustomGeneralBottomSheetDialog;
import com.sdsmdg.harjot.MusicDNA.Models.Track;
import com.sdsmdg.harjot.MusicDNA.Models.UnifiedTrack;
import com.squareup.leakcanary.RefWatcher;


/**
 * A simple {@link Fragment} subclass.
 */
public class StreamMusicFragment extends Fragment {

    public static StreamTrackListAdapter adapter;
    OnTrackSelectedListener mCallback;
    Context ctx;

    RecyclerView lv;

    public StreamMusicFragment() {
        // Required empty public constructor
    }

    public interface OnTrackSelectedListener {
        public void onTrackSelected(int position);

        public void addToPlaylist(UnifiedTrack ut);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnTrackSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
        ctx = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stream_music, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lv = (RecyclerView) view.findViewById(R.id.trackList);

        adapter = new StreamTrackListAdapter(getContext(), HomeActivity.streamingTrackList);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        lv.setLayoutManager(mLayoutManager2);
        lv.setItemAnimator(new DefaultItemAnimator());
        lv.setAdapter(adapter);

        lv.addOnItemTouchListener(new ClickItemTouchListener(lv) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                Track track = HomeActivity.streamingTrackList.get(position);
                if (HomeActivity.queue.getQueue().size() == 0) {
                    HomeActivity.queueCurrentIndex = 0;
                    HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
                } else if (HomeActivity.queueCurrentIndex == HomeActivity.queue.getQueue().size() - 1) {
                    HomeActivity.queueCurrentIndex++;
                    HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
                } else if (HomeActivity.isReloaded) {
                    HomeActivity.isReloaded = false;
                    HomeActivity.queueCurrentIndex = HomeActivity.queue.getQueue().size();
                    HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
                } else {
                    HomeActivity.queue.getQueue().add(++HomeActivity.queueCurrentIndex, new UnifiedTrack(false, null, track));
                }
                HomeActivity.selectedTrack = track;
                HomeActivity.streamSelected = true;
                HomeActivity.localSelected = false;
                HomeActivity.queueCall = false;
                HomeActivity.isReloaded = false;
                mCallback.onTrackSelected(position);
                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
//                PopupMenu popup = new PopupMenu(getContext(), view);
//                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
//
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//                        if (item.getTitle().equals("Add to Playlist")) {
//                            Track track = HomeActivity.streamingTrackList.get(position);
//                            mCallback.addToPlaylist(new UnifiedTrack(false, null, track));
//                            HomeActivity.pAdapter.notifyDataSetChanged();
//                        }
//                        if (item.getTitle().equals("Add to Queue")) {
//                            Track track = HomeActivity.streamingTrackList.get(position);
//                            HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
//                        }
//                        if (item.getTitle().equals("Play")) {
//                            Track track = HomeActivity.streamingTrackList.get(position);
//                            if (HomeActivity.queue.getQueue().size() == 0) {
//                                HomeActivity.queueCurrentIndex = 0;
//                                HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
//                            } else if (HomeActivity.queueCurrentIndex == HomeActivity.queue.getQueue().size() - 1) {
//                                HomeActivity.queueCurrentIndex++;
//                                HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
//                            } else if (HomeActivity.isReloaded) {
//                                HomeActivity.isReloaded = false;
//                                HomeActivity.queueCurrentIndex = HomeActivity.queue.getQueue().size();
//                                HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
//                            } else {
//                                HomeActivity.queue.getQueue().add(++HomeActivity.queueCurrentIndex, new UnifiedTrack(false, null, track));
//                            }
//                            HomeActivity.selectedTrack = track;
//                            HomeActivity.streamSelected = true;
//                            HomeActivity.localSelected = false;
//                            HomeActivity.queueCall = false;
//                            HomeActivity.isReloaded = false;
//                            mCallback.onTrackSelected(position);
//                        }
//                        if (item.getTitle().equals("Play Next")) {
//                            Track track = HomeActivity.streamingTrackList.get(position);
//                            if (HomeActivity.queue.getQueue().size() == 0) {
//                                HomeActivity.queueCurrentIndex = 0;
//                                HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
//                                HomeActivity.selectedTrack = track;
//                                HomeActivity.streamSelected = true;
//                                HomeActivity.localSelected = false;
//                                HomeActivity.queueCall = false;
//                                HomeActivity.isReloaded = false;
//                                mCallback.onTrackSelected(position);
//                            } else if (HomeActivity.queueCurrentIndex == HomeActivity.queue.getQueue().size() - 1) {
//                                HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
//                            } else if (HomeActivity.isReloaded) {
//                                HomeActivity.isReloaded = false;
//                                HomeActivity.queueCurrentIndex = HomeActivity.queue.getQueue().size();
//                                HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
//                                HomeActivity.selectedTrack = track;
//                                HomeActivity.streamSelected = true;
//                                HomeActivity.localSelected = false;
//                                HomeActivity.queueCall = false;
//                                HomeActivity.isReloaded = false;
//                                mCallback.onTrackSelected(position);
//                            } else {
//                                HomeActivity.queue.getQueue().add(HomeActivity.queueCurrentIndex + 1, new UnifiedTrack(false, null, track));
//                            }
//                        }
//                        if (item.getTitle().equals("Add to Favourites")) {
//                            UnifiedTrack ut = new UnifiedTrack(false, null, HomeActivity.streamingTrackList.get(position));
//                            HomeActivity.addToFavourites(ut);
//                        }
//                        return true;
//                    }
//                });
//
//                popup.show();
                CustomGeneralBottomSheetDialog generalBottomSheetDialog = new CustomGeneralBottomSheetDialog();
                generalBottomSheetDialog.setPosition(position);
                generalBottomSheetDialog.setTrack(new UnifiedTrack(false, null, HomeActivity.streamingTrackList.get(position)));
                generalBottomSheetDialog.setFragment("Stream");
                generalBottomSheetDialog.show(getActivity().getSupportFragmentManager(), "general_bottom_sheet_dialog");
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

    public void dataChanged() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

}
