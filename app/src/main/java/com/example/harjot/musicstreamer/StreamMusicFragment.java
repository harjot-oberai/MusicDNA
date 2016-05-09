package com.example.harjot.musicstreamer;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harjot.musicstreamer.Interfaces.StreamService;
import com.example.harjot.musicstreamer.Models.Track;
import com.squareup.picasso.Picasso;

import java.io.IOException;
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
    private TrackListAdapter adapter;
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
    ListView listView;

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
                                    adapter = new TrackListAdapter(getActivity(), tracks);
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
                Track track = tracks.get(position);
                selectedTrack = track;
                Toast.makeText(getContext(), track.getTitle(), Toast.LENGTH_SHORT).show();
                mCallback.onTrackSelected(position);

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
