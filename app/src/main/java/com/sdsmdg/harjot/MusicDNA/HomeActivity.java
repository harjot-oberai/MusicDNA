package com.sdsmdg.harjot.MusicDNA;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.harjot.MusicDNA.Interfaces.StreamService;
import com.sdsmdg.harjot.MusicDNA.Models.AllDNAModels;
import com.sdsmdg.harjot.MusicDNA.Models.AllMusicFolders;
import com.sdsmdg.harjot.MusicDNA.Models.AllPlaylists;
import com.sdsmdg.harjot.MusicDNA.Models.AllSavedDNA;
import com.sdsmdg.harjot.MusicDNA.Models.DNAModel;
import com.sdsmdg.harjot.MusicDNA.Models.Favourite;
import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.Models.MusicFolder;
import com.sdsmdg.harjot.MusicDNA.Models.Playlist;
import com.sdsmdg.harjot.MusicDNA.Models.Queue;
import com.sdsmdg.harjot.MusicDNA.Models.RecentlyPlayed;
import com.sdsmdg.harjot.MusicDNA.Models.SavedDNA;
import com.sdsmdg.harjot.MusicDNA.Models.Track;
import com.sdsmdg.harjot.MusicDNA.Models.UnifiedTrack;
import com.sdsmdg.harjot.MusicDNA.NotificationManager.Constants;
import com.sdsmdg.harjot.MusicDNA.NotificationManager.MediaPlayerService;
import com.sdsmdg.harjot.MusicDNA.imageLoader.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bz.tsung.android.objectify.NoSuchPreferenceFoundException;
import bz.tsung.android.objectify.ObjectPreferenceLoader;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener,
        PlayerFragment.onSmallPlayerTouchedListener,
        PlayerFragment.onCompleteListener,
        PlayerFragment.onPreviousTrackListener,
        LocalMusicFragment.OnLocalTrackSelectedListener,
        StreamMusicFragment.OnTrackSelectedListener,
        QueueFragment.onQueueItemClickedListener,
        ViewPlaylistFragment.onPLaylistItemClickedListener,
        FavouritesFragment.onFavouriteItemClickedListener,
        ViewPlaylistFragment.onPlaylistPlayAllListener,
        FavouritesFragment.onFavouritePlayAllListener,
        QueueFragment.onQueueSaveListener,
        PlayerFragment.onEqualizerClickedListener,
        PlayerFragment.onQueueClickListener,
        PlayerFragment.onPreparedLsitener,
        PlayListFragment.onPLaylistTouchedListener,
        PlayListFragment.onPlaylistMenuPlayAllListener,
        FolderFragment.onFolderClickedListener,
        FolderContentFragment.onFolderContentPlayAllListener,
        FolderContentFragment.onFolderContentItemClickListener {


    public static List<LocalTrack> localTrackList = new ArrayList<>();
    public static List<LocalTrack> finalLocalSearchResultList = new ArrayList<>();
    public static List<Track> streamingTrackList = new ArrayList<>();

    static float ratio, ratio2;

    AppBarLayout appBarLayout;

    Toolbar spHome;
    ImageView playerControllerHome;
    FrameLayout bottomToolbar;
    CircleImageView spImgHome;
    TextView spTitleHome;

    static ImageView playerControllerAB;
    static CircleImageView spImgAB;
    static TextView spTitleAB;

    ImageLoader imgLoader;

    RecentlyPlayed recentlyPlayed;
    static Favourite favouriteTracks;
    static Queue queue;
    static Playlist tempPlaylist;
    static AllPlaylists allPlaylists;
    static AllDNAModels allDNAs;
    static AllMusicFolders allMusicFolders;

    static AllSavedDNA savedDNAs;
    static SavedDNA tempSavedDNA;

    static List<LocalTrack> tempFolderContent;
    static MusicFolder tempMusicFolder;

    static boolean repeatEnabled = false;
    static boolean shuffleEnabled = false;

    static boolean isFavourite = false;

    static boolean isReloaded = false;

    public static int queueCurrentIndex = 0;

    public static boolean isSaveDNAEnabled = true;

    public static Context ctx;

    static boolean queueCall = false;

    static boolean isDNAavailable = false;

    static float max_max = Float.MIN_VALUE;
    static float min_min = Float.MAX_VALUE;
    static float avg_max = 0;
    static float avg_min = 0;
    static float avg = 0;
    static int k = 0;

    DrawerLayout drawer;

    static NotificationManager notificationManager;

    LocalTracksHorizontalAdapter adapter;
    StreamTracksHorizontalAdapter sAdapter;
    static PlayListsHorizontalAdapter pAdapter;
    RecentsListHorizontalAdapter rAdapter;

    Call<List<Track>> call;

    SearchView searchView;
    MenuItem searchItem;

    RecyclerView streamingListView;
    RecyclerView localListView;
    static RecyclerView playlistsRecycler;
    RecyclerView recentsRecycler;

    RelativeLayout localRecyclerContainer;
    RelativeLayout recentsRecyclerContainer;
    RelativeLayout streamRecyclerContainer;
    RelativeLayout playlistRecyclerContainer;

    TextView localViewAll, streamViewAll;

    TextView localNothingText;
    TextView streamNothingText;
    TextView recentsNothingText;
    static TextView playlistNothingText;

    static int screen_width;
    static int screen_height;

    static Toolbar toolbar;
    static Toolbar spToolbar;

    static Activity main;

    static float seekBarColor;

    static byte[] mBytes;

    View playerContainer;

    static android.app.FragmentManager fragMan;

    static boolean isPlayerVisible = false;
    static boolean isLocalVisible = false;
    static boolean isStreamVisible = false;
    static boolean isQueueVisible = false;
    static boolean isPlaylistVisible = false;
    static boolean isEqualizerVisible = false;
    static boolean isFavouriteVisible = false;
    static boolean isAnalogVisible = false;
    static boolean isAllPlaylistVisible = false;
    static boolean isAllFolderVisible = false;
    static boolean isFolderContentVisible = false;
    static boolean isAllSavedDnaVisisble = false;
    static boolean isSavedDNAVisible = false;

    boolean isNotificationVisible = false;

    Button recentBtn, playlistBtn, favBtn;

    static LocalTrack localSelectedTrack;
    static Track selectedTrack;

    static boolean localSelected = false;
    static boolean streamSelected = false;

    public void onTrackSelected(int position) {

        HideBottomFakeToolbar();

        if (!queueCall) {
            hideKeyboard();

            searchView.setQuery("", false);
            searchView.setIconified(true);

            hideTabs();
            isPlayerVisible = true;

            PlayerFragment frag = (PlayerFragment) getFragmentManager().findFragmentByTag("player");
            android.app.FragmentManager fm = getFragmentManager();
            PlayerFragment newFragment = new PlayerFragment();
            if (frag == null) {
                PlayerFragment.mCallback = this;
                PlayerFragment.mCallback2 = this;
                PlayerFragment.mCallback3 = this;
                PlayerFragment.mCallback4 = this;
                PlayerFragment.mCallback5 = this;
                PlayerFragment.mCallback6 = this;
                int flag = 0;
                for (int i = 0; i < favouriteTracks.getFavourite().size(); i++) {
                    UnifiedTrack ut = favouriteTracks.getFavourite().get(i);
                    if (!ut.getType() && ut.getStreamTrack().getTitle().equals(selectedTrack.getTitle())) {
                        flag = 1;
                        isFavourite = true;
                        break;
                    }
                }
                if (flag == 0) {
                    isFavourite = false;
                }
                fm.beginTransaction()
                        .setCustomAnimations(R.animator.slide_up,
                                R.animator.slide_down,
                                R.animator.slide_up,
                                R.animator.slide_down)
                        .add(R.id.playerFragContainer, newFragment, "player")
                        .show(newFragment)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            } else {
                if (PlayerFragment.track != null && !PlayerFragment.localIsPlaying && selectedTrack.getTitle() == PlayerFragment.track.getTitle()) {

                } else {
                    int flag = 0;
                    for (int i = 0; i < favouriteTracks.getFavourite().size(); i++) {
                        UnifiedTrack ut = favouriteTracks.getFavourite().get(i);
                        if (!ut.getType() && ut.getStreamTrack().getTitle().equals(selectedTrack.getTitle())) {
                            flag = 1;
                            isFavourite = true;
                            break;
                        }
                    }
                    if (flag == 0) {
                        isFavourite = false;
                    }
                    frag.refresh();
                }
            }
            if (!isQueueVisible)
                showPlayer();
            PlayerFragment.localIsPlaying = false;
            PlayerFragment.track = selectedTrack;
        } else {
            PlayerFragment frag = (PlayerFragment) getFragmentManager().findFragmentByTag("player");
            PlayerFragment.localIsPlaying = false;
            PlayerFragment.track = selectedTrack;
            int flag = 0;
            for (int i = 0; i < favouriteTracks.getFavourite().size(); i++) {
                UnifiedTrack ut = favouriteTracks.getFavourite().get(i);
                if (!ut.getType() && ut.getStreamTrack().getTitle().equals(selectedTrack.getTitle())) {
                    flag = 1;
                    isFavourite = true;
                    break;
                }
            }
            if (flag == 0) {
                isFavourite = false;
            }
            frag.refresh();
        }

        if (QueueFragment.qAdapter != null)
            QueueFragment.qAdapter.notifyDataSetChanged();

        if (recentlyPlayed.getRecentlyPlayed().size() < 15) {
            UnifiedTrack track = new UnifiedTrack(false, null, PlayerFragment.track);
            for (int i = 0; i < recentlyPlayed.getRecentlyPlayed().size(); i++) {
                if (!recentlyPlayed.getRecentlyPlayed().get(i).getType() && recentlyPlayed.getRecentlyPlayed().get(i).getStreamTrack().getTitle().equals(track.getStreamTrack().getTitle())) {
                    recentlyPlayed.getRecentlyPlayed().remove(i);
                    break;
                }
            }
            recentlyPlayed.getRecentlyPlayed().add(0, track);
            recentsRecycler.setVisibility(View.VISIBLE);
            recentsNothingText.setVisibility(View.INVISIBLE);
            rAdapter.notifyDataSetChanged();
        } else {
            UnifiedTrack track = new UnifiedTrack(false, null, PlayerFragment.track);
            for (int i = 0; i < recentlyPlayed.getRecentlyPlayed().size(); i++) {
                if (!recentlyPlayed.getRecentlyPlayed().get(i).getType() && recentlyPlayed.getRecentlyPlayed().get(i).getStreamTrack().getTitle().equals(track.getStreamTrack().getTitle())) {
                    recentlyPlayed.getRecentlyPlayed().remove(i);
                    break;
                }
            }
            recentlyPlayed.getRecentlyPlayed().add(0, track);
            if (recentlyPlayed.getRecentlyPlayed().size() > 15)
                recentlyPlayed.getRecentlyPlayed().remove(15);
            rAdapter.notifyDataSetChanged();
        }
    }

    public void onLocalTrackSelected(int position) {

        HideBottomFakeToolbar();

        if (!queueCall) {
            hideKeyboard();

            searchView.setQuery("", false);
            searchView.setIconified(true);

            hideTabs();
            isPlayerVisible = true;

            PlayerFragment frag = (PlayerFragment) getFragmentManager().findFragmentByTag("player");
            android.app.FragmentManager fm = getFragmentManager();
            PlayerFragment newFragment = new PlayerFragment();
            if (frag == null) {
                PlayerFragment.mCallback = this;
                PlayerFragment.mCallback2 = this;
                PlayerFragment.mCallback3 = this;
                PlayerFragment.mCallback4 = this;
                PlayerFragment.mCallback5 = this;
                PlayerFragment.mCallback6 = this;
                int flag = 0;
                for (int i = 0; i < favouriteTracks.getFavourite().size(); i++) {
                    UnifiedTrack ut = favouriteTracks.getFavourite().get(i);
                    if (ut.getType() && ut.getLocalTrack().getTitle().equals(localSelectedTrack.getTitle())) {
                        flag = 1;
                        isFavourite = true;
                        break;
                    }
                }
                if (flag == 0) {
                    isFavourite = false;
                }
                fm.beginTransaction()
                        .setCustomAnimations(R.animator.slide_up,
                                R.animator.slide_down,
                                R.animator.slide_up,
                                R.animator.slide_down)
                        .add(R.id.playerFragContainer, newFragment, "player")
                        .show(newFragment)
                        .commitAllowingStateLoss();
            } else {
                if (PlayerFragment.localTrack != null && PlayerFragment.localIsPlaying && localSelectedTrack.getTitle() == PlayerFragment.localTrack.getTitle()) {

                } else {
                    int flag = 0;
                    for (int i = 0; i < favouriteTracks.getFavourite().size(); i++) {
                        UnifiedTrack ut = favouriteTracks.getFavourite().get(i);
                        if (ut.getType() && ut.getLocalTrack().getTitle().equals(localSelectedTrack.getTitle())) {
                            flag = 1;
                            isFavourite = true;
                            break;
                        }
                    }
                    if (flag == 0) {
                        isFavourite = false;
                    }
                    frag.refresh();
                }
            }

            if (!isQueueVisible)
                showPlayer();
            PlayerFragment.localIsPlaying = true;
            PlayerFragment.localTrack = localSelectedTrack;

        } else {
            PlayerFragment frag = (PlayerFragment) getFragmentManager().findFragmentByTag("player");
            PlayerFragment.localIsPlaying = true;
            PlayerFragment.localTrack = localSelectedTrack;

            int flag = 0;
            for (int i = 0; i < favouriteTracks.getFavourite().size(); i++) {
                UnifiedTrack ut = favouriteTracks.getFavourite().get(i);
                if (ut.getType() && ut.getLocalTrack().getTitle().equals(localSelectedTrack.getTitle())) {
                    flag = 1;
                    isFavourite = true;
                    break;
                }
            }
            if (flag == 0) {
                isFavourite = false;
            }

            frag.refresh();
        }

        if (QueueFragment.qAdapter != null)
            QueueFragment.qAdapter.notifyDataSetChanged();

        if (recentlyPlayed.getRecentlyPlayed().size() < 15) {
            UnifiedTrack track = new UnifiedTrack(true, PlayerFragment.localTrack, null);
            for (int i = 0; i < recentlyPlayed.getRecentlyPlayed().size(); i++) {
                if (recentlyPlayed.getRecentlyPlayed().get(i).getType() && recentlyPlayed.getRecentlyPlayed().get(i).getLocalTrack().getTitle().equals(track.getLocalTrack().getTitle())) {
                    recentlyPlayed.getRecentlyPlayed().remove(i);
                    break;
                }
            }
            recentlyPlayed.getRecentlyPlayed().add(0, track);
            recentsRecycler.setVisibility(View.VISIBLE);
            recentsNothingText.setVisibility(View.INVISIBLE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rAdapter.notifyDataSetChanged();
                }
            }, 320);
        } else {
            UnifiedTrack track = new UnifiedTrack(true, PlayerFragment.localTrack, null);
            for (int i = 0; i < recentlyPlayed.getRecentlyPlayed().size(); i++) {
                if (recentlyPlayed.getRecentlyPlayed().get(i).getType() && recentlyPlayed.getRecentlyPlayed().get(i).getLocalTrack().getTitle().equals(track.getLocalTrack().getTitle())) {
                    recentlyPlayed.getRecentlyPlayed().remove(i);
                    break;
                }
            }
            recentlyPlayed.getRecentlyPlayed().add(0, track);
            if (recentlyPlayed.getRecentlyPlayed().size() > 15)
                recentlyPlayed.getRecentlyPlayed().remove(15);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rAdapter.notifyDataSetChanged();
                }
            }, 320);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgLoader = new ImageLoader(this);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ctx = this;

        setContentView(R.layout.activity_home);

        fragMan = getFragmentManager();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spToolbar = (Toolbar) findViewById(R.id.smallPlayer_AB);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setShowHideAnimationEnabled(true);

        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        VisualizerView.act = this;
        main = this;

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screen_width = display.getWidth();
        screen_height = display.getHeight();

        recentBtn = (Button) findViewById(R.id.recent_btn);
        playlistBtn = (Button) findViewById(R.id.playlist_btn);
        favBtn = (Button) findViewById(R.id.fav_btn);

        recentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 21)
                    recentBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3F334D")));
                else
                    recentBtn.setBackgroundColor(Color.parseColor("#3F334D"));
                recentBtn.setTextColor(Color.WHITE);

                if (Build.VERSION.SDK_INT >= 21)
                    playlistBtn.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                else
                    playlistBtn.setBackgroundColor(Color.WHITE);
                playlistBtn.setTextColor(Color.parseColor("#3F334D"));

                if (Build.VERSION.SDK_INT >= 21)
                    favBtn.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                else
                    favBtn.setBackgroundColor(Color.WHITE);
                favBtn.setTextColor(Color.parseColor("#3F334D"));

            }
        });

        playlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 21)
                    playlistBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3F334D")));
                else
                    playlistBtn.setBackgroundColor(Color.parseColor("#3F334D"));
                playlistBtn.setTextColor(Color.WHITE);

                if (Build.VERSION.SDK_INT >= 21)
                    recentBtn.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                else
                    recentBtn.setBackgroundColor(Color.WHITE);
                recentBtn.setTextColor(Color.parseColor("#3F334D"));

                if (Build.VERSION.SDK_INT >= 21)
                    favBtn.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                else
                    favBtn.setBackgroundColor(Color.WHITE);
                favBtn.setTextColor(Color.parseColor("#3F334D"));
            }
        });

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 21)
                    favBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3F334D")));
                else
                    favBtn.setBackgroundColor(Color.parseColor("#3F334D"));
                favBtn.setTextColor(Color.WHITE);

                if (Build.VERSION.SDK_INT >= 21)
                    recentBtn.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                else
                    recentBtn.setBackgroundColor(Color.WHITE);
                recentBtn.setTextColor(Color.parseColor("#3F334D"));

                if (Build.VERSION.SDK_INT >= 21)
                    playlistBtn.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                else
                    playlistBtn.setBackgroundColor(Color.WHITE);
                playlistBtn.setTextColor(Color.parseColor("#3F334D"));
            }
        });

        ratio = (float) screen_height / (float) 1920;
        ratio2 = (float) screen_width / (float) 1080;
        ratio = Math.min(ratio, ratio2);

        playerControllerAB = (ImageView) findViewById(R.id.player_control_sp_AB);
        spImgAB = (CircleImageView) findViewById(R.id.selected_track_image_sp_AB);
        spTitleAB = (TextView) findViewById(R.id.selected_track_title_sp_AB);
        spTitleAB.setSelected(true);

        bottomToolbar = (FrameLayout) findViewById(R.id.bottomMargin);
        spHome = (Toolbar) findViewById(R.id.smallPlayer_home);
        playerControllerHome = (ImageView) findViewById(R.id.player_control_sp_home);
        spImgHome = (CircleImageView) findViewById(R.id.selected_track_image_sp_home);
        spTitleHome = (TextView) findViewById(R.id.selected_track_title_sp_home);

        playerControllerHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (queue != null && queue.getQueue().size() > 0) {
                    onQueueItemClicked(queueCurrentIndex);
                    bottomToolbar.setVisibility(View.INVISIBLE);
                }
            }
        });

        playerControllerHome.setImageResource(R.drawable.ic_play_arrow_white_48dp);

        spHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (queue != null && queue.getQueue().size() > 0) {
                    onQueueItemClicked(queueCurrentIndex);
                    bottomToolbar.setVisibility(View.INVISIBLE);
                }
            }
        });

        localRecyclerContainer = (RelativeLayout) findViewById(R.id.localRecyclerContainer);
        recentsRecyclerContainer = (RelativeLayout) findViewById(R.id.recentsRecyclerContainer);
        streamRecyclerContainer = (RelativeLayout) findViewById(R.id.streamRecyclerContainer);
        playlistRecyclerContainer = (RelativeLayout) findViewById(R.id.playlistRecyclerContainer);

        localNothingText = (TextView) findViewById(R.id.localNothingText);
        streamNothingText = (TextView) findViewById(R.id.streamNothingText);
        recentsNothingText = (TextView) findViewById(R.id.recentsNothingText);
        playlistNothingText = (TextView) findViewById(R.id.playlistNothingText);

        localViewAll = (TextView) findViewById(R.id.localViewAll);
        localViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment("local");
            }
        });
        streamViewAll = (TextView) findViewById(R.id.streamViewAll);
        streamViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment("stream");
            }
        });

        try {
            savedDNAs = new ObjectPreferenceLoader(ctx, "savedDNAs", AllSavedDNA.class).load();
            allPlaylists = new ObjectPreferenceLoader(ctx, "AllPlayLists", AllPlaylists.class).load();
            queue = new ObjectPreferenceLoader(ctx, "Queue", Queue.class).load();
            favouriteTracks = new ObjectPreferenceLoader(ctx, "Favourites", Favourite.class).load();
            recentlyPlayed = new ObjectPreferenceLoader(ctx, "RecentlyPlayed", RecentlyPlayed.class).load();
            isReloaded = new ObjectPreferenceLoader(ctx, "isReloaded", Boolean.class).load();
            queueCurrentIndex = new ObjectPreferenceLoader(ctx, "queueCurrentIndex", Integer.class).load();
        } catch (NoSuchPreferenceFoundException e) {
            Toast.makeText(HomeActivity.this, e.getMessage() + "::", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        if (allPlaylists == null) {
            allPlaylists = new AllPlaylists();
        }

        if (tempPlaylist == null) {
            tempPlaylist = new Playlist(null, null);
        }

        if (queue == null) {
            queue = new Queue();
        }

        if (favouriteTracks == null) {
            favouriteTracks = new Favourite();
        }

        if (recentlyPlayed == null) {
            recentlyPlayed = new RecentlyPlayed();
        }
        if (allDNAs == null) {
            allDNAs = new AllDNAModels();
        }
        if (allMusicFolders == null) {
            allMusicFolders = new AllMusicFolders();
        }
        if (savedDNAs == null) {
            Toast.makeText(HomeActivity.this, "null!", Toast.LENGTH_SHORT).show();
            savedDNAs = new AllSavedDNA();
        }

        if (queue != null && queue.getQueue().size() != 0) {
            UnifiedTrack utHome = queue.getQueue().get(queueCurrentIndex);
            if (utHome.getType()) {
                imgLoader.DisplayImage(utHome.getLocalTrack().getPath(), spImgHome);
                spTitleHome.setText(utHome.getLocalTrack().getTitle());
            } else {
                imgLoader.DisplayImage(utHome.getStreamTrack().getArtworkURL(), spImgHome);
                spTitleHome.setText(utHome.getStreamTrack().getTitle());
            }
        } else {
            bottomToolbar.setVisibility(View.INVISIBLE);
        }

        getLocalSongs();

        rAdapter = new RecentsListHorizontalAdapter(recentlyPlayed.getRecentlyPlayed(), ctx);
        recentsRecycler = (RecyclerView) findViewById(R.id.recentsMusicList_home);
        LinearLayoutManager mLayoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentsRecycler.setLayoutManager(mLayoutManager3);
        recentsRecycler.setItemAnimator(new DefaultItemAnimator());
        AlphaInAnimationAdapter alphaAdapter3 = new AlphaInAnimationAdapter(rAdapter);
        alphaAdapter3.setFirstOnly(false);
        ScaleInAnimationAdapter scaleAdapter3 = new ScaleInAnimationAdapter(alphaAdapter3);
        scaleAdapter3.setFirstOnly(false);
        recentsRecycler.setAdapter(scaleAdapter3);

        recentsRecycler.addOnItemTouchListener(new ClickItemTouchListener(recentsRecycler) {
            @Override
            boolean onClick(RecyclerView parent, View view, final int position, long id) {
                UnifiedTrack ut = recentlyPlayed.getRecentlyPlayed().get(position);
                boolean isRepeat = false;
                int pos = 0;
                for (int i = 0; i < queue.getQueue().size(); i++) {
                    UnifiedTrack ut1 = queue.getQueue().get(i);
                    if (ut1.getType() && ut.getType() && ut1.getLocalTrack().getTitle().equals(ut.getLocalTrack().getTitle())) {
                        isRepeat = true;
                        pos = i;
                        break;
                    }
                    if (!ut1.getType() && !ut.getType() && ut1.getStreamTrack().getTitle().equals(ut.getStreamTrack().getTitle())) {
                        isRepeat = true;
                        pos = i;
                        break;
                    }
                }
                if (!isRepeat && isReloaded) {
                    if (ut.getType()) {
                        LocalTrack track = ut.getLocalTrack();
                        if (queue.getQueue().size() == 0) {
                            queueCurrentIndex = 0;
                            queue.getQueue().add(new UnifiedTrack(true, track, null));
                        } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                            queueCurrentIndex++;
                            queue.getQueue().add(new UnifiedTrack(true, track, null));
                        } else if (isReloaded) {
                            isReloaded = false;
                            queueCurrentIndex = queue.getQueue().size();
                            queue.getQueue().add(new UnifiedTrack(true, track, null));
                        } else {
                            queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(true, track, null));
                        }
                        localSelectedTrack = track;
                        streamSelected = false;
                        localSelected = true;
                        queueCall = false;
                        isReloaded = false;
                        onLocalTrackSelected(position);
                    } else {
                        Track track = ut.getStreamTrack();
                        if (queue.getQueue().size() == 0) {
                            queueCurrentIndex = 0;
                            queue.getQueue().add(new UnifiedTrack(false, null, track));
                        } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                            queueCurrentIndex++;
                            queue.getQueue().add(new UnifiedTrack(false, null, track));
                        } else if (isReloaded) {
                            isReloaded = false;
                            queueCurrentIndex = queue.getQueue().size();
                            queue.getQueue().add(new UnifiedTrack(false, null, track));
                        } else {
                            queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(false, null, track));
                        }
                        selectedTrack = track;
                        streamSelected = true;
                        localSelected = false;
                        queueCall = false;
                        isReloaded = false;
                        onTrackSelected(position);
                    }
                } else {
                    onQueueItemClicked(pos);
                }

                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                final UnifiedTrack ut = recentlyPlayed.getRecentlyPlayed().get(position);

                PopupMenu popup = new PopupMenu(ctx, view);
                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Add to Playlist")) {
                            showAddToPlaylistDialog(ut);
                            pAdapter.notifyDataSetChanged();
                        }
                        if (item.getTitle().equals("Add to Queue")) {
                            Log.d("QUEUE", "CALLED");
                            queue.getQueue().add(ut);
                            logQueue();
                        }
                        if (item.getTitle().equals("Play")) {
                            boolean isRepeat = false;
                            int pos = 0;
                            for (int i = 0; i < queue.getQueue().size(); i++) {
                                UnifiedTrack ut1 = queue.getQueue().get(i);
                                if (ut1.getType() && ut.getType() && ut1.getLocalTrack().getTitle().equals(ut.getLocalTrack().getTitle())) {
                                    isRepeat = true;
                                    pos = i;
                                    break;
                                }
                                if (!ut1.getType() && !ut.getType() && ut1.getStreamTrack().getTitle().equals(ut.getStreamTrack().getTitle())) {
                                    isRepeat = true;
                                    pos = i;
                                    break;
                                }
                            }
                            if (!isRepeat && isReloaded) {
                                if (ut.getType()) {
                                    LocalTrack track = ut.getLocalTrack();
                                    if (queue.getQueue().size() == 0) {
                                        queueCurrentIndex = 0;
                                        queue.getQueue().add(new UnifiedTrack(true, track, null));
                                    } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                                        queueCurrentIndex++;
                                        queue.getQueue().add(new UnifiedTrack(true, track, null));
                                    } else if (isReloaded) {
                                        isReloaded = false;
                                        queueCurrentIndex = queue.getQueue().size();
                                        queue.getQueue().add(new UnifiedTrack(true, track, null));
                                    } else {
                                        queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(true, track, null));
                                    }
                                    localSelectedTrack = track;
                                    streamSelected = false;
                                    localSelected = true;
                                    queueCall = false;
                                    isReloaded = false;
                                    onLocalTrackSelected(position);
                                } else {
                                    Track track = ut.getStreamTrack();
                                    if (queue.getQueue().size() == 0) {
                                        queueCurrentIndex = 0;
                                        queue.getQueue().add(new UnifiedTrack(false, null, track));
                                    } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                                        queueCurrentIndex++;
                                        queue.getQueue().add(new UnifiedTrack(false, null, track));
                                    } else if (isReloaded) {
                                        isReloaded = false;
                                        queueCurrentIndex = queue.getQueue().size();
                                        queue.getQueue().add(new UnifiedTrack(false, null, track));
                                    } else {
                                        queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(false, null, track));
                                    }
                                    selectedTrack = track;
                                    streamSelected = true;
                                    localSelected = false;
                                    queueCall = false;
                                    isReloaded = false;
                                    onTrackSelected(position);
                                }
                            } else {
                                onQueueItemClicked(pos);
                            }
                        }
                        if (item.getTitle().equals("Play Next")) {
                            if (ut.getType()) {
                                LocalTrack track = finalLocalSearchResultList.get(position);
                                if (queue.getQueue().size() == 0) {
                                    queueCurrentIndex = 0;
                                    queue.getQueue().add(new UnifiedTrack(true, track, null));
                                    localSelectedTrack = track;
                                    streamSelected = false;
                                    localSelected = true;
                                    queueCall = false;
                                    isReloaded = false;
                                    onLocalTrackSelected(position);
                                } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                                    queue.getQueue().add(new UnifiedTrack(true, track, null));
                                } else if (isReloaded) {
                                    isReloaded = false;
                                    queueCurrentIndex = queue.getQueue().size();
                                    queue.getQueue().add(new UnifiedTrack(true, track, null));
                                    localSelectedTrack = track;
                                    streamSelected = false;
                                    localSelected = true;
                                    queueCall = false;
                                    isReloaded = false;
                                    onLocalTrackSelected(position);
                                } else {
                                    queue.getQueue().add(queueCurrentIndex + 1, new UnifiedTrack(true, track, null));
                                }
                            } else {
                                Track track = ut.getStreamTrack();
                                if (queue.getQueue().size() == 0) {
                                    queueCurrentIndex = 0;
                                    queue.getQueue().add(new UnifiedTrack(false, null, track));
                                    selectedTrack = track;
                                    streamSelected = true;
                                    localSelected = false;
                                    queueCall = false;
                                    isReloaded = false;
                                    onTrackSelected(position);
                                } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                                    queue.getQueue().add(new UnifiedTrack(false, null, track));
                                } else if (isReloaded) {
                                    isReloaded = false;
                                    queueCurrentIndex = queue.getQueue().size();
                                    queue.getQueue().add(new UnifiedTrack(false, null, track));
                                    selectedTrack = track;
                                    streamSelected = true;
                                    localSelected = false;
                                    queueCall = false;
                                    isReloaded = false;
                                    onTrackSelected(position);
                                } else {
                                    queue.getQueue().add(queueCurrentIndex + 1, new UnifiedTrack(false, null, track));
                                }
                            }
                        }
                        if (item.getTitle().equals("Add to Favourites")) {
                            addToFavourites(ut);
                        }
                        return true;
                    }
                });

                popup.show();
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        pAdapter = new PlayListsHorizontalAdapter(allPlaylists.getPlaylists());
        playlistsRecycler = (RecyclerView) findViewById(R.id.playlist_home);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        playlistsRecycler.setLayoutManager(mLayoutManager2);
        playlistsRecycler.setItemAnimator(new DefaultItemAnimator());
        AlphaInAnimationAdapter alphaAdapter2 = new AlphaInAnimationAdapter(pAdapter);
        alphaAdapter2.setFirstOnly(false);
        ScaleInAnimationAdapter scaleAdapter2 = new ScaleInAnimationAdapter(alphaAdapter2);
        scaleAdapter2.setFirstOnly(false);
        playlistsRecycler.setAdapter(scaleAdapter2);

        playlistsRecycler.addOnItemTouchListener(new ClickItemTouchListener(playlistsRecycler) {
            @Override
            boolean onClick(RecyclerView parent, View view, final int position, long id) {
                tempPlaylist = allPlaylists.getPlaylists().get(position);
                showFragment("playlist");
                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                PopupMenu popup = new PopupMenu(ctx, view);
                popup.getMenuInflater().inflate(R.menu.playlist_popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Play")) {
                            tempPlaylist = allPlaylists.getPlaylists().get(position);
                            queue.setQueue(tempPlaylist.getSongList());
                            queueCurrentIndex = 0;
                            onPlaylistPLayAll();
                        } else if (item.getTitle().equals("Delete")) {
                            allPlaylists.getPlaylists().remove(position);
                            if (PlayListFragment.vpAdapter != null) {
                                PlayListFragment.vpAdapter.notifyItemRemoved(position);
                            }
                            pAdapter.notifyItemRemoved(position);
                        }
                        return true;
                    }
                });
                popup.show();
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        adapter = new LocalTracksHorizontalAdapter(finalLocalSearchResultList);
        localListView = (RecyclerView) findViewById(R.id.localMusicList_home);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        localListView.setLayoutManager(mLayoutManager);
        localListView.setItemAnimator(new DefaultItemAnimator());
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        alphaAdapter.setFirstOnly(false);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
        scaleAdapter.setFirstOnly(false);
        localListView.setAdapter(scaleAdapter);

        localListView.addOnItemTouchListener(new ClickItemTouchListener(localListView) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                LocalTrack track = finalLocalSearchResultList.get(position);
                if (queue.getQueue().size() == 0) {
                    queueCurrentIndex = 0;
                    queue.getQueue().add(new UnifiedTrack(true, track, null));
                } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                    queueCurrentIndex++;
                    queue.getQueue().add(new UnifiedTrack(true, track, null));
                } else if (isReloaded) {
                    isReloaded = false;
                    queueCurrentIndex = queue.getQueue().size();
                    queue.getQueue().add(new UnifiedTrack(true, track, null));
                } else {
                    queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(true, track, null));
                }
                localSelectedTrack = track;
                streamSelected = false;
                localSelected = true;
                queueCall = false;
                isReloaded = false;
                onLocalTrackSelected(position);
                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                PopupMenu popup = new PopupMenu(ctx, view);
                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Add to Playlist")) {
                            showAddToPlaylistDialog(new UnifiedTrack(true, finalLocalSearchResultList.get(position), null));
                            pAdapter.notifyDataSetChanged();
                        }
                        if (item.getTitle().equals("Add to Queue")) {
                            Log.d("QUEUE", "CALLED");
                            queue.getQueue().add(new UnifiedTrack(true, finalLocalSearchResultList.get(position), null));
                            logQueue();
                        }
                        if (item.getTitle().equals("Play")) {
                            LocalTrack track = finalLocalSearchResultList.get(position);
                            if (queue.getQueue().size() == 0) {
                                queueCurrentIndex = 0;
                                queue.getQueue().add(new UnifiedTrack(true, track, null));
                            } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                                queueCurrentIndex++;
                                queue.getQueue().add(new UnifiedTrack(true, track, null));
                            } else if (isReloaded) {
                                isReloaded = false;
                                queueCurrentIndex = queue.getQueue().size();
                                queue.getQueue().add(new UnifiedTrack(true, track, null));
                            } else {
                                queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(true, track, null));
                            }
                            localSelectedTrack = track;
                            streamSelected = false;
                            localSelected = true;
                            queueCall = false;
                            isReloaded = false;
                            onLocalTrackSelected(position);
                        }
                        if (item.getTitle().equals("Play Next")) {
                            LocalTrack track = finalLocalSearchResultList.get(position);
                            if (queue.getQueue().size() == 0) {
                                queueCurrentIndex = 0;
                                queue.getQueue().add(new UnifiedTrack(true, track, null));
                                localSelectedTrack = track;
                                streamSelected = false;
                                localSelected = true;
                                queueCall = false;
                                isReloaded = false;
                                onLocalTrackSelected(position);
                            } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                                queue.getQueue().add(new UnifiedTrack(true, track, null));
                            } else if (isReloaded) {
                                isReloaded = false;
                                queueCurrentIndex = queue.getQueue().size();
                                queue.getQueue().add(new UnifiedTrack(true, track, null));
                                localSelectedTrack = track;
                                streamSelected = false;
                                localSelected = true;
                                queueCall = false;
                                isReloaded = false;
                                onLocalTrackSelected(position);
                            } else {
                                queue.getQueue().add(queueCurrentIndex + 1, new UnifiedTrack(true, track, null));
                            }
                        }
                        if (item.getTitle().equals("Add to Favourites")) {
                            UnifiedTrack ut = new UnifiedTrack(true, finalLocalSearchResultList.get(position), null);
                            addToFavourites(ut);
                        }
                        return true;
                    }
                });

                popup.show();
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        streamingListView = (RecyclerView) findViewById(R.id.trackList_home);

        streamingListView.addOnItemTouchListener(new ClickItemTouchListener(streamingListView) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                Track track = streamingTrackList.get(position);
                if (queue.getQueue().size() == 0) {
                    queueCurrentIndex = 0;
                    queue.getQueue().add(new UnifiedTrack(false, null, track));
                } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                    queueCurrentIndex++;
                    queue.getQueue().add(new UnifiedTrack(false, null, track));
                } else if (isReloaded) {
                    isReloaded = false;
                    queueCurrentIndex = queue.getQueue().size();
                    queue.getQueue().add(new UnifiedTrack(false, null, track));
                } else {
                    queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(false, null, track));
                }
                selectedTrack = track;
                streamSelected = true;
                localSelected = false;
                queueCall = false;
                isReloaded = false;
                onTrackSelected(position);
                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                PopupMenu popup = new PopupMenu(ctx, view);
                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Add to Playlist")) {
                            showAddToPlaylistDialog(new UnifiedTrack(false, null, streamingTrackList.get(position)));
                            pAdapter.notifyDataSetChanged();
                        }
                        if (item.getTitle().equals("Add to Queue")) {
                            Log.d("QUEUE", "CALLED");
                            queue.getQueue().add(new UnifiedTrack(false, null, streamingTrackList.get(position)));
                            logQueue();
                        }
                        if (item.getTitle().equals("Play")) {
                            Track track = streamingTrackList.get(position);
                            if (queue.getQueue().size() == 0) {
                                queueCurrentIndex = 0;
                                queue.getQueue().add(new UnifiedTrack(false, null, track));
                            } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                                queueCurrentIndex++;
                                queue.getQueue().add(new UnifiedTrack(false, null, track));
                            } else if (isReloaded) {
                                isReloaded = false;
                                queueCurrentIndex = queue.getQueue().size();
                                queue.getQueue().add(new UnifiedTrack(false, null, track));
                            } else {
                                queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(false, null, track));
                            }
                            selectedTrack = track;
                            streamSelected = true;
                            localSelected = false;
                            queueCall = false;
                            isReloaded = false;
                            onTrackSelected(position);
                        }
                        if (item.getTitle().equals("Play Next")) {
                            Track track = streamingTrackList.get(position);
                            if (queue.getQueue().size() == 0) {
                                queueCurrentIndex = 0;
                                queue.getQueue().add(new UnifiedTrack(false, null, track));
                                selectedTrack = track;
                                streamSelected = true;
                                localSelected = false;
                                queueCall = false;
                                isReloaded = false;
                                onTrackSelected(position);
                            } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                                queue.getQueue().add(new UnifiedTrack(false, null, track));
                            } else if (isReloaded) {
                                isReloaded = false;
                                queueCurrentIndex = queue.getQueue().size();
                                queue.getQueue().add(new UnifiedTrack(false, null, track));
                                selectedTrack = track;
                                streamSelected = true;
                                localSelected = false;
                                queueCall = false;
                                isReloaded = false;
                                onTrackSelected(position);
                            } else {
                                queue.getQueue().add(queueCurrentIndex + 1, new UnifiedTrack(false, null, track));
                            }
                        }
                        if (item.getTitle().equals("Add to Favourites")) {
                            UnifiedTrack ut = new UnifiedTrack(false, null, streamingTrackList.get(position));
                            addToFavourites(ut);
                        }
                        return true;
                    }
                });
                popup.show();
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        playerContainer = findViewById(R.id.playerFragContainer);

        if (finalLocalSearchResultList.size() == 0) {
            localListView.setVisibility(View.GONE);
            localNothingText.setVisibility(View.VISIBLE);
        } else {
            localListView.setVisibility(View.VISIBLE);
            localNothingText.setVisibility(View.INVISIBLE);
        }

        if (recentlyPlayed.getRecentlyPlayed().size() == 0) {
            recentsRecycler.setVisibility(View.GONE);
            recentsNothingText.setVisibility(View.VISIBLE);
        } else {
            recentsRecycler.setVisibility(View.VISIBLE);
            recentsNothingText.setVisibility(View.INVISIBLE);
        }

        if (streamingTrackList.size() == 0) {
            streamRecyclerContainer.setVisibility(View.GONE);
            streamNothingText.setVisibility(View.VISIBLE);
        } else {
            streamRecyclerContainer.setVisibility(View.VISIBLE);
            streamNothingText.setVisibility(View.INVISIBLE);
        }

        if (allPlaylists.getPlaylists().size() == 0) {
            playlistsRecycler.setVisibility(View.GONE);
            playlistNothingText.setVisibility(View.VISIBLE);
        } else {
            playlistsRecycler.setVisibility(View.VISIBLE);
            playlistNothingText.setVisibility(View.INVISIBLE);
        }

    }

    private void getLocalSongs() {

        ContentResolver musicResolver = this.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

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
                if (duration > 10000) {
                    LocalTrack lt = new LocalTrack(thisId, thisTitle, thisArtist, path, duration);
                    localTrackList.add(lt);
                    finalLocalSearchResultList.add(lt);

                    File f = new File(path);
                    String dirName = f.getParentFile().getName();
                    if (getFolder(dirName) == null) {
                        Toast.makeText(HomeActivity.this, dirName, Toast.LENGTH_SHORT).show();
                        MusicFolder mf = new MusicFolder(dirName);
                        mf.getLocalTracks().add(lt);
                        allMusicFolders.getMusicFolders().add(mf);
                    } else {
                        getFolder(dirName).getLocalTracks().add(lt);
                    }
                }

            }
            while (musicCursor.moveToNext());
        }
    }

    public MusicFolder getFolder(String folderName) {
        MusicFolder mf = null;
        for (int i = 0; i < allMusicFolders.getMusicFolders().size(); i++) {
            MusicFolder mf1 = allMusicFolders.getMusicFolders().get(i);
            if (mf1.getFolderName().equals(folderName)) {
                mf = mf1;
                break;
            }
        }
        return mf;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isEqualizerVisible) {
                showPlayer2();
            } else if (isQueueVisible) {
                if (isPlayerVisible)
                    showPlayer3();
                else
                    hideFragment("queue");
            } else {
                if (isPlayerVisible) {
                    hidePlayer();
                    showTabs();
                    isPlayerVisible = false;
                } else if (isLocalVisible) {
                    hideFragment("local");
                    setTitle("Music DNA");
                } else if (isQueueVisible) {
                    hideFragment("queue");
                    setTitle("Music DNA");
                } else if (isStreamVisible) {
                    hideFragment("stream");
                    setTitle("Music DNA");
                } else if (isPlaylistVisible) {
                    hideFragment("playlist");
                    setTitle("Music DNA");
                } else if (isEqualizerVisible) {
                    hideFragment("equalizer");
                    setTitle("Music DNA");
                } else if (isFavouriteVisible) {
                    hideFragment("favourite");
                    setTitle("Music DNA");
                } else if (isAnalogVisible) {
                    hideFragment("analog");
                    setTitle("Music DNA");
                } else if (isAllPlaylistVisible) {
                    hideFragment("allPlaylists");
                    setTitle("Music DNA");
                } else if (isFolderContentVisible) {
                    hideFragment("folderContent");
                    setTitle("Music DNA");
                } else if (isAllFolderVisible) {
                    hideFragment("allFolders");
                    setTitle("Music DNA");
                } else if (isAllSavedDnaVisisble) {
                    hideFragment("allSavedDNAs");
                    setTitle("Music DNA");
                } else if (isSavedDNAVisible) {
                    hideFragment("savedDNA");
                    setTitle("Music DNA");
                } else {
                    startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
                }
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
        if (id == R.id.action_analog) {
            showFragment("analog");
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            hideAllFrags();
            hideFragment("allPlaylists");
        } else if (id == R.id.nav_local) {
            showFragment("local");
        } else if (id == R.id.nav_playlists) {
            showFragment("allPlaylists");
        } else if (id == R.id.nav_fav) {
            showFragment("favourite");
        } else if (id == R.id.nav_folder) {
            showFragment("allFolders");
        } else if (id == R.id.nav_view) {
            showFragment("allSavedDNAs");
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_account_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
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

        if (finalLocalSearchResultList.size() == 0) {
            localListView.setVisibility(View.GONE);
            localNothingText.setVisibility(View.VISIBLE);
        } else {
            localListView.setVisibility(View.VISIBLE);
            localNothingText.setVisibility(View.INVISIBLE);
        }

        (localListView.getAdapter()).notifyDataSetChanged();
        if ((LocalMusicFragment.lv) != null)
            (LocalMusicFragment.lv.getAdapter()).notifyDataSetChanged();


    }

    private void updateStreamingList(String query) {

        new Thread(new CancelCall()).start();

        if (isPlayerVisible) {
            hidePlayer();
        }

        /*Update the Streaming List*/

        if (!query.equals("")) {
            /*streamingTrackList.clear();
            if(sAdapter!=null){
                streamingListView.getAdapter().notifyDataSetChanged();
            }*/
            streamRecyclerContainer.setVisibility(View.VISIBLE);
            startLoadingIndicator();
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
                        Log.d("RETRO", response.body() + "");
                        streamingTrackList = response.body();
                        sAdapter = new StreamTracksHorizontalAdapter(streamingTrackList, ctx);
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                        streamingListView.setLayoutManager(mLayoutManager);
                        streamingListView.setItemAnimator(new DefaultItemAnimator());
                        streamingListView.setAdapter(sAdapter);

                        if (streamingTrackList.size() == 0) {
                            streamRecyclerContainer.setVisibility(View.GONE);
                        } else {
                            streamRecyclerContainer.setVisibility(View.VISIBLE);
                        }

                        stopLoadingIndicator();
                        (streamingListView.getAdapter()).notifyDataSetChanged();

                        if (StreamMusicFragment.adapter != null)
                            StreamMusicFragment.adapter.notifyDataSetChanged();
                    } else {
                        stopLoadingIndicator();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("RETRO", t.getMessage());
                }
            });

        } else {
            stopLoadingIndicator();
        }

    }

    public void hideTabs() {
        /*appBarLayout.animate()
                .translationY(-1 * appBarLayout.getHeight())
                .setDuration(300);*/
        /*final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().hide();
            }
        }, 270);*/

        toolbar.animate()
                .setDuration(300)
                .translationY(-1 * toolbar.getHeight())
                .alpha(0.0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        toolbar.setVisibility(View.GONE);
                    }
                });

        spToolbar.setVisibility(View.VISIBLE);
        spToolbar.setAlpha(0.0f);
        spToolbar.setY(spToolbar.getHeight());
        spToolbar.animate()
                .setDuration(300)
                .translationY(0)
                .alpha(1.0f);

    }

    public void showTabs() {
        /*appBarLayout.animate()
                .translationY(0)
                .setDuration(300);*/
        /*final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().show();
            }
        }, 30);*/

        spToolbar.animate()
                .setDuration(300)
                .translationY(spToolbar.getHeight())
                .alpha(0.0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        spToolbar.setVisibility(View.GONE);
                    }
                });

        toolbar.setVisibility(View.VISIBLE);
        toolbar.setAlpha(0.0f);
        toolbar.animate()
                .setDuration(300)
                .translationY(0)
                .alpha(1.0f);

    }

    public void hidePlayer() {

        if (PlayerFragment.mVisualizerView != null)
            PlayerFragment.mVisualizerView.setVisibility(View.INVISIBLE);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        isPlayerVisible = false;

        if (PlayerFragment.cpb != null) {
            PlayerFragment.cpb.setAlpha(0.0f);
            PlayerFragment.cpb.setVisibility(View.VISIBLE);
            PlayerFragment.cpb.animate()
                    .alpha(1.0f);
        }
        if (PlayerFragment.smallPlayer != null) {
            PlayerFragment.smallPlayer.setAlpha(0.0f);
            PlayerFragment.smallPlayer.setVisibility(View.VISIBLE);
            PlayerFragment.smallPlayer.animate()
                    .alpha(1.0f);
        }

        playerContainer.setVisibility(View.VISIBLE);

        playerContainer.animate()
                .translationY(playerContainer.getHeight() - PlayerFragment.smallPlayer.getHeight())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (isAllSavedDnaVisisble) {
                            ViewSavedDNA.mVisualizerView2.setVisibility(View.VISIBLE);
                        }
                    }
                });

        PlayerFragment.player_controller.setAlpha(0.0f);
        PlayerFragment.player_controller.setImageDrawable(PlayerFragment.mainTrackController.getDrawable());

        PlayerFragment.player_controller.animate()
                .alpha(1.0f);
    }

    public void hidePlayer2() {

        isEqualizerVisible = true;

        if (PlayerFragment.mVisualizerView != null)
            PlayerFragment.mVisualizerView.setVisibility(View.INVISIBLE);

        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                playerContainer.animate()
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .translationX(playerContainer.getWidth());
            }
        }, 50);

        playerContainer.setVisibility(View.VISIBLE);

    }

    public void hidePlayer3() {

        isQueueVisible = true;

        if (PlayerFragment.mVisualizerView != null)
            PlayerFragment.mVisualizerView.setVisibility(View.INVISIBLE);

        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                playerContainer.animate()
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .translationX(-1 * playerContainer.getWidth());
            }
        }, 50);

        playerContainer.setVisibility(View.VISIBLE);

    }

    public void showPlayer() {

        if (isAllSavedDnaVisisble) {
            ViewSavedDNA.mVisualizerView2.setVisibility(View.INVISIBLE);
        }

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        searchView.setQuery("", false);
        searchView.setIconified(true);

        isPlayerVisible = true;
        isEqualizerVisible = false;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideFragment("equalizer");
            }
        }, 350);

        playerContainer.setVisibility(View.VISIBLE);
        if (PlayerFragment.mVisualizerView != null)
            PlayerFragment.mVisualizerView.setVisibility(View.INVISIBLE);

        if (PlayerFragment.player_controller != null) {
            PlayerFragment.player_controller.setAlpha(1.0f);
            PlayerFragment.player_controller.animate()
                    .setDuration(300)
                    .alpha(0.0f);
        }

        if (PlayerFragment.cpb != null) {
            PlayerFragment.cpb.animate()
                    .alpha(0.0f)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            if (isQueueVisible) {
                                hideFragment("queue");
                            }
                            isQueueVisible = false;
                        }
                    });
        }
        if (PlayerFragment.smallPlayer != null) {
            PlayerFragment.smallPlayer.animate()
                    .alpha(0.0f);
        }

        playerContainer.animate()
                .setDuration(300)
                .translationY(0);

        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PlayerFragment.mVisualizerView != null)
                    PlayerFragment.mVisualizerView.setVisibility(View.VISIBLE);
                if (PlayerFragment.player_controller != null) {
                    PlayerFragment.player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
                    PlayerFragment.player_controller.setAlpha(1.0f);
                }
            }
        }, 400);

    }

    public void showPlayer2() {
        searchView.setQuery("", false);
        searchView.setIconified(true);

        isPlayerVisible = true;
        isEqualizerVisible = false;
        isQueueVisible = false;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideFragment("equalizer");
            }
        }, 350);

        playerContainer.setVisibility(View.VISIBLE);
        if (PlayerFragment.mVisualizerView != null)
            PlayerFragment.mVisualizerView.setVisibility(View.INVISIBLE);

        playerContainer.animate()
                .setDuration(300)
                .translationX(0);

        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PlayerFragment.mVisualizerView != null)
                    PlayerFragment.mVisualizerView.setVisibility(View.VISIBLE);
            }
        }, 400);


    }

    public void showPlayer3() {

        isPlayerVisible = true;
        isEqualizerVisible = false;
        isQueueVisible = false;

        if (PlayerFragment.mVisualizerView != null)
            PlayerFragment.mVisualizerView.setVisibility(View.INVISIBLE);

        playerContainer.animate()
                .setDuration(300)
                .translationX(0);

        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PlayerFragment.mVisualizerView != null)
                    PlayerFragment.mVisualizerView.setVisibility(View.VISIBLE);
                hideFragment("queue");
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
//        PlayerFragment.mVisualizerView.normalizedPosition = ((float) ((System.currentTimeMillis() - PlayerFragment.startTime) + PlayerFragment.totalElapsedTime + PlayerFragment.deltaTime)) / (float) (PlayerFragment.durationInMilliSec);
        PlayerFragment.mVisualizerView.normalizedPosition = ((float) (PlayerFragment.mMediaPlayer.getCurrentPosition()) / (float) (PlayerFragment.durationInMilliSec));
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

        if (max > max_max) {
            max_max = max;
        }
        if (min < min_min) {
            min_min = min;
        }

        avg = ((avg * k) + ((max + min) / ((float) 2))) / ((float) (k + 1));

        avg_max = ((avg_max * k) + (max)) / ((float) (k + 1));

        avg_min = ((avg_min * k) + (min)) / ((float) (k + 1));

        k++;

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
            if (PlayerFragment.mVisualizerView.volume < 0.85) {
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
            PlayerFragment.mVisualizerView.size = ratio * ((float) (4.5 * PlayerFragment.mVisualizerView.volume * PlayerFragment.mVisualizerView.MAX_DOT_SIZE + Math.random() * 2));

            // alpha also based on volume ( amplitude )
            PlayerFragment.mVisualizerView.alpha = (float) (PlayerFragment.mVisualizerView.volume * 0.09);

            // final cartesian coordinates for drawing on canvas
            x = x * PlayerFragment.mVisualizerView.distance;
            y = y * PlayerFragment.mVisualizerView.distance;


            float[] hsv = new float[3];
            hsv[0] = PlayerFragment.mVisualizerView.color;
            hsv[1] = (float) 0.8;
            hsv[2] = (float) 0.72;

            // setting color of the Paint
            PlayerFragment.mVisualizerView.mForePaint.setColor(Color.HSVToColor(hsv));

            if (PlayerFragment.mVisualizerView.size >= 10.0 && PlayerFragment.mVisualizerView.size < 29.0) {
                PlayerFragment.mVisualizerView.mForePaint.setAlpha(15);
            } else if (PlayerFragment.mVisualizerView.size >= 29.0 && PlayerFragment.mVisualizerView.size <= 60.0) {
                PlayerFragment.mVisualizerView.mForePaint.setAlpha(9);
            } else if (PlayerFragment.mVisualizerView.size > 60.0) {
                PlayerFragment.mVisualizerView.mForePaint.setAlpha(3);
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

    public static void updatePoints2() {

    }

    public boolean checkInDNAmodels(LocalTrack lt, Track t) {

        boolean isRepeat = false;
        if (t == null) {
            for (int i = 0; i < allDNAs.getAllDNAs().size(); i++) {
                DNAModel dm = allDNAs.getAllDNAs().get(i);
//                if (dm.getType() && lt.getTitle().equals(dm.getTitle())) {
//                    isRepeat = true;
//                    break;
//                }
            }
        } else {
            for (int i = 0; i < allDNAs.getAllDNAs().size(); i++) {
                DNAModel dm = allDNAs.getAllDNAs().get(i);
//                if (!dm.getType() && t.getTitle().equals(dm.getTitle())) {
//                    isRepeat = true;
//                    break;
//                }
            }
        }
        return isRepeat;
    }

    @Override
    public void onComplete() {

        Log.d("FULLSONGMAXMIN", max_max + ":" + min_min);
        Toast.makeText(HomeActivity.this, max_max + " : " + min_min, Toast.LENGTH_LONG).show();
        Toast.makeText(HomeActivity.this, avg_max + " : " + avg + " : " + avg_min, Toast.LENGTH_LONG).show();
        max_max = Float.MIN_VALUE;
        min_min = Float.MAX_VALUE;
        avg = 0;
        avg_max = 0;
        avg_min = 0;
        k = 0;

        queueCall = true;
        if (!shuffleEnabled) {
            if (queueCurrentIndex < queue.getQueue().size() - 1) {
                queueCurrentIndex++;
                if (queue.getQueue().get(queueCurrentIndex).getType()) {
                    localSelectedTrack = queue.getQueue().get(queueCurrentIndex).getLocalTrack();
                    streamSelected = false;
                    localSelected = true;
                    onLocalTrackSelected(-1);
                } else {
                    selectedTrack = queue.getQueue().get(queueCurrentIndex).getStreamTrack();
                    streamSelected = true;
                    localSelected = false;
                    onTrackSelected(-1);
                }
            } else {
                if (repeatEnabled) {
                    queueCurrentIndex = 0;
                    onQueueItemClicked(0);
                } else {
                    PlayerFragment.mMediaPlayer.stop();
                }
            }
        } else {
            Random r = new Random();
            int x;
            while (true) {
                x = r.nextInt(queue.getQueue().size());
                if (x != queueCurrentIndex) {
                    break;
                }
            }
            queueCurrentIndex = x;
            if (queue.getQueue().get(queueCurrentIndex).getType()) {
                localSelectedTrack = queue.getQueue().get(queueCurrentIndex).getLocalTrack();
                streamSelected = false;
                localSelected = true;
                onLocalTrackSelected(-1);
            } else {
                selectedTrack = queue.getQueue().get(queueCurrentIndex).getStreamTrack();
                streamSelected = true;
                localSelected = false;
                onTrackSelected(-1);
            }
        }

    }

    @Override
    public void onPreviousTrack() {
        if (!shuffleEnabled) {
            if (queueCurrentIndex > 0) {
                queueCall = true;
                queueCurrentIndex--;
                if (queue.getQueue().get(queueCurrentIndex).getType()) {
                    localSelectedTrack = queue.getQueue().get(queueCurrentIndex).getLocalTrack();
                    streamSelected = false;
                    localSelected = true;
                    onLocalTrackSelected(-1);
                } else {
                    selectedTrack = queue.getQueue().get(queueCurrentIndex).getStreamTrack();
                    streamSelected = true;
                    localSelected = false;
                    onTrackSelected(-1);
                }
            } else {
                PlayerFragment.mMediaPlayer.stop();
            }
        } else {
            Random r = new Random();
            int x;
            while (true) {
                x = r.nextInt(queue.getQueue().size());
                if (x != queueCurrentIndex) {
                    break;
                }
            }
            queueCurrentIndex = x;
            if (queue.getQueue().get(queueCurrentIndex).getType()) {
                localSelectedTrack = queue.getQueue().get(queueCurrentIndex).getLocalTrack();
                streamSelected = false;
                localSelected = true;
                onLocalTrackSelected(-1);
            } else {
                selectedTrack = queue.getQueue().get(queueCurrentIndex).getStreamTrack();
                streamSelected = true;
                localSelected = false;
                onTrackSelected(-1);
            }
        }
    }

    @Override
    public void onQueueItemClicked(int position) {

        if (isPlayerVisible)
            showPlayer3();
        else
            showPlayer();

        queueCurrentIndex = position;
        UnifiedTrack ut = HomeActivity.queue.getQueue().get(position);
        if (ut.getType()) {
            LocalTrack track = ut.getLocalTrack();
            localSelectedTrack = track;
            streamSelected = false;
            localSelected = true;
            queueCall = false;
            isReloaded = false;
            onLocalTrackSelected(position);
        } else {
            Track track = ut.getStreamTrack();
            selectedTrack = track;
            streamSelected = true;
            localSelected = false;
            queueCall = false;
            isReloaded = false;
            onTrackSelected(position);
        }

    }

    @Override
    public void onPLaylistItemClicked(int position) {
        UnifiedTrack ut = HomeActivity.tempPlaylist.getSongList().get(position);
        if (ut.getType()) {
            LocalTrack track = ut.getLocalTrack();
            if (queue.getQueue().size() == 0) {
                queueCurrentIndex = 0;
                queue.getQueue().add(new UnifiedTrack(true, track, null));
            } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                queueCurrentIndex++;
                queue.getQueue().add(new UnifiedTrack(true, track, null));
            } else if (isReloaded) {
                isReloaded = false;
                queueCurrentIndex = queue.getQueue().size();
                queue.getQueue().add(new UnifiedTrack(true, track, null));
            } else {
                queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(true, track, null));
            }
            localSelectedTrack = track;
            streamSelected = false;
            localSelected = true;
            queueCall = false;
            isReloaded = false;
            onLocalTrackSelected(position);
        } else {
            Track track = ut.getStreamTrack();
            if (queue.getQueue().size() == 0) {
                queueCurrentIndex = 0;
                queue.getQueue().add(new UnifiedTrack(false, null, track));
            } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                queueCurrentIndex++;
                queue.getQueue().add(new UnifiedTrack(false, null, track));
            } else if (isReloaded) {
                isReloaded = false;
                queueCurrentIndex = queue.getQueue().size();
                queue.getQueue().add(new UnifiedTrack(false, null, track));
            } else {
                queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(false, null, track));
            }
            selectedTrack = track;
            streamSelected = true;
            localSelected = false;
            queueCall = false;
            isReloaded = false;
            onTrackSelected(position);
        }
    }

    @Override
    public void onFavouriteItemClicked(int position) {
        UnifiedTrack ut = HomeActivity.favouriteTracks.getFavourite().get(position);
        if (ut.getType()) {
            LocalTrack track = ut.getLocalTrack();
            if (queue.getQueue().size() == 0) {
                queueCurrentIndex = 0;
                queue.getQueue().add(new UnifiedTrack(true, track, null));
            } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                queueCurrentIndex++;
                queue.getQueue().add(new UnifiedTrack(true, track, null));
            } else if (isReloaded) {
                isReloaded = false;
                queueCurrentIndex = queue.getQueue().size();
                queue.getQueue().add(new UnifiedTrack(true, track, null));
            } else {
                queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(true, track, null));
            }
            localSelectedTrack = track;
            streamSelected = false;
            localSelected = true;
            queueCall = false;
            isReloaded = false;
            onLocalTrackSelected(position);
        } else {
            Track track = ut.getStreamTrack();
            if (queue.getQueue().size() == 0) {
                queueCurrentIndex = 0;
                queue.getQueue().add(new UnifiedTrack(false, null, track));
            } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                queueCurrentIndex++;
                queue.getQueue().add(new UnifiedTrack(false, null, track));
            } else if (isReloaded) {
                isReloaded = false;
                queueCurrentIndex = queue.getQueue().size();
                queue.getQueue().add(new UnifiedTrack(false, null, track));
            } else {
                queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(false, null, track));
            }
            selectedTrack = track;
            streamSelected = true;
            localSelected = false;
            queueCall = false;
            isReloaded = false;
            onTrackSelected(position);
        }
    }

    @Override
    public void onPlaylistPLayAll() {
        onQueueItemClicked(0);
        hideFragment("playlist");
        showFragment("queue");
    }

    @Override
    public void onFavouritePlayAll() {
        if (queue.getQueue().size() > 0) {
            onQueueItemClicked(0);
            hideFragment("favourite");
            showFragment("queue");
        }
    }

    @Override
    public void onQueueSave() {
        showSaveQueueDialog();
    }

    @Override
    public void onEqualizerClicked() {
        hideAllFrags();
        hidePlayer2();
        showFragment("equalizer");
    }

    @Override
    public void onQueueClicked() {
        hideAllFrags();
        hidePlayer3();
        showFragment("queue");
    }

    @Override
    public void onPrepared() {
        showNotification();
    }

    @Override
    public void onPlaylistTouched(int pos) {
        tempPlaylist = allPlaylists.getPlaylists().get(pos);
        showFragment("playlist");
    }

    @Override
    public void onPlaylistMenuPLayAll() {
        onPlaylistPLayAll();
    }

    @Override
    public void onFolderClicked(int pos) {
        tempMusicFolder = allMusicFolders.getMusicFolders().get(pos);
        tempFolderContent = tempMusicFolder.getLocalTracks();
        showFragment("folderContent");
    }

    @Override
    public void onFolderContentPlayAll() {
        List<UnifiedTrack> lut = new ArrayList<>();
        for (int i = 0; i < tempFolderContent.size(); i++) {
            lut.add(new UnifiedTrack(true, tempFolderContent.get(i), null));
        }
        queue.setQueue(lut);
        queueCurrentIndex = 0;
        onPlaylistMenuPLayAll();
    }

    @Override
    public void onFolderContentItemClick(int position) {
        onLocalTrackSelected(position);
    }

    public static class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            updatePoints();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            PlayerFragment.mVisualizerView.updateVisualizer(mBytes);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        new ObjectPreferenceLoader(ctx, "savedDNAs", AllSavedDNA.class).save(savedDNAs);
        new ObjectPreferenceLoader(ctx, "AllPlayLists", AllPlaylists.class).save(allPlaylists);
        new ObjectPreferenceLoader(ctx, "Queue", Queue.class).save(queue);
        new ObjectPreferenceLoader(ctx, "RecentlyPlayed", RecentlyPlayed.class).save(recentlyPlayed);
        new ObjectPreferenceLoader(ctx, "Favourites", Favourite.class).save(favouriteTracks);
        new ObjectPreferenceLoader(ctx, "queueCurrentIndex", Integer.class).save(queueCurrentIndex);
        new ObjectPreferenceLoader(ctx, "isReloaded", Boolean.class).save(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    public static void showAddToPlaylistDialog(final UnifiedTrack track) {
        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.add_to_playlist_dialog);
        dialog.setTitle("Add to Playlist");

        ListView lv = (ListView) dialog.findViewById(R.id.playlist_list);
        PlayListAdapter adapter;
        if (allPlaylists.getPlaylists() != null && allPlaylists.getPlaylists().size() != 0) {
            adapter = new PlayListAdapter(allPlaylists.getPlaylists(), ctx);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    allPlaylists.getPlaylists().get(position).addSong(track);
                    playlistsRecycler.setVisibility(View.VISIBLE);
                    playlistNothingText.setVisibility(View.INVISIBLE);
                    pAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
        } else {
            lv.setVisibility(View.GONE);
        }

        // set the custom dialog components - text, image and button
        final EditText text = (EditText) dialog.findViewById(R.id.new_playlist_name);
        ImageView image = (ImageView) dialog.findViewById(R.id.confirm_button);
        // if button is clicked, close the custom dialog
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().toString().trim().equals("")) {
                    text.setError("Enter Playlist Name!");
                } else {
                    List<UnifiedTrack> l = new ArrayList<UnifiedTrack>();
                    l.add(track);
                    Playlist pl = new Playlist(l, text.getText().toString().trim());
                    allPlaylists.addPlaylist(pl);
                    playlistsRecycler.setVisibility(View.VISIBLE);
                    playlistNothingText.setVisibility(View.INVISIBLE);
                    pAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    public void showSaveQueueDialog() {
        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.add_to_playlist_dialog);
        dialog.setTitle("Save Queue");

        ListView lv = (ListView) dialog.findViewById(R.id.playlist_list);
        lv.setVisibility(View.GONE);

        // set the custom dialog components - text, image and button
        final EditText text = (EditText) dialog.findViewById(R.id.new_playlist_name);
        ImageView image = (ImageView) dialog.findViewById(R.id.confirm_button);
        // if button is clicked, close the custom dialog
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().toString().trim().equals("")) {
                    text.setError("Enter Playlist Name!");
                } else {
                    Playlist pl = new Playlist(text.getText().toString());
                    pl.setSongList(queue.getQueue());
                    allPlaylists.addPlaylist(pl);
                    playlistsRecycler.setVisibility(View.VISIBLE);
                    playlistNothingText.setVisibility(View.INVISIBLE);
                    pAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public void logQueue() {
        Log.d("QUEUE", "ENTERED");
        for (int i = 0; i < queue.getQueue().size(); i++) {
            if (queue.getQueue().get(i).getLocalTrack() != null)
                Log.d("QUEUE", queue.getQueue().get(i).getLocalTrack().getTitle() + ":" + queue.getQueue().get(i).getStreamTrack());
            else
                Log.d("QUEUE", queue.getQueue().get(i).getLocalTrack() + ":" + queue.getQueue().get(i).getStreamTrack().getTitle());
        }
    }

    public void startLoadingIndicator() {
        findViewById(R.id.loadingIndicator).setVisibility(View.VISIBLE);
        streamingListView.setVisibility(View.INVISIBLE);
        streamNothingText.setVisibility(View.INVISIBLE);
    }

    public void stopLoadingIndicator() {
        findViewById(R.id.loadingIndicator).setVisibility(View.INVISIBLE);
        streamingListView.setVisibility(View.VISIBLE);
        if (streamingTrackList.size() == 0) {
            streamNothingText.setVisibility(View.VISIBLE);
        }
    }

    public void showFragment(String type) {

        hideAllFrags();

        if (type.equals("local") && !isLocalVisible) {
            setTitle("Local");
            isLocalVisible = true;
            android.app.FragmentManager fm = getFragmentManager();
            LocalMusicFragment newFragment = new LocalMusicFragment();
            LocalMusicFragment.mCallback = this;
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.fragContainer, newFragment, "local")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("queue") && !isQueueVisible) {
            isQueueVisible = true;
            android.app.FragmentManager fm = getFragmentManager();
            QueueFragment newFragment = new QueueFragment();
            QueueFragment.mCallback = this;
            QueueFragment.mCallback2 = this;
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.fragContainer, newFragment, "queue")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("stream") && !isStreamVisible) {
            setTitle("SoundCloud");
            isStreamVisible = true;
            android.app.FragmentManager fm = getFragmentManager();
            StreamMusicFragment newFragment = new StreamMusicFragment();
            StreamMusicFragment.mCallback = this;
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.fragContainer, newFragment, "stream")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("playlist") && !isPlaylistVisible) {
            setTitle(tempPlaylist.getPlaylistName());
            isPlaylistVisible = true;
            android.app.FragmentManager fm = getFragmentManager();
            ViewPlaylistFragment newFragment = new ViewPlaylistFragment();
            ViewPlaylistFragment.mCallback = this;
            ViewPlaylistFragment.mCallback2 = this;
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.fragContainer, newFragment, "playlist")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("equalizer") && !isEqualizerVisible) {
            isEqualizerVisible = true;
            android.app.FragmentManager fm = getFragmentManager();
            EqualizerFragment newFragment = new EqualizerFragment();
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.fragContainer, newFragment, "equalizer")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("favourite") && !isFavouriteVisible) {
            setTitle("Favourites");
            isFavouriteVisible = true;
            android.app.FragmentManager fm = getFragmentManager();
            FavouritesFragment newFragment = new FavouritesFragment();
            FavouritesFragment.mCallback = this;
            FavouritesFragment.mCallback2 = this;
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.fragContainer, newFragment, "favourite")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("analog") && !isAnalogVisible) {
            setTitle("Analog");
            isAnalogVisible = true;
            android.app.FragmentManager fm = getFragmentManager();
            AnalogControllerFragment newFragment = new AnalogControllerFragment();
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.fragContainer, newFragment, "analog")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("allPlaylists") && !isAllPlaylistVisible) {
            setTitle("All Playlists");
            isAllPlaylistVisible = true;
            android.app.FragmentManager fm = getFragmentManager();
            PlayListFragment newFragment = new PlayListFragment();
            PlayListFragment.mCallback = this;
            PlayListFragment.mCallback2 = this;
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.fragContainer, newFragment, "allPlaylists")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("folderContent") && !isFolderContentVisible) {
            setTitle(tempMusicFolder.getFolderName());
            isFolderContentVisible = true;
            android.app.FragmentManager fm = getFragmentManager();
            FolderContentFragment.mCallback = this;
            FolderContentFragment.mCallback2 = this;
            FolderContentFragment newFragment = new FolderContentFragment();
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.fragContainer, newFragment, "folderContent")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("allFolders") && !isAllFolderVisible) {
            setTitle("Folders");
            isAllFolderVisible = true;
            android.app.FragmentManager fm = getFragmentManager();
            FolderFragment newFragment = new FolderFragment();
            FolderFragment.mCallback = this;
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.fragContainer, newFragment, "allFolders")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("allSavedDNAs") && !isAllSavedDnaVisisble) {
            setTitle("Saved DNAs");
            isAllSavedDnaVisisble = true;
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getSupportActionBar().hide();
            android.app.FragmentManager fm = getFragmentManager();
            ViewSavedDNA newFragment = new ViewSavedDNA();
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.fragContainer, newFragment, "allSavedDNAs")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }

    public void hideFragment(String type) {
        if (type.equals("local")) {
            isLocalVisible = false;
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("local");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("queue")) {
            isQueueVisible = false;
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("queue");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("stream")) {
            isStreamVisible = false;
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("stream");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("playlist")) {
            isPlaylistVisible = false;
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("playlist");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("equalizer")) {
            isEqualizerVisible = false;
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("equalizer");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("favourite")) {
            isFavouriteVisible = false;
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("favourite");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("analog")) {
            isAnalogVisible = false;
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("analog");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("allPlaylists")) {
            isAllPlaylistVisible = false;
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("allPlaylists");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("folderContent")) {
            isFolderContentVisible = false;
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("folderContent");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("allFolders")) {
            isAllFolderVisible = false;
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("allFolders");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("allSavedDNAs")) {
            isAllSavedDnaVisisble = false;
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            getSupportActionBar().show();
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("allSavedDNAs");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        }
    }

    public void hideAllFrags() {
        hideFragment("local");
        hideFragment("queue");
        hideFragment("stream");
        hideFragment("playlist");
        hideFragment("equalizer");
        hideFragment("favourite");
        hideFragment("folderContent");
        hideFragment("allFolders");
        hideFragment("savedDNA");
        hideFragment("allSavedDNAs");

        setTitle("Music DNA");

    }

    public void showNotification() {

        if (Build.VERSION.SDK_INT >= 21) {
            if (!isNotificationVisible) {
                Toast.makeText(HomeActivity.this, "Starting", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MediaPlayerService.class);
                intent.setAction(Constants.ACTION_PLAY);
                startService(intent);
                isNotificationVisible = true;
            }
        }


        /*Notification notification;

        String ns = Context.NOTIFICATION_SERVICE;
        notificationManager = (NotificationManager) getSystemService(ns);

        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification_view);
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent switchIntent = new Intent("com.sdsmdg.harjot.MusicDNA.ACTION_PLAY_PAUSE");
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 100, switchIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.btn_pause_play_in_notification, pendingSwitchIntent);

        try {
            if (PlayerFragment.mMediaPlayer.isPlaying()) {
                notificationView.setImageViewResource(R.id.btn_pause_play_in_notification, R.drawable.ic_pause_white_48dp);
            } else {
                notificationView.setImageViewResource(R.id.btn_pause_play_in_notification, R.drawable.ic_play_arrow_white_48dp);
            }
        } catch (Exception e) {

        }

        Intent switchIntent2 = new Intent("com.sdsmdg.harjot.MusicDNA.ACTION_NEXT");
        PendingIntent pendingSwitchIntent2 = PendingIntent.getBroadcast(this, 100, switchIntent2, 0);
        notificationView.setOnClickPendingIntent(R.id.btn_next_in_notification, pendingSwitchIntent2);

        Intent switchIntent3 = new Intent("com.sdsmdg.harjot.MusicDNA.ACTION_PREVIOUS");
        PendingIntent pendingSwitchIntent3 = PendingIntent.getBroadcast(this, 100, switchIntent3, 0);
        notificationView.setOnClickPendingIntent(R.id.btn_prev_in_notification, pendingSwitchIntent3);

        Notification.Builder builder = new Notification.Builder(this);
        notification = builder.setContentTitle("MusicDNA")
                .setContentText("Slide down on note to expand")
                .setSmallIcon(R.drawable.ic_default)
                .setContentTitle("Title")
                .setContentText("Artist")
                .addAction(R.drawable.ic_skip_previous_white_48dp, "Prev", pendingSwitchIntent3)
                .addAction(R.drawable.ic_play_arrow_white_48dp, "Play", pendingSwitchIntent)
                .addAction(R.drawable.ic_skip_next_white_48dp, "Next", pendingSwitchIntent2)
                .setLargeIcon(((BitmapDrawable) PlayerFragment.selected_track_image.getDrawable()).getBitmap())
                .build();

        notification.priority = Notification.PRIORITY_MAX;
        notification.bigContentView = notificationView;
        notification.contentIntent = pendingNotificationIntent;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        notificationView.setImageViewBitmap(R.id.image_in_notification, ((BitmapDrawable) PlayerFragment.selected_track_image.getDrawable()).getBitmap());
        if (PlayerFragment.localIsPlaying) {
            notificationView.setTextViewText(R.id.title_in_notification, PlayerFragment.localTrack.getTitle());
            notificationView.setTextViewText(R.id.artist_in_notification, PlayerFragment.localTrack.getArtist());
        } else {
            notificationView.setTextViewText(R.id.title_in_notification, PlayerFragment.track.getTitle());
            notificationView.setTextViewText(R.id.artist_in_notification, "");
        }

        notificationManager.notify(1, notification);*/

    }

    public void updatePlayPauseIcon() {
        showNotification();
    }

    public void HideBottomFakeToolbar() {
        bottomToolbar.setVisibility(View.INVISIBLE);
    }

    public static void addToFavourites(UnifiedTrack ut) {
        boolean isRepeat = false;
        for (int i = 0; i < HomeActivity.favouriteTracks.getFavourite().size(); i++) {
            UnifiedTrack ut1 = HomeActivity.favouriteTracks.getFavourite().get(i);
            if (ut.getType() && ut1.getType()) {
                if (ut.getLocalTrack().getTitle().equals(ut1.getLocalTrack().getTitle())) {
                    isRepeat = true;
                    break;
                }
            } else if (!ut.getType() && !ut1.getType()) {
                if (ut.getStreamTrack().getTitle().equals(ut1.getStreamTrack().getTitle())) {
                    isRepeat = true;
                    break;
                }
            }
        }

        if (!isRepeat)
            favouriteTracks.getFavourite().add(ut);

    }

    public static void saveBitmapAsImage(Bitmap bmp, String fileName) {
        String path = Environment.getExternalStorageDirectory().toString() + "/SavedDNAs/";
        File f = new File(path);
        f.mkdirs();
        OutputStream fOut = null;
        File file = new File(path, fileName + ".png");
        try {
            fOut = new FileOutputStream(file);
            Bitmap pictureBitmap = bmp;
            pictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        } catch (FileNotFoundException e) {
            Log.e("ERROR", e.getMessage());
            e.printStackTrace();
        }
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
