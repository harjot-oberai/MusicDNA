package com.sdsmdg.harjot.MusicDNA;


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
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment implements
        AudioPlayerBroadcastReceiver.onCallbackListener {

    public static VisualizerView mVisualizerView;
    public static MediaPlayer mMediaPlayer;
    public static Visualizer mVisualizer;
    public static Equalizer mEqualizer;
    public static BassBoost bassBoost;
    public static PresetReverb presetReverb;

    static boolean isReplayIconVisible = false;

    static boolean isPrepared = false;

    private float x1, x2;
    static final int MIN_DISTANCE = 200;

    View bufferingIndicator;

    static View fullscreenExtraSpaceOccupier;

    static CustomProgressBar cpb;

    Pair<String, String> temp;

    TextView currTime, totalTime;

    public ImageView repeatController;
    public ImageView shuffleController;

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

    static RelativeLayout smallPlayer;

    ImageView favControllerSp, nextControllerSp;

    ImageLoader imgLoader;

    public static SeekBar progressBar;

    public static int durationInMilliSec;
    static boolean completed = false;
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
    public onFavouritesListener mCallback10;
    public onShuffleListener mCallback11;

    HomeActivity homeActivity;
    public static Context ctx;

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

        if (homeActivity.isEqualizerEnabled) {
            try {
                bassBoost = new BassBoost(0, mMediaPlayer.getAudioSessionId());
                bassBoost.setEnabled(true);
                BassBoost.Settings bassBoostSettingTemp = bassBoost.getProperties();
                BassBoost.Settings bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
                if (homeActivity.bassStrength == -1) {
                    bassBoostSetting.strength = (1000 / 19);
                } else {
                    bassBoostSetting.strength = homeActivity.bassStrength;
                }
                bassBoost.setProperties(bassBoostSetting);
                mMediaPlayer.setAuxEffectSendLevel(1.0f);

                presetReverb = new PresetReverb(0, mMediaPlayer.getAudioSessionId());
                if (homeActivity.reverbPreset == -1) {
                    presetReverb.setPreset(PresetReverb.PRESET_NONE);
                } else {
                    presetReverb.setPreset(homeActivity.reverbPreset);
                }
                presetReverb.setEnabled(true);
                mMediaPlayer.setAuxEffectSendLevel(1.0f);
            } catch (Exception e) {

            }
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
                            homeActivity.updateVisualizer(bytes);
                        }
                    }, Visualizer.getMaxCaptureRate() / 2, false, true);
        } catch (Exception e) {
//            Log.d("VisualizerException", e.getMessage() + ":");
        }
    }

    public void togglePlayPause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (homeActivity.isPlayerVisible) {
                mainTrackController.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
                isReplayIconVisible = false;
            } else {
                player_controller.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                mainTrackController.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                isReplayIconVisible = false;
            }
            mVisualizer.setEnabled(false);
            if (!isStart && mCallback7 != null)
                mCallback7.onPlayPause();
        } else {
            if (!completed) {
                setupVisualizerFxAndUI();
                mVisualizer.setEnabled(true);
                if (homeActivity.isPlayerVisible) {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
                    isReplayIconVisible = false;
                } else {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                    isReplayIconVisible = false;
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
                if (homeActivity.isPlayerVisible) {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                    isReplayIconVisible = false;
                } else {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                    isReplayIconVisible = false;
                }
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        homeActivity = (HomeActivity) context;
        ctx = context;
        try {
            mCallback = (onSmallPlayerTouchedListener) context;
            mCallback2 = (onCompleteListener) context;
            mCallback3 = (onPreviousTrackListener) context;
            mCallback4 = (onEqualizerClickedListener) context;
            mCallback5 = (onQueueClickListener) context;
            mCallback6 = (onPreparedLsitener) context;
            mCallback8 = (fullScreenListener) context;
            mCallback9 = (onSettingsClickedListener) context;
            mCallback10 = (onFavouritesListener) context;
            mCallback11 = (onShuffleListener) context;
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
                if (homeActivity.queueCurrentIndex != 0) {
                    mMediaPlayer.pause();
                    mCallback3.onPreviousTrack();
                }
                break;
            case 2:
                mMediaPlayer.pause();
                homeActivity.nextControllerClicked = true;
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

    public interface onFavouritesListener {
        void onAddedtoFavfromPlayer();

        void onRemovedfromFavfromPlayer();
    }

    public interface onShuffleListener {
        public void onShuffleEnabled();

        public void onShuffleDisabled();
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgLoader = new ImageLoader(getContext());

        rootView = (SlidingRelativeLayout) view.findViewById(R.id.root_view);

        currentAlbumArtHolder = (ImageView) view.findViewById(R.id.current_album_art_holder);

        if (homeActivity.settings != null && homeActivity.settings.isAlbumArtBackgroundEnabled() && (currentAlbumArtHolder.getVisibility() == View.GONE || currentAlbumArtHolder.getVisibility() == View.INVISIBLE)) {
            currentAlbumArtHolder.setVisibility(View.VISIBLE);
        }

        fullscreenExtraSpaceOccupier = view.findViewById(R.id.fullscreen_extra_space_occupier);

        bufferingIndicator = view.findViewById(R.id.bufferingIndicator);
        currTime = (TextView) view.findViewById(R.id.currTime);
        totalTime = (TextView) view.findViewById(R.id.totalTime);

        repeatController = (ImageView) view.findViewById(R.id.repeat_controller);
        shuffleController = (ImageView) view.findViewById(R.id.shuffle_controller);

        if (homeActivity.shuffleEnabled) {
            shuffleController.setImageResource(R.drawable.ic_shuffle_filled);
        } else {
            shuffleController.setImageResource(R.drawable.ic_shuffle_outline);
        }

        if (homeActivity.repeatEnabled) {
            repeatController.setImageResource(R.drawable.ic_repeat_filled);
        } else if (homeActivity.repeatOnceEnabled) {
            repeatController.setImageResource(R.drawable.ic_repeat_once);
        } else {
            repeatController.setImageResource(R.drawable.ic_repeat_outline);
        }

        repeatController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeActivity.repeatOnceEnabled) {
                    homeActivity.repeatOnceEnabled = false;
                    repeatController.setImageResource(R.drawable.ic_repeat_outline);
                } else if (homeActivity.repeatEnabled) {
                    homeActivity.repeatEnabled = false;
                    homeActivity.repeatOnceEnabled = true;
                    repeatController.setImageResource(R.drawable.ic_repeat_once);
                } else {
                    homeActivity.repeatEnabled = true;
                    repeatController.setImageResource(R.drawable.ic_repeat_filled);
                }
            }
        });

        shuffleController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeActivity.shuffleEnabled) {
                    homeActivity.shuffleEnabled = false;
                    shuffleController.setImageResource(R.drawable.ic_shuffle_outline);
                    mCallback11.onShuffleDisabled();
                } else {
                    homeActivity.shuffleEnabled = true;
                    shuffleController.setImageResource(R.drawable.ic_shuffle_filled);
                    mCallback11.onShuffleEnabled();
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
        if (homeActivity.isSaveDNAEnabled) {
            saveDNAToggle.setImageResource(R.drawable.ic_save_filled);
        } else {
            saveDNAToggle.setImageResource(R.drawable.ic_save_outline);
        }

        saveDNAToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeActivity.isSaveDNAEnabled) {
                    homeActivity.isSaveDNAEnabled = false;
                    saveDNAToggle.setImageResource(R.drawable.ic_save_outline);
                } else {
                    homeActivity.isSaveDNAEnabled = true;
                    saveDNAToggle.setImageResource(R.drawable.ic_save_filled);
                }
            }
        });

        mainTrackController = (ImageView) view.findViewById(R.id.controller);
        nextTrackController = (ImageView) view.findViewById(R.id.next);
        previousTrackController = (ImageView) view.findViewById(R.id.previous);

        isFav = false;

        favouriteIcon = (ImageView) view.findViewById(R.id.fav_icon);
        favControllerSp = (ImageView) view.findViewById(R.id.fav_controller_sp);

        if (homeActivity.isFavourite) {
            favouriteIcon.setImageResource(R.drawable.ic_heart_filled_1);
            favControllerSp.setImageResource(R.drawable.ic_heart_filled_1);
            isFav = true;
        } else {
            favouriteIcon.setImageResource(R.drawable.ic_heart_out_1);
            favControllerSp.setImageResource(R.drawable.ic_heart_out_1);
            isFav = false;
        }

        favouriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    favouriteIcon.setImageResource(R.drawable.ic_heart_out_1);
                    favControllerSp.setImageResource(R.drawable.ic_heart_out_1);
                    isFav = false;
                    removeFromFavourite();
                } else {
                    favouriteIcon.setImageResource(R.drawable.ic_heart_filled_1);
                    favControllerSp.setImageResource(R.drawable.ic_heart_filled_1);
                    isFav = true;
                    addToFavourite();
                }
                new HomeActivity.SaveFavourites().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        favControllerSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    favouriteIcon.setImageResource(R.drawable.ic_heart_out_1);
                    favControllerSp.setImageResource(R.drawable.ic_heart_out_1);
                    isFav = false;
                    removeFromFavourite();
                } else {
                    favouriteIcon.setImageResource(R.drawable.ic_heart_filled_1);
                    favControllerSp.setImageResource(R.drawable.ic_heart_filled_1);
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

        smallPlayer = (RelativeLayout) view.findViewById(R.id.smallPlayer);

        nextControllerSp = (ImageView) view.findViewById(R.id.next_controller_sp);

        nextControllerSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.pause();
                homeActivity.nextControllerClicked = true;
                mCallback2.onComplete();
            }
        });

        bottomContainer = (RelativeLayout) view.findViewById(R.id.mainControllerContainer);
        seekBarContainer = (RelativeLayout) view.findViewById(R.id.seekBarContainer);
        toggleContainer = (RelativeLayout) view.findViewById(R.id.toggleContainer);

        mVisualizerView = (VisualizerView) view.findViewById(R.id.myvisualizerview);

        mVisualizerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (homeActivity.isFullScreenEnabled) {
                    homeActivity.isFullScreenEnabled = false;
                    bottomContainer.setVisibility(View.VISIBLE);
                    seekBarContainer.setVisibility(View.VISIBLE);
                    toggleContainer.setVisibility(View.VISIBLE);
                    homeActivity.spToolbar.setVisibility(View.VISIBLE);
                    fullscreenExtraSpaceOccupier.getLayoutParams().height = 0;
                    mCallback8.onFullScreen();
                } else {
                    homeActivity.isFullScreenEnabled = true;
                    bottomContainer.setVisibility(View.INVISIBLE);
                    seekBarContainer.setVisibility(View.INVISIBLE);
                    toggleContainer.setVisibility(View.INVISIBLE);
                    homeActivity.spToolbar.setVisibility(View.INVISIBLE);
                    fullscreenExtraSpaceOccupier.getLayoutParams().height = homeActivity.statusBarHeightinDp;
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
                                        homeActivity.nextControllerClicked = true;
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
                if (homeActivity.isPlayerVisible) {
                    player_controller.setVisibility(View.VISIBLE);
                    player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
                } else {
                    player_controller.setVisibility(View.VISIBLE);
                    player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                }
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
                if (homeActivity.isPlayerVisible) {
                    mainTrackController.setImageResource(R.drawable.ic_replay_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_replay_white_48dp);
                    isReplayIconVisible = true;
                } else {
                    player_controller.setImageResource(R.drawable.ic_replay_white_48dp);
                    mainTrackController.setImageResource(R.drawable.ic_replay_white_48dp);
                    isReplayIconVisible = true;
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


        track = homeActivity.selectedTrack;
        localTrack = homeActivity.localSelectedTrack;

        if (homeActivity.streamSelected) {
            try {
                durationInMilliSec = track.getDuration();
            } catch (Exception e) {

            }
            if (track.getArtworkURL() != null) {
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(selected_track_image);
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(homeActivity.spImgAB);
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(currentAlbumArtHolder);
            } else {
                selected_track_image.setImageResource(R.drawable.ic_default);
                homeActivity.spImgAB.setImageResource(R.drawable.ic_default);
                currentAlbumArtHolder.setImageResource(R.drawable.ic_default);
            }
            try {
                homeActivity.spTitleAB.setText(track.getTitle());
                selected_track_title.setText(track.getTitle());
            } catch (Exception e) {
            }
        } else {
            try {
                durationInMilliSec = (int) localTrack.getDuration();
            } catch (Exception e) {

            }
            try {
                imgLoader.DisplayImage(localTrack.getPath(), homeActivity.spImgAB);
                imgLoader.DisplayImage(localTrack.getPath(), selected_track_image);
                imgLoader.DisplayImage(localTrack.getPath(), currentAlbumArtHolder);
            } catch (Exception e) {

            }
            try {
                homeActivity.spTitleAB.setText(localTrack.getTitle());
                selected_track_title.setText(localTrack.getTitle());
            } catch (Exception e) {

            }
        }

        temp = getTime(durationInMilliSec);
        totalTime.setText(temp.first + ":" + temp.second);

        try {
            if (homeActivity.streamSelected) {
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

        homeActivity.overflowMenuAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popMenu = new PopupMenu(getContext(), homeActivity.overflowMenuAB);
                popMenu.getMenuInflater().inflate(R.menu.player_overflow_menu, popMenu.getMenu());

                popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Full Screen")) {
                            if (homeActivity.isFullScreenEnabled) {
                                homeActivity.isFullScreenEnabled = false;
                                bottomContainer.setVisibility(View.VISIBLE);
                                seekBarContainer.setVisibility(View.VISIBLE);
                                toggleContainer.setVisibility(View.VISIBLE);
                                homeActivity.spToolbar.setVisibility(View.VISIBLE);
                                fullscreenExtraSpaceOccupier.getLayoutParams().height = 0;
                                mCallback8.onFullScreen();
                            } else {
                                homeActivity.isFullScreenEnabled = true;
                                bottomContainer.setVisibility(View.INVISIBLE);
                                seekBarContainer.setVisibility(View.INVISIBLE);
                                toggleContainer.setVisibility(View.INVISIBLE);
                                homeActivity.spToolbar.setVisibility(View.INVISIBLE);
                                fullscreenExtraSpaceOccupier.getLayoutParams().height = homeActivity.statusBarHeightinDp;
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


        player_controller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReplayIconVisible) {
                    homeActivity.hasQueueEnded = true;
                    mCallback2.onComplete();
                } else {
                    if (!pauseClicked) {
                        pauseClicked = true;
                    }
                    togglePlayPause();
                }
            }
        });

        mainTrackController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReplayIconVisible) {
                    homeActivity.hasQueueEnded = true;
                    mCallback2.onComplete();
                } else {
                    if (!pauseClicked) {
                        pauseClicked = true;
                    }
                    togglePlayPause();
                }
            }
        });

        nextTrackController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!homeActivity.repeatEnabled && !homeActivity.repeatOnceEnabled && homeActivity.queueCurrentIndex == homeActivity.queue.getQueue().size() - 1) {
//
//                } else {
//                    mMediaPlayer.pause();
//                    homeActivity.nextControllerClicked = true;
//                    mCallback2.onComplete();
//                }
                mMediaPlayer.pause();
                homeActivity.nextControllerClicked = true;
                mCallback2.onComplete();
            }
        });

        previousTrackController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeActivity.queueCurrentIndex != 0) {
                    mMediaPlayer.pause();
                    mCallback3.onPreviousTrack();
                }
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
                                    hsv[0] = homeActivity.seekBarColor;
                                    hsv[1] = (float) 0.8;
                                    hsv[2] = (float) 0.5;
                                    progressBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.HSVToColor(hsv), PorterDuff.Mode.SRC_IN));
                                    cpb.update();
                                }
                            });
                            try {
                                temp = getTime(mMediaPlayer.getCurrentPosition());
                                getActivity().runOnUiThread(new Runnable() {
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
                getActivity().runOnUiThread(new Runnable() {
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
        mEndButton.setBackgroundColor(homeActivity.themeColor);
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
                                .setContentTitlePaint(homeActivity.tp)
                                .setTarget(new ViewTarget(mVisualizerView.getId(), getActivity()))
                                .setContentTitle("The DNA")
                                .setContentText("The DNA of the currently playing song.")
                                .build();
                        showCase.setButtonText("Next");
                        showCase.setButtonPosition(homeActivity.lps);
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
                                        showCase.setButtonPosition(homeActivity.lps);
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
                                        showCase.setButtonPosition(homeActivity.lps);
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

        if (homeActivity.localSelected)
            ut = new UnifiedTrack(true, localTrack, null);
        else
            ut = new UnifiedTrack(false, null, track);

        homeActivity.favouriteTracks.getFavourite().add(ut);
        mCallback10.onAddedtoFavfromPlayer();
    }

    public void removeFromFavourite() {

        UnifiedTrack ut;

        if (homeActivity.localSelected)
            ut = new UnifiedTrack(true, localTrack, null);
        else
            ut = new UnifiedTrack(false, null, track);

        for (int i = 0; i < homeActivity.favouriteTracks.getFavourite().size(); i++) {
            UnifiedTrack ut1 = homeActivity.favouriteTracks.getFavourite().get(i);
            if (ut.getType() && ut1.getType()) {
                if (ut.getLocalTrack().getTitle().equals(ut1.getLocalTrack().getTitle())) {
                    homeActivity.favouriteTracks.getFavourite().remove(i);
                    break;
                }
            } else if (!ut.getType() && !ut1.getType()) {
                if (ut.getStreamTrack().getTitle().equals(ut1.getStreamTrack().getTitle())) {
                    homeActivity.favouriteTracks.getFavourite().remove(i);
                    break;
                }
            }
        }
        mCallback10.onAddedtoFavfromPlayer();
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

        if (homeActivity.isPlayerVisible) {
            player_controller.setVisibility(View.VISIBLE);
            player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
        } else {
            player_controller.setVisibility(View.VISIBLE);
            player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
        }

        isFav = false;

        if (homeActivity.isFavourite) {
            favouriteIcon.setImageResource(R.drawable.ic_heart_filled_1);
            favControllerSp.setImageResource(R.drawable.ic_heart_filled_1);
            isFav = true;
        } else {
            favouriteIcon.setImageResource(R.drawable.ic_heart_out_1);
            favControllerSp.setImageResource(R.drawable.ic_heart_out_1);
            isFav = false;
        }

        if (homeActivity.shuffleEnabled) {
            shuffleController.setImageResource(R.drawable.ic_shuffle_filled);
        } else {
            shuffleController.setImageResource(R.drawable.ic_shuffle_outline);
        }

        if (homeActivity.repeatEnabled) {
            repeatController.setImageResource(R.drawable.ic_repeat_filled);
        } else if (homeActivity.repeatOnceEnabled) {
            repeatController.setImageResource(R.drawable.ic_repeat_once);
        } else {
            repeatController.setImageResource(R.drawable.ic_repeat_outline);
        }

        if (homeActivity.isSaveDNAEnabled) {
            saveDNAToggle.setImageResource(R.drawable.ic_save_filled);
        } else {
            saveDNAToggle.setImageResource(R.drawable.ic_save_outline);
        }

        equalizerIcon.setVisibility(View.INVISIBLE);

        track = homeActivity.selectedTrack;
        localTrack = homeActivity.localSelectedTrack;

        if (homeActivity.streamSelected) {
            durationInMilliSec = track.getDuration();
            if (track.getArtworkURL() != null) {
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(selected_track_image);
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(homeActivity.spImgAB);
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(currentAlbumArtHolder);
            } else {
                selected_track_image.setImageResource(R.drawable.ic_default);
                homeActivity.spImgAB.setImageResource(R.drawable.ic_default);
                currentAlbumArtHolder.setImageResource(R.drawable.ic_default);
            }
            try {
                homeActivity.spTitleAB.setText(track.getTitle());
                selected_track_title.setText(track.getTitle());
            } catch (Exception e) {
            }
        } else {
            try {
                durationInMilliSec = (int) localTrack.getDuration();
            } catch (Exception e) {

            }
            try {
                imgLoader.DisplayImage(localTrack.getPath(), homeActivity.spImgAB);
                imgLoader.DisplayImage(localTrack.getPath(), selected_track_image);
                imgLoader.DisplayImage(localTrack.getPath(), currentAlbumArtHolder);
            } catch (Exception e) {

            }
            try {
                homeActivity.spTitleAB.setText(localTrack.getTitle());
                selected_track_title.setText(localTrack.getTitle());
            } catch (Exception e) {

            }
        }

        temp = getTime(durationInMilliSec);
        totalTime.setText(temp.first + ":" + temp.second);

        try {
            if (homeActivity.streamSelected) {
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
                                    hsv[0] = homeActivity.seekBarColor;
                                    hsv[1] = (float) 0.8;
                                    hsv[2] = (float) 0.5;
                                    progressBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.HSVToColor(hsv), PorterDuff.Mode.SRC_IN));
                                    cpb.update();
                                }
                            });
                            try {
                                temp = getTime(mMediaPlayer.getCurrentPosition());
                                getActivity().runOnUiThread(new Runnable() {
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
                getActivity().runOnUiThread(new Runnable() {
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
            if (homeActivity.isSaveDNAEnabled) {
                Bitmap bmp = mVisualizerView.bmp;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                if (localIsPlaying) {
                    DNAModel model = new DNAModel(true, localTrack, null, byteArray);
                    SavedDNA sDna = new SavedDNA(localTrack.getTitle(), model);
                    homeActivity.savedDNAs.getSavedDNAs().add(0, sDna);
                } else {
                    DNAModel model = new DNAModel(false, null, track, byteArray);
                    SavedDNA sDna = new SavedDNA(track.getTitle(), model);
                    for (int i = 0; i < homeActivity.savedDNAs.getSavedDNAs().size(); i++) {
                        DNAModel dModel = homeActivity.savedDNAs.getSavedDNAs().get(i).getModel();
                        if (model.equals(dModel)) {
                            homeActivity.savedDNAs.getSavedDNAs().remove(i);
                            break;
                        }
                    }
                    homeActivity.savedDNAs.getSavedDNAs().add(0, sDna);
                }
                if (homeActivity.savedDNAs.getSavedDNAs().size() > 10) {
                    homeActivity.savedDNAs.getSavedDNAs().remove(10);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(ctx, "DNA Saved!", Toast.LENGTH_SHORT).show();
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
