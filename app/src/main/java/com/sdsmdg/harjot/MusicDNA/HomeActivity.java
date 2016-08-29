package com.sdsmdg.harjot.MusicDNA;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.Gson;
import com.sdsmdg.harjot.MusicDNA.Interfaces.StreamService;
import com.sdsmdg.harjot.MusicDNA.LocalMusicFragments.AlbumFragment;
import com.sdsmdg.harjot.MusicDNA.LocalMusicFragments.ArtistFragment;
import com.sdsmdg.harjot.MusicDNA.Models.Album;
import com.sdsmdg.harjot.MusicDNA.Models.AllDNAModels;
import com.sdsmdg.harjot.MusicDNA.Models.AllMusicFolders;
import com.sdsmdg.harjot.MusicDNA.Models.AllPlaylists;
import com.sdsmdg.harjot.MusicDNA.Models.AllSavedDNA;
import com.sdsmdg.harjot.MusicDNA.Models.Artist;
import com.sdsmdg.harjot.MusicDNA.Models.Favourite;
import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.Models.MusicFolder;
import com.sdsmdg.harjot.MusicDNA.Models.Playlist;
import com.sdsmdg.harjot.MusicDNA.Models.Queue;
import com.sdsmdg.harjot.MusicDNA.Models.RecentlyPlayed;
import com.sdsmdg.harjot.MusicDNA.Models.SavedDNA;
import com.sdsmdg.harjot.MusicDNA.Models.Settings;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
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
        PlayerFragment.onPlayPauseListener,
        PlayerFragment.fullScreenListener,
        PlayerFragment.onSettingsClickedListener,
        PlayerFragment.resetNotificationListener,
        PlayListFragment.onPLaylistTouchedListener,
        PlayListFragment.onPlaylistMenuPlayAllListener,
        FolderFragment.onFolderClickedListener,
        FolderContentFragment.onFolderContentPlayAllListener,
        FolderContentFragment.onFolderContentItemClickListener,
        ViewSavedDNA.onShareListener,
        AlbumFragment.onAlbumClickListener,
        ViewAlbumFragment.onAlbumSongClickListener,
        ViewAlbumFragment.onAlbumPlayAllListener,
        ArtistFragment.onArtistClickListener,
        ViewArtistFragment.onArtistSongClickListener,
        ViewArtistFragment.onArtistPlayAllListener,
        RecentsFragment.onRecentItemClickedListener,
        RecentsFragment.onRepeatListener {


    public static List<LocalTrack> localTrackList = new ArrayList<>();
    public static List<LocalTrack> finalLocalSearchResultList = new ArrayList<>();
    public static List<Track> streamingTrackList = new ArrayList<>();
    public static List<Album> albums = new ArrayList<>();
    public static List<Album> finalAlbums = new ArrayList<>();
    public static List<Artist> artists = new ArrayList<>();
    public static List<Artist> finalArtists = new ArrayList<>();
    public static List<UnifiedTrack> continuePlayingList = new ArrayList<>();

    String version;
    TextView copyrightText;

    static Canvas cacheCanvas;

    public static Album tempAlbum;
    public static Artist tempArtist;

    private Dialog progress;

    static float ratio, ratio2;

    AppBarLayout appBarLayout;

    View gradientView;

    Toolbar spHome;
    ImageView playerControllerHome;
    static FrameLayout bottomToolbar;
    CircleImageView spImgHome;
    TextView spTitleHome;

    static ImageView playerControllerAB;
    static ImageView overflowMenuAB;
    static CircleImageView spImgAB;
    static TextView spTitleAB;

    static SwitchCompat equalizerSwitch;

    SharedPreferences mPrefs;
    static SharedPreferences.Editor prefsEditor;
    static Gson gson;

    ImageLoader imgLoader;

    public static RecentlyPlayed recentlyPlayed;
    static Favourite favouriteTracks;
    static Settings settings;
    static Queue queue;
    static Playlist tempPlaylist;
    static int tempPlaylistNumber;
    static int renamePlaylistNumber;
    static AllPlaylists allPlaylists;
    static AllDNAModels allDNAs;
    static AllMusicFolders allMusicFolders;

    static AllSavedDNA savedDNAs;
    static SavedDNA tempSavedDNA;

    static List<LocalTrack> tempFolderContent;
    static MusicFolder tempMusicFolder;

    static boolean shuffleEnabled = true;
    static boolean repeatEnabled = false;
    static boolean repeatOnceEnabled = false;

    static boolean nextControllerClicked = false;

    static boolean isFavourite = false;

    public static boolean isReloaded = false;

    public static int queueCurrentIndex = 0;

    public static boolean isSaveDNAEnabled = false;

    public static Context ctx;

    static boolean queueCall = false;

    boolean wasMediaPlayerPlaying = false;

    static float max_max = Float.MIN_VALUE;
    static float min_min = Float.MAX_VALUE;
    static float avg_max = 0;
    static float avg_min = 0;
    static float avg = 0;
    static int k = 0;

    DrawerLayout drawer;

    static NotificationManager notificationManager;

    PhoneStateListener phoneStateListener;

    LocalTracksHorizontalAdapter adapter;
    StreamTracksHorizontalAdapter sAdapter;
    static PlayListsHorizontalAdapter pAdapter;
    static RecentsListHorizontalAdapter rAdapter;

    NavigationView navigationView;

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

    RelativeLayout localBanner;
    RelativeLayout favBanner;
    RelativeLayout recentBanner;
    RelativeLayout folderBanner;
    RelativeLayout savedDNABanner;

    ImageView localBannerPlayAll;

    TextView localViewAll, streamViewAll;

    TextView localNothingText;
    TextView streamNothingText;
    TextView recentsNothingText;
    static TextView playlistNothingText;

    static int screen_width;
    static int screen_height;

    static Toolbar toolbar;
    static Toolbar spToolbar;
    static Toolbar equalizerToolbar;

    Toolbar queueToolbar;
    ImageView queueBackButton;
    TextView queueClearText;

    static Toolbar fragmentToolbar;
    ImageView fragmentBackButton;
    TextView fragmentToolbarTitle;

    static int themeColor = Color.parseColor("#000000");
    static float minAudioStrength = 0.40f;

    public static Activity main;

    static float seekBarColor;

    static byte[] mBytes;

    ShowcaseView showCase;

    View playerContainer;

    static android.app.FragmentManager fragMan;

    public static boolean isPlayerVisible = false;
    public static boolean isLocalVisible = false;
    public static boolean isStreamVisible = false;
    public static boolean isQueueVisible = false;
    public static boolean isPlaylistVisible = false;
    public static boolean isEqualizerVisible = false;
    public static boolean isFavouriteVisible = false;
    public static boolean isAllPlaylistVisible = false;
    public static boolean isAllFolderVisible = false;
    public static boolean isFolderContentVisible = false;
    public static boolean isAllSavedDnaVisisble = false;
    public static boolean isSavedDNAVisible = false;
    public static boolean isAlbumVisible = false;
    public static boolean isArtistVisible = false;
    public static boolean isRecentVisible = false;
    public static boolean isFullScreenEnabled = false;
    public static boolean isSettingsVisible = false;

    public static boolean hasQueueEnded = false;

    static boolean isEqualizerEnabled = false;
    static boolean isEqualizerReloaded = false;

    static int[] seekbarpos = new int[5];
    static int presetPos;

    boolean isNotificationVisible = false;

    static LocalTrack localSelectedTrack;
    static Track selectedTrack;

    static boolean localSelected = false;
    static boolean streamSelected = false;

    static short reverbPreset = -1, bassStrength = -1;
    static int y = 0;

    public void onTrackSelected(int position) {

        HideBottomFakeToolbar();

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = ((Activity) (ctx)).getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#000000"));
        }

        if (!queueCall) {
            hideKeyboard();

            searchView.setQuery("", false);
            searchView.setIconified(true);
            new Thread(new CancelCall()).start();

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
                PlayerFragment.mCallback8 = this;
                PlayerFragment.mCallback9 = this;
                if (Build.VERSION.SDK_INT < 21)
                    PlayerFragment.mCallback7 = this;
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
                        .add(R.id.player_frag_container, newFragment, "player")
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

        UnifiedTrack track = new UnifiedTrack(false, null, PlayerFragment.track);
        for (int i = 0; i < recentlyPlayed.getRecentlyPlayed().size(); i++) {
            if (!recentlyPlayed.getRecentlyPlayed().get(i).getType() && recentlyPlayed.getRecentlyPlayed().get(i).getStreamTrack().getTitle().equals(track.getStreamTrack().getTitle())) {
                recentlyPlayed.getRecentlyPlayed().remove(i);
                rAdapter.notifyItemRemoved(i);
                break;
            }
        }
        recentlyPlayed.getRecentlyPlayed().add(0, track);
        recentsRecycler.setVisibility(View.VISIBLE);
        recentsNothingText.setVisibility(View.INVISIBLE);
        continuePlayingList.clear();
        for (int i = 0; i < Math.min(10, recentlyPlayed.getRecentlyPlayed().size()); i++) {
            continuePlayingList.add(recentlyPlayed.getRecentlyPlayed().get(i));
        }
        rAdapter.notifyDataSetChanged();
    }

    public void onLocalTrackSelected(int position) {

        HideBottomFakeToolbar();

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = ((Activity) (ctx)).getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#000000"));
        }

        if (!queueCall) {
            hideKeyboard();

            searchView.setQuery("", false);
            searchView.setIconified(true);
            new Thread(new CancelCall()).start();

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
                PlayerFragment.mCallback8 = this;
                PlayerFragment.mCallback9 = this;
                if (Build.VERSION.SDK_INT < 21)
                    PlayerFragment.mCallback7 = this;
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
                        .add(R.id.player_frag_container, newFragment, "player")
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

        UnifiedTrack track = new UnifiedTrack(true, PlayerFragment.localTrack, null);
        for (int i = 0; i < recentlyPlayed.getRecentlyPlayed().size(); i++) {
            if (recentlyPlayed.getRecentlyPlayed().get(i).getType() && recentlyPlayed.getRecentlyPlayed().get(i).getLocalTrack().getTitle().equals(track.getLocalTrack().getTitle())) {
                recentlyPlayed.getRecentlyPlayed().remove(i);
                rAdapter.notifyItemRemoved(i);
                break;
            }
        }
        recentlyPlayed.getRecentlyPlayed().add(0, track);
        recentsRecycler.setVisibility(View.VISIBLE);
        recentsNothingText.setVisibility(View.INVISIBLE);
        continuePlayingList.clear();
        for (int i = 0; i < Math.min(10, recentlyPlayed.getRecentlyPlayed().size()); i++) {
            continuePlayingList.add(recentlyPlayed.getRecentlyPlayed().get(i));
        }
        rAdapter.notifyDataSetChanged();
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 320);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_home);

        gradientView = findViewById(R.id.gradient_view);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        copyrightText = (TextView) findViewById(R.id.copyright_text);
        copyrightText.setTypeface(SplashActivity.tf2);
        copyrightText.setText("\nMusic DNA v" + version + " \n© 2016");

        imgLoader = new ImageLoader(this);
        ctx = this;
        fragMan = getFragmentManager();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        spToolbar = (Toolbar) findViewById(R.id.smallPlayer_AB);

        queueToolbar = (Toolbar) findViewById(R.id.queue_toolbar);
        queueBackButton = (ImageView) findViewById(R.id.queue_toolbar_back_button_img);
        queueBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        queueClearText = (TextView) findViewById(R.id.clear_queue_txt);
        queueClearText.setTypeface(SplashActivity.tf2);
        queueClearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearQueue();
            }
        });

        equalizerToolbar = (Toolbar) findViewById(R.id.equalizerToolbar);
        equalizerSwitch = (SwitchCompat) findViewById(R.id.equalizerSwitch);
        isEqualizerEnabled = true;
        equalizerSwitch.setChecked(true);
        equalizerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isEqualizerEnabled = true;
                    int pos = presetPos;
                    if (pos != 0) {
                        PlayerFragment.mEqualizer.usePreset((short) (pos - 1));
                    } else {
                        for (short i = 0; i < 5; i++) {
                            PlayerFragment.mEqualizer.setBandLevel(i, (short) seekbarpos[i]);
                        }
                    }
                    if (bassStrength != -1 && reverbPreset != -1) {
                        PlayerFragment.bassBoost.setStrength(bassStrength);
                        PlayerFragment.presetReverb.setPreset(reverbPreset);
                    }
                    EqualizerFragment.equalizerBlocker.setVisibility(View.GONE);
                } else {
                    isEqualizerEnabled = false;
                    PlayerFragment.mEqualizer.usePreset((short) 0);
                    PlayerFragment.bassBoost.setStrength((short) (((float) 1000 / 19) * (1)));
                    PlayerFragment.presetReverb.setPreset((short) 0);
                    EqualizerFragment.equalizerBlocker.setVisibility(View.VISIBLE);
                }
            }
        });

        fragmentToolbar = (Toolbar) findViewById(R.id.standard_fragment_toolbar);
        fragmentBackButton = (ImageView) findViewById(R.id.fragment_toolbar_back_button_img);
        fragmentBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fragmentToolbarTitle = (TextView) findViewById(R.id.fragment_toolbar_title);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setItemIconTintList(null);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    //Incoming call: Pause music
                    if (PlayerFragment.mMediaPlayer != null && PlayerFragment.mMediaPlayer.isPlaying()) {
                        wasMediaPlayerPlaying = true;
                        PlayerFragment.togglePlayPause();
                    } else {
                        wasMediaPlayerPlaying = false;
                    }
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not in call: Play music
                    if (PlayerFragment.mMediaPlayer != null && !PlayerFragment.mMediaPlayer.isPlaying() && wasMediaPlayerPlaying) {
                        PlayerFragment.togglePlayPause();
                    }
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                    if (PlayerFragment.mMediaPlayer != null && PlayerFragment.mMediaPlayer.isPlaying()) {
                        wasMediaPlayerPlaying = true;
                        PlayerFragment.togglePlayPause();
                    } else {
                        wasMediaPlayerPlaying = false;
                    }
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        mPrefs = getPreferences(MODE_PRIVATE);
        prefsEditor = mPrefs.edit();
        gson = new Gson();

        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        VisualizerView.act = this;
        main = this;

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screen_width = display.getWidth();
        screen_height = display.getHeight();

        ratio = (float) screen_height / (float) 1920;
        ratio2 = (float) screen_width / (float) 1080;
        ratio = Math.min(ratio, ratio2);

        playerControllerAB = (ImageView) findViewById(R.id.player_control_sp_AB);
        playerControllerAB.setImageResource(R.drawable.ic_queue_music_white_48dp);
        overflowMenuAB = (ImageView) findViewById(R.id.menuIcon);
        spImgAB = (CircleImageView) findViewById(R.id.selected_track_image_sp_AB);
        spTitleAB = (TextView) findViewById(R.id.selected_track_title_sp_AB);
        spTitleAB.setSelected(true);

        localBanner = (RelativeLayout) findViewById(R.id.localBanner);
        favBanner = (RelativeLayout) findViewById(R.id.favbanner);
        recentBanner = (RelativeLayout) findViewById(R.id.recentBanner);
        folderBanner = (RelativeLayout) findViewById(R.id.folderBanner);
        savedDNABanner = (RelativeLayout) findViewById(R.id.savedDNABanner);

        localBannerPlayAll = (ImageView) findViewById(R.id.local_banner_play_all);

        localBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment("local");
            }
        });
        favBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment("favourite");
            }
        });
        recentBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment("recent");
            }
        });
        folderBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment("allFolders");
            }
        });
        savedDNABanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment("allSavedDNAs");
            }
        });

        localBannerPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queue.getQueue().clear();
                for (int i = 0; i < localTrackList.size(); i++) {
                    UnifiedTrack ut = new UnifiedTrack(true, localTrackList.get(i), null);
                    queue.getQueue().add(ut);
                }
                Random r = new Random();
                shuffleEnabled = true;
                int tmp = r.nextInt(HomeActivity.queue.getQueue().size());
                queueCurrentIndex = tmp;
                LocalTrack track = localTrackList.get(tmp);
                localSelectedTrack = track;
                streamSelected = false;
                localSelected = true;
                queueCall = false;
                isReloaded = false;
                onLocalTrackSelected(-1);
            }
        });

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

        if (SplashActivity.tf2 != null) {
            ((TextView) findViewById(R.id.playListRecyclerLabel)).setTypeface(SplashActivity.tf2);
            ((TextView) findViewById(R.id.recentsRecyclerLabel)).setTypeface(SplashActivity.tf2);
        }

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

        progress = new Dialog(ctx);
        progress.setCancelable(false);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setContentView(R.layout.custom_progress_dialog);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progress.show();

        showCase = new ShowcaseView.Builder(this)
                .blockAllTouches()
                .singleShot(0)
                .setStyle(R.style.CustomShowcaseTheme)
                .useDecorViewAsParent()
                .setTarget(new ViewTarget(R.id.recentsRecyclerContainer, this))
                .setContentTitle("Recents and Playlists")
                .setContentText("Here all you recent songs and playlists will be listed. Long press the cards or playlists for more options")
                .build();
        showCase.setButtonText("Next");
        showCase.overrideButtonClick(new View.OnClickListener() {
            int count1 = 0;

            @Override
            public void onClick(View v) {
                count1++;
                switch (count1) {
                    case 1:
                        showCase.setTarget(new ViewTarget(R.id.localBanner, HomeActivity.this));
                        showCase.setContentTitle("Local Songs");
                        showCase.setContentText("See all songs available locally, classified on basis of Artist and Album");
                        showCase.setButtonText("Next");
                        break;
                    case 2:
                        showCase.setTarget(new ViewTarget(searchView.getId(), HomeActivity.this));
                        showCase.setContentTitle("Search");
                        showCase.setContentText("Search for songs from local library and SoundCloud™");
                        showCase.setButtonText("Done");
                        break;
                    case 3:
                        showCase.hide();
                        break;
                }
            }

        });

        new loadSavedData().execute();

    }

    private void getSavedData() {
        try {
            Gson gson = new Gson();
            String json = mPrefs.getString("savedDNAs", "");
            savedDNAs = gson.fromJson(json, AllSavedDNA.class);
            String json2 = mPrefs.getString("allPlaylists", "");
            allPlaylists = gson.fromJson(json2, AllPlaylists.class);
            String json3 = mPrefs.getString("queue", "");
            queue = gson.fromJson(json3, Queue.class);
            String json4 = mPrefs.getString("recentlyPlayed", "");
            recentlyPlayed = gson.fromJson(json4, RecentlyPlayed.class);
            String json5 = mPrefs.getString("favouriteTracks", "");
            favouriteTracks = gson.fromJson(json5, Favourite.class);
            String json6 = mPrefs.getString("queueCurrentIndex", "");
            queueCurrentIndex = gson.fromJson(json6, Integer.class);
            String json7 = mPrefs.getString("isReloaded", "");
            isReloaded = gson.fromJson(json7, Boolean.class);
            String json8 = mPrefs.getString("settings", "");
            settings = gson.fromJson(json8, Settings.class);
        } catch (Exception e) {
            e.printStackTrace();
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
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int pathColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.DATA);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                String path = musicCursor.getString(pathColumn);
                long duration = musicCursor.getLong(durationColumn);
                if (duration > 10000) {
                    LocalTrack lt = new LocalTrack(thisId, thisTitle, thisArtist, thisAlbum, path, duration);
                    localTrackList.add(lt);
                    finalLocalSearchResultList.add(lt);

                    int pos = checkAlbum(thisAlbum);

                    if (pos != -1) {
                        albums.get(pos).getAlbumSongs().add(lt);
                        finalAlbums.get(pos).getAlbumSongs().add(lt);
                    } else {
                        List<LocalTrack> llt = new ArrayList<>();
                        llt.add(lt);
                        Album ab = new Album(thisAlbum, llt);
                        albums.add(ab);
                        finalAlbums.add(ab);
                    }

                    pos = checkArtist(thisArtist);
                    if (pos != -1) {
                        artists.get(pos).getArtistSongs().add(lt);
                        finalArtists.get(pos).getArtistSongs().add(lt);
                    } else {
                        List<LocalTrack> llt = new ArrayList<>();
                        llt.add(lt);
                        Artist ab = new Artist(thisArtist, llt);
                        artists.add(ab);
                        finalArtists.add(ab);
                    }

                    File f = new File(path);
                    String dirName = f.getParentFile().getName();
                    if (getFolder(dirName) == null) {
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

        Collections.sort(localTrackList, new localMusicComparator());
        Collections.sort(finalLocalSearchResultList, new localMusicComparator());
        Collections.sort(albums, new albumComparator());
        Collections.sort(finalAlbums, new albumComparator());
        Collections.sort(artists, new artistComparator());
        Collections.sort(finalArtists, new artistComparator());

    }

    public int checkAlbum(String album) {
        for (int i = 0; i < albums.size(); i++) {
            Album ab = albums.get(i);
            if (ab.getName().equals(album)) {
                return i;
            }
        }
        return -1;
    }

    public int checkArtist(String artist) {
        for (int i = 0; i < artists.size(); i++) {
            Artist at = artists.get(i);
            if (at.getName().equals(artist)) {
                return i;
            }
        }
        return -1;
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
        } else if (isFullScreenEnabled) {
            HomeActivity.isFullScreenEnabled = false;
            PlayerFragment.bottomContainer.setVisibility(View.VISIBLE);
            PlayerFragment.seekBarContainer.setVisibility(View.VISIBLE);
            PlayerFragment.toggleContainer.setVisibility(View.VISIBLE);
            HomeActivity.spToolbar.setVisibility(View.VISIBLE);
            onFullScreen();
        } else if (!searchView.isIconified()) {
            searchView.setQuery("", true);
            searchView.setIconified(true);
            new Thread(new CancelCall()).start();
        } else {
            if (isEqualizerVisible) {
                showPlayer2();
            } else if (isQueueVisible) {
                if (isPlayerVisible)
                    showPlayer3();
                else
                    hideFragment("queue");
            } else if (isPlayerVisible) {
                hidePlayer();
                showTabs();
                isPlayerVisible = false;
            } else if (isAlbumVisible) {
                hideFragment("viewAlbum");
            } else if (isArtistVisible) {
                hideFragment("viewArtist");
            } else {
                if (isLocalVisible) {
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
                } else if (isAllPlaylistVisible) {
                    hideFragment("allPlaylists");
                    setTitle("Music DNA");
                } else if (isFolderContentVisible) {
                    hideFragment("folderContent");
                    setTitle("Folders");
                } else if (isAllFolderVisible) {
                    hideFragment("allFolders");
                    setTitle("Music DNA");
                } else if (isAllSavedDnaVisisble) {
                    hideFragment("allSavedDNAs");
                    setTitle("Music DNA");
                } else if (isSavedDNAVisible) {
                    hideFragment("savedDNA");
                    setTitle("Music DNA");
                } else if (isRecentVisible) {
                    hideFragment("recent");
                    setTitle("Music DNA");
                } else if (isSettingsVisible) {
                    hideFragment("settings");
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
            showFragment("settings");
            return true;
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
        } else if (id == R.id.nav_recent) {
            showFragment("recent");
        } else if (id == R.id.nav_fav) {
            showFragment("favourite");
        } else if (id == R.id.nav_folder) {
            showFragment("allFolders");
        } else if (id == R.id.nav_view) {
            showFragment("allSavedDNAs");
        } else if (id == R.id.nav_settings) {
            showFragment("settings");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        hideKeyboard();
        updateLocalList(query.trim());
        updateAlbumList(query.trim());
        updateArtistList(query.trim());
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        updateLocalList(newText.trim());
        updateStreamingList(newText.trim());
        updateAlbumList(newText.trim());
        updateArtistList(newText.trim());
        return true;
    }

    private void updateLocalList(String query) {

        if (isPlayerVisible) {
            hidePlayer();
        }

        if (LocalMusicFragment.shuffleFab != null)
            LocalMusicFragment.shuffleFab.setVisibility(View.INVISIBLE);

        /*Update the Local List*/

        localRecyclerContainer.setVisibility(View.VISIBLE);
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
        if (query.equals("")) {
            localRecyclerContainer.setVisibility(View.GONE);
        }
        if (query.equals("") && isLocalVisible) {
            if (LocalMusicFragment.shuffleFab != null)
                LocalMusicFragment.shuffleFab.setVisibility(View.VISIBLE);
        }

    }

    private void updateAlbumList(String query) {
        finalAlbums.clear();
        for (int i = 0; i < albums.size(); i++) {
            Album album = albums.get(i);
            String tmp1 = album.getName().toLowerCase();
            String tmp2 = query.toLowerCase();
            if (tmp1.contains(tmp2)) {
                finalAlbums.add(album);
            }
        }
        if (AlbumFragment.rv != null) {
            AlbumFragment.rv.getAdapter().notifyDataSetChanged();
        }

    }

    private void updateArtistList(String query) {
        finalArtists.clear();
        for (int i = 0; i < artists.size(); i++) {
            Artist artist = artists.get(i);
            String tmp1 = artist.getName().toLowerCase();
            String tmp2 = query.toLowerCase();
            if (tmp1.contains(tmp2)) {
                finalArtists.add(artist);
            }
        }
        if (ArtistFragment.rv != null) {
            ArtistFragment.rv.getAdapter().notifyDataSetChanged();
        }
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
            streamRecyclerContainer.setVisibility(View.GONE);
        }

    }

    public void hideTabs() {
        toolbar.animate()
                .setDuration(300)
                .translationY(-1 * toolbar.getHeight())
                .alpha(0.0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
//                        toolbar.setVisibility(View.GONE);
                    }
                });

        fragmentToolbar.animate()
                .setDuration(300)
                .translationY(-1 * fragmentToolbar.getHeight())
                .alpha(0.0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
//                        toolbar.setVisibility(View.GONE);
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

        toolbar.setAlpha(0.0f);
        toolbar.animate()
                .setDuration(300)
                .translationY(0)
                .alpha(1.0f);

        fragmentToolbar.setAlpha(0.0f);
        fragmentToolbar.animate()
                .setDuration(300)
                .translationY(0)
                .alpha(1.0f);


    }

    public void hidePlayer() {

        if (PlayerFragment.mVisualizerView != null)
            PlayerFragment.mVisualizerView.setVisibility(View.INVISIBLE);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = ((Activity) (ctx)).getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(themeColor);
        }

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
                .setDuration(300);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAllSavedDnaVisisble) {
                    ViewSavedDNA.mVisualizerView2.setVisibility(View.VISIBLE);
                }
            }
        }, 350);

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
                switchToolbar(spToolbar, equalizerToolbar, "right");

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

        setUpFragmentToolbar(Color.BLACK, "Queue");
        switchToolbar(spToolbar, queueToolbar, "left");

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
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = ((Activity) (ctx)).getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#000000"));
        }

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        searchView.setQuery("", false);
        searchView.setIconified(true);
        new Thread(new CancelCall()).start();

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

        /*equalizerToolbar.animate()
                .alpha(0.0f)
                .translationX(-1 * equalizerToolbar.getWidth())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        equalizerToolbar.setVisibility(View.GONE);
                    }
                });
        spToolbar.setVisibility(View.VISIBLE);
        spToolbar.setX(spToolbar.getWidth());
        spToolbar.setAlpha(0.0f);
        spToolbar.animate()
                .alpha(1.0f)
                .translationX(0);*/

        switchToolbar(equalizerToolbar, spToolbar, "left");

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

        switchToolbar(queueToolbar, spToolbar, "right");

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
//        updatePoints();
//        PlayerFragment.mVisualizerView.updateVisualizer(mBytes);
        new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void updatePoints() {

        PlayerFragment.mVisualizerView.outerRadius = (float) (Math.min(PlayerFragment.mVisualizerView.width, PlayerFragment.mVisualizerView.height) * 0.42);
        PlayerFragment.mVisualizerView.normalizedPosition = ((float) (PlayerFragment.mMediaPlayer.getCurrentPosition()) / (float) (PlayerFragment.durationInMilliSec));
        if (mBytes == null) {
            return;
        }
        PlayerFragment.mVisualizerView.angle = (float) (Math.PI - PlayerFragment.mVisualizerView.normalizedPosition * PlayerFragment.mVisualizerView.TAU);
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

            /*amp = (amp - fftMin) / (fftMax - fftMin);
            if (amp > 1) {
                amp = 1;
            } else if (amp < 0) {
                amp = 0;
            }*/

            Log.d("AMP", amp + "");

            PlayerFragment.mVisualizerView.volume = (amp);

            // converting polar to cartesian (distance calculated afterwards acts as radius for polar co-ords)
            x = (float) Math.sin(PlayerFragment.mVisualizerView.angle);
            y = (float) Math.cos(PlayerFragment.mVisualizerView.angle);

            // filtering low amplitude
            if (PlayerFragment.mVisualizerView.volume < minAudioStrength) {
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

            if (PlayerFragment.mVisualizerView.size >= 8.0 && PlayerFragment.mVisualizerView.size < 29.0) {
                PlayerFragment.mVisualizerView.mForePaint.setAlpha(17);
            } else if (PlayerFragment.mVisualizerView.size >= 29.0 && PlayerFragment.mVisualizerView.size <= 60.0) {
                PlayerFragment.mVisualizerView.mForePaint.setAlpha(9);
            } else if (PlayerFragment.mVisualizerView.size > 60.0) {
                PlayerFragment.mVisualizerView.mForePaint.setAlpha(3);
            } else {
                PlayerFragment.mVisualizerView.mForePaint.setAlpha((int) (PlayerFragment.mVisualizerView.alpha * 1000));
            }

            // Add points and paint config to lists for redraw
            PlayerFragment.mVisualizerView.pts.add(Pair.create(midx + x, midy + y));
            PlayerFragment.mVisualizerView.ptPaint.add(Pair.create(PlayerFragment.mVisualizerView.size, Pair.create(PlayerFragment.mVisualizerView.mForePaint.getColor(), PlayerFragment.mVisualizerView.mForePaint.getAlpha())));

            cacheCanvas.drawCircle(midx + x, midy + y, PlayerFragment.mVisualizerView.size, PlayerFragment.mVisualizerView.mForePaint);

        }
    }

    @Override
    public void onComplete() {

        if (isSaveDNAEnabled) {
            new SaveTheDNAs().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        queueCall = true;
        if (repeatOnceEnabled && !nextControllerClicked) {
            PlayerFragment.progressBar.setProgress(0);
            PlayerFragment.progressBar.setSecondaryProgress(0);
            PlayerFragment.mVisualizerView.clear();
            PlayerFragment.mMediaPlayer.seekTo(0);
            PlayerFragment.mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
            PlayerFragment.isPrepared = true;
            PlayerFragment.mMediaPlayer.start();
        } else {
            if (nextControllerClicked) {
                nextControllerClicked = false;
            }
            if (!shuffleEnabled) {
                if (queueCurrentIndex < queue.getQueue().size() - 1) {
                    queueCurrentIndex++;
                    if (QueueFragment.qAdapter != null)
                        QueueFragment.qAdapter.notifyDataSetChanged();
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
                        if (QueueFragment.qAdapter != null)
                            QueueFragment.qAdapter.notifyDataSetChanged();
                        onQueueItemClicked(0);
                    } else {
                        if (!hasQueueEnded) {
                            PlayerFragment.mMediaPlayer.stop();
                            hasQueueEnded = true;
                        } else if (queue.getQueue().size() > 0) {
                            hasQueueEnded = false;
                            queueCurrentIndex = 0;
                            if (QueueFragment.qAdapter != null) {
                                QueueFragment.qAdapter.notifyDataSetChanged();
                            }
                            onQueueItemClicked(0);
                        }
                    }
                }
            } else {
                Random r = new Random();
                int x;
                while (true) {
                    if (queue.getQueue().size() == 1) {
                        x = 0;
                        break;
                    }
                    x = r.nextInt(queue.getQueue().size());
                    if (x != queueCurrentIndex) {
                        break;
                    }
                }
                queueCurrentIndex = x;
                if (QueueFragment.qAdapter != null)
                    QueueFragment.qAdapter.notifyDataSetChanged();
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

    }

    @Override
    public void onPreviousTrack() {

        if (queueCurrentIndex == 0) {
            PlayerFragment.progressBar.setProgress(0);
            PlayerFragment.progressBar.setSecondaryProgress(0);
            PlayerFragment.mVisualizerView.clear();
            PlayerFragment.mMediaPlayer.seekTo(0);
            PlayerFragment.mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
            PlayerFragment.isPrepared = true;
            PlayerFragment.mMediaPlayer.start();
        } else {
            if (!shuffleEnabled) {
                if (queueCurrentIndex > 0) {
                    queueCall = true;
                    queueCurrentIndex--;
                    if (QueueFragment.qAdapter != null)
                        QueueFragment.qAdapter.notifyDataSetChanged();
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
                if (QueueFragment.qAdapter != null)
                    QueueFragment.qAdapter.notifyDataSetChanged();
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
    }

    @Override
    public void onQueueItemClicked(final int position) {

        if (isPlayerVisible)
            showPlayer3();
        else
            showPlayer();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
        }, 500);
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
        /*Playlist tmp = allPlaylists.getPlaylists().get(pos);
        tempPlaylist.setPlaylistName(tmp.getPlaylistName());
        tempPlaylist.setSongList(new ArrayList<UnifiedTrack>());
        for (int i = 0; i < tmp.getSongList().size(); i++) {
            tempPlaylist.getSongList().add(tmp.getSongList().get(i));
        }*/
        tempPlaylistNumber = pos;
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

    @Override
    public void onShare(Bitmap bmp, String fileName) {
        shareBitmapAsImage(bmp, fileName);
    }

    @Override
    public void onAlbumClick() {
        showFragment("viewAlbum");
    }

    @Override
    public void onAlbumSongClickListener() {
        onLocalTrackSelected(-1);
    }

    @Override
    public void onAlbumPlayAll() {
        onQueueItemClicked(0);
        showPlayer();
    }

    @Override
    public void onRecentItemClicked(boolean isLocal) {
        if (isLocal) {
            onLocalTrackSelected(-1);
        } else {
            onTrackSelected(-1);
        }
    }

    @Override
    public void onRecent(int pos) {
        onQueueItemClicked(pos);
    }

    @Override
    public void onPlayPause() {
        showNotification();
    }

    @Override
    public void onFullScreen() {
        if (isFullScreenEnabled) {
            Toast.makeText(HomeActivity.this, "Long Press to Exit", Toast.LENGTH_SHORT).show();
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = getSupportActionBar();
            actionBar.show();
        }
    }

    @Override
    public void onArtistClick() {
        showFragment("viewArtist");
    }

    @Override
    public void onArtistPlayAll() {
        onQueueItemClicked(0);
        showPlayer();
    }

    @Override
    public void onArtistSongClick() {
        onLocalTrackSelected(-1);
    }

    @Override
    public void onSettingsClicked() {
        hidePlayer();
        showTabs();
        isPlayerVisible = false;
        showFragment("settings");
    }

    @Override
    public void resetNotificationCalled() {
        showNotification();
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
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PlayerFragment.mVisualizerView.updateVisualizer(mBytes);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        new SavePlaylists().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new SaveQueue().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new SaveSettings().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new SaveData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSmallPlayerTouched() {
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

    public static void renamePlaylistDialog(String oldName) {
        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.save_image_dialog);
        dialog.setTitle("Rename");

        Button btn = (Button) dialog.findViewById(R.id.save_image_btn);
        final EditText newName = (EditText) dialog.findViewById(R.id.save_image_filename_text);

        newName.setText(oldName);
        btn.setBackgroundColor(themeColor);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newName.getText().toString().trim().equals("")) {
                    newName.setError("Enter Playlist Name!");
                } else {
                    allPlaylists.getPlaylists().get(renamePlaylistNumber).setPlaylistName(newName.getText().toString());
                    if (pAdapter != null) {
                        pAdapter.notifyItemChanged(renamePlaylistNumber);
                    }
                    if (PlayListFragment.vpAdapter != null) {
                        PlayListFragment.vpAdapter.notifyItemChanged(renamePlaylistNumber);
                    }
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

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

        if (!type.equals("viewAlbum") && !type.equals("folderContent") && !type.equals("viewArtist"))
            hideAllFrags();

        if (!searchView.isIconified()) {
            searchView.setQuery("", true);
            searchView.setIconified(true);
            new Thread(new CancelCall()).start();
        }

        if (type.equals("local") && !isLocalVisible) {
            setTitle("Local");
            navigationView.setCheckedItem(R.id.nav_local);
            isLocalVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            FullLocalMusicFragment newFragment = (FullLocalMusicFragment) fm.findFragmentByTag("local");
            if (newFragment == null) {
                newFragment = new FullLocalMusicFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.fragContainer, newFragment, "local")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("queue") && !isQueueVisible) {
            hideAllFrags();
            isQueueVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            QueueFragment newFragment = (QueueFragment) fm.findFragmentByTag("queue");
            if (newFragment == null) {
                newFragment = new QueueFragment();
            }
            fm.beginTransaction()
                    .add(R.id.equalizer_queue_frag_container, newFragment, "queue")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("stream") && !isStreamVisible) {
            setTitle("SoundCloud");
            setUpFragmentToolbar(themeColor, (String) getTitle());
            switchToolbar(toolbar, fragmentToolbar, "left");
            isStreamVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            StreamMusicFragment newFragment = (StreamMusicFragment) fm.findFragmentByTag("stream");
            if (newFragment == null) {
                newFragment = new StreamMusicFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.fragContainer, newFragment, "stream")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("playlist") && !isPlaylistVisible) {
            setTitle(tempPlaylist.getPlaylistName());
            setUpFragmentToolbar(themeColor, (String) getTitle());
            switchToolbar(toolbar, fragmentToolbar, "left");
            isPlaylistVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            ViewPlaylistFragment newFragment = (ViewPlaylistFragment) fm.findFragmentByTag("playlist");
            if (newFragment == null) {
                newFragment = new ViewPlaylistFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.content_frag, newFragment, "playlist")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("equalizer") && !isEqualizerVisible) {
            isEqualizerVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            EqualizerFragment newFragment = (EqualizerFragment) fm.findFragmentByTag("equalizer");
            if (newFragment == null) {
                newFragment = new EqualizerFragment();
            }
            fm.beginTransaction()
                    .add(R.id.equalizer_queue_frag_container, newFragment, "equalizer")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("favourite") && !isFavouriteVisible) {
            setTitle("Favourites");
            setUpFragmentToolbar(themeColor, (String) getTitle());
            switchToolbar(toolbar, fragmentToolbar, "left");
            navigationView.setCheckedItem(R.id.nav_fav);
            isFavouriteVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            FavouritesFragment newFragment = (FavouritesFragment) fm.findFragmentByTag("favourite");
            if (newFragment == null) {
                newFragment = new FavouritesFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.fragContainer, newFragment, "favourite")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("allPlaylists") && !isAllPlaylistVisible) {
            setTitle("All Playlists");
            setUpFragmentToolbar(themeColor, (String) getTitle());
            switchToolbar(toolbar, fragmentToolbar, "left");
            navigationView.setCheckedItem(R.id.nav_playlists);
            isAllPlaylistVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            PlayListFragment newFragment = new PlayListFragment();
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.fragContainer, newFragment, "allPlaylists")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("folderContent") && !isFolderContentVisible) {
            setTitle(tempMusicFolder.getFolderName());
            setUpFragmentToolbar(themeColor, (String) getTitle());
            isFolderContentVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            FolderContentFragment newFragment = (FolderContentFragment) fm.findFragmentByTag("folderContent");
            if (newFragment == null) {
                newFragment = new FolderContentFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.content_frag, newFragment, "folderContent")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("allFolders") && !isAllFolderVisible) {
            setTitle("Folders");
            setUpFragmentToolbar(themeColor, (String) getTitle());
            switchToolbar(toolbar, fragmentToolbar, "left");
            navigationView.setCheckedItem(R.id.nav_folder);
            isAllFolderVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            FolderFragment newFragment = (FolderFragment) fm.findFragmentByTag("allFolders");
            if (newFragment == null) {
                newFragment = new FolderFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.fragContainer, newFragment, "allFolders")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("allSavedDNAs") && !isAllSavedDnaVisisble) {
            setTitle("Saved DNAs");
            setUpFragmentToolbar(themeColor, (String) getTitle());
            switchToolbar(toolbar, fragmentToolbar, "left");
            navigationView.setCheckedItem(R.id.nav_view);
            isAllSavedDnaVisisble = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            ViewSavedDNA newFragment = (ViewSavedDNA) fm.findFragmentByTag("allSavedDNAs");
            if (newFragment == null) {
                newFragment = new ViewSavedDNA();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.fragContainer, newFragment, "allSavedDNAs")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("viewAlbum") && !isAlbumVisible) {
            setTitle(tempAlbum.getName());
            setUpFragmentToolbar(themeColor, (String) getTitle());
            switchToolbar(toolbar, fragmentToolbar, "left");
            isAlbumVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            ViewAlbumFragment newFragment = (ViewAlbumFragment) fm.findFragmentByTag("viewAlbum");
            if (newFragment == null) {
                newFragment = new ViewAlbumFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.fragContainer, newFragment, "viewAlbum")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("viewArtist") && !isArtistVisible) {
            setTitle("Artist");
            setUpFragmentToolbar(themeColor, (String) getTitle());
            switchToolbar(toolbar, fragmentToolbar, "left");
            isArtistVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            ViewArtistFragment newFragment = (ViewArtistFragment) fm.findFragmentByTag("viewArtist");
            if (newFragment == null) {
                newFragment = new ViewArtistFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.fragContainer, newFragment, "viewArtist")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("recent") && !isRecentVisible) {
            setTitle("Recently Played");
            setUpFragmentToolbar(themeColor, (String) getTitle());
            switchToolbar(toolbar, fragmentToolbar, "left");
            HomeActivity.isRecentVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            RecentsFragment newFragment = (RecentsFragment) fm.findFragmentByTag("recent");
            if (newFragment == null) {
                newFragment = new RecentsFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.fragContainer, newFragment, "recent")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("settings") && !isSettingsVisible) {
            setTitle("Settings");
            setUpFragmentToolbar(themeColor, (String) getTitle());
            switchToolbar(toolbar, fragmentToolbar, "left");
            isSettingsVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            SettingsFragment newFragment = (SettingsFragment) fm.findFragmentByTag("settings");
            if (newFragment == null) {
                newFragment = new SettingsFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.fragContainer, newFragment, "settings")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }

    public void hideFragment(String type) {
        if (type.equals("local")) {
            isLocalVisible = false;
            setTitle("Music DNA");
            navigationView.setCheckedItem(R.id.nav_home);
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("local");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("queue")) {
            isQueueVisible = false;
            navigationView.setCheckedItem(R.id.nav_home);
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("queue");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("stream")) {
            isStreamVisible = false;
            switchToolbar(fragmentToolbar, toolbar, "instant");
            setTitle("Music DNA");
            navigationView.setCheckedItem(R.id.nav_home);
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("stream");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("playlist")) {
            isPlaylistVisible = false;
            switchToolbar(fragmentToolbar, toolbar, "instant");
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("playlist");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("equalizer")) {
            isEqualizerVisible = false;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("equalizer");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("favourite")) {
            isFavouriteVisible = false;
            switchToolbar(fragmentToolbar, toolbar, "instant");
            setTitle("Music DNA");
            navigationView.setCheckedItem(R.id.nav_home);
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("favourite");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("allPlaylists")) {
            isAllPlaylistVisible = false;
            switchToolbar(fragmentToolbar, toolbar, "instant");
            setTitle("Music DNA");
            navigationView.setCheckedItem(R.id.nav_home);
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("allPlaylists");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("folderContent")) {
            isFolderContentVisible = false;
            setUpFragmentToolbar(themeColor, "Folders");
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("folderContent");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("allFolders")) {
            isAllFolderVisible = false;
            switchToolbar(fragmentToolbar, toolbar, "instant");
            setTitle("Music DNA");
            navigationView.setCheckedItem(R.id.nav_home);
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("allFolders");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("allSavedDNAs")) {
            isAllSavedDnaVisisble = false;
            switchToolbar(fragmentToolbar, toolbar, "instant");
            setTitle("Music DNA");
            navigationView.setCheckedItem(R.id.nav_home);
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("allSavedDNAs");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("viewAlbum")) {
            isAlbumVisible = false;
            switchToolbar(fragmentToolbar, toolbar, "instant");
            setTitle("Local");
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("viewAlbum");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("viewArtist")) {
            isArtistVisible = false;
            switchToolbar(fragmentToolbar, toolbar, "instant");
            setTitle("Local");
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("viewArtist");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("recent")) {
            isRecentVisible = false;
            switchToolbar(fragmentToolbar, toolbar, "instant");
            setTitle("Music DNA");
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("recent");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("settings")) {
            isSettingsVisible = false;
            switchToolbar(fragmentToolbar, toolbar, "instant");
            setTitle("Music DNA");
            navigationView.setCheckedItem(R.id.nav_home);
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("settings");
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
        hideFragment("allSavedDNAs");
        hideFragment("viewAlbum");
        hideFragment("viewArtist");
        hideFragment("recent");
        hideFragment("settings");

        navigationView.setCheckedItem(R.id.nav_home);

        setTitle("Music DNA");

    }

    public void showNotification() {

        if (Build.VERSION.SDK_INT >= 21) {
            if (!isNotificationVisible) {
                Intent intent = new Intent(this, MediaPlayerService.class);
                intent.setAction(Constants.ACTION_PLAY);
                startService(intent);
                isNotificationVisible = true;
            }
        } else {
            setNotification();
        }

    }

    public void setNotification() {
        Notification notification;
        String ns = Context.NOTIFICATION_SERVICE;
        notificationManager = (NotificationManager) getSystemService(ns);
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification_view);
        RemoteViews notificationViewSmall = new RemoteViews(getPackageName(), R.layout.notification_view_small);
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
        Intent switchIntent3 = new Intent("com.sdsmdg.harjot.MusicDNA.ACTION_PREV");
        PendingIntent pendingSwitchIntent3 = PendingIntent.getBroadcast(this, 100, switchIntent3, 0);
        notificationView.setOnClickPendingIntent(R.id.btn_prev_in_notification, pendingSwitchIntent3);

        notificationViewSmall.setOnClickPendingIntent(R.id.btn_pause_play_in_notification, pendingSwitchIntent);
        try {
            if (PlayerFragment.mMediaPlayer.isPlaying()) {
                notificationViewSmall.setImageViewResource(R.id.btn_pause_play_in_notification, R.drawable.ic_pause_white_48dp);
            } else {
                notificationViewSmall.setImageViewResource(R.id.btn_pause_play_in_notification, R.drawable.ic_play_arrow_white_48dp);
            }
        } catch (Exception e) {
        }
        notificationViewSmall.setOnClickPendingIntent(R.id.btn_next_in_notification, pendingSwitchIntent2);
        notificationViewSmall.setOnClickPendingIntent(R.id.btn_prev_in_notification, pendingSwitchIntent3);

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
        notification.contentView = notificationViewSmall;
        notification.contentIntent = pendingNotificationIntent;
        if (PlayerFragment.mMediaPlayer.isPlaying()) {
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
        }
        notificationView.setImageViewBitmap(R.id.image_in_notification, ((BitmapDrawable) PlayerFragment.selected_track_image.getDrawable()).getBitmap());
        if (PlayerFragment.localIsPlaying) {
            notificationView.setTextViewText(R.id.title_in_notification, PlayerFragment.localTrack.getTitle());
            notificationView.setTextViewText(R.id.artist_in_notification, PlayerFragment.localTrack.getArtist());
        } else {
            notificationView.setTextViewText(R.id.title_in_notification, PlayerFragment.track.getTitle());
            notificationView.setTextViewText(R.id.artist_in_notification, "");
        }
        notificationViewSmall.setImageViewBitmap(R.id.image_in_notification, ((BitmapDrawable) PlayerFragment.selected_track_image.getDrawable()).getBitmap());
        if (PlayerFragment.localIsPlaying) {
            notificationViewSmall.setTextViewText(R.id.title_in_notification, PlayerFragment.localTrack.getTitle());
            notificationViewSmall.setTextViewText(R.id.artist_in_notification, PlayerFragment.localTrack.getArtist());
        } else {
            notificationViewSmall.setTextViewText(R.id.title_in_notification, PlayerFragment.track.getTitle());
            notificationViewSmall.setTextViewText(R.id.artist_in_notification, "");
        }
        PlayerFragment.isStart = false;
        notificationManager.notify(1, notification);
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

    public void shareBitmapAsImage(Bitmap bmp, String fileName) {
        try {
            File cachePath = new File(ctx.getCacheDir(), "images");
            if (cachePath.exists())
                deleteRecursive(cachePath);
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/" + fileName + ".png"); // overwrites this image every time
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File imagePath = new File(ctx.getCacheDir(), "images");
        File newFile = new File(imagePath, fileName + ".png");
        Uri contentUri = FileProvider.getUriForFile(ctx, "com.sdsmdg.harjot.MusicDNA.fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }

    }

    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    public static void shareLocalSong(String path) {
        Uri contentUri = Uri.parse("file:///" + path);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("audio/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            main.startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }

    public class localMusicComparator implements Comparator<LocalTrack> {

        @Override
        public int compare(LocalTrack lhs, LocalTrack rhs) {
            if (lhs.getTitle().toString().charAt(0) < rhs.getTitle().toString().charAt(0)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public class albumComparator implements Comparator<Album> {

        @Override
        public int compare(Album lhs, Album rhs) {
            if (lhs.getName().toString().charAt(0) < rhs.getName().toString().charAt(0)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public class artistComparator implements Comparator<Artist> {

        @Override
        public int compare(Artist lhs, Artist rhs) {
            if (lhs.getName().toString().charAt(0) < rhs.getName().toString().charAt(0)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public class loadSavedData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            getSavedData();
            return "done";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {

//                    HomeActivity.toolbar.setBackgroundColor(themeColor);
//                    SettingsFragment.themeColorImg.setBackgroundColor(themeColor);
//                    if (Build.VERSION.SDK_INT >= 21) {
//                        Window window = ((Activity)(HomeActivity.ctx)).getWindow();
//                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                        window.setStatusBarColor(themeColor);
//                    }

                    if (settings == null) {
                        settings = new Settings();
                    }

                    themeColor = settings.getThemeColor();
                    minAudioStrength = settings.getMinAudioStrength();

                    toolbar.setBackgroundColor(themeColor);

                    if (Build.VERSION.SDK_INT >= 21) {
                        Window window = ((Activity) (HomeActivity.ctx)).getWindow();
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(themeColor);
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
                        bottomToolbar.setVisibility(View.GONE);
                    }

                    getLocalSongs();

                    for (int i = 0; i < Math.min(10, recentlyPlayed.getRecentlyPlayed().size()); i++) {
                        continuePlayingList.add(recentlyPlayed.getRecentlyPlayed().get(i));
                    }

                    rAdapter = new RecentsListHorizontalAdapter(continuePlayingList, ctx);
                    recentsRecycler = (RecyclerView) findViewById(R.id.recentsMusicList_home);
                    LinearLayoutManager mLayoutManager3 = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                    recentsRecycler.setLayoutManager(mLayoutManager3);
                    recentsRecycler.setItemAnimator(new DefaultItemAnimator());
                    AlphaInAnimationAdapter alphaAdapter3 = new AlphaInAnimationAdapter(rAdapter);
                    alphaAdapter3.setFirstOnly(false);
                    recentsRecycler.setAdapter(alphaAdapter3);

                    recentsRecycler.addOnItemTouchListener(new ClickItemTouchListener(recentsRecycler) {
                        @Override
                        boolean onClick(RecyclerView parent, View view, final int position, long id) {
                            UnifiedTrack ut = continuePlayingList.get(position);
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
                            if (!isRepeat) {
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
                            final UnifiedTrack ut = continuePlayingList.get(position);

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
                                            LocalTrack track = ut.getLocalTrack();
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
                    LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                    playlistsRecycler.setLayoutManager(mLayoutManager2);
                    playlistsRecycler.setItemAnimator(new DefaultItemAnimator());
                    AlphaInAnimationAdapter alphaAdapter2 = new AlphaInAnimationAdapter(pAdapter);
                    alphaAdapter2.setFirstOnly(false);
                    playlistsRecycler.setAdapter(alphaAdapter2);

                    playlistsRecycler.addOnItemTouchListener(new ClickItemTouchListener(playlistsRecycler) {
                        @Override
                        boolean onClick(RecyclerView parent, View view, final int position, long id) {
                            tempPlaylist = allPlaylists.getPlaylists().get(position);
                            tempPlaylistNumber = position;
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
                                        tempPlaylistNumber = position;

                                        int size = tempPlaylist.getSongList().size();
                                        queue.getQueue().clear();
                                        for (int i = 0; i < size; i++) {
                                            queue.addToQueue(tempPlaylist.getSongList().get(i));
                                        }

                                        queueCurrentIndex = 0;
                                        onPlaylistPLayAll();
                                    } else if (item.getTitle().equals("Delete")) {
                                        allPlaylists.getPlaylists().remove(position);
                                        if (PlayListFragment.vpAdapter != null) {
                                            PlayListFragment.vpAdapter.notifyItemRemoved(position);
                                        }
                                        pAdapter.notifyItemRemoved(position);
                                    } else if (item.getTitle().equals("Rename")) {
                                        renamePlaylistNumber = position;
                                        renamePlaylistDialog(allPlaylists.getPlaylists().get(position).getPlaylistName());
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
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                    localListView.setLayoutManager(mLayoutManager);
                    localListView.setItemAnimator(new DefaultItemAnimator());
                    AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
                    alphaAdapter.setFirstOnly(false);
                    localListView.setAdapter(alphaAdapter);

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
                            popup.getMenuInflater().inflate(R.menu.popup_local, popup.getMenu());

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
                                    if (item.getTitle().equals("Share")) {
                                        shareLocalSong(finalLocalSearchResultList.get(position).getPath());
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

                    playerContainer = findViewById(R.id.player_frag_container);

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
            });
        }
    }

    public class SaveData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            String json4 = gson.toJson(recentlyPlayed);
            prefsEditor.putString("recentlyPlayed", json4);
            String json5 = gson.toJson(favouriteTracks);
            prefsEditor.putString("favouriteTracks", json5);
            String json6 = gson.toJson(queueCurrentIndex);
            prefsEditor.putString("queueCurrentIndex", json6);
            isReloaded = true;
            String json7 = gson.toJson(isReloaded);
            prefsEditor.putString("isReloaded", json7);

            prefsEditor.commit();

            return null;
        }
    }

    public class SaveSettings extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String json8 = gson.toJson(settings);
            prefsEditor.putString("settings", json8);
            prefsEditor.commit();
            return null;
        }
    }

    public static class SaveTheDNAs extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String json = gson.toJson(savedDNAs);
            prefsEditor.putString("savedDNAs", json);
            prefsEditor.commit();
            return null;
        }
    }

    public class SaveQueue extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String json3 = gson.toJson(queue);
            prefsEditor.putString("queue", json3);
            prefsEditor.commit();
            return null;
        }
    }

    public class SavePlaylists extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String json2 = gson.toJson(allPlaylists);
            prefsEditor.putString("allPlaylists", json2);
            prefsEditor.commit();
            return null;
        }
    }

    public void switchToolbar(final Toolbar t1, Toolbar t2, String direction) {
        if (direction.equals("right")) {
            t1.animate()
                    .alpha(0.0f)
                    .translationX(t1.getWidth())
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            t1.setVisibility(View.GONE);
                        }
                    });
            t2.setVisibility(View.VISIBLE);
            t2.setX(-1 * t2.getWidth());
            t2.setAlpha(0.0f);
            t2.animate()
                    .alpha(1.0f)
                    .translationX(0);
        } else if (direction.equals("left")) {
            t1.animate()
                    .alpha(0.0f)
                    .translationX(-1 * t1.getWidth())
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            t1.setVisibility(View.GONE);
                        }
                    });
            t2.setVisibility(View.VISIBLE);
            t2.setX(t2.getWidth());
            t2.setAlpha(0.0f);
            t2.animate()
                    .alpha(1.0f)
                    .translationX(0);
        } else {
            t1.setVisibility(View.GONE);
            t2.setX(0);
            t2.setAlpha(1.0f);
            t2.setVisibility(View.VISIBLE);
        }
    }

    public void setUpFragmentToolbar(int color, String title) {
        fragmentToolbar.setBackgroundColor(color);
        fragmentToolbarTitle.setText(title);
    }

    public void clearQueue() {
        for (int i = 0; i < queue.getQueue().size(); i++) {
            if (i < queueCurrentIndex) {
                queue.getQueue().remove(i);
                queueCurrentIndex--;
                QueueFragment.qAdapter.notifyItemRemoved(i);
                i--;
            } else if (i > queueCurrentIndex) {
                queue.getQueue().remove(i);
                QueueFragment.qAdapter.notifyItemRemoved(i);
                i--;
            }
        }


    }

}
