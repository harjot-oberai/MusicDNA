package com.sdsmdg.harjot.MusicDNA;


import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.sdsmdg.harjot.MusicDNA.Models.DNAModel;
import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.Models.SavedDNA;
import com.sdsmdg.harjot.MusicDNA.Models.Track;
import com.sdsmdg.harjot.MusicDNA.Models.UnifiedTrack;
import com.sdsmdg.harjot.MusicDNA.NotificationManager.AudioPlayerBroadcastReceiver;
import com.sdsmdg.harjot.MusicDNA.imageLoader.ImageLoader;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment implements
        Serializable,
        AudioPlayerBroadcastReceiver.onCallbackListener {

    public static VisualizerView mVisualizerView;
    public static MediaPlayer mMediaPlayer;
    public static Visualizer mVisualizer;
    public static Equalizer mEqualizer;
    public static BassBoost bassBoost;
    public static PresetReverb presetReverb;

    private static final long serialVersionUID = 1L;

    static boolean isPrepared = false;

    private float x1, x2;
    static final int MIN_DISTANCE = 200;

    View bufferingIndicator;

    static View fullscreenExtraSpaceOccupier;

    static CustomProgressBar cpb;

    Pair<String, String> temp;

    TextView currTime, totalTime;

    public static ImageView repeatIcon;

    public ImageView equalizerIcon;
    public static ImageView mainTrackController;
    public ImageView nextTrackController;
    public ImageView previousTrackController;
    public ImageView favouriteIcon;
    public ImageView queueIcon;

    public ImageView saveDNAToggle;

    boolean isFav = false;

    static RelativeLayout bottomContainer;
    static RelativeLayout seekBarContainer;
    static RelativeLayout toggleContainer;

    public static ImageView selected_track_image;
    public static TextView selected_track_title;
    public static ImageView player_controller;

    static Toolbar smallPlayer;

    ImageLoader imgLoader;

    public static SeekBar progressBar;

    public static int durationInMilliSec;
    boolean completed = false;
    boolean pauseClicked = false;
    boolean isTracking = false;

    public static boolean localIsPlaying = false;

    Timer t;

    public static Track track;
    public static LocalTrack localTrack;

    static boolean isRefreshed = false;

    public onSmallPlayerTouchedListener mCallback;
    public onCompleteListener mCallback2;
    public onPreviousTrackListener mCallback3;
    public onEqualizerClickedListener mCallback4;
    public onQueueClickListener mCallback5;
    public onPreparedLsitener mCallback6;
    public onPlayPauseListener mCallback7;
    public fullScreenListener mCallback8;
    public onSettingsClickedListener mCallback9;

    static ImageView currentAlbumArtHolder;

    public boolean isStart = true;

    ShowcaseView showCase;

    SlidingRelativeLayout rootView;

    long startTrack;
    long endTrack;

    public PlayerFragment() {
        // Required empty public constructor
    }

    public void setupVisualizerFxAndUI() {

        try {
            mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
            mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
            mEqualizer.setEnabled(true);
            mMediaPlayer.setAuxEffectSendLevel(1.0f);
        } catch (Exception e) {

        }

        if (HomeActivity.isEqualizerEnabled) {
            bassBoost = new BassBoost(0, mMediaPlayer.getAudioSessionId());
            bassBoost.setEnabled(true);
            BassBoost.Settings bassBoostSettingTemp = bassBoost.getProperties();
            BassBoost.Settings bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
            if (HomeActivity.bassStrength == -1) {
                bassBoostSetting.strength = (1000 / 19);
            } else {
                bassBoostSetting.strength = HomeActivity.bassStrength;
            }
            bassBoost.setProperties(bassBoostSetting);
            mMediaPlayer.setAuxEffectSendLevel(1.0f);

            presetReverb = new PresetReverb(0, mMediaPlayer.getAudioSessionId());
            if (HomeActivity.reverbPreset == -1) {
                presetReverb.setPreset(PresetReverb.PRESET_NONE);
            } else {
                presetReverb.setPreset(HomeActivity.reverbPreset);
            }
            presetReverb.setEnabled(true);
            mMediaPlayer.setAuxEffectSendLevel(1.0f);
        }


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

    public void togglePlayPause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (HomeActivity.isPlayerVisible) {
                mainTrackController.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
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
            mCallback8 = (fullScreenListener) context;
            mCallback9 = (onSettingsClickedListener) context;
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
    public void onCallbackCalled(int i) {
        switch (i) {
            case 6:
                mCallback6.onPrepared();
                break;
            case 3:
                mCallback3.onPreviousTrack();
                break;
            case 2:
                mCallback2.onComplete();
                break;
        }
    }

    @Override
    public void togglePLayPauseCallback() {
        togglePlayPause();
    }

    @Override
    public boolean getPauseClicked() {
        return pauseClicked;
    }

    @Override
    public void setPauseClicked(boolean bool) {
        pauseClicked = bool;
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
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

    public interface fullScreenListener {
        public void onFullScreen();
    }

    public interface onSettingsClickedListener {
        public void onSettingsClicked();
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgLoader = new ImageLoader(getContext());

        rootView = (SlidingRelativeLayout) view.findViewById(R.id.root_view);

        currentAlbumArtHolder = (ImageView) view.findViewById(R.id.current_album_art_holder);

        fullscreenExtraSpaceOccupier = view.findViewById(R.id.fullscreen_extra_space_occupier);

        bufferingIndicator = view.findViewById(R.id.bufferingIndicator);
        currTime = (TextView) view.findViewById(R.id.currTime);
        totalTime = (TextView) view.findViewById(R.id.totalTime);

        repeatIcon = (ImageView) view.findViewById(R.id.repeat_icon);
        if (HomeActivity.shuffleEnabled) {
            repeatIcon.setImageResource(R.drawable.ic_shuffle_white_48dp);
        } else if (HomeActivity.repeatEnabled) {
            repeatIcon.setImageResource(R.drawable.ic_repeat_white_48dp);
        } else if (HomeActivity.repeatOnceEnabled) {
            repeatIcon.setImageResource(R.drawable.ic_repeat_once_white_48dp);
        }
        repeatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HomeActivity.shuffleEnabled) {
                    HomeActivity.shuffleEnabled = false;
                    HomeActivity.repeatEnabled = true;
                    repeatIcon.setImageResource(R.drawable.ic_repeat_white_48dp);
                } else if (HomeActivity.repeatEnabled) {
                    HomeActivity.repeatEnabled = false;
                    HomeActivity.repeatOnceEnabled = true;
                    repeatIcon.setImageResource(R.drawable.ic_repeat_once_white_48dp);
                } else if (HomeActivity.repeatOnceEnabled) {
                    HomeActivity.repeatOnceEnabled = false;
                    HomeActivity.shuffleEnabled = true;
                    repeatIcon.setImageResource(R.drawable.ic_shuffle_white_48dp);
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
        equalizerIcon.setVisibility(View.INVISIBLE);

        saveDNAToggle = (ImageView) view.findViewById(R.id.toggleSaveDNA);
        if (HomeActivity.isSaveDNAEnabled) {
            saveDNAToggle.setImageResource(R.drawable.ic_save_red_2);
        } else {
            saveDNAToggle.setImageResource(R.drawable.ic_save_white_2);
        }

        saveDNAToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HomeActivity.isSaveDNAEnabled) {
                    HomeActivity.isSaveDNAEnabled = false;
                    saveDNAToggle.setImageResource(R.drawable.ic_save_white_2);
                } else {
                    HomeActivity.isSaveDNAEnabled = true;
                    saveDNAToggle.setImageResource(R.drawable.ic_save_red_2);
                }
            }
        });

        mainTrackController = (ImageView) view.findViewById(R.id.controller);
        nextTrackController = (ImageView) view.findViewById(R.id.next);
        previousTrackController = (ImageView) view.findViewById(R.id.previous);

        isFav = false;

        favouriteIcon = (ImageView) view.findViewById(R.id.fav_icon);
        if (HomeActivity.isFavourite) {
            favouriteIcon.setImageResource(R.drawable.ic_heart_filled_1);
            isFav = true;
        } else {
            favouriteIcon.setImageResource(R.drawable.ic_heart_out_1);
            isFav = false;
        }

        favouriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    favouriteIcon.setImageResource(R.drawable.ic_heart_out_1);
                    isFav = false;
                    removeFromFavourite();
                } else {
                    favouriteIcon.setImageResource(R.drawable.ic_heart_filled_1);
                    isFav = true;
                    addToFavourite();
                }
                new HomeActivity.SaveFavourites().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        queueIcon = (ImageView) view.findViewById(R.id.queue_icon);
        queueIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback5.onQueueClicked();
            }
        });

        selected_track_image = (ImageView) view.findViewById(R.id.selected_track_image_sp);
        selected_track_title = (TextView) view.findViewById(R.id.selected_track_title_sp);
        player_controller = (ImageView) view.findViewById(R.id.player_control_sp);

        smallPlayer = (Toolbar) view.findViewById(R.id.smallPlayer);

        bottomContainer = (RelativeLayout) view.findViewById(R.id.mainControllerContainer);
        seekBarContainer = (RelativeLayout) view.findViewById(R.id.seekBarContainer);
        toggleContainer = (RelativeLayout) view.findViewById(R.id.toggleContainer);

        mVisualizerView = (VisualizerView) view.findViewById(R.id.myvisualizerview);

        mVisualizerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (HomeActivity.isFullScreenEnabled) {
                    HomeActivity.isFullScreenEnabled = false;
                    bottomContainer.setVisibility(View.VISIBLE);
                    seekBarContainer.setVisibility(View.VISIBLE);
                    toggleContainer.setVisibility(View.VISIBLE);
                    HomeActivity.spToolbar.setVisibility(View.VISIBLE);
                    fullscreenExtraSpaceOccupier.getLayoutParams().height = 0;
                    mCallback8.onFullScreen();
                } else {
                    HomeActivity.isFullScreenEnabled = true;
                    bottomContainer.setVisibility(View.INVISIBLE);
                    seekBarContainer.setVisibility(View.INVISIBLE);
                    toggleContainer.setVisibility(View.INVISIBLE);
                    HomeActivity.spToolbar.setVisibility(View.INVISIBLE);
                    fullscreenExtraSpaceOccupier.getLayoutParams().height = HomeActivity.statusBarHeightinDp;
                    mCallback8.onFullScreen();
                }
                return true;
            }
        });

        mVisualizerView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                x1 = event.getX();
                                break;
                            case MotionEvent.ACTION_UP:
                                x2 = event.getX();
                                float deltaX = x2 - x1;

                                if (Math.abs(deltaX) > MIN_DISTANCE) {
                                    if (x2 > x1) {
                                        mMediaPlayer.pause();
                                        mCallback3.onPreviousTrack();
                                        return false;
                                    } else {
                                        mMediaPlayer.pause();
                                        HomeActivity.nextControllerClicked = true;
                                        mCallback2.onComplete();
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                        }
                        return false;
                    }
                }
        );

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
                } else {
                    player_controller.setVisibility(View.VISIBLE);
                    player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                }
