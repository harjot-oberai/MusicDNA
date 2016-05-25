package com.sdsmdg.harjot.MusicDNA;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Harjot on 26-May-16.
 */
public class HelperBroadcastReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equalsIgnoreCase("com.sdsmdg.harjot.MusicDNA.ACTION_PLAY_PAUSE")) {
            if (!PlayerFragment.pauseClicked) {
                PlayerFragment.pauseClicked = true;
            }
            PlayerFragment.togglePlayPause();
            PlayerFragment.mCallback6.onPrepared();
        } else if (action.equalsIgnoreCase("com.sdsmdg.harjot.MusicDNA.ACTION_NEXT")) {
            PlayerFragment.mMediaPlayer.stop();
            PlayerFragment.mCallback2.onComplete();
        } else if (action.equalsIgnoreCase("com.sdsmdg.harjot.MusicDNA.ACTION_PREVIOUS")) {
            PlayerFragment.mMediaPlayer.stop();
            PlayerFragment.mCallback3.onPreviousTrack();
        }
    }
}
