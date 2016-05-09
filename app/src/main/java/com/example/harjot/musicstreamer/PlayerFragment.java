package com.example.harjot.musicstreamer;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.harjot.musicstreamer.Models.Track;
import com.squareup.picasso.Picasso;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {

    public static VisualizerView mVisualizerView;
    TextView title;
    static ImageView mController;

    public static MediaPlayer mMediaPlayer;

    private TextView mSelectedTrackTitle;
    private ImageView mSelectedTrackImage;

    public static Visualizer mVisualizer;

    public static int durationInMilliSec;

    static long startTime = 0;

    static long pauseTime = 0;
    static long resumeTime = 0;

    static long totalPauseTime = 0;

    static boolean pauseClicked  = false;

    static Track track;

    public PlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        MainActivity.toolbar.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVisualizerView = (VisualizerView) view.findViewById(R.id.myvisualizerview);
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
                mController.setImageResource(R.drawable.ic_play);
            }
        });

        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());

        title = (TextView) view.findViewById(R.id.selected_track_title);
        mSelectedTrackImage = (ImageView) view.findViewById(R.id.selected_track_image);
        mController = (ImageView) view.findViewById(R.id.player_control);

        track = StreamMusicFragment.selectedTrack;

        mMediaPlayer.stop();
        mMediaPlayer.reset();
        durationInMilliSec = track.getDuration();

        Picasso.with(getContext()).load(track.getArtworkURL()).resize(100,100).into(mSelectedTrackImage);

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

        mController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pauseClicked){
                    pauseClicked = true;
                }
                togglePlayPause();
            }
        });
        title.setText(track.getTitle());
    }

    public static void setupVisualizerFxAndUI() {
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
                }, Visualizer.getMaxCaptureRate() / 2, false, true);
    }

    public static void togglePlayPause() {
        if (mMediaPlayer.isPlaying()) {
            pauseTime = System.currentTimeMillis();
            mVisualizer.release();
            mMediaPlayer.pause();
            mController.setImageResource(R.drawable.ic_play);
        } else {
            if(pauseClicked){
                resumeTime = System.currentTimeMillis();
            }
            setupVisualizerFxAndUI();
            mVisualizer.setEnabled(true);
            mMediaPlayer.start();
            mController.setImageResource(R.drawable.ic_pause);
        }
    }

    @Override
    public void onDestroyView() {
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

}
