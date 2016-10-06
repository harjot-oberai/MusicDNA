package com.sdsmdg.harjot.MusicDNA.HeadsetHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Harjot on 06-Oct-16.
 */
public class HeadSetReceiver extends BroadcastReceiver {

    onHeadsetRemovedListener mCallback;

    public interface onHeadsetRemovedListener {
        public void onHeadsetRemoved();
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        mCallback = (onHeadsetRemovedListener) context;

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
    }
}
