package com.example.harjot.musicstreamer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements StreamMusicFragment.OnTrackSelectedListener
        , LocalMusicFragment.OnLocalTrackSelectedListener
        , PlayerFragment.reloadCurrentInstanceListener {

    static Toolbar toolbar;
    static Toolbar bottomPlayer;
    static TabLayout tabLayout;
    static ViewPager viewPager;

    static ImageView selected_track_image;
    static TextView selected_track_title;
    static ImageView player_controller;

    static float seekBarColor;

    static byte[] mBytes;

    View playerContainer;

    static View ab;

    boolean isPlayerVisible = false;

    static boolean localSelected = false;
    static boolean streamSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ab = findViewById(R.id.appBar);

        playerContainer = findViewById(R.id.playerFragContainer);

        requestPermissions();

        selected_track_image = (ImageView) findViewById(R.id.selected_track_image_bp);
        selected_track_title = (TextView) findViewById(R.id.selected_track_title_bp);
        player_controller = (ImageView) findViewById(R.id.player_control_bp);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        bottomPlayer = (Toolbar) findViewById(R.id.bottomPlayer);
        bottomPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.Fragment frag = getFragmentManager().findFragmentByTag("player");
                android.app.FragmentManager fm = getFragmentManager();
                if (!isPlayerVisible) {
                    isPlayerVisible = true;
                    hideTabs();
                    showPlayer();
                    moveBottomPlayerUP();
                } else {
                    isPlayerVisible = false;
                    showTabs();
                    hidePlayer();
                    moveBottomPlayerDown();
                }
            }
        });

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StreamMusicFragment(), "Stream");
        adapter.addFragment(new LocalMusicFragment(), "Local");
        viewPager.setAdapter(adapter);
    }

    public void onTrackSelected(int position) {

        bottomPlayer.setVisibility(View.VISIBLE);
        hideTabs();
        isPlayerVisible = true;

        android.app.Fragment frag = getFragmentManager().findFragmentByTag("player");
        android.app.FragmentManager fm = getFragmentManager();
        PlayerFragment newFragment = new PlayerFragment();
        if (frag == null) {
            showBottomPlayer();
            moveBottomPlayerUP();
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.playerFragContainer, newFragment, "player")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            if (PlayerFragment.track != null && !PlayerFragment.localIsPlaying && StreamMusicFragment.selectedTrack.getTitle() == PlayerFragment.track.getTitle()) {
                moveBottomPlayerUP();
            } else {
                moveBottomPlayerUP();
                PlayerFragment.mMediaPlayer.stop();
                PlayerFragment.mMediaPlayer.reset();
                PlayerFragment.mVisualizer.release();
                PlayerFragment.init();
                fm.beginTransaction()
                        .remove(frag)
                        .setCustomAnimations(R.animator.slide_up,
                                R.animator.slide_down,
                                R.animator.slide_up,
                                R.animator.slide_down)
                        .add(R.id.playerFragContainer, newFragment, "player")
                        .show(newFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }

        showPlayer();
        PlayerFragment.localIsPlaying = false;

    }

    @Override
    public void onLocalTrackSelected(int position) {

        bottomPlayer.setVisibility(View.VISIBLE);
        hideTabs();
        isPlayerVisible = true;

        android.app.Fragment frag = getFragmentManager().findFragmentByTag("player");
        android.app.FragmentManager fm = getFragmentManager();
        PlayerFragment newFragment = new PlayerFragment();
        if (frag == null) {
            showBottomPlayer();
            moveBottomPlayerUP();
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.playerFragContainer, newFragment, "player")
                    .show(newFragment)
                    .commit();
        } else {
            if (PlayerFragment.localTrack != null && PlayerFragment.localIsPlaying && LocalMusicFragment.selectedTrack.getTitle() == PlayerFragment.localTrack.getTitle()) {
                moveBottomPlayerUP();
            } else {
                moveBottomPlayerUP();
                PlayerFragment.mMediaPlayer.stop();
                PlayerFragment.mMediaPlayer.reset();
                PlayerFragment.mVisualizer.release();
                PlayerFragment.init();
                fm.beginTransaction()
                        .remove(frag)
                        .setCustomAnimations(R.animator.slide_up,
                                R.animator.slide_down,
                                R.animator.slide_up,
                                R.animator.slide_down)
                        .add(R.id.playerFragContainer, newFragment, "player")
                        .show(newFragment)
                        .commit();
            }
        }

        showPlayer();
        PlayerFragment.localIsPlaying = true;
    }

    @Override
    public void reloadCurrentInstance() {
        Fragment frag = getSupportFragmentManager().findFragmentByTag("player");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.playerFragContainer, frag, "player")
                .show(frag)
                .commit();
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
//        android.app.Fragment frag = getFragmentManager().findFragmentByTag("player");
//        if (frag == null || !frag.isVisible())
//            super.onBackPressed();
//        else {
//            isPlayerVisible = false;
//            showTabs();
//            hidePlayer();
//            /*getFragmentManager().beginTransaction()
//                    .setCustomAnimations(R.animator.slide_up,
//                            R.animator.slide_down,
//                            R.animator.slide_up,
//                            R.animator.slide_down)
//                    .hide(frag)
//                    .commit();*/
//        }

        if (isPlayerVisible) {
            hidePlayer();
            showTabs();
            moveBottomPlayerDown();
            isPlayerVisible = false;
        } else {
            super.onBackPressed();
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

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
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
            }
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
            }
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
            }
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
            }
        }
    }

    public void hideTabs() {
        ab.setVisibility(View.VISIBLE);
        ab.setAlpha(1.0f);

        ab.animate()
                .translationY(-1 * ab.getHeight())
                .alpha(0.0f);
    }

    public void showTabs() {
        ab.setVisibility(View.VISIBLE);
        ab.setAlpha(1.0f);

        ab.animate()
                .translationY(0)
                .alpha(1.0f);
    }

    public void hidePlayer() {
        playerContainer.setVisibility(View.VISIBLE);
        playerContainer.setAlpha(1.0f);
        if (PlayerFragment.mVisualizerView != null)
            PlayerFragment.mVisualizerView.setAlpha(0.0f);

        playerContainer.animate()
                .translationY(playerContainer.getHeight())
                .alpha(1.0f);

        player_controller.setVisibility(View.VISIBLE);

        player_controller.animate()
                .alpha(1.0f);


    }

    public void showPlayer() {
        playerContainer.setVisibility(View.VISIBLE);
        playerContainer.setAlpha(1.0f);
        if (PlayerFragment.mVisualizerView != null)
            PlayerFragment.mVisualizerView.setAlpha(0.0f);

        player_controller.setAlpha(1.0f);
        player_controller.animate()
                .alpha(0.0f)
                .setDuration(300);

        playerContainer.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(300);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PlayerFragment.mVisualizerView != null)
                    PlayerFragment.mVisualizerView.setAlpha(1.0f);
                player_controller.setVisibility(View.INVISIBLE);
            }
        }, 350);


    }

    public void showBottomPlayer() {
        bottomPlayer.setTranslationY(-1 * bottomPlayer.getHeight());
        bottomPlayer.setVisibility(View.VISIBLE);
        bottomPlayer.setAlpha(0.0f);

        bottomPlayer.animate()
                .translationY(0)
                .alpha(1.0f);

    }

    public void moveBottomPlayerUP() {
        bottomPlayer.animate()
                .translationY(-1 * (playerContainer.getHeight() - bottomPlayer.getHeight()))
                .alpha(1.0f);
    }

    public void moveBottomPlayerDown() {
        bottomPlayer.animate()
                .translationY(0)
                .alpha(1.0f);
    }

    public static void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        updatePoints();
        PlayerFragment.mVisualizerView.updateVisualizer(bytes);
    }

    public static void updatePoints() {
        VisualizerView.outerRadius = (float) (Math.min(VisualizerView.width, VisualizerView.height) * 0.47);
        VisualizerView.normalizedPosition = ((float) ((System.currentTimeMillis() - PlayerFragment.startTime) + PlayerFragment.totalElapsedTime + PlayerFragment.deltaTime)) / (float) (PlayerFragment.durationInMilliSec);
        if (mBytes == null) {
            return;
        }
        VisualizerView.angle = (float) (Math.PI - VisualizerView.normalizedPosition * VisualizerView.TAU);
        Log.d("ANGLE", VisualizerView.angle + "");
        VisualizerView.color = 0;
        VisualizerView.lnDataDistance = 0;
        VisualizerView.distance = 0;
        VisualizerView.size = 0;
        VisualizerView.volume = 0;
        VisualizerView.power = 0;

        float x, y;

        int midx = (int) (VisualizerView.width / 2);
        int midy = (int) (VisualizerView.height / 2);

        // calculate min and max amplitude for current byte array
        float max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        for (int a = 16; a < (mBytes.length / 2); a++) {
            Log.d("BYTE", mBytes[a] + "");
            float amp = mBytes[(a * 2) + 0] * mBytes[(a * 2) + 0] + mBytes[(a * 2) + 1] * mBytes[(a * 2) + 1];
            if (amp > max) {
                max = amp;
            }
            if (amp < min) {
                min = amp;
            }
        }

        Log.d("MAXMIN", max + ":" + min);

        /**
         * Number Fishing is all that is used here to get the best looking DNA
         * Number fishing is HOW YOU WIN AT LIFE. -- paullewis :)
         * **/

        for (int a = 16; a < (mBytes.length / 2); a++) {
            Log.d("BYTE", mBytes[a] + "");
            if (max <= 10.0) {
                break;
            }

            // scale the amplitude to the range [0,1]
            float amp = mBytes[(a * 2) + 0] * mBytes[(a * 2) + 0] + mBytes[(a * 2) + 1] * mBytes[(a * 2) + 1];
            if (max != min)
                amp = (amp - min) / (max - min);
            else {
                amp = 0;
            }

            VisualizerView.volume = ((float) amp);             // REDUNDANT :P

            // converting polar to cartesian (distance calculated afterwards acts as radius for polar co-ords)
            x = (float) Math.sin(VisualizerView.angle);
            y = (float) Math.cos(VisualizerView.angle);

            // filtering low amplitude
            if (VisualizerView.volume < 0.39) {
                continue;
            }

            // color ( value of hue inn HSV ) calculated based on current progress of the song or audio clip
            VisualizerView.color = (float) (VisualizerView.normalizedPosition - 0.12 + Math.random() * 0.24);
            VisualizerView.color = Math.round(VisualizerView.color * 360);
            seekBarColor = (float) (VisualizerView.normalizedPosition);
            seekBarColor = Math.round(seekBarColor * 360);

            // calculating distance from center ( 'r' in polar coordinates)
            VisualizerView.lnDataDistance = (float) ((Math.log(a - 4) / VisualizerView.LOG_MAX) - VisualizerView.BASE);
            VisualizerView.distance = VisualizerView.lnDataDistance * VisualizerView.outerRadius;

            // size of the circle to be rendered at the calculated position
            VisualizerView.size = (float) (4.5 * VisualizerView.volume * VisualizerView.MAX_DOT_SIZE + Math.random() * 2);

            // alpha also based on volume ( amplitude )
            VisualizerView.alpha = (float) (VisualizerView.volume * 0.09);

            // final cartesian coordinates for drawing on canvas
            x = x * VisualizerView.distance;
            y = y * VisualizerView.distance;


            float[] hsv = new float[3];
            hsv[0] = VisualizerView.color;
            hsv[1] = (float) 0.8;
            hsv[2] = (float) 0.5;

            // setting color of the Paint
            VisualizerView.mForePaint.setColor(Color.HSVToColor(hsv));

            if (VisualizerView.size >= 15.0 && VisualizerView.size < 29.0) {
                VisualizerView.mForePaint.setAlpha(15);
            } else if (VisualizerView.size >= 29.0 && VisualizerView.size <= 60.0) {
                VisualizerView.mForePaint.setAlpha(9);
            } else if (VisualizerView.size > 60.0) {
                VisualizerView.mForePaint.setAlpha(0);
            } else {
                VisualizerView.mForePaint.setAlpha((int) (VisualizerView.alpha * 1000));
            }

            // Setting alpha of the Paint
            //mForePaint.setAlpha((int) (alpha * 1000));

            // Draw the circles at correct position


            // Add points and paint config to lists for redraw
            VisualizerView.pts.add(Pair.create(midx + x, midy + y));
            VisualizerView.ptPaint.add(Pair.create(VisualizerView.size, Pair.create(VisualizerView.mForePaint.getColor(), VisualizerView.mForePaint.getAlpha())));
        }
    }

}
