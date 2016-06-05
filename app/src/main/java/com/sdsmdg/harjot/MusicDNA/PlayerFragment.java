package com.sdsmdg.harjot.MusicDNA;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.harjot.MusicDNA.Models.DNAModel;
import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.Models.SavedDNA;
import com.sdsmdg.harjot.MusicDNA.Models.Track;
import com.sdsmdg.harjot.MusicDNA.Models.UnifiedTrack;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {

    public static VisualizerView mVisualizerView;
    public static MediaPlayer mMediaPlayer;
    public static Visualizer mVisualizer;
    public static Equalizer mEqualizer;
    public static BassBoost bassBoost;
    public static PresetReverb presetReverb;

    static boolean isPrepared = false;

    static CustomProgressBar cpb;

    public static ImageView repeatIcon;
    public static ImageView shuffleIcon;

    public static ImageView equalizerIcon;
    public static ImageView mainTrackController;
    public static ImageView nextTrackController;
    public static ImageView previousTrackController;
    public static ImageView favouriteIcon;

    public static ImageView saveDNAToggle;

    boolean isFav = false;

    static RelativeLayout bottomContainer;

    public static ImageView selected_track_image;
    public static TextView selected_track_title;
    public static ImageView player_controller;

    static Toolbar smallPlayer;

    public static SeekBar progressBar;

    public static int durationInMilliSec;
    static boolean completed = false;
    static boolean pauseClicked = false;
    static boolean isTracking = false;

    public static boolean localIsPlaying = false;

    Timer t;

    public static Track track;
    public static LocalTrack localTrack;

    static boolean isRefreshed = false;

    public static onSmallPlayerTouchedListener mCallback;
    public static onCompleteListener mCallback2;
    public static onPreviousTrackListener mCallback3;
    public static onEqualizerClickedListener mCallback4;
    public static onQueueClickListener mCallback5;
    public static onPreparedLsitener mCallback6;
    public static onPlayPauseListener mCallback7;

    public static boolean isStart = true;


    long startTrack;
    long endTrack;

    public PlayerFragment() {
        // Required empty public constructor
    }

    public static void setupVisualizerFxAndUI() {
        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);
        mMediaPlayer.setAuxEffectSendLevel(1.0f);

        bassBoost = new BassBoost(0, mMediaPlayer.getAudioSessionId());
        bassBoost.setEnabled(true);
        BassBoost.Settings bassBoostSettingTemp = bassBoost.getProperties();
        BassBoost.Settings bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
        if (EqualizerFragment.bassStrength == -1) {
            bassBoostSetting.strength = (1000 / 19);
        } else {
            bassBoostSetting.strength = EqualizerFragment.bassStrength;
        }
        bassBoost.setProperties(bassBoostSetting);
        mMediaPlayer.setAuxEffectSendLevel(1.0f);

        presetReverb = new PresetReverb(0, mMediaPlayer.getAudioSessionId());
        if (EqualizerFragment.reverbPreset == -1) {
            presetReverb.setPreset(PresetReverb.PRESET_NONE);
        } else {
            presetReverb.setPreset(EqualizerFragment.reverbPreset);
        }
        presetReverb.setEnabled(true);
        mMediaPlayer.setAuxEffectSendLevel(1.0f);

        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());

        try {
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            mVisualizer.setDataCaptureListener(
                    new Visualizer.OnDataCaptureListener() {
                        public void onWaveFormDataCapture(Visualizer visualizer,
                                                          byte[] bytes, int samplingRate) {
                        }

                        public void onFftDataCapture(Visualizer visualizer,
                                                     byte[] bytes, int samplingRate) {
                            HomeActivity.updateVisualizer(bytes);
                        }
                    }, Visualizer.getMaxCaptureRate() / 2, false, true);
        } catch (Exception e) {
//            Log.d("VisualizerException", e.getMessage() + ":");
        }
    }

    public static void togglePlayPause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (HomeActivity.isPlayerVisible) {
                mainTrackController.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
                HomeActivity.playerControllerAB.setImageResource(R.drawable.ic_queue_music_white_48dp);
            } else {
                player_controller.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                mainTrackController.setImageResource(R.drawable.ic_play_arrow_white_48dp);
            }
            mVisualizer.setEnabled(false);
            if (!isStart && mCallback7 != null)
                mCallback7.onPlayPause();
        } else {
            if (!completed) {
                setupVisualizerFxAndUI();
                mVisualizer.setEnabled(true);
                if (HomeActivity.isPlayerVisible) {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
                    HomeActivity.playerControllerAB.setImageResource(R.drawable.ic_queue_music_white_48dp);
                } else {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                }
                mMediaPlayer.start();
                if (!isStart && mCallback7 != null)
                    mCallback7.onPlayPause();
            } else {
                mVisualizerView.clear();
                mMediaPlayer.seekTo(0);
                setupVisualizerFxAndUI();
                mVisualizer.setEnabled(true);
                mMediaPlayer.start();
                completed = false;
                if (HomeActivity.isPlayerVisible) {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                } else {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                }
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (onSmallPlayerTouchedListener) context;
            mCallback2 = (onCompleteListener) context;
            mCallback3 = (onPreviousTrackListener) context;
            mCallback4 = (onEqualizerClickedListener) context;
            mCallback5 = (onQueueClickListener) context;
            mCallback6 = (onPreparedLsitener) context;
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

    public interface onCompleteListener {
        public void onComplete();
    }

    public interface onPreviousTrackListener {
        public void onPreviousTrack();
    }

    public interface onEqualizerClickedListener {
        public void onEqualizerClicked();
    }

    public interface onQueueClickListener {
        public void onQueueClicked();
    }

    public interface onPreparedLsitener {
        public void onPrepared();
    }

    public interface onPlayPauseListener {
        public void onPlayPause();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repeatIcon = (ImageView) view.findViewById(R.id.repeat_icon);
        repeatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HomeActivity.repeatEnabled) {
                    HomeActivity.repeatEnabled = true;
                    repeatIcon.setImageResource(R.drawable.ic_repeat_red_48dp);
                } else {
                    HomeActivity.repeatEnabled = false;
                    repeatIcon.setImageResource(R.drawable.ic_repeat_white_48dp);
                }
            }
        });

        shuffleIcon = (ImageView) view.findViewById(R.id.shuffle_icon);
        shuffleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HomeActivity.shuffleEnabled) {
                    HomeActivity.shuffleEnabled = true;
                    shuffleIcon.setImageResource(R.drawable.ic_shuffle_red_48dp);
                } else {
                    HomeActivity.shuffleEnabled = false;
                    shuffleIcon.setImageResource(R.drawable.ic_shuffle_white_48dp);
                }
            }
        });

        equalizerIcon = (ImageView) view.findViewById(R.id.equalizer_icon);
        equalizerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback4.onEqualizerClicked();
            }
        });

        saveDNAToggle = (ImageView) view.findViewById(R.id.toggleSaveDNA);
        if (HomeActivity.isSaveDNAEnabled) {
            saveDNAToggle.setImageResource(R.drawable.ic_download_red);
        } else {
            saveDNAToggle.setImageResource(R.drawable.ic_download);
        }

        saveDNAToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HomeActivity.isSaveDNAEnabled) {
                    HomeActivity.isSaveDNAEnabled = false;
                    saveDNAToggle.setImageResource(R.drawable.ic_download);
                } else {
                    HomeActivity.isSaveDNAEnabled = true;
                    saveDNAToggle.setImageResource(R.drawable.ic_download_red);
                }
            }
        });

        mainTrackController = (ImageView) view.findViewById(R.id.controller);
        nextTrackController = (ImageView) view.findViewById(R.id.next);
        previousTrackController = (ImageView) view.findViewById(R.id.previous);

        isFav = false;

        favouriteIcon = (ImageView) view.findViewById(R.id.fav_icon);
        if (HomeActivity.isFavourite) {
            favouriteIcon.setImageResource(R.drawable.ic_heart_filled);
            isFav = true;
        } else {
            favouriteIcon.setImageResource(R.drawable.ic_heart_out);
            isFav = false;
        }

        favouriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    favouriteIcon.setImageResource(R.drawable.ic_heart_out);
                    isFav = false;
                    removeFromFavourite();
                } else {
                    favouriteIcon.setImageResource(R.drawable.ic_heart_filled);
                    isFav = true;
                    addToFavourite();
                }
            }
        });

        selected_track_image = (ImageView) view.findViewById(R.id.selected_track_image_sp);
        selected_track_title = (TextView) view.findViewById(R.id.selected_track_title_sp);
        player_controller = (ImageView) view.findViewById(R.id.player_control_sp);

        smallPlayer = (Toolbar) view.findViewById(R.id.smallPlayer);

        bottomContainer = (RelativeLayout) view.findViewById(R.id.bottomContainer);

        mVisualizerView = (VisualizerView) view.findViewById(R.id.myvisualizerview);
        cpb = (CustomProgressBar) view.findViewById(R.id.customProgress);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                completed = false;
                pauseClicked = false;
                isPrepared = true;
                mCallback6.onPrepared();
                if (HomeActivity.isPlayerVisible) {
                    player_controller.setVisibility(View.VISIBLE);
                    player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
                    HomeActivity.playerControllerAB.setImageResource(R.drawable.ic_queue_music_white_48dp);
                } else {
                    player_controller.setVisibility(View.VISIBLE);
                    player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                }
                togglePlayPause();
                togglePlayPause();
                togglePlayPause();
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                completed = true;
                if (HomeActivity.isPlayerVisible) {
                    mainTrackController.setImageResource(R.drawable.ic_replay_white_48dp);
                } else {
                    player_controller.setImageResource(R.drawable.ic_replay_white_48dp);
                    mainTrackController.setImageResource(R.drawable.ic_replay_white_48dp);
                }

                if (HomeActivity.isSaveDNAEnabled) {
                    Toast.makeText(HomeActivity.ctx, "SAVING...", Toast.LENGTH_SHORT).show();
                    if (!HomeActivity.isPlayerVisible) {
                        mVisualizerView.setVisibility(View.INVISIBLE);
                    }
                    List<Pair<Float, Float>> pts = new ArrayList<Pair<Float, Float>>();
                    List<Pair<Float, Pair<Integer, Integer>>> ptPaint = new ArrayList<Pair<Float, Pair<Integer, Integer>>>();
                    for (int i = 0; i < VisualizerView.pts.size(); i++) {
                        pts.add(VisualizerView.pts.get(i));
                        ptPaint.add(VisualizerView.ptPaint.get(i));
                    }
                    if (localIsPlaying) {
                        DNAModel model = new DNAModel(true, localTrack, null, pts, ptPaint);
                        SavedDNA sDna = new SavedDNA(localTrack.getTitle(), model);
                        HomeActivity.savedDNAs.getSavedDNAs().add(sDna);
                    } else {
                        DNAModel model = new DNAModel(false, null, track, pts, ptPaint);
                        SavedDNA sDna = new SavedDNA(track.getTitle(), model);
                        HomeActivity.savedDNAs.getSavedDNAs().add(sDna);
                    }
                }

                completed = false;
                isPrepared = false;
                mMediaPlayer.stop();
                mCallback2.onComplete();

            }
        });

        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                double ratio = percent / 100.0;
                double bufferingLevel = (int) (mp.getDuration() * ratio);
                if (progressBar != null) {
                    progressBar.setSecondaryProgress((int) bufferingLevel);
                }
            }
        });

        mMediaPlayer.setOnErrorListener(
                new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Toast.makeText(HomeActivity.ctx, what + ":" + extra, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
        );

        smallPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSmallPlayerTouched();
            }
        });


        track = HomeActivity.selectedTrack;
        localTrack = HomeActivity.localSelectedTrack;

        if (HomeActivity.streamSelected) {
            try {
                durationInMilliSec = track.getDuration();
            } catch (Exception e) {

            }
            if (track.getArtworkURL() != null) {
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(selected_track_image);
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(HomeActivity.spImgAB);
            } else {
                selected_track_image.setImageResource(R.drawable.ic_default);
                HomeActivity.spImgAB.setImageResource(R.drawable.ic_default);
            }
            HomeActivity.spTitleAB.setText(track.getTitle());
            selected_track_title.setText(track.getTitle());
        } else {
            try {
                durationInMilliSec = (int) localTrack.getDuration();
            } catch (Exception e) {

            }
            Bitmap temp = LocalTrackListAdapter.getAlbumArt(localTrack.getPath());
            if (temp != null) {
                HomeActivity.spImgAB.setImageBitmap(temp);
                selected_track_image.setImageBitmap(temp);
            } else {
                HomeActivity.spImgAB.setImageResource(R.drawable.ic_default);
                selected_track_image.setImageResource(R.drawable.ic_default);
            }
            HomeActivity.spTitleAB.setText(localTrack.getTitle());
            selected_track_title.setText(localTrack.getTitle());
        }


        try {
            if (HomeActivity.streamSelected) {
                isPrepared = false;
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(track.getStreamURL() + "?client_id=" + Config.CLIENT_ID);
                mMediaPlayer.prepareAsync();
            } else {
                isPrepared = false;
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(localTrack.getPath());
                mMediaPlayer.prepareAsync();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        player_controller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pauseClicked) {
                    pauseClicked = true;
                }
                if (!HomeActivity.isPlayerVisible)
                    togglePlayPause();
                else
                    mCallback5.onQueueClicked();
            }
        });

        HomeActivity.playerControllerAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pauseClicked) {
                    pauseClicked = true;
                }
                if (!HomeActivity.isPlayerVisible)
                    togglePlayPause();
                else
                    mCallback5.onQueueClicked();
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

        nextTrackController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                mCallback2.onComplete();
            }
        });

        previousTrackController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                mCallback3.onPreviousTrack();
            }
        });

        progressBar = (SeekBar) view.findViewById(R.id.progressBar);
        progressBar.setMax(durationInMilliSec);

        t = new Timer();
        t.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        if (isPrepared && !PlayerFragment.isTracking && getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    float[] hsv = new float[3];
                                    hsv[0] = HomeActivity.seekBarColor;
                                    hsv[1] = (float) 0.8;
                                    hsv[2] = (float) 0.5;
                                    progressBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.HSVToColor(hsv), PorterDuff.Mode.SRC_IN));
                                    cpb.update();
                                }
                            });
                            try {
                                progressBar.setProgress(mMediaPlayer.getCurrentPosition());
                            } catch (Exception e) {
                                Log.e("MEDIA", e.getMessage() + ":");
                            }
                        }
                    }
                }, 0, 50);

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
                mMediaPlayer.seekTo(seekBar.getProgress());
                mMediaPlayer.start();
                isTracking = false;
            }

        });

    }

    public static void addToFavourite() {

        UnifiedTrack ut;

        if (HomeActivity.localSelected)
            ut = new UnifiedTrack(true, localTrack, null);
        else
            ut = new UnifiedTrack(false, null, track);

        HomeActivity.favouriteTracks.getFavourite().add(ut);
    }

    public static void removeFromFavourite() {

        UnifiedTrack ut;

        if (HomeActivity.localSelected)
            ut = new UnifiedTrack(true, localTrack, null);
        else
            ut = new UnifiedTrack(false, null, track);

        for (int i = 0; i < HomeActivity.favouriteTracks.getFavourite().size(); i++) {
            UnifiedTrack ut1 = HomeActivity.favouriteTracks.getFavourite().get(i);
            if (ut.getType() && ut1.getType()) {
                if (ut.getLocalTrack().getTitle().equals(ut1.getLocalTrack().getTitle())) {
                    HomeActivity.favouriteTracks.getFavourite().remove(i);
                    break;
                }
            } else if (!ut.getType() && !ut1.getType()) {
                if (ut.getStreamTrack().getTitle().equals(ut1.getStreamTrack().getTitle())) {
                    HomeActivity.favouriteTracks.getFavourite().remove(i);
                    break;
                }
            }
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

    public interface onSmallPlayerTouchedListener {
        void onSmallPlayerTouched();
    }

    public void refresh() {

        isRefreshed = true;

        mVisualizerView.clear();
        pauseClicked = false;
        completed = false;
        isTracking = false;

        if (HomeActivity.isPlayerVisible) {
            player_controller.setVisibility(View.VISIBLE);
            player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
            HomeActivity.playerControllerAB.setImageResource(R.drawable.ic_queue_music_white_48dp);
        } else {
            player_controller.setVisibility(View.VISIBLE);
            player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
        }

        isFav = false;

        if (HomeActivity.isFavourite) {
            favouriteIcon.setImageResource(R.drawable.ic_heart_filled);
            isFav = true;
        } else {
            favouriteIcon.setImageResource(R.drawable.ic_heart_out);
            isFav = false;
        }

        if (HomeActivity.isSaveDNAEnabled) {
            saveDNAToggle.setImageResource(R.drawable.ic_download_red);
        } else {
            saveDNAToggle.setImageResource(R.drawable.ic_download);
        }


        track = HomeActivity.selectedTrack;
        localTrack = HomeActivity.localSelectedTrack;

        if (HomeActivity.streamSelected) {
            durationInMilliSec = track.getDuration();
            if (track.getArtworkURL() != null) {
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(selected_track_image);
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(HomeActivity.spImgAB);
            } else {
                selected_track_image.setImageResource(R.drawable.ic_default);
                HomeActivity.spImgAB.setImageResource(R.drawable.ic_default);
            }
            HomeActivity.spTitleAB.setText(track.getTitle());
            selected_track_title.setText(track.getTitle());
        } else {
            durationInMilliSec = (int) localTrack.getDuration();
            Bitmap temp = LocalTrackListAdapter.getAlbumArt(localTrack.getPath());
            if (temp != null) {
                HomeActivity.spImgAB.setImageBitmap(temp);
                selected_track_image.setImageBitmap(temp);
            } else {
                HomeActivity.spImgAB.setImageResource(R.drawable.ic_default);
                selected_track_image.setImageResource(R.drawable.ic_default);
            }
            HomeActivity.spTitleAB.setText(localTrack.getTitle());
            selected_track_title.setText(localTrack.getTitle());
        }

        try {
            if (HomeActivity.streamSelected) {
                isPrepared = false;
                mMediaPlayer.reset();
                progressBar.setProgress(0);
                progressBar.setSecondaryProgress(0);
                mMediaPlayer.setDataSource(track.getStreamURL() + "?client_id=" + Config.CLIENT_ID);
                mMediaPlayer.prepareAsync();
            } else {
                isPrepared = false;
                mMediaPlayer.reset();
                progressBar.setProgress(0);
                progressBar.setSecondaryProgress(0);
                mMediaPlayer.setDataSource(localTrack.getPath());
                mMediaPlayer.prepareAsync();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        progressBar.setMax(durationInMilliSec);

        t.cancel();
        t = new Timer();
        t.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        if (isPrepared && !PlayerFragment.isTracking && getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    float[] hsv = new float[3];
                                    hsv[0] = HomeActivity.seekBarColor;
                                    hsv[1] = (float) 0.8;
                                    hsv[2] = (float) 0.5;
                                    progressBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.HSVToColor(hsv), PorterDuff.Mode.SRC_IN));
                                    cpb.update();
                                }
                            });
                            progressBar.setProgress(mMediaPlayer.getCurrentPosition());
                        }
                    }
                }, 0, 50);

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
                mMediaPlayer.seekTo(seekBar.getProgress());
                mMediaPlayer.start();
                isTracking = false;
            }

        });
    }

}
