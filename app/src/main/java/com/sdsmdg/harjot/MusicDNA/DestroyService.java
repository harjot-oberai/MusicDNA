package com.sdsmdg.harjot.MusicDNA;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Harjot on 30-May-16.
 */
public class DestroyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("SERVice", "exit");
        Toast.makeText(DestroyService.this, "exit", Toast.LENGTH_SHORT).show();
        HomeActivity.notificationManager.cancel(1);
    }
}
