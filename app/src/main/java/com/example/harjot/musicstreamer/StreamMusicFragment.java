package com.example.harjot.musicstreamer;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.harjot.musicstreamer.Interfaces.StreamService;
import com.example.harjot.musicstreamer.Models.Track;
import com.example.harjot.musicstreamer.Models.UnifiedTrack;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class StreamMusicFragment extends Fragment {

    private List<Track> tracks;
    private StreamTrackListAdapter adapter;
//
//    private static MediaPlayer mMediaPlayer;
//    private ImageView mPlayerControl;
//
//    private TextView mSelectedTrackTitle;
//    private ImageView mSelectedTrackImage;
//
//    private static Visualizer mVisualizer;
//
//    public static int durationInMilliSec;

    public static Track selectedTrack;

//    static long startTime = 0;

    OnTrackSelectedListener mCallback;

    EditText query;
    Button searchBtn;
    static ListView listView;

    public StreamMusicFragment() {
        // Required empty public constructor
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
    }

    public interface OnTrackSelectedListener {
        public void onTrackSelected(int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stream_music, container, false);
        query = (EditText) view.findViewById(R.id.searchBox);
        searchBtn = (Button) view.findViewById(R.id.searchBtn);
        listView = (ListView) view.findViewById(R.id.trackList);

        tracks = new ArrayList<Track>();

        searchBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "loading...");

                        InputMethodManager inputManager = (InputMethodManager)
                                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputManager.hideSoftInputFromWindow(searchBtn.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                        Retrofit client = new Retrofit.Builder()
                                .baseUrl(Config.API_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        StreamService ss = client.create(StreamService.class);
                        Call<List<Track>> call = ss.getTracks(query.getText().toString(), 75);
                        call.enqueue(new Callback<List<Track>>() {
                            @Override
                            public void onResponse(Response<List<Track>> response) {
                                dialog.dismiss();
                                if (response.isSuccess()) {
                                    // request successful (status code 200, 201)
                                    Log.d("RETRO", response.body() + "");
                                    tracks = response.body();
                                    adapter = new StreamTrackListAdapter(getActivity(), tracks);
                                    listView.setAdapter(adapter);
                                } else {
                                    //request not successful (like 400,401,403 etc)
                                    //Handle errors
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                                Log.d("RETRO", t.getMessage());
                            }
                        });
                    }
                }
        );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track track = HomeActivity.streamingTrackList.get(position);
                if (HomeActivity.queue.getQueue().size() == 0) {
                    HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
                } else if (HomeActivity.queueCurrentIndex == HomeActivity.queue.getQueue().size() - 1) {
                    HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
                } else if (HomeActivity.isReloaded) {
                    HomeActivity.queueCurrentIndex = HomeActivity.queue.getQueue().size();
                    HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
                } else {
                    HomeActivity.queue.getQueue().add(++HomeActivity.queueCurrentIndex, new UnifiedTrack(false, null, track));
                }
                selectedTrack = track;
                HomeActivity.streamSelected = true;
                HomeActivity.localSelected = false;
                HomeActivity.queueCall = false;
                HomeActivity.isReloaded = false;
                mCallback.onTrackSelected(position);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu popup = new PopupMenu(HomeActivity.ctx, view);
                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Add to Playlist")) {
                            HomeActivity.showAddToPlaylistDialog(new UnifiedTrack(false, null, HomeActivity.streamingTrackList.get(position)));
                            HomeActivity.pAdapter.notifyDataSetChanged();
                        }
                        if (item.getTitle().equals("Add to Queue")) {
                            HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, HomeActivity.streamingTrackList.get(position)));
                        }
                        return true;
                    }
                });
                popup.show();
                return false;
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if (mMediaPlayer != null) {
//            mVisualizer.release();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        if (mMediaPlayer != null) {
//            if (mMediaPlayer.isPlaying()) {
//                mMediaPlayer.stop();
//            }
//            mMediaPlayer.release();
//            mVisualizer.release();
//            mMediaPlayer = null;
//        }
//    }

}
