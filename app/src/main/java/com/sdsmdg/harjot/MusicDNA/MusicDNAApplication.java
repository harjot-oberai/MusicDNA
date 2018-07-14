package com.sdsmdg.harjot.MusicDNA;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.RefWatcher;

//import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Harjot on 28-Aug-16.
 */
public class MusicDNAApplication extends Application {

    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        MusicDNAApplication application = (MusicDNAApplication) context.getApplicationContext();
        return application.refWatcher;
    }
}