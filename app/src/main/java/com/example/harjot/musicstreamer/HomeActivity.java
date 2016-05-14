package com.example.harjot.musicstreamer;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.example.harjot.musicstreamer.Interfaces.StreamService;
import com.example.harjot.musicstreamer.Models.LocalTrack;
import com.example.harjot.musicstreamer.Models.Track;
import com.example.harjot.musicstreamer.Models.UnifiedTrack;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, PlayerFragment.onSmallPlayerTouchedListener {

    public static List<LocalTrack> localTrackList = new ArrayList<>();
    public static List<LocalTrack> finalLocalSearchResultList = new ArrayList<>();
    public static List<Track> streamingTrackList = new ArrayList<>();

    private List<UnifiedTrack> recentlyPlayed = new ArrayList<>();
    private List<UnifiedTrack> favouriteTracks = new ArrayList<>();
    private List<UnifiedTrack> queue = new ArrayList<>();
    private Pair<String, List<UnifiedTrack>> tempPlaylist;
    private List<Pair<String, List<UnifiedTrack>>> playlists;

    Context ctx;

    DrawerLayout drawer;

    LocalTracksHorizontalAdapter adapter;
    StreamTracksHorizontalAdapter sAdapter;

    Call<List<Track>> call;

    SearchView searchView;
    MenuItem searchItem;

    RecyclerView streamingListView;
    RecyclerView localListView;

    static Toolbar toolbar;

    static Activity main;

    static float seekBarColor;

    static byte[] mBytes;

    View playerContainer;

    static android.app.FragmentManager fragMan;

    Animation slideUp, slideDown;

    static boolean isPlayerVisible = false;

    static LocalTrack localSelectedTrack;
    static Track selectedTrack;

    static boolean localSelected = false;
    static boolean streamSelected = false;

    public void onTrackSelected(int position) {

        hideKeyboard();
        searchView.setQuery("", false);
        searchView.setIconified(true);

        hideTabs();
        isPlayerVisible = true;

        android.app.Fragment frag = getFragmentManager().findFragmentByTag("player");
        android.app.FragmentManager fm = getFragmentManager();
        PlayerFragment newFragment = new PlayerFragment();
        if (frag == null) {
            PlayerFragment.mCallback = this;
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
            if (PlayerFragment.track != null && !PlayerFragment.localIsPlaying && selectedTrack.getTitle() == PlayerFragment.track.getTitle()) {

            } else {
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
        PlayerFragment.track = streamingTrackList.get(position);

    }

    public void onLocalTrackSelected(int position) {

        hideKeyboard();
        searchView.setQuery("", false);
        searchView.setIconified(true);

        hideTabs();
        isPlayerVisible = true;

        android.app.Fragment frag = getFragmentManager().findFragmentByTag("player");
        android.app.FragmentManager fm = getFragmentManager();
        PlayerFragment newFragment = new PlayerFragment();
        if (frag == null) {
            PlayerFragment.mCallback = this;
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.playerFragContainer, newFragment, "player")
                    .show(newFragment)
                    .commit();
        } else {
            if (PlayerFragment.localTrack != null && PlayerFragment.localIsPlaying && localSelectedTrack.getTitle() == PlayerFragment.localTrack.getTitle()) {

            } else {
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
        PlayerFragment.localTrack = localTrackList.get(position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        slideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        slideUp.setFillAfter(true);

        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        slideDown.setFillAfter(true);

        setContentView(R.layout.activity_home);

        fragMan = getFragmentManager();

        ctx = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getLocalSongs();

        adapter = new LocalTracksHorizontalAdapter(finalLocalSearchResultList);
        localListView = (RecyclerView) findViewById(R.id.localMusicList_home);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        localListView.setLayoutManager(mLayoutManager);
        localListView.setItemAnimator(new DefaultItemAnimator());
        localListView.setAdapter(adapter);

        localListView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), localListView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LocalTrack track = finalLocalSearchResultList.get(position);
                localSelectedTrack = track;
                streamSelected = false;
                localSelected = true;
                onLocalTrackSelected(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        streamingListView = (RecyclerView) findViewById(R.id.trackList_home);

        streamingListView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), localListView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Track track = streamingTrackList.get(position);
                selectedTrack = track;
                streamSelected = true;
                localSelected = false;
                onTrackSelected(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        playerContainer = findViewById(R.id.playerFragContainer);

        requestPermissions();

    }

    private void getLocalSongs() {
        ContentResolver musicResolver = this.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%Music%"}, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int pathColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.DATA);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String path = musicCursor.getString(pathColumn);
                long duration = musicCursor.getLong(durationColumn);
                localTrackList.add(new LocalTrack(thisId, thisTitle, thisArtist, path, duration));
                finalLocalSearchResultList.add(new LocalTrack(thisId, thisTitle, thisArtist, path, duration));
            }
            while (musicCursor.moveToNext());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isPlayerVisible) {
                hidePlayer();
                showTabs();
                isPlayerVisible = false;
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        updateList(query.trim());
        updateStreamingList(query.trim());
        hideKeyboard();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        updateList(newText.trim());
        updateStreamingList(newText.trim());
        return true;
    }

    private void updateList(String query) {

        if (isPlayerVisible) {
            hidePlayer();
        }

        /*Update the Local List*/

        finalLocalSearchResultList.clear();
        for (int i = 0; i < localTrackList.size(); i++) {
            LocalTrack lt = localTrackList.get(i);
            String tmp1 = lt.getTitle().toLowerCase();
            String tmp2 = query.toLowerCase();
            if (tmp1.contains(tmp2)) {
                finalLocalSearchResultList.add(lt);
            }
        }
        (localListView.getAdapter()).notifyDataSetChanged();


    }

    private void updateStreamingList(String query) {

        new Thread(new CancelCall()).start();

        if (isPlayerVisible) {
            hidePlayer();
        }

        /*Update the Streaming List*/

        if (!query.equals("")) {
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(Config.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            StreamService ss = client.create(StreamService.class);
            call = ss.getTracks(query, 75);
            call.enqueue(new Callback<List<Track>>() {
                @Override
                public void onResponse(Response<List<Track>> response) {

                    if (response.isSuccess()) {
                        // request successful (status code 200, 201)
                        Log.d("RETRO", response.body() + "");
                        streamingTrackList = response.body();
                        sAdapter = new StreamTracksHorizontalAdapter(streamingTrackList, ctx);
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                        streamingListView.setLayoutManager(mLayoutManager);
                        streamingListView.setItemAnimator(new DefaultItemAnimator());
                        streamingListView.setAdapter(sAdapter);
                        (streamingListView.getAdapter()).notifyDataSetChanged();
                    } else {
                        //request not successful (like 400,401,403 etc)
                        //Handle errors
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("RETRO", t.getMessage());
                }
            });

        } else {

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
//        toolbar.setAlpha(1.0f);
//
//        toolbar.animate()
//                .translationY(-1 * toolbar.getHeight())
//                .alpha(0.0f);
//
//        toolbar.setVisibility(View.GONE);
    }

    public void showTabs() {
//        toolbar.setVisibility(View.VISIBLE);
//        toolbar.setAlpha(1.0f);
//
//        toolbar.animate()
//                .translationY(0)
//                .alpha(1.0f);
    }

    public void hidePlayer() {

        if (PlayerFragment.mVisualizerView != null)
            PlayerFragment.mVisualizerView.setVisibility(View.INVISIBLE);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        if (PlayerFragment.cpb != null) {
            PlayerFragment.cpb.setAlpha(0.0f);
            PlayerFragment.cpb.setVisibility(View.VISIBLE);
            PlayerFragment.cpb.animate()
                    .alpha(1.0f);
        }

        playerContainer.setVisibility(View.VISIBLE);

        playerContainer.animate()
                .translationY(playerContainer.getHeight() - PlayerFragment.smallPlayer.getHeight());

        PlayerFragment.player_controller.setAlpha(0.0f);
        PlayerFragment.player_controller.setImageDrawable(PlayerFragment.mainTrackController.getDrawable());

        PlayerFragment.player_controller.animate()
                .alpha(1.0f);
//        if (PlayerFragment.progressBar2 != null) {
//            PlayerFragment.progressBar2.setVisibility(View.VISIBLE);
//            PlayerFragment.progressBar2.animate()
//                    .alpha(1.0f);
//        }


    }

    public void showPlayer() {

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        searchView.setQuery("", false);
        searchView.setIconified(true);

        playerContainer.setVisibility(View.VISIBLE);
        if (PlayerFragment.mVisualizerView != null)
            PlayerFragment.mVisualizerView.setVisibility(View.INVISIBLE);

        if (PlayerFragment.player_controller != null) {
            PlayerFragment.player_controller.setAlpha(1.0f);
            PlayerFragment.player_controller.animate()
                    .alpha(0.0f);
        }

        if (PlayerFragment.cpb != null) {
            PlayerFragment.cpb.animate()
                    .alpha(0.0f)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            PlayerFragment.cpb.setVisibility(View.INVISIBLE);
                        }
                    });
        }
//        if (PlayerFragment.progressBar2 != null) {
//            PlayerFragment.progressBar2.animate()
//                    .alpha(0.0f)
//                    .withEndAction(new Runnable() {
//                        @Override
//                        public void run() {
//                            PlayerFragment.progressBar2.setVisibility(View.GONE);
//                        }
//                    });
//        }

        playerContainer.animate()
                .setDuration(300)
                .translationY(0);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PlayerFragment.mVisualizerView != null)
                    PlayerFragment.mVisualizerView.setVisibility(View.VISIBLE);
                if (PlayerFragment.player_controller != null) {
                    PlayerFragment.player_controller.setImageResource(R.drawable.ic_cancel_white_48dp);
                    PlayerFragment.player_controller.setAlpha(1.0f);
                }
            }
        }, 400);


    }

    public static void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        updatePoints();
        PlayerFragment.mVisualizerView.updateVisualizer(mBytes);
//        new MyAsyncTask().execute();
    }

    public static void updatePoints() {
//        VisualizerView.outerRadius = (float) (Math.min(VisualizerView.width, VisualizerView.height) * 0.47);
//        VisualizerView.normalizedPosition = ((float) ((System.currentTimeMillis() - PlayerFragment.startTime) + PlayerFragment.totalElapsedTime + PlayerFragment.deltaTime)) / (float) (PlayerFragment.durationInMilliSec);
//        if (mBytes == null) {
//            return;
//        }
//        VisualizerView.angle = (float) (Math.PI - VisualizerView.normalizedPosition * VisualizerView.TAU);
//        Log.d("ANGLE", VisualizerView.angle + "");
//        VisualizerView.color = 0;
//        VisualizerView.lnDataDistance = 0;
//        VisualizerView.distance = 0;
//        VisualizerView.size = 0;
//        VisualizerView.volume = 0;
//        VisualizerView.power = 0;
//
//        float x, y;
//
//        int midx = (int) (VisualizerView.width / 2);
//        int midy = (int) (VisualizerView.height / 2);
//
//        // calculate min and max amplitude for current byte array
//        float max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
//        for (int a = 16; a < (mBytes.length / 2); a++) {
//            Log.d("BYTE", mBytes[a] + "");
//            float amp = mBytes[(a * 2) + 0] * mBytes[(a * 2) + 0] + mBytes[(a * 2) + 1] * mBytes[(a * 2) + 1];
//            if (amp > max) {
//                max = amp;
//            }
//            if (amp < min) {
//                min = amp;
//            }
//        }
//
//        Log.d("MAXMIN", max + ":" + min);
//
//        /**
//         * Number Fishing is all that is used here to get the best looking DNA
//         * Number fishing is HOW YOU WIN AT LIFE. -- paullewis :)
//         * **/
//
//        for (int a = 16; a < (mBytes.length / 2); a++) {
//            Log.d("BYTE", mBytes[a] + "");
//            if (max <= 10.0) {
//                break;
//            }
//
//            // scale the amplitude to the range [0,1]
//            float amp = mBytes[(a * 2) + 0] * mBytes[(a * 2) + 0] + mBytes[(a * 2) + 1] * mBytes[(a * 2) + 1];
//            if (max != min)
//                amp = (amp - min) / (max - min);
//            else {
//                amp = 0;
//            }
//
//            VisualizerView.volume = ((float) amp);             // REDUNDANT :P
//
//            // converting polar to cartesian (distance calculated afterwards acts as radius for polar co-ords)
//            x = (float) Math.sin(VisualizerView.angle);
//            y = (float) Math.cos(VisualizerView.angle);
//
//            // filtering low amplitude
//            if (VisualizerView.volume < 0.39) {
//                continue;
//            }
//
//            // color ( value of hue inn HSV ) calculated based on current progress of the song or audio clip
//            VisualizerView.color = (float) (VisualizerView.normalizedPosition - 0.12 + Math.random() * 0.24);
//            VisualizerView.color = Math.round(VisualizerView.color * 360);
//            seekBarColor = (float) (VisualizerView.normalizedPosition);
//            seekBarColor = Math.round(seekBarColor * 360);
//
//            // calculating distance from center ( 'r' in polar coordinates)
//            VisualizerView.lnDataDistance = (float) ((Math.log(a - 4) / VisualizerView.LOG_MAX) - VisualizerView.BASE);
//            VisualizerView.distance = VisualizerView.lnDataDistance * VisualizerView.outerRadius;
//
//            // size of the circle to be rendered at the calculated position
//            VisualizerView.size = (float) (4.5 * VisualizerView.volume * VisualizerView.MAX_DOT_SIZE + Math.random() * 2);
//
//            // alpha also based on volume ( amplitude )
//            VisualizerView.alpha = (float) (VisualizerView.volume * 0.09);
//
//            // final cartesian coordinates for drawing on canvas
//            x = x * VisualizerView.distance;
//            y = y * VisualizerView.distance;
//
//
//            float[] hsv = new float[3];
//            hsv[0] = VisualizerView.color;
//            hsv[1] = (float) 0.8;
//            hsv[2] = (float) 0.5;
//
//            // setting color of the Paint
//            VisualizerView.mForePaint.setColor(Color.HSVToColor(hsv));
//
//            if (VisualizerView.size >= 15.0 && VisualizerView.size < 29.0) {
//                VisualizerView.mForePaint.setAlpha(15);
//            } else if (VisualizerView.size >= 29.0 && VisualizerView.size <= 60.0) {
//                VisualizerView.mForePaint.setAlpha(9);
//            } else if (VisualizerView.size > 60.0) {
//                VisualizerView.mForePaint.setAlpha(0);
//            } else {
//                VisualizerView.mForePaint.setAlpha((int) (VisualizerView.alpha * 1000));
//            }
//
//            // Setting alpha of the Paint
//            //mForePaint.setAlpha((int) (alpha * 1000));
//
//            // Draw the circles at correct position
//
//
//            // Add points and paint config to lists for redraw
//            VisualizerView.pts.add(Pair.create(midx + x, midy + y));
//            VisualizerView.ptPaint.add(Pair.create(VisualizerView.size, Pair.create(VisualizerView.mForePaint.getColor(), VisualizerView.mForePaint.getAlpha())));
//        }
        PlayerFragment.mVisualizerView.outerRadius = (float) (Math.min(PlayerFragment.mVisualizerView.width, PlayerFragment.mVisualizerView.height) * 0.42);
        PlayerFragment.mVisualizerView.normalizedPosition = ((float) ((System.currentTimeMillis() - PlayerFragment.startTime) + PlayerFragment.totalElapsedTime + PlayerFragment.deltaTime)) / (float) (PlayerFragment.durationInMilliSec);
        if (mBytes == null) {
            return;
        }
        PlayerFragment.mVisualizerView.angle = (float) (Math.PI - PlayerFragment.mVisualizerView.normalizedPosition * PlayerFragment.mVisualizerView.TAU);
        Log.d("ANGLE", PlayerFragment.mVisualizerView.angle + "");
        PlayerFragment.mVisualizerView.color = 0;
        PlayerFragment.mVisualizerView.lnDataDistance = 0;
        PlayerFragment.mVisualizerView.distance = 0;
        PlayerFragment.mVisualizerView.size = 0;
        PlayerFragment.mVisualizerView.volume = 0;
        PlayerFragment.mVisualizerView.power = 0;

        float x, y;

        int midx = (int) (PlayerFragment.mVisualizerView.width / 2);
        int midy = (int) (PlayerFragment.mVisualizerView.height / 2);

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

            PlayerFragment.mVisualizerView.volume = ((float) amp);             // REDUNDANT :P

            // converting polar to cartesian (distance calculated afterwards acts as radius for polar co-ords)
            x = (float) Math.sin(PlayerFragment.mVisualizerView.angle);
            y = (float) Math.cos(PlayerFragment.mVisualizerView.angle);

            // filtering low amplitude
            if (PlayerFragment.mVisualizerView.volume < 0.39) {
                continue;
            }

            // color ( value of hue inn HSV ) calculated based on current progress of the song or audio clip
            PlayerFragment.mVisualizerView.color = (float) (PlayerFragment.mVisualizerView.normalizedPosition - 0.12 + Math.random() * 0.24);
            PlayerFragment.mVisualizerView.color = Math.round(PlayerFragment.mVisualizerView.color * 360);
            seekBarColor = (float) (PlayerFragment.mVisualizerView.normalizedPosition);
            seekBarColor = Math.round(seekBarColor * 360);

            // calculating distance from center ( 'r' in polar coordinates)
            PlayerFragment.mVisualizerView.lnDataDistance = (float) ((Math.log(a - 4) / PlayerFragment.mVisualizerView.LOG_MAX) - PlayerFragment.mVisualizerView.BASE);
            PlayerFragment.mVisualizerView.distance = PlayerFragment.mVisualizerView.lnDataDistance * PlayerFragment.mVisualizerView.outerRadius;

            // size of the circle to be rendered at the calculated position
            PlayerFragment.mVisualizerView.size = (float) (4.5 * PlayerFragment.mVisualizerView.volume * PlayerFragment.mVisualizerView.MAX_DOT_SIZE + Math.random() * 2);

            // alpha also based on volume ( amplitude )
            PlayerFragment.mVisualizerView.alpha = (float) (PlayerFragment.mVisualizerView.volume * 0.09);

            // final cartesian coordinates for drawing on canvas
            x = x * PlayerFragment.mVisualizerView.distance;
            y = y * PlayerFragment.mVisualizerView.distance;


            float[] hsv = new float[3];
            hsv[0] = PlayerFragment.mVisualizerView.color;
            hsv[1] = (float) 0.8;
            hsv[2] = (float) 0.5;

            // setting color of the Paint
            PlayerFragment.mVisualizerView.mForePaint.setColor(Color.HSVToColor(hsv));

            if (PlayerFragment.mVisualizerView.size >= 15.0 && PlayerFragment.mVisualizerView.size < 29.0) {
                PlayerFragment.mVisualizerView.mForePaint.setAlpha(15);
            } else if (PlayerFragment.mVisualizerView.size >= 29.0 && PlayerFragment.mVisualizerView.size <= 60.0) {
                PlayerFragment.mVisualizerView.mForePaint.setAlpha(9);
            } else if (PlayerFragment.mVisualizerView.size > 60.0) {
                PlayerFragment.mVisualizerView.mForePaint.setAlpha(0);
            } else {
                PlayerFragment.mVisualizerView.mForePaint.setAlpha((int) (PlayerFragment.mVisualizerView.alpha * 1000));
            }

            // Setting alpha of the Paint
            //mForePaint.setAlpha((int) (alpha * 1000));

            // Draw the circles at correct position


            // Add points and paint config to lists for redraw
            PlayerFragment.mVisualizerView.pts.add(Pair.create(midx + x, midy + y));
            PlayerFragment.mVisualizerView.ptPaint.add(Pair.create(PlayerFragment.mVisualizerView.size, Pair.create(PlayerFragment.mVisualizerView.mForePaint.getColor(), PlayerFragment.mVisualizerView.mForePaint.getAlpha())));
        }
    }

    public static class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            updatePoints();
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PlayerFragment.mVisualizerView.updateVisualizer(mBytes);
                }
            });
            return null;
        }
    }

    @Override
    public void onSmallPlayerTouched() {
        android.app.Fragment frag = fragMan.findFragmentByTag("player");
        android.app.FragmentManager fm = fragMan;
        if (!isPlayerVisible) {
            isPlayerVisible = true;
            hideTabs();
            showPlayer();
        } else {
            isPlayerVisible = false;
            showTabs();
            hidePlayer();
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    class CancelCall implements Runnable {

        @Override
        public void run() {
            if (call != null)
                call.cancel();
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private HomeActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final HomeActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