//                setupVisualizerFxAndUI();
//                mVisualizer.setEnabled(true);
                togglePlayPause();
                togglePlayPause();
                togglePlayPause();
                bufferingIndicator.setVisibility(View.GONE);
                equalizerIcon.setVisibility(View.VISIBLE);

                new HomeActivity.SaveQueue().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                new HomeActivity.SaveRecents().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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
                new SaveDNA().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                        Toast.makeText(getContext(), what + ":" + extra, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
        );

        mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        bufferingIndicator.setVisibility(View.VISIBLE);
                        isPrepared = false;
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        bufferingIndicator.setVisibility(View.GONE);
                        isPrepared = true;
                        break;
                }
                return true;
            }
        });

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
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(currentAlbumArtHolder);
            } else {
                selected_track_image.setImageResource(R.drawable.ic_default);
                HomeActivity.spImgAB.setImageResource(R.drawable.ic_default);
                currentAlbumArtHolder.setImageResource(R.drawable.ic_default);
            }
            HomeActivity.spTitleAB.setText(track.getTitle());
            selected_track_title.setText(track.getTitle());
        } else {
            try {
                durationInMilliSec = (int) localTrack.getDuration();
            } catch (Exception e) {

            }
            try {
                imgLoader.DisplayImage(localTrack.getPath(), HomeActivity.spImgAB);
                imgLoader.DisplayImage(localTrack.getPath(), selected_track_image);
                imgLoader.DisplayImage(localTrack.getPath(), currentAlbumArtHolder);
            } catch (Exception e) {

            }
            HomeActivity.spTitleAB.setText(localTrack.getTitle());
            selected_track_title.setText(localTrack.getTitle());
        }

        temp = getTime(durationInMilliSec);
        totalTime.setText(temp.first + ":" + temp.second);

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
            bufferingIndicator.setVisibility(View.VISIBLE);
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

        HomeActivity.overflowMenuAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popMenu = new PopupMenu(getContext(), HomeActivity.overflowMenuAB);
                popMenu.getMenuInflater().inflate(R.menu.player_overflow_menu, popMenu.getMenu());

                popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Full Screen")) {
                            if (HomeActivity.isFullScreenEnabled) {
                                HomeActivity.isFullScreenEnabled = false;
                                bottomContainer.setVisibility(View.VISIBLE);
                                seekBarContainer.setVisibility(View.VISIBLE);
                                toggleContainer.setVisibility(View.VISIBLE);
                                HomeActivity.spToolbar.setVisibility(View.VISIBLE);
                                fullscreenExtraSpaceOccupier.getLayoutParams().height = 0;
                                mCallback8.onFullScreen();
                            } else {
                                HomeActivity.isFullScreenEnabled = true;
                                bottomContainer.setVisibility(View.INVISIBLE);
                                seekBarContainer.setVisibility(View.INVISIBLE);
                                toggleContainer.setVisibility(View.INVISIBLE);
                                HomeActivity.spToolbar.setVisibility(View.INVISIBLE);
                                fullscreenExtraSpaceOccupier.getLayoutParams().height = HomeActivity.statusBarHeightinDp;
                                mCallback8.onFullScreen();
                            }
                        } else if (item.getTitle().equals("Settings")) {
                            mCallback9.onSettingsClicked();
                        }
                        return true;
                    }
                });

                popMenu.show();
            }
        });

        mainTrackController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HomeActivity.hasQueueEnded) {
                    if (!pauseClicked) {
                        pauseClicked = true;
                    }
                    togglePlayPause();
                } else {
                    mCallback2.onComplete();
                }
            }
        });

        nextTrackController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.pause();
                HomeActivity.nextControllerClicked = true;
                mCallback2.onComplete();
            }
        });

        previousTrackController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.pause();
                mCallback3.onPreviousTrack();
            }
        });

        progressBar = (SeekBar) view.findViewById(R.id.progressBar);
        progressBar.setMax(durationInMilliSec);

        t = new Timer();
        t.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        if (isPrepared && !isTracking && getActivity() != null) {
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
                                temp = getTime(mMediaPlayer.getCurrentPosition());
                                HomeActivity.main.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        currTime.setText(temp.first + ":" + temp.second);
                                    }
                                });
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
                temp = getTime(progress);
                HomeActivity.main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currTime.setText(temp.first + ":" + temp.second);
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(seekBar.getProgress());
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.start();
                isTracking = false;
            }

        });

        final Button mEndButton = new Button(getContext());
        mEndButton.setBackgroundColor(HomeActivity.themeColor);
        mEndButton.setTextColor(Color.WHITE);

        Handler handler = new Handler();
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        showCase = new ShowcaseView.Builder(getActivity())
                                .blockAllTouches()
                                .singleShot(2)
                                .setStyle(R.style.CustomShowcaseTheme)
                                .useDecorViewAsParent()
                                .replaceEndButton(mEndButton)
                                .setContentTitlePaint(HomeActivity.tp)
                                .setTarget(new ViewTarget(mVisualizerView.getId(), getActivity()))
                                .setContentTitle("The DNA")
                                .setContentText("The DNA of the currently playing song.")
                                .build();
                        showCase.setButtonText("Next");
                        showCase.setButtonPosition(HomeActivity.lps);
                        showCase.overrideButtonClick(new View.OnClickListener() {
                            int count1 = -1;

                            @Override
                            public void onClick(View v) {
                                count1++;
                                switch (count1) {
                                    case 0:
                                        showCase.setTarget(new ViewTarget(mVisualizerView.getId(), getActivity()));
                                        showCase.setContentTitle("The DNA");
                                        showCase.setContentText("Swipe Left or Right to change Song." +
                                                "Long Press for fullscreen");
                                        showCase.setButtonPosition(HomeActivity.lps);
                                        showCase.setButtonText("Next");
                                        break;
                                    case 1:
                                        showCase.setTarget(new ViewTarget(R.id.toggleContainer, getActivity()));
                                        showCase.setContentTitle("The Controls");
                                        showCase.setContentText("Equalizer \n" +
                                                "Repeat Controller \n" +
                                                "Save DNA toggle\n" +
                                                "Add to Favourites \n" +
                                                "Queue");
                                        showCase.setButtonPosition(HomeActivity.lps);
                                        showCase.setButtonText("Done");
                                        break;
                                    case 2:
                                        showCase.hide();
                                        break;
                                }
                            }

                        });
                    }
                }, 500);

    }

    public void addToFavourite() {

        UnifiedTrack ut;

        if (HomeActivity.localSelected)
            ut = new UnifiedTrack(true, localTrack, null);
        else
            ut = new UnifiedTrack(false, null, track);

        HomeActivity.favouriteTracks.getFavourite().add(ut);
    }

    public void removeFromFavourite() {

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
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mVisualizer != null) {
            mVisualizer.release();
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
        } else {
            player_controller.setVisibility(View.VISIBLE);
            player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
        }

        isFav = false;

        if (HomeActivity.isFavourite) {
            favouriteIcon.setImageResource(R.drawable.ic_heart_filled_1);
            isFav = true;
        } else {
            favouriteIcon.setImageResource(R.drawable.ic_heart_out_1);
            isFav = false;
        }

        if (HomeActivity.shuffleEnabled) {
            repeatIcon.setImageResource(R.drawable.ic_shuffle_white_48dp);
        } else if (HomeActivity.repeatEnabled) {
            repeatIcon.setImageResource(R.drawable.ic_repeat_white_48dp);
        } else if (HomeActivity.repeatOnceEnabled) {
            repeatIcon.setImageResource(R.drawable.ic_repeat_once_white_48dp);
        }

        if (HomeActivity.isSaveDNAEnabled) {
            saveDNAToggle.setImageResource(R.drawable.ic_save_red_2);
        } else {
            saveDNAToggle.setImageResource(R.drawable.ic_save_white_2);
        }

        equalizerIcon.setVisibility(View.INVISIBLE);

        track = HomeActivity.selectedTrack;
        localTrack = HomeActivity.localSelectedTrack;

        if (HomeActivity.streamSelected) {
            durationInMilliSec = track.getDuration();
            if (track.getArtworkURL() != null) {
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(selected_track_image);
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(HomeActivity.spImgAB);
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(currentAlbumArtHolder);
            } else {
                selected_track_image.setImageResource(R.drawable.ic_default);
                HomeActivity.spImgAB.setImageResource(R.drawable.ic_default);
                currentAlbumArtHolder.setImageResource(R.drawable.ic_default);
            }
            HomeActivity.spTitleAB.setText(track.getTitle());
            selected_track_title.setText(track.getTitle());
        } else {
            durationInMilliSec = (int) localTrack.getDuration();
            imgLoader.DisplayImage(localTrack.getPath(), HomeActivity.spImgAB);
            imgLoader.DisplayImage(localTrack.getPath(), currentAlbumArtHolder);
            imgLoader.DisplayImage(localTrack.getPath(), selected_track_image);
            HomeActivity.spTitleAB.setText(localTrack.getTitle());
            selected_track_title.setText(localTrack.getTitle());
        }

        temp = getTime(durationInMilliSec);
        totalTime.setText(temp.first + ":" + temp.second);

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
            bufferingIndicator.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        progressBar.setMax(durationInMilliSec);

        t.cancel();
        t = new Timer();
        t.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        if (isPrepared && !isTracking && getActivity() != null) {
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
                                temp = getTime(mMediaPlayer.getCurrentPosition());
                                HomeActivity.main.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        currTime.setText(temp.first + ":" + temp.second);
                                    }
                                });
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
                temp = getTime(progress);
                HomeActivity.main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currTime.setText(temp.first + ":" + temp.second);
                    }
                });
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
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.start();
                isTracking = false;
            }

        });
    }

    public Pair<String, String> getTime(int millsec) {
        int min, sec;
        sec = millsec / 1000;
        min = sec / 60;
        sec = sec % 60;
        String minS, secS;
        minS = String.valueOf(min);
        secS = String.valueOf(sec);
        if (sec < 10) {
            secS = "0" + secS;
        }
        Pair<String, String> pair = Pair.create(minS, secS);
        return pair;
    }

    public class SaveDNA extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (HomeActivity.isSaveDNAEnabled) {
                Bitmap bmp = mVisualizerView.bmp;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                if (localIsPlaying) {
                    DNAModel model = new DNAModel(true, localTrack, null, byteArray);
                    SavedDNA sDna = new SavedDNA(localTrack.getTitle(), model);
                    HomeActivity.savedDNAs.getSavedDNAs().add(0, sDna);
                } else {
                    DNAModel model = new DNAModel(false, null, track, byteArray);
                    SavedDNA sDna = new SavedDNA(track.getTitle(), model);
                    HomeActivity.savedDNAs.getSavedDNAs().add(0, sDna);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            completed = false;
            isPrepared = false;
            mMediaPlayer.pause();
            mCallback2.onComplete();
        }
    }

    public boolean isShowcaseVisible() {
        return (showCase != null && showCase.isShowing());
    }

    public void hideShowcase() {
        showCase.hide();
    }

    public void toggleAlbumArtBackground(int visibility) {
        currentAlbumArtHolder.setVisibility(visibility);
    }
}
