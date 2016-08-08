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
import android.util.Log;
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
    boolean perm6 = false;
    boolean perm7 = false;

    RelativeLayout relSplash;
    TextView tx;

    static Typeface tf;
    static Typeface tf2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
            tf2 = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");
        } catch (Exception e) {
            Log.d("TYPEFACE", e.getMessage() + ":");
            Toast.makeText(SplashActivity.this, "typeface error", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions();
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(i);
                    Log.d("HOME", "Starting");
                    finish();
                }
            }, 1000);
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
                    requestPermissions();
                    Toast.makeText(SplashActivity.this, "one", Toast.LENGTH_SHORT).show();
                } else {
                }
                break;
            }
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    perm2 = true;
                    requestPermissions();
                    Toast.makeText(SplashActivity.this, "two", Toast.LENGTH_SHORT).show();
                } else {
                }
                break;
            }
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    perm3 = true;
                    requestPermissions();
                    Toast.makeText(SplashActivity.this, "three", Toast.LENGTH_SHORT).show();
                } else {
                }
                break;
            }
            case 3: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    perm4 = true;
                    requestPermissions();
                    Toast.makeText(SplashActivity.this, "four", Toast.LENGTH_SHORT).show();
                } else {
                }
                break;
            }
            case 4: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    perm5 = true;
                    requestPermissions();
                    Toast.makeText(SplashActivity.this, "five", Toast.LENGTH_SHORT).show();
                } else {
                }
                break;
            }
            case 5: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    perm6 = true;
                    requestPermissions();
                    Toast.makeText(SplashActivity.this, "six", Toast.LENGTH_SHORT).show();
                } else {
                }
                break;
            }
            case 6: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    perm7 = true;
                    requestPermissions();
                    Toast.makeText(SplashActivity.this, "seven", Toast.LENGTH_SHORT).show();
                } else {
                }
                break;
            }
        }
    }

    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (!perm1) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 0);
                Toast.makeText(SplashActivity.this, "perm1", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm1 = true;
            Toast.makeText(SplashActivity.this, "perm1_granted", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (!perm2 && perm1) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        1);
                Toast.makeText(SplashActivity.this, "perm2", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm2 = true;
            Toast.makeText(SplashActivity.this, "perm2_granted", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            if (!perm3 && perm2 && perm1) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS}, 2);
                Toast.makeText(SplashActivity.this, "perm3", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm3 = true;
            Toast.makeText(SplashActivity.this, "perm3_granted", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!perm4 && perm3 && perm2 && perm1) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                Toast.makeText(SplashActivity.this, "perm4", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm4 = true;
            Toast.makeText(SplashActivity.this, "perm4_granted ", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!perm5 && perm4 && perm3 && perm2 && perm1) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 4);
                Toast.makeText(SplashActivity.this, "perm5", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm5 = true;
            Toast.makeText(SplashActivity.this, "perm5_granted", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (!perm6 && perm5 && perm4 && perm3 && perm2 && perm1) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 5);
                Toast.makeText(SplashActivity.this, "perm6", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm6 = true;
            Toast.makeText(SplashActivity.this, "perm6_granted", Toast.LENGTH_SHORT).show();
        }

        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.MEDIA_CONTENT_CONTROL) != PackageManager.PERMISSION_GRANTED) {
            if (!perm7 && perm6 && perm5 && perm4 && perm3 && perm2 && perm1) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MEDIA_CONTENT_CONTROL}, 6);
                Toast.makeText(SplashActivity.this, "perm7", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm7 = true;
            Toast.makeText(SplashActivity.this, "perm7_granted", Toast.LENGTH_SHORT).show();
        }*/

        if (perm1 && perm2 && perm3 && perm4 && perm5 && perm6) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 1000);
        }
    }

    public void requestPermissions2() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

            } else if (!perm1) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        0);

                Toast.makeText(SplashActivity.this, "perm1", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm1 = true;
            Toast.makeText(SplashActivity.this, "perm1_granted", Toast.LENGTH_SHORT).show();
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

            } else if (!perm2 && perm1) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        1);
                Toast.makeText(SplashActivity.this, "perm2", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm2 = true;
            Toast.makeText(SplashActivity.this, "perm2_granted", Toast.LENGTH_SHORT).show();
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.MODIFY_AUDIO_SETTINGS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS)) {

            } else if (!perm3 && perm2 && perm1) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS},
                        2);
                Toast.makeText(SplashActivity.this, "perm3", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm3 = true;
            Toast.makeText(SplashActivity.this, "perm3_granted", Toast.LENGTH_SHORT).show();
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else if (!perm4 && perm3 && perm2 && perm1) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        3);
                Toast.makeText(SplashActivity.this, "perm4", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm4 = true;
            Toast.makeText(SplashActivity.this, "perm4_granted ", Toast.LENGTH_SHORT).show();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else if (!perm5 && perm4 && perm3 && perm2 && perm1) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 4);
                Toast.makeText(SplashActivity.this, "perm5", Toast.LENGTH_SHORT).show();
            }
        } else {
            perm5 = true;
            Toast.makeText(SplashActivity.this, "perm5_granted", Toast.LENGTH_SHORT).show();
        }

        if (perm1 && perm2 && perm3 && perm4 && perm5) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 1000);
        }
    }

}
