package com.sdsmdg.harjot.MusicDNA;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    boolean perm1 = false;
    boolean perm2 = false;
    boolean perm3 = false;
    boolean perm4 = false;
    boolean perm5 = false;

    CustomImageHolder cih;
    RelativeLayout relSplash;
    TextView tx;

    static Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        cih = (CustomImageHolder) findViewById(R.id.splash_cih);

        tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        tx = (TextView) findViewById(R.id.splash_text);
        tx.setTypeface(tf);

        TextView tx2 = (TextView) findViewById(R.id.text_soundcloud);
        tx2.setTypeface(tf);

        relSplash = (RelativeLayout) findViewById(R.id.rel_splash);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions();
        } else {
            cih.start(tx,relSplash);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 2300);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    perm1 = true;
                    Toast.makeText(SplashActivity.this, "one", Toast.LENGTH_SHORT).show();
                } else {
                }
                break;
            }
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    perm2 = true;
                    Toast.makeText(SplashActivity.this, "two", Toast.LENGTH_SHORT).show();
                    if (perm2 && perm4 && perm5) {
                        cih.start(tx,relSplash);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }, 2300);
                    }
                } else {
                }
                break;
            }
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    perm3 = true;
                    Toast.makeText(SplashActivity.this, "three", Toast.LENGTH_SHORT).show();
                } else {
                }
                break;
            }
            case 3: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    perm4 = true;
                    Toast.makeText(SplashActivity.this, "four", Toast.LENGTH_SHORT).show();
                } else {
                }
                break;
            }
            case 4: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    perm5 = true;
                    Toast.makeText(SplashActivity.this, "five", Toast.LENGTH_SHORT).show();
                } else {
                }
                break;
            }
        }
    }

    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        0);

                Toast.makeText(SplashActivity.this, "perm1", Toast.LENGTH_SHORT).show();

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            perm1 = true;
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        1);
                Toast.makeText(SplashActivity.this, "perm2", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm2 = true;
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.MODIFY_AUDIO_SETTINGS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS},
                        2);
                Toast.makeText(SplashActivity.this, "perm3", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm3 = true;
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        3);
                Toast.makeText(SplashActivity.this, "perm4", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm4 = true;
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        4);
                Toast.makeText(SplashActivity.this, "perm5", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm5 = true;
        }

        if (perm2 && perm4 && perm5) {
            cih.start(tx,relSplash);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 2300);
        }

    }

}
