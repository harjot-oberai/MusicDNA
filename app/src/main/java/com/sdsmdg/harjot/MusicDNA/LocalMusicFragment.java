package com.sdsmdg.harjot.MusicDNA;


//import android.app.Fragment;

import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
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

import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.Models.UnifiedTrack;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocalMusicFragment extends Fragment {

    static LocalTrackListAdapter adapter;
    static OnLocalTrackSelectedListener mCallback;
    Context ctx;

    static RecyclerView lv;

    static FloatingActionButton shuffleFab;

    public LocalMusicFragment() {
        // Required empty public constructor
    }

    public interface OnLocalTrackSelectedListener {
        void onLocalTrackSelected(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnLocalTrackSelectedListener) context;
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
        return inflater.inflate(R.layout.fragment_local_music, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shuffleFab = (FloatingActionButton) view.findViewById(R.id.play_all_fab_local);
        shuffleFab.setBackgroundTintList(ColorStateList.valueOf(HomeActivity.themeColor));
        shuffleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.queue.getQueue().clear();
                for (int i = 0; i < HomeActivity.localTrackList.size(); i++) {
                    UnifiedTrack ut = new UnifiedTrack(true, HomeActivity.localTrackList.get(i), null);
                    HomeActivity.queue.getQueue().add(ut);
                }
                Random r = new Random();
                HomeActivity.shuffleEnabled = true;
                int tmp = r.nextInt(HomeActivity.queue.getQueue().size());
                HomeActivity.queueCurrentIndex = tmp;
                LocalTrack track = HomeActivity.localTrackList.get(tmp);
                HomeActivity.localSelectedTrack = track;
                HomeActivity.streamSelected = false;
                HomeActivity.localSelected = true;
                HomeActivity.queueCall = false;
                HomeActivity.isReloaded = false;
                mCallback.onLocalTrackSelected(-1);
            }
        });
        lv = (RecyclerView) view.findViewById(R.id.localMusicList);
        adapter = new LocalTrackListAdapter(HomeActivity.finalLocalSearchResultList);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(HomeActivity.ctx, LinearLayoutManager.VERTICAL, false);
        lv.setLayoutManager(mLayoutManager2);
        lv.setItemAnimator(new DefaultItemAnimator());
        lv.setAdapter(adapter);

        lv.addOnItemTouchListener(new ClickItemTouchListener(lv) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                /*LocalTrack track = HomeActivity.finalLocalSearchResultList.get(position);
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
                mCallback.onLocalTrackSelected(position);*/

                HomeActivity.queue.getQueue().clear();
                for (int i = 0; i < HomeActivity.localTrackList.size(); i++) {
                    UnifiedTrack ut = new UnifiedTrack(true, HomeActivity.localTrackList.get(i), null);
                    HomeActivity.queue.getQueue().add(ut);
                }
                HomeActivity.queueCurrentIndex = position;
                LocalTrack track = HomeActivity.localTrackList.get(position);
                HomeActivity.localSelectedTrack = track;
                HomeActivity.streamSelected = false;
                HomeActivity.localSelected = true;
                HomeActivity.queueCall = false;
                HomeActivity.isReloaded = false;
                mCallback.onLocalTrackSelected(-1);

                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                PopupMenu popup = new PopupMenu(HomeActivity.ctx, view);
                popup.getMenuInflater().inflate(R.menu.popup_local, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Add to Playlist")) {
                            HomeActivity.showAddToPlaylistDialog(new UnifiedTrack(true, HomeActivity.finalLocalSearchResultList.get(position), null));
                            HomeActivity.pAdapter.notifyDataSetChanged();
                        }
                        if (item.getTitle().equals("Add to Queue")) {
                            Log.d("QUEUE", "CALLED");
                            HomeActivity.queue.getQueue().add(new UnifiedTrack(true, HomeActivity.finalLocalSearchResultList.get(position), null));
                        }
                        if (item.getTitle().equals("Play")) {
                            HomeActivity.queue.getQueue().clear();
                            for (int i = 0; i < HomeActivity.localTrackList.size(); i++) {
                                UnifiedTrack ut = new UnifiedTrack(true, HomeActivity.localTrackList.get(i), null);
                                HomeActivity.queue.getQueue().add(ut);
                            }
                            HomeActivity.queueCurrentIndex = position;
                            LocalTrack track = HomeActivity.localTrackList.get(position);
                            HomeActivity.localSelectedTrack = track;
                            HomeActivity.streamSelected = false;
                            HomeActivity.localSelected = true;
                            HomeActivity.queueCall = false;
                            HomeActivity.isReloaded = false;
                            mCallback.onLocalTrackSelected(-1);
                        }
                        if (item.getTitle().equals("Play Next")) {
                            LocalTrack track = HomeActivity.finalLocalSearchResultList.get(position);
                            if (HomeActivity.queue.getQueue().size() == 0) {
                                HomeActivity.queueCurrentIndex = 0;
                                HomeActivity.queue.getQueue().add(new UnifiedTrack(true, track, null));
                                HomeActivity.localSelectedTrack = track;
                                HomeActivity.streamSelected = false;
                                HomeActivity.localSelected = true;
                                HomeActivity.queueCall = false;
                                HomeActivity.isReloaded = false;
                                mCallback.onLocalTrackSelected(position);
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
                                mCallback.onLocalTrackSelected(position);
                            } else {
                                HomeActivity.queue.getQueue().add(HomeActivity.queueCurrentIndex + 1, new UnifiedTrack(true, track, null));
                            }
                        }
                        if (item.getTitle().equals("Add to Favourites")) {
                            UnifiedTrack ut = new UnifiedTrack(true, HomeActivity.finalLocalSearchResultList.get(position), null);
                            HomeActivity.addToFavourites(ut);
                        }
                        if (item.getTitle().equals("Share")) {
                            HomeActivity.shareLocalSong(HomeActivity.finalLocalSearchResultList.get(position).getPath());
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
