package com.sdsmdg.harjot.MusicDNA.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.sdsmdg.harjot.MusicDNA.R;

public class SplashActivity extends AppCompatActivity {
    public static Typeface tf3;
    public static Typeface tf4;

    ImageView img;
    private int permissionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        img = (ImageView) findViewById(R.id.splash_img);

        try {
            tf4 = Typeface.createFromAsset(getAssets(), "fonts/Intro_Cond_Light.otf");
            tf3 = Typeface.createFromAsset(getAssets(), "fonts/Gidole-Regular.ttf");
        } catch (Exception ignored) {
        }

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions();
        } else {
            Intent i = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //if permission granted, move on to next request
        //else stop application
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionIndex++;
            requestPermissions();
        } else {
            finish();
        }
    }

    public void requestPermissions() {
        if (!allPermissionsPassed()) {
            return;
        }

        Intent i = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    public boolean allPermissionsPassed() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE
        };

        //start at last request
        //if permission needed, request
        for (;permissionIndex < permissions.length; permissionIndex++) {
            String permission = permissions[permissionIndex];
            if (ContextCompat.checkSelfPermission(this,
                                                  permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                                                  new String[]{permission},
                                                  permissionIndex);
                return false;
            }
            permissionIndex++;
        }
        return true;
    }
}