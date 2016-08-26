package com.sdsmdg.harjot.MusicDNA.NotificationManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sdsmdg.harjot.MusicDNA.PlayerFragment;

/**
 * Created by Harjot on 29-Jun-16.
 */
public class AudioPlayerBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equalsIgnoreCase("com.sdsmdg.harjot.MusicDNA.ACTION_PLAY_PAUSE")) {
            try {
                if (!PlayerFragment.pauseClicked) {
                    PlayerFragment.pauseClicked = true;
                }
                PlayerFragment.togglePlayPause();
                PlayerFragment.mCallback6.onPrepared();
            } catch (Exception e) {

            }

        } else if (action.equalsIgnoreCase("com.sdsmdg.harjot.MusicDNA.ACTION_NEXT")) {

            try {
                PlayerFragment.mMediaPlayer.stop();
                PlayerFragment.mCallback2.onComplete();
            } catch (Exception e) {

            }
        } else if (action.equalsIgnoreCase("com.sdsmdg.harjot.MusicDNA.ACTION_PREV")) {
            try {
                PlayerFragment.mMediaPlayer.stop();
                PlayerFragment.mCallback3.onPreviousTrack();
            } catch (Exception e) {

            }
        }
    }
}
