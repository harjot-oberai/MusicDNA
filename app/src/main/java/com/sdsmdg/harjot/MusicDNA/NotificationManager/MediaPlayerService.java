package com.sdsmdg.harjot.MusicDNA.NotificationManager;

/**
 * Created by Harjot on 03-Jun-16.
 */

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.Rating;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.Rating;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSession.Callback;
import android.media.session.MediaSessionManager;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import com.sdsmdg.harjot.MusicDNA.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.PlayerFragment;
import com.sdsmdg.harjot.MusicDNA.R;

import java.util.logging.LogRecord;

public class MediaPlayerService extends Service implements PlayerFragment.onPlayPauseListener {


    private MediaSessionManager m_objMediaSessionManager;
    private MediaSession m_objMediaSession;
    private MediaController m_objMediaController;
    private MediaPlayer m_objMediaPlayer;


    @Override
    public IBinder onBind(Intent intent) {
        PlayerFragment.mCallback7 = this;
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null)
            return;

        String action = intent.getAction();

        if (action.equalsIgnoreCase(Constants.ACTION_PLAY)) {
            m_objMediaController.getTransportControls().play();
        } else if (action.equalsIgnoreCase(Constants.ACTION_PAUSE)) {
            m_objMediaController.getTransportControls().pause();
        } else if (action.equalsIgnoreCase(Constants.ACTION_FAST_FORWARD)) {
            m_objMediaController.getTransportControls().fastForward();
        } else if (action.equalsIgnoreCase(Constants.ACTION_REWIND)) {
            m_objMediaController.getTransportControls().rewind();
        } else if (action.equalsIgnoreCase(Constants.ACTION_PREVIOUS)) {
            m_objMediaController.getTransportControls().skipToPrevious();
        } else if (action.equalsIgnoreCase(Constants.ACTION_NEXT)) {
            m_objMediaController.getTransportControls().skipToNext();
        } else if (action.equalsIgnoreCase(Constants.ACTION_STOP)) {
            m_objMediaController.getTransportControls().stop();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void buildNotification(Notification.Action action) {

        Notification.MediaStyle style = new Notification.MediaStyle();
        style.setShowActionsInCompactView(new int[]{1});
        style.setMediaSession(m_objMediaSession.getSessionToken());

        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(Constants.ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);

        String artist;
        if (PlayerFragment.localIsPlaying) {
            artist = PlayerFragment.localTrack.getArtist();
        } else {
            artist = "";
        }

        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this)
                .setStyle(style)
                .setSmallIcon(R.drawable.ic_default)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setDeleteIntent(pendingIntent)
                .addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", Constants.ACTION_PREVIOUS))
                .addAction(action)
                .addAction(generateAction(android.R.drawable.ic_media_next, "Next", Constants.ACTION_NEXT))
                .setContentTitle(PlayerFragment.selected_track_title.getText())
                .setContentText(artist)
                .setLargeIcon(((BitmapDrawable) PlayerFragment.selected_track_image.getDrawable()).getBitmap())
                .build();

        notification.contentIntent = pendingNotificationIntent;
        notification.priority = Notification.PRIORITY_MAX;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private Notification.Action generateAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder(icon, title, pendingIntent).build();
    }

    private PendingIntent retreivePlaybackAction(int which) {
        Intent action;
        PendingIntent pendingIntent;
        final ComponentName serviceName = new ComponentName(this, MediaPlayerService.class);
        switch (which) {
            case 1:
                // Play and pause
                action = new Intent(Constants.ACTION_PLAY);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 1, action, 0);
                return pendingIntent;
            case 2:
                // Skip tracks
                action = new Intent(Constants.ACTION_NEXT);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 2, action, 0);
                return pendingIntent;
            case 3:
                // Previous tracks
                action = new Intent(Constants.ACTION_PREVIOUS);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 3, action, 0);
                return pendingIntent;
            case 4:
                //fast forward tracks
                action = new Intent(Constants.ACTION_FAST_FORWARD);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 4, action, 0);
                return pendingIntent;
            case 5:
                //rewind tracks
                action = new Intent(Constants.ACTION_REWIND);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 5, action, 0);
                return pendingIntent;
            default:
                break;
        }
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (m_objMediaSessionManager == null) {
            initMediaSessions();
        }
        PlayerFragment.mCallback7 = this;
        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initMediaSessions() {

        PlayerFragment.mCallback7 = this;
        m_objMediaPlayer = PlayerFragment.mMediaPlayer;
        m_objMediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        m_objMediaSession = new MediaSession(getApplicationContext(), "sample session");
        m_objMediaController = new MediaController(getApplicationContext(), m_objMediaSession.getSessionToken());
        m_objMediaSession.setActive(true);
        m_objMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS);
        m_objMediaSession.setCallback(new Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                Log.e(Constants.LOG_TAG, "onPlay");
                if (!PlayerFragment.isStart)
                    PlayerFragment.togglePlayPause();
                PlayerFragment.isStart = false;
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
            }

            @Override
            public void onPause() {
                super.onPause();
                Log.e(Constants.LOG_TAG, "onPause");
                PlayerFragment.togglePlayPause();
                buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", Constants.ACTION_PLAY));
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                Log.e(Constants.LOG_TAG, "onSkipToNext");
                PlayerFragment.mCallback2.onComplete();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
                    }
                }, 100);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                Log.e(Constants.LOG_TAG, "onSkipToPrevious");
                PlayerFragment.mCallback3.onPreviousTrack();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
                    }
                }, 100);
            }

            @Override
            public void onFastForward() {
                super.onFastForward();
                Log.e(Constants.LOG_TAG, "onFastForward");
            }

            @Override
            public void onRewind() {
                super.onRewind();
                Log.e(Constants.LOG_TAG, "onRewind");
            }

            @Override
            public void onStop() {
                super.onStop();
                Log.e(Constants.LOG_TAG, "onStop");
                if (PlayerFragment.mMediaPlayer.isPlaying()) {
                    buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
                } else {
                    buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", Constants.ACTION_PLAY));
                }
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
            }

            @Override
            public void onSetRating(Rating rating) {
                super.onSetRating(rating);
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onUnbind(Intent intent) {
        m_objMediaSession.release();
        return super.onUnbind(intent);
    }

    @Override
    public void onPlayPause() {
        if (PlayerFragment.mMediaPlayer.isPlaying()) {
            buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
        } else {
            buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", Constants.ACTION_PLAY));
        }
    }
}