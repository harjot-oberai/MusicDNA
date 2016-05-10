package com.example.harjot.musicstreamer;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harjot.musicstreamer.Models.LocalTrack;
import com.example.harjot.musicstreamer.Models.Track;
import com.squareup.picasso.Picasso;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {

    public static VisualizerView mVisualizerView;
    public static MediaPlayer mMediaPlayer;
    public static Visualizer mVisualizer;

//    static ImageView mController;
//    private TextView mSelectedTrackTitle;
//    private ImageView mSelectedTrackImage;

    static boolean completed = false;

    public static int durationInMilliSec;
    static boolean pauseClicked = false;

    static long startTime = 0;
    static long pauseTime = 0;
    static long totalElapsedTime = 0;

    static Track track;
    static LocalTrack localTrack;

    static reloadCurrentInstanceListener mCallback;

    public PlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (reloadCurrentInstanceListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
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
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        MainActivity.ab.setShowHideAnimationEnabled(false);
//        MainActivity.ab.hide();

        mVisualizerView = (VisualizerView) view.findViewById(R.id.myvisualizerview);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                startTime = System.currentTimeMillis();
                completed = false;
                pauseClicked = false;
                togglePlayPause();
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                completed = true;
                MainActivity.player_controller.setImageResource(R.drawable.ic_replay);
            }
        });

        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());

//        mSelectedTrackTitle = (TextView) view.findViewById(R.id.selected_track_title);
//        mSelectedTrackImage = (ImageView) view.findViewById(R.id.selected_track_image);
//        mController = (ImageView) view.findViewById(R.id.player_control);

        track = StreamMusicFragment.selectedTrack;
        localTrack = LocalMusicFragment.selectedTrack;

        mMediaPlayer.stop();
        mMediaPlayer.reset();
        if (MainActivity.streamSelected) {
            durationInMilliSec = track.getDuration();
            Picasso.with(getContext()).load(track.getArtworkURL()).resize(100, 100).into(MainActivity.selected_track_image);
            MainActivity.selected_track_title.setText(track.getTitle());
        } else {
            durationInMilliSec = (int) localTrack.getDuration();
            MainActivity.selected_track_image.setImageBitmap(LocalTrackListAdapter.getAlbumArt(localTrack.getPath()));
            MainActivity.selected_track_title.setText(localTrack.getTitle());
        }


        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }

        try {
            if (MainActivity.streamSelected) {
                mMediaPlayer.setDataSource(track.getStreamURL() + "?client_id=" + Config.CLIENT_ID);
                mMediaPlayer.prepareAsync();
            } else {
                mMediaPlayer.setDataSource(localTrack.getPath());
                mMediaPlayer.prepareAsync();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        MainActivity.player_controller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pauseClicked) {
                    pauseClicked = true;
                }
                togglePlayPause();
            }
        });
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

    public interface reloadCurrentInstanceListener {
        void reloadCurrentInstance();
    }

    public static void togglePlayPause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            MainActivity.player_controller.setImageResource(R.drawable.ic_play);
            mVisualizer.release();
            pauseTime = System.currentTimeMillis();
            totalElapsedTime += (pauseTime - startTime);
        } else {
            if (pauseClicked) {
                startTime = System.currentTimeMillis();
            }
            if (!completed) {
                setupVisualizerFxAndUI();
                mVisualizer.setEnabled(true);
                mMediaPlayer.start();
                MainActivity.player_controller.setImageResource(R.drawable.ic_pause);
            } else {
                mVisualizerView.clear();
                startTime = System.currentTimeMillis();
                totalElapsedTime = 0;
                mMediaPlayer.seekTo(0);
                mMediaPlayer.start();
                completed = false;
                MainActivity.player_controller.setImageResource(R.drawable.ic_pause);
            }
        }
    }

    public static void init(){
        startTime = System.currentTimeMillis();
        totalElapsedTime = 0;
        pauseTime = 0;
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
