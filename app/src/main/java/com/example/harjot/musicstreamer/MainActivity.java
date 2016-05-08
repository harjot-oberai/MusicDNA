package com.example.harjot.musicstreamer;

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

public class MainActivity extends AppCompatActivity {

    public String searchQuery;
    private List<Track> tracks;
    private TrackListAdapter adapter;

    private MediaPlayer mMediaPlayer;
    private ImageView mPlayerControl;

    private TextView mSelectedTrackTitle;
    private ImageView mSelectedTrackImage;

    private RelativeLayout toolBar;

    VisualizerView mVisualizerView;
    private Visualizer mVisualizer;

    public static int durationInMilliSec;

    static long startTime = 0;

    EditText query;
    Button searchBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVisualizerView = (VisualizerView) findViewById(R.id.myvisualizerview);
        toolBar = (RelativeLayout) findViewById(R.id.toolBar);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                startTime = System.currentTimeMillis();
                togglePlayPause();
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerControl.setImageResource(R.drawable.ic_play);
            }
        });

        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());

        query = (EditText) findViewById(R.id.searchBox);
        searchBtn = (Button) findViewById(R.id.searchBtn);

        final ListView listView = (ListView) findViewById(R.id.trackList);
        tracks = new ArrayList<Track>();

        searchBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", "loading...");

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
                                    adapter = new TrackListAdapter(MainActivity.this, tracks);
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

        mSelectedTrackTitle = (TextView) findViewById(R.id.selected_track_title);
        mSelectedTrackImage = (ImageView) findViewById(R.id.selected_track_image);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track track = tracks.get(position);
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mVisualizer.release();
                setupVisualizerFxAndUI();
                toolBar.setVisibility(View.VISIBLE);
                mSelectedTrackTitle.setText(track.getTitle());
                durationInMilliSec = track.getDuration();
                Picasso.with(MainActivity.this).load(track.getArtworkURL()).resize(100,100).into(mSelectedTrackImage);

                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                }

                try {
                    mMediaPlayer.setDataSource(track.getStreamURL() + "?client_id=" + Config.CLIENT_ID);
                    mMediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        mPlayerControl = (ImageView) findViewById(R.id.player_control);

        mPlayerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });

    }

    private void togglePlayPause() {
        if (mMediaPlayer.isPlaying()) {
            mVisualizer.release();
            mMediaPlayer.pause();
            mPlayerControl.setImageResource(R.drawable.ic_play);
        } else {
            setupVisualizerFxAndUI();
            mVisualizer.setEnabled(true);
            mMediaPlayer.start();
            mPlayerControl.setImageResource(R.drawable.ic_pause);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && mMediaPlayer != null) {
            mVisualizer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mVisualizer.release();
            mMediaPlayer = null;
        }
    }

    private void setupVisualizerFxAndUI() {
        // Create the Visualizer object and attach it to our media player.
        Equalizer mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);
        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        mVisualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                        mVisualizerView.updateVisualizer(bytes);
                    }
                }, Visualizer.getMaxCaptureRate() / 2 , false, true);
    }

    @Override
    public void onBackPressed() {
        if (toolBar.getVisibility() == View.VISIBLE)
            toolBar.setVisibility(View.INVISIBLE);
        else
            super.onBackPressed();
    }
}
