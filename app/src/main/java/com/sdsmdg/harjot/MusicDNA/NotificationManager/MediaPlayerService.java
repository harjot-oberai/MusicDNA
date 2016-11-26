package com.sdsmdg.harjot.MusicDNA.NotificationManager;

/**
 * Created by Harjot on 03-Jun-16.
 */

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadata;
import android.media.Rating;
import android.media.session.PlaybackState;
import android.os.Binder;
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
import android.widget.Toast;

import com.sdsmdg.harjot.MusicDNA.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.Interfaces.ServiceCallbacks;
import com.sdsmdg.harjot.MusicDNA.PlayerFragment;
import com.sdsmdg.harjot.MusicDNA.R;

import java.util.logging.LogRecord;

public class MediaPlayerService extends Service implements PlayerFragment.onPlayPauseListener {


    private MediaSessionManager m_objMediaSessionManager;
    private MediaSession m_objMediaSession;
    private MediaController m_objMediaController;
    private MediaPlayer m_objMediaPlayer;
    private NotificationManager notificationManager;

    private ServiceCallbacks serviceCallbacks;

    Intent startIntent;

    PlayerFragment pFragment;

    onCallbackListener callback;

    private boolean isSwipable = false;

    public interface onCallbackListener {
        public PlayerFragment getPlayerFragmentFromHome();
    }

    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            // Return this instance of MyService so clients can call public methods
            return MediaPlayerService.this;
        }
    }

    public void setCallbacks(ServiceCallbacks callbacks) {
        serviceCallbacks = callbacks;
        pFragment = serviceCallbacks.getPlayerFragment();
        if (pFragment != null)
            pFragment.mCallback7 = this;
        if (m_objMediaSessionManager == null) {
            initMediaSessions();
        }
        handleIntent(startIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        startIntent = intent;
        IBinder binder = new LocalBinder();
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            pFragment = ((HomeActivity) PlayerFragment.ctx).getPlayerFragment();
        } catch (Exception e) {
        }
        if (pFragment != null)
            pFragment.mCallback7 = this;

        if (m_objMediaSessionManager == null) {
            initMediaSessions();
        }
        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

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

    private void buildNotification(Notification.Action action) {

        Notification.MediaStyle style = new Notification.MediaStyle();
        style.setShowActionsInCompactView(1);
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

        Bitmap bmp = null;

        try {
            bmp = ((BitmapDrawable) PlayerFragment.selected_track_image.getDrawable()).getBitmap();
        } catch (Exception e) {
        }

        if (bmp == null) {
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_default);
        }

        Notification notification = new Notification.Builder(this)
                .setStyle(style)
                .setSmallIcon(R.drawable.ic_notification)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setDeleteIntent(pendingIntent)
                .addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", Constants.ACTION_PREVIOUS))
                .addAction(action)
                .addAction(generateAction(android.R.drawable.ic_media_next, "Next", Constants.ACTION_NEXT))
                .setContentTitle(PlayerFragment.selected_track_title.getText())
                .setContentText(artist)
                .setLargeIcon(bmp)
                .build();

        notification.contentIntent = pendingNotificationIntent;
        notification.priority = Notification.PRIORITY_MAX;

        if (isSwipable || (PlayerFragment.mMediaPlayer != null && PlayerFragment.mMediaPlayer.isPlaying())) {
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
        }


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

        updateMediaSession();

    }

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

    void updateMediaSession() {
        m_objMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder();
        if (PlayerFragment.localIsPlaying) {
            metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, PlayerFragment.localTrack.getTitle());
            metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, PlayerFragment.localTrack.getArtist());
        } else {
            metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, PlayerFragment.track.getTitle());
            metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, "");
        }
        if (((BitmapDrawable) PlayerFragment.selected_track_image.getDrawable()).getBitmap() != null) {

            metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, ((BitmapDrawable) PlayerFragment.selected_track_image.getDrawable()).getBitmap());
        }

        m_objMediaSession.setMetadata(metadataBuilder.build());
        PlaybackState.Builder stateBuilder = new PlaybackState.Builder();
        stateBuilder.setActions(PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE | PlaybackState.ACTION_PAUSE | PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS);
        stateBuilder.setState(!PlayerFragment.mMediaPlayer.isPlaying() ? PlaybackState.STATE_PAUSED : PlaybackState.STATE_PLAYING, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 1.0f);
        m_objMediaSession.setPlaybackState(stateBuilder.build());
        m_objMediaSession.setActive(true);
    }

    private void initMediaSessions() {

        if (pFragment != null) {
            pFragment.mCallback7 = this;
        }
        m_objMediaPlayer = PlayerFragment.mMediaPlayer;
        m_objMediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        m_objMediaSession = new MediaSession(getApplicationContext(), "sample session");

        m_objMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder();
        if (PlayerFragment.localIsPlaying) {
            if (PlayerFragment.localTrack != null) {
                metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, PlayerFragment.localTrack.getTitle());
                metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, PlayerFragment.localTrack.getArtist());
            }
        } else {
            if (PlayerFragment.track != null) {
                metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, PlayerFragment.track.getTitle());
                metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, "");
            }
        }
        if (PlayerFragment.selected_track_image != null) {
            if (((BitmapDrawable) PlayerFragment.selected_track_image.getDrawable()).getBitmap() != null) {
                metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, ((BitmapDrawable) PlayerFragment.selected_track_image.getDrawable()).getBitmap());
            }
        }


        m_objMediaSession.setMetadata(metadataBuilder.build());
        PlaybackState.Builder stateBuilder = new PlaybackState.Builder();
        stateBuilder.setActions(PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE | PlaybackState.ACTION_PAUSE | PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS);
        stateBuilder.setState(!PlayerFragment.mMediaPlayer.isPlaying() ? PlaybackState.STATE_PAUSED : PlaybackState.STATE_PLAYING, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 1.0f);
        m_objMediaSession.setPlaybackState(stateBuilder.build());

        m_objMediaSession.setActive(true);
        m_objMediaController = m_objMediaSession.getController();
        m_objMediaSession.setCallback(new MediaSession.Callback() {

            @Override
            public void onPlay() {
                super.onPlay();
                try {
                    Log.d(Constants.LOG_TAG, "onPlay");
                    PlayerFragment pFrag = pFragment;
                    if (pFrag != null) {
                        if (!pFrag.isStart) {
                            pFrag.togglePlayPause();
                        }
                        pFrag.isStart = false;
                        buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onPause() {
                super.onPause();
                try {
                    PlayerFragment pFrag = pFragment;
                    if (pFrag != null) {
                        pFrag.togglePlayPause();
                        buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", Constants.ACTION_PLAY));
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                try {
                    if (pFragment != null) {
                        pFragment.onCallbackCalled(2);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
                            }
                        }, 100);
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                try {
                    if (pFragment != null) {
                        pFragment.onCallbackCalled(3);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
                            }
                        }, 100);
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFastForward() {
                super.onFastForward();
                Log.d(Constants.LOG_TAG, "onFastForward");
                if (pFragment != null)
                    pFragment.mCallback2.onComplete();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
                    }
                }, 100);
            }

            @Override
            public void onRewind() {
                super.onRewind();
                Log.d(Constants.LOG_TAG, "onRewind");
                if (pFragment != null)
                    pFragment.mCallback3.onPreviousTrack();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
                    }
                }, 100);
            }

            @Override
            public void onStop() {
                super.onStop();
//                if (PlayerFragment.mMediaPlayer != null) {
//                    if (PlayerFragment.mMediaPlayer.isPlaying()) {
//                        buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
//                    } else {
//                        buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", Constants.ACTION_PLAY));
//                    }
//                }
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

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        try {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1);
        } catch (Exception e) {

        }
    }
}