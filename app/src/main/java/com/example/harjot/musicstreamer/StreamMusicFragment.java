package com.example.harjot.musicstreamer;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.harjot.musicstreamer.Models.Track;
import com.example.harjot.musicstreamer.Models.UnifiedTrack;


/**
 * A simple {@link Fragment} subclass.
 */
public class StreamMusicFragment extends Fragment {

    private StreamTrackListAdapter adapter;
    static OnTrackSelectedListener mCallback;
    Context ctx;

    static ListView lv;

    public StreamMusicFragment() {
        // Required empty public constructor
    }

    public interface OnTrackSelectedListener {
        public void onTrackSelected(int position);
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
        lv = (ListView) view.findViewById(R.id.trackList);

        adapter = new StreamTrackListAdapter(HomeActivity.ctx, HomeActivity.streamingTrackList);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu popup = new PopupMenu(HomeActivity.ctx, view);
                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

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
                        }
                        if (item.getTitle().equals("Play Next")) {
                            Track track = HomeActivity.streamingTrackList.get(position);
                            if (HomeActivity.queue.getQueue().size() == 0) {
                                HomeActivity.queueCurrentIndex = 0;
                                HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
                                HomeActivity.selectedTrack = track;
                                HomeActivity.streamSelected = true;
                                HomeActivity.localSelected = false;
                                HomeActivity.queueCall = false;
                                HomeActivity.isReloaded = false;
                                mCallback.onTrackSelected(position);
                            } else if (HomeActivity.queueCurrentIndex == HomeActivity.queue.getQueue().size() - 1) {
                                HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
                            } else if (HomeActivity.isReloaded) {
                                HomeActivity.isReloaded = false;
                                HomeActivity.queueCurrentIndex = HomeActivity.queue.getQueue().size();
                                HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
                                HomeActivity.selectedTrack = track;
                                HomeActivity.streamSelected = true;
                                HomeActivity.localSelected = false;
                                HomeActivity.queueCall = false;
                                HomeActivity.isReloaded = false;
                                mCallback.onTrackSelected(position);
                            } else {
                                HomeActivity.queue.getQueue().add(HomeActivity.queueCurrentIndex + 1, new UnifiedTrack(false, null, track));
                            }
                        }
                        return true;
                    }
                });

                popup.show();
                return false;
            }
        });
    }
}
