package com.example.harjot.musicstreamer;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.harjot.musicstreamer.Models.LocalTrack;
import com.example.harjot.musicstreamer.Models.Track;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

//import android.support.v4.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {

    public static VisualizerView mVisualizerView;
    public static MediaPlayer mMediaPlayer;
    public static Visualizer mVisualizer;

    public static ImageView mainTrackController;
    public static ImageView nextTrackController;
    public static ImageView previousTrackController;

    public static SeekBar progressBar;
    public static int durationInMilliSec;
    static boolean completed = false;
    static boolean pauseClicked = false;
    static boolean isTracking = false;

    static boolean localIsPlaying = false;

    Timer t;

    static long startTime = 0;
    static long pauseTime = 0;
    static long totalElapsedTime = 0;
    static long deltaTime = 0;
    static Track track;
    static LocalTrack localTrack;
    static reloadCurrentInstanceListener mCallback;
    long startTrack;
    long endTrack;

    public PlayerFragment() {
        // Required empty public constructor
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
//                        mVisualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
//                        mVisualizerView.updateVisualizer(bytes);
                        MainActivity.updateVisualizer(bytes);
                    }
                }, Visualizer.getMaxCaptureRate() / 2, false, true);


    }

    public static void togglePlayPause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            MainActivity.player_controller.setImageResource(R.drawable.ic_play);
            mainTrackController.setImageResource(R.drawable.ic_play);
            pauseTime = System.currentTimeMillis();
            totalElapsedTime += (pauseTime - startTime);
            mVisualizer.release();
        } else {
            if (pauseClicked) {
                startTime = System.currentTimeMillis();
            }
            if (!completed) {
                setupVisualizerFxAndUI();
                mVisualizer.setEnabled(true);
                mMediaPlayer.start();
                MainActivity.player_controller.setImageResource(R.drawable.ic_pause);
                mainTrackController.setImageResource(R.drawable.ic_pause);
            } else {
                mVisualizerView.clear();
                startTime = System.currentTimeMillis();
                totalElapsedTime = 0;
                mMediaPlayer.seekTo(0);
                setupVisualizerFxAndUI();
                mVisualizer.setEnabled(true);
                mMediaPlayer.start();
                completed = false;
                MainActivity.player_controller.setImageResource(R.drawable.ic_pause);
                mainTrackController.setImageResource(R.drawable.ic_pause);
            }
        }
    }

    public static void init() {
        startTime = System.currentTimeMillis();
        totalElapsedTime = 0;
        pauseTime = 0;
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
    public void onPause() {
        super.onPause();
        //mVisualizerView.onPauseMySurfaceView();
    }

    @Override
    public void onResume() {
        super.onResume();
        //mVisualizerView.onResumeMySurfaceView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        MainActivity.ab.setShowHideAnimationEnabled(false);
//        MainActivity.ab.hide();

        mainTrackController = (ImageView) view.findViewById(R.id.controller);
        nextTrackController = (ImageView) view.findViewById(R.id.next);
        previousTrackController = (ImageView) view.findViewById(R.id.previous);

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
                mVisualizer.release();
                MainActivity.player_controller.setImageResource(R.drawable.ic_replay);
                mainTrackController.setImageResource(R.drawable.ic_replay);
            }
        });

        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());

        track = StreamMusicFragment.selectedTrack;
        localTrack = LocalMusicFragment.selectedTrack;

        mMediaPlayer.stop();
        mMediaPlayer.reset();
        if (MainActivity.streamSelected) {
            durationInMilliSec = track.getDuration();
            if(track.getArtworkURL()!=null)
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(MainActivity.selected_track_image);
            else{
                MainActivity.selected_track_image.setImageResource(R.drawable.ic_default);
            }
            MainActivity.selected_track_title.setText(track.getTitle());
        } else {
            durationInMilliSec = (int) localTrack.getDuration();
            Bitmap temp = LocalTrackListAdapter.getAlbumArt(localTrack.getPath());
            if(temp!=null)
                MainActivity.selected_track_image.setImageBitmap(temp);
            else{
                MainActivity.selected_track_image.setImageResource(R.drawable.ic_default);
            }
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

        mainTrackController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pauseClicked) {
                    pauseClicked = true;
                }
                togglePlayPause();
            }
        });

        progressBar = (SeekBar) view.findViewById(R.id.progressBar);

        progressBar.setMax(durationInMilliSec);

        t = new Timer();
        t.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        if (!PlayerFragment.isTracking && mMediaPlayer!=null && mMediaPlayer.isPlaying() && getActivity()!=null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    float[] hsv = new float[3];
                                    hsv[0] = MainActivity.seekBarColor;
                                    hsv[1] = (float) 0.8;
                                    hsv[2] = (float) 0.5;
                                    progressBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.HSVToColor(hsv), PorterDuff.Mode.SRC_IN));
                                }
                            });
                            PlayerFragment.progressBar.setProgress(mMediaPlayer.getCurrentPosition());
                        }
                    }
                }, 0, 10);

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                startTrack = System.currentTimeMillis();
                isTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                endTrack = System.currentTimeMillis();
                deltaTime += (endTrack - startTrack);
                mMediaPlayer.seekTo(seekBar.getProgress());
                mMediaPlayer.start();
                isTracking = false;
            }
        });

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

    public interface reloadCurrentInstanceListener {
        void reloadCurrentInstance();
    }

}
