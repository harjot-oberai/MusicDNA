package com.sdsmdg.harjot.MusicDNA.headsethandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by Harjot on 06-Oct-16.
 */
public class HeadSetReceiver extends BroadcastReceiver {

    onHeadsetEventListener mCallback;

    public interface onHeadsetEventListener {
        void onHeadsetRemoved();

        void onHeadsetNextClicked();

        void onHeadsetPreviousClicked();

        void onHeadsetPlayPauseClicked();
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        mCallback = (onHeadsetEventListener) context;

        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    mCallback.onHeadsetRemoved();
                    break;
                case 1:
                    break;
            }
        }
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent event = (KeyEvent) intent
                    .getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            int keycode = event.getKeyCode();
            int action = event.getAction();
            Log.i("keycode", String.valueOf(keycode));
            Log.i("action", String.valueOf(action));
            //onKeyDown(keyCode, event)
            if (keycode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                    || keycode == KeyEvent.KEYCODE_HEADSETHOOK)
                if (action == KeyEvent.ACTION_DOWN)
                    mCallback.onHeadsetPlayPauseClicked();
            if (keycode == KeyEvent.KEYCODE_MEDIA_NEXT)
                if (action == KeyEvent.ACTION_DOWN)
                    mCallback.onHeadsetNextClicked();
            if (keycode == KeyEvent.KEYCODE_MEDIA_PREVIOUS)
                if (action == KeyEvent.ACTION_DOWN)
                    mCallback.onHeadsetPreviousClicked();

        }
    }
}

