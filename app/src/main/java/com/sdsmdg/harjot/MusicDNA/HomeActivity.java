package com.sdsmdg.harjot.MusicDNA;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
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
import android.text.TextPaint;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.Gson;
import com.lantouzi.wheelview.WheelView;
import com.sdsmdg.harjot.MusicDNA.CustomBottomSheetDialogs.CustomGeneralBottomSheetDialog;
import com.sdsmdg.harjot.MusicDNA.CustomBottomSheetDialogs.CustomLocalBottomSheetDialog;
import com.sdsmdg.harjot.MusicDNA.HeadsetHandler.HeadSetReceiver;
import com.sdsmdg.harjot.MusicDNA.Interfaces.ServiceCallbacks;
import com.sdsmdg.harjot.MusicDNA.Interfaces.StreamService;
import com.sdsmdg.harjot.MusicDNA.LocalMusicFragments.AlbumFragment;
import com.sdsmdg.harjot.MusicDNA.LocalMusicFragments.ArtistFragment;
import com.sdsmdg.harjot.MusicDNA.Models.Album;
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
import com.squareup.leakcanary.RefWatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import static android.view.View.GONE;

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
        PlayerFragment.onFavouritesListener,
        PlayerFragment.onShuffleListener,
        PlayListFragment.onPlaylistTouchedListener,
        PlayListFragment.onPlaylistMenuPlayAllListener,
        PlayListFragment.onPlaylistRenameListener,
        PlayListFragment.newPlaylistListerner,
        PlaylistTrackAdapter.onPlaylistEmptyListener,
        FolderFragment.onFolderClickedListener,
        FolderContentFragment.onFolderContentPlayAllListener,
        FolderContentFragment.onFolderContentItemClickListener,
        FolderContentFragment.folderContentAddToPlaylistListener,
        ViewSavedDNA.onShareListener,
        AlbumFragment.onAlbumClickListener,
        ViewAlbumFragment.onAlbumSongClickListener,
        ViewAlbumFragment.onAlbumPlayAllListener,
        ArtistFragment.onArtistClickListener,
        ViewArtistFragment.onArtistSongClickListener,
        ViewArtistFragment.onArtistPlayAllListener,
        RecentsFragment.onRecentItemClickedListener,
        RecentsFragment.onRepeatListener,
        MediaPlayerService.onCallbackListener,
        SettingsFragment.onColorChangedListener,
        SettingsFragment.onAlbumArtBackgroundToggled,
        SettingsFragment.onAboutClickedListener,
        AddToPlaylistFragment.newPlaylistListener,
        HeadSetReceiver.onHeadsetRemovedListener,
        EditLocalSongFragment.onEditSongSaveListener,
        EditLocalSongFragment.newCoverListener,
        ServiceCallbacks {


    ScrollView container;

    public static List<LocalTrack> localTrackList = new ArrayList<>();
    public static List<LocalTrack> finalLocalSearchResultList = new ArrayList<>();
    public static List<LocalTrack> finalSelectedTracks = new ArrayList<>();
    public static List<Track> streamingTrackList = new ArrayList<>();
    public static List<Album> albums = new ArrayList<>();
    public static List<Album> finalAlbums = new ArrayList<>();
    public static List<Artist> artists = new ArrayList<>();
    public static List<Artist> finalArtists = new ArrayList<>();
    public static List<UnifiedTrack> continuePlayingList = new ArrayList<>();

    String versionName;
    int versionCode;
    int prevVersionCode = -1;
    TextView copyrightText;

    static Canvas cacheCanvas;

    public static Album tempAlbum;
    public static Artist tempArtist;

    private Dialog progress;

    static float ratio, ratio2;

    Toolbar spHome;
    ImageView playerControllerHome;
    FrameLayout bottomToolbar;
    CircleImageView spImgHome;
    TextView spTitleHome;

    ImageView overflowMenuAB;
    static CircleImageView spImgAB;
    TextView spTitleAB;
    TextView spArtistAB;

    SwitchCompat equalizerSwitch;

    Bitmap selectedImage;

    SharedPreferences mPrefs;
    static SharedPreferences.Editor prefsEditor;
    static Gson gson;

    ImageLoader imgLoader;

    ConnectivityManager connManager;
    NetworkInfo mWifi;

    public static RecentlyPlayed recentlyPlayed;
    static Favourite favouriteTracks;
    public static Settings settings;

    static Queue queue;
    static Queue originalQueue;

    static Playlist tempPlaylist;
    static int tempPlaylistNumber;
    static int renamePlaylistNumber;
    static AllPlaylists allPlaylists;
    static AllMusicFolders allMusicFolders;

    static AllSavedDNA savedDNAs;
    static SavedDNA tempSavedDNA;

    static List<LocalTrack> tempFolderContent;
    static MusicFolder tempMusicFolder;

    PlayerFragment playerFragment;

    static boolean shuffleEnabled = false;
    static boolean repeatEnabled = false;
    static boolean repeatOnceEnabled = false;

    static boolean nextControllerClicked = false;

    static boolean isFavourite = false;

    public static boolean isReloaded = true;

    public static int queueCurrentIndex = 0;
    public int originalQueueIndex = 0;

    public static boolean isSaveDNAEnabled = false;

    public Context ctx;

    static boolean queueCall = false;

    boolean wasMediaPlayerPlaying = false;

    DrawerLayout drawer;

    static NotificationManager notificationManager;

    PhoneStateListener phoneStateListener;

    LocalTracksHorizontalAdapter adapter;
    StreamTracksHorizontalAdapter sAdapter;
    PlayListsHorizontalAdapter pAdapter;
    RecentsListHorizontalAdapter rAdapter;

    NavigationView navigationView;

    Call<List<Track>> call;

    SearchView searchView;
    MenuItem searchItem;

    RecyclerView soundcloudRecyclerView;
    RecyclerView localsongsRecyclerView;
    RecyclerView playlistsRecycler;
    RecyclerView recentsRecycler;

    RelativeLayout localRecyclerContainer;
    RelativeLayout recentsRecyclerContainer;
    RelativeLayout streamRecyclerContainer;
    RelativeLayout playlistRecyclerContainer;

    RelativeLayout localBanner;
    ImageView favBanner;
    ImageView recentBanner;
    ImageView folderBanner;
    ImageView savedDNABanner;

    ImageView localBannerPlayAll;

    ImageView navImageView;

    TextView localViewAll, streamViewAll;

    TextView newPlaylistText;

    TextView localNothingText;
    TextView streamNothingText;
    TextView recentsNothingText;
    TextView playlistNothingText;

    static int screen_width;
    static int screen_height;

    Toolbar toolbar;
    Toolbar spToolbar;
    Toolbar equalizerToolbar;

    Toolbar queueToolbar;
    ImageView queueBackButton;
    TextView queueClearText;

    Toolbar fragmentToolbar;
    ImageView fragmentBackButton;
    TextView fragmentToolbarTitle;

    static int themeColor = Color.parseColor("#B24242");
    static float minAudioStrength = 0.40f;

    static TextPaint tp;

    public Activity main;

    static float seekBarColor;

    static byte[] mBytes;

    HeadSetReceiver headSetReceiver;

    ShowcaseView showCase;

    View playerContainer;

    ServiceConnection serviceConnection;
    private MediaPlayerService myService;
    private boolean bound = false;

    android.support.v4.app.FragmentManager fragMan;
    android.support.v4.app.FragmentManager fragMan2;

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
    public static boolean isNewPlaylistVisible = false;
    public static boolean isAboutVisible = false;
    public static boolean isEditVisible = false;

    boolean isPlayerTransitioning = false;

    static boolean isSaveRecentsRunning = false;
    static boolean isSaveFavouritesRunning = false;
    static boolean isSaveSettingsRunning = false;
    static boolean isSaveDNAsRunning = false;
    static boolean isSaveQueueRunning = false;
    static boolean isSavePLaylistsRunning = false;

    public static boolean hasQueueEnded = false;

    static boolean isEqualizerEnabled = false;
    static boolean isEqualizerReloaded = false;

    static int[] seekbarpos = new int[5];
    static int presetPos;

    boolean isNotificationVisible = false;

    static LocalTrack localSelectedTrack;
    static Track selectedTrack;

    static LocalTrack editSong;

    static boolean localSelected = false;
    static boolean streamSelected = false;

    static short reverbPreset = -1, bassStrength = -1;

    Button mEndButton;

    static int statusBarHeightinDp;
    static int navBarHeightSizeinDp;
    public static boolean hasSoftNavbar = false;
    static RelativeLayout.LayoutParams lps;

    boolean isSleepTimerEnabled = false;
    boolean isSleepTimerTimeout = false;
    long timerSetTime = 0;
    int timerTimeOutDuration = 0;
    List<String> minuteList;
    Handler sleepHandler;

    public void onTrackSelected(int position) {

        isReloaded = false;
        HideBottomFakeToolbar();

        if (!queueCall) {
            hideKeyboard();

            searchView.setQuery("", false);
            searchView.setIconified(true);
            new Thread(new CancelCall()).start();

            hideTabs();
            isPlayerVisible = true;

            PlayerFragment frag = playerFragment;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            PlayerFragment newFragment = new PlayerFragment();
            if (frag == null) {
                playerFragment = newFragment;
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
                playerFragment.localIsPlaying = false;
                playerFragment.track = selectedTrack;
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_up,
                                R.anim.slide_down,
                                R.anim.slide_up,
                                R.anim.slide_down)
                        .add(R.id.player_frag_container, newFragment, "player")
                        .show(newFragment)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            } else {
                if (playerFragment.track != null && !playerFragment.localIsPlaying && selectedTrack.getTitle() == playerFragment.track.getTitle()) {

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
                    playerFragment.localIsPlaying = false;
                    playerFragment.track = selectedTrack;
                    frag.refresh();
                }
            }
            if (!isQueueVisible)
                showPlayer();
        } else {
            PlayerFragment frag = playerFragment;
            playerFragment.localIsPlaying = false;
            playerFragment.track = selectedTrack;
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

        QueueFragment qFrag = (QueueFragment) fragMan.findFragmentByTag("queue");
        if (qFrag != null) {
            qFrag.updateQueueAdapter();
        }

        UnifiedTrack track = new UnifiedTrack(false, null, playerFragment.track);
        for (int i = 0; i < recentlyPlayed.getRecentlyPlayed().size(); i++) {
            if (!recentlyPlayed.getRecentlyPlayed().get(i).getType() && recentlyPlayed.getRecentlyPlayed().get(i).getStreamTrack().getTitle().equals(track.getStreamTrack().getTitle())) {
                recentlyPlayed.getRecentlyPlayed().remove(i);
                rAdapter.notifyItemRemoved(i);
                break;
            }
        }
        recentlyPlayed.getRecentlyPlayed().add(0, track);
        if (recentlyPlayed.getRecentlyPlayed().size() > 50) {
            recentlyPlayed.getRecentlyPlayed().remove(50);
        }
        recentsRecycler.setVisibility(View.VISIBLE);
        recentsNothingText.setVisibility(View.INVISIBLE);
        continuePlayingList.clear();
        for (int i = 0; i < Math.min(10, recentlyPlayed.getRecentlyPlayed().size()); i++) {
            continuePlayingList.add(recentlyPlayed.getRecentlyPlayed().get(i));
        }
        rAdapter.notifyDataSetChanged();
        RecentsFragment rFrag = (RecentsFragment) fragMan.findFragmentByTag("recent");
        if (rFrag != null && rFrag.rtAdpater != null) {
            rFrag.rtAdpater.notifyDataSetChanged();
        }
    }

    public void onLocalTrackSelected(int position) {

        isReloaded = false;
        HideBottomFakeToolbar();

        if (!queueCall) {

            hideKeyboard();

            searchView.setQuery("", true);
            searchView.setIconified(true);
            new Thread(new CancelCall()).start();

            hideTabs();
            isPlayerVisible = true;

            PlayerFragment frag = playerFragment;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            PlayerFragment newFragment = new PlayerFragment();
            if (frag == null) {
                playerFragment = newFragment;
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
                playerFragment.localIsPlaying = true;
                playerFragment.localTrack = localSelectedTrack;
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_up,
                                R.anim.slide_down,
                                R.anim.slide_up,
                                R.anim.slide_down)
                        .add(R.id.player_frag_container, newFragment, "player")
                        .show(newFragment)
                        .commitAllowingStateLoss();
            } else {
                if (playerFragment.localTrack != null && playerFragment.localIsPlaying && localSelectedTrack.getTitle() == playerFragment.localTrack.getTitle()) {

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
                    playerFragment.localIsPlaying = true;
                    playerFragment.localTrack = localSelectedTrack;
                    frag.refresh();
                }
            }

            if (!isQueueVisible)
                showPlayer();

        } else {
            PlayerFragment frag = playerFragment;
            playerFragment.localIsPlaying = true;
            playerFragment.localTrack = localSelectedTrack;

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

        QueueFragment qFrag = (QueueFragment) fragMan.findFragmentByTag("queue");
        if (qFrag != null) {
            qFrag.updateQueueAdapter();
        }

        UnifiedTrack track = new UnifiedTrack(true, playerFragment.localTrack, null);
        for (int i = 0; i < recentlyPlayed.getRecentlyPlayed().size(); i++) {
            if (recentlyPlayed.getRecentlyPlayed().get(i).getType() && recentlyPlayed.getRecentlyPlayed().get(i).getLocalTrack().getTitle().equals(track.getLocalTrack().getTitle())) {
                recentlyPlayed.getRecentlyPlayed().remove(i);
                rAdapter.notifyItemRemoved(i);
                break;
            }
        }
        recentlyPlayed.getRecentlyPlayed().add(0, track);
        if (recentlyPlayed.getRecentlyPlayed().size() == 51) {
            recentlyPlayed.getRecentlyPlayed().remove(50);
        }
        recentsRecycler.setVisibility(View.VISIBLE);
        recentsNothingText.setVisibility(View.INVISIBLE);
        continuePlayingList.clear();
        for (int i = 0; i < Math.min(10, recentlyPlayed.getRecentlyPlayed().size()); i++) {
            continuePlayingList.add(recentlyPlayed.getRecentlyPlayed().get(i));
        }
        rAdapter.notifyDataSetChanged();

        RecentsFragment rFrag = (RecentsFragment) fragMan.findFragmentByTag("recent");
        if (rFrag != null && rFrag.rtAdpater != null) {
            rFrag.rtAdpater.notifyDataSetChanged();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screen_width = display.getWidth();
        screen_height = display.getHeight();

        ratio = (float) screen_height / (float) 1920;
        ratio2 = (float) screen_width / (float) 1080;
        ratio = Math.min(ratio, ratio2);

        setContentView(R.layout.activity_home);

        headSetReceiver = new HeadSetReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headSetReceiver, filter);

        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mEndButton = new Button(this);
        mEndButton.setBackgroundColor(themeColor);
        mEndButton.setTextColor(Color.WHITE);

        tp = new TextPaint();
        tp.setColor(themeColor);
        tp.setTextSize(65 * ratio);
        tp.setFakeBoldText(true);

        copyrightText = (TextView) findViewById(R.id.copyright_text);
        copyrightText.setTypeface(SplashActivity.tf3);
        copyrightText.setText("\nMusic DNA v" + versionName);

        imgLoader = new ImageLoader(this);
        ctx = this;

        hasSoftNavbar = hasNavBar(getResources());
        statusBarHeightinDp = getStatusBarHeight();
        navBarHeightSizeinDp = hasSoftNavbar ? getNavBarHeight() : 0;

        serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                // cast the IBinder and get MyService instance
                MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
                myService = binder.getService();
                bound = true;
                myService.setCallbacks(HomeActivity.this); // register
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                bound = false;
            }
        };

        minuteList = new ArrayList<>();
        for (int i = 1; i < 25; i++) {
            minuteList.add(String.valueOf(i * 5));
        }

        sleepHandler = new Handler();

        lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, navBarHeightSizeinDp + ((Number) (getResources().getDisplayMetrics().density * 5)).intValue());

        fragMan = getSupportFragmentManager();
        fragMan2 = getSupportFragmentManager();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        spToolbar = (Toolbar) findViewById(R.id.smallPlayer_AB);
        spToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePlayer();
                showTabs();
                isPlayerVisible = false;
            }
        });

        newPlaylistText = (TextView) findViewById(R.id.new_playlist_text);
        newPlaylistText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment("newPlaylist");
            }
        });

        queueToolbar = (Toolbar) findViewById(R.id.queue_toolbar);
        queueBackButton = (ImageView) findViewById(R.id.queue_toolbar_back_button_img);
        queueBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        queueClearText = (TextView) findViewById(R.id.clear_queue_txt);
        queueClearText.setTypeface(SplashActivity.tf3);
        queueClearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearQueue();
                new SaveQueue().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        equalizerToolbar = (Toolbar) findViewById(R.id.equalizerToolbar);
        equalizerSwitch = (SwitchCompat) findViewById(R.id.equalizerSwitch);
        isEqualizerEnabled = true;
        equalizerSwitch.setChecked(true);
        equalizerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EqualizerFragment eqFrag = (EqualizerFragment) fragMan.findFragmentByTag("equalizer");
                if (isChecked) {
                    try {
                        isEqualizerEnabled = true;
                        int pos = presetPos;
                        if (pos != 0) {
                            playerFragment.mEqualizer.usePreset((short) (pos - 1));
                        } else {
                            for (short i = 0; i < 5; i++) {
                                playerFragment.mEqualizer.setBandLevel(i, (short) seekbarpos[i]);
                            }
                        }
                        if (bassStrength != -1 && reverbPreset != -1) {
                            playerFragment.bassBoost.setStrength(bassStrength);
                            playerFragment.presetReverb.setPreset(reverbPreset);
                        }
                        if (eqFrag != null)
                            eqFrag.setBlockerVisibility(GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        isEqualizerEnabled = false;
                        playerFragment.mEqualizer.usePreset((short) 0);
                        playerFragment.bassBoost.setStrength((short) (((float) 1000 / 19) * (1)));
                        playerFragment.presetReverb.setPreset((short) 0);
                        if (eqFrag != null)
                            eqFrag.setBlockerVisibility(View.VISIBLE);
                    } catch (Exception e) {

                    }
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
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
        navigationView.setCheckedItem(R.id.nav_home);

        View header = navigationView.getHeaderView(0);
        navImageView = (ImageView) header.findViewById(R.id.nav_image_view);
        if (navImageView != null) {
            navImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayerFragment pFrag = getPlayerFragment();
                    if (pFrag != null) {
                        if (pFrag.mMediaPlayer != null && pFrag.mMediaPlayer.isPlaying()) {
                            onBackPressed();
                            isPlayerVisible = true;
                            hideTabs();
                            showPlayer();
                        }
                    }
                }
            });
        }

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {

                PlayerFragment pFrag = playerFragment;

                if (playerFragment != null) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        //Incoming call: Pause music
                        if (pFrag.mMediaPlayer != null && pFrag.mMediaPlayer.isPlaying()) {
                            wasMediaPlayerPlaying = true;
                            pFrag.togglePlayPause();
                        } else {
                            wasMediaPlayerPlaying = false;
                        }
                    } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                        //Not in call: Play music
                        if (pFrag.mMediaPlayer != null && !pFrag.mMediaPlayer.isPlaying() && wasMediaPlayerPlaying) {
                            pFrag.togglePlayPause();
                        }
                    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        //A call is dialing, active or on hold
                        if (playerFragment.mMediaPlayer != null && pFrag.mMediaPlayer.isPlaying()) {
                            wasMediaPlayerPlaying = true;
                            pFrag.togglePlayPause();
                        } else {
                            wasMediaPlayerPlaying = false;
                        }
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

        main = this;

        overflowMenuAB = (ImageView) findViewById(R.id.menuIcon);
        spImgAB = (CircleImageView) findViewById(R.id.selected_track_image_sp_AB);
        spImgAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePlayer();
                showTabs();
                isPlayerVisible = false;
            }
        });
        spTitleAB = (TextView) findViewById(R.id.selected_track_title_sp_AB);
        spTitleAB.setSelected(true);

        spArtistAB = (TextView) findViewById(R.id.selected_track_artist_sp_AB);

        localBanner = (RelativeLayout) findViewById(R.id.localBanner);
        favBanner = (ImageView) findViewById(R.id.favBanner);
        recentBanner = (ImageView) findViewById(R.id.recentBanner);
        folderBanner = (ImageView) findViewById(R.id.folderBanner);
        savedDNABanner = (ImageView) findViewById(R.id.savedDNABanner);

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
                if (queue.getQueue().size() > 0) {
                    Random r = new Random();
                    int tmp = r.nextInt(queue.getQueue().size());
                    queueCurrentIndex = tmp;
                    LocalTrack track = localTrackList.get(tmp);
                    localSelectedTrack = track;
                    streamSelected = false;
                    localSelected = true;
                    queueCall = false;
                    isReloaded = false;
                    onLocalTrackSelected(-1);
                }
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

        container = (ScrollView) findViewById(R.id.container);
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    localRecyclerContainer.setVisibility(GONE);
                    streamRecyclerContainer.setVisibility(GONE);
                    if (!searchView.isIconified()) {
                        searchView.setQuery("", true);
                        searchView.setIconified(true);
                    }
                }
                return false;
            }
        });

        if (SplashActivity.tf3 != null) {
            ((TextView) findViewById(R.id.playListRecyclerLabel)).setTypeface(SplashActivity.tf3);
            ((TextView) findViewById(R.id.recentsRecyclerLabel)).setTypeface(SplashActivity.tf3);
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
                .replaceEndButton(mEndButton)
                .setContentTitlePaint(tp)
                .setTarget(new ViewTarget(R.id.recentsRecyclerLabel, this))
                .setContentTitle("Recents and Playlists")
                .setContentText("Here all you recent songs and playlists will be listed." +
                        "Long press the cards or playlists for more options \n" +
                        "\n" +
                        "(Press Next to continue / Press back to Hide)")
                .build();
        showCase.setButtonText("Next");
        showCase.setButtonPosition(lps);
        showCase.overrideButtonClick(new View.OnClickListener() {
            int count1 = 0;

            @Override
            public void onClick(View v) {
                count1++;
                switch (count1) {
                    case 1:
                        showCase.setTarget(new ViewTarget(R.id.local_banner_alt_showcase, (Activity) ctx));
                        showCase.setContentTitle("Local Songs");
                        showCase.setContentText("See all songs available locally, classified on basis of Artist and Album");
                        showCase.setButtonPosition(lps);
                        showCase.setButtonText("Next");
                        break;
                    case 2:
                        showCase.setTarget(new ViewTarget(searchView.getId(), (Activity) ctx));
                        showCase.setContentTitle("Search");
                        showCase.setContentText("Search for songs from local library and SoundCloud™");
                        showCase.setButtonPosition(lps);
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
            Log.d("TIME", "start");
            String json2 = mPrefs.getString("allPlaylists", "");
            allPlaylists = gson.fromJson(json2, AllPlaylists.class);
            Log.d("TIME", "allPlaylists");
            String json3 = mPrefs.getString("queue", "");
            queue = gson.fromJson(json3, Queue.class);
            Log.d("TIME", "queue");
            String json4 = mPrefs.getString("recentlyPlayed", "");
            recentlyPlayed = gson.fromJson(json4, RecentlyPlayed.class);
            Log.d("TIME", "recents");
            String json5 = mPrefs.getString("favouriteTracks", "");
            favouriteTracks = gson.fromJson(json5, Favourite.class);
            Log.d("TIME", "fav");
            String json6 = mPrefs.getString("queueCurrentIndex", "");
            queueCurrentIndex = gson.fromJson(json6, Integer.class);
            Log.d("TIME", "queueCurrentindex");
            String json8 = mPrefs.getString("settings", "");
            settings = gson.fromJson(json8, Settings.class);
            Log.d("TIME", "settings");
            String json = mPrefs.getString("savedDNAs", "");
            savedDNAs = gson.fromJson(json, AllSavedDNA.class);
            Log.d("TIME", "savedDNAs");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String json7 = mPrefs.getString("versionCode", "");
            prevVersionCode = gson.fromJson(json7, Integer.class);
            Log.d("TIME", "VersionCode : " + prevVersionCode + " : " + versionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getLocalSongs() {

        localTrackList.clear();
        finalLocalSearchResultList.clear();
        albums.clear();
        finalAlbums.clear();
        artists.clear();
        finalArtists.clear();

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

                    int pos;
                    if (thisAlbum != null) {
                        pos = checkAlbum(thisAlbum);
                        if (pos != -1) {
                            albums.get(pos).getAlbumSongs().add(lt);
                        } else {
                            List<LocalTrack> llt = new ArrayList<>();
                            llt.add(lt);
                            Album ab = new Album(thisAlbum, llt);
                            albums.add(ab);
                        }
                        if (pos != -1) {
                            finalAlbums.get(pos).getAlbumSongs().add(lt);
                        } else {
                            List<LocalTrack> llt = new ArrayList<>();
                            llt.add(lt);
                            Album ab = new Album(thisAlbum, llt);
                            finalAlbums.add(ab);
                        }
                    }

                    if (thisArtist != null) {
                        pos = checkArtist(thisArtist);
                        if (pos != -1) {
                            artists.get(pos).getArtistSongs().add(lt);
                        } else {
                            List<LocalTrack> llt = new ArrayList<>();
                            llt.add(lt);
                            Artist ab = new Artist(thisArtist, llt);
                            artists.add(ab);
                        }
                        if (pos != -1) {
                            finalArtists.get(pos).getArtistSongs().add(lt);
                        } else {
                            List<LocalTrack> llt = new ArrayList<>();
                            llt.add(lt);
                            Artist ab = new Artist(thisArtist, llt);
                            finalArtists.add(ab);
                        }
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

        musicCursor.close();

        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        try {
            if (localTrackList.size() > 0) {
                Collections.sort(localTrackList, new localMusicComparator());
                Collections.sort(finalLocalSearchResultList, new localMusicComparator());
            }
            if (albums.size() > 0) {
                Collections.sort(albums, new albumComparator());
                Collections.sort(finalAlbums, new albumComparator());
            }
            if (artists.size() > 0) {
                Collections.sort(artists, new artistComparator());
                Collections.sort(finalArtists, new artistComparator());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<UnifiedTrack> tmp = new ArrayList<>();
        boolean queueCurrentIndexCollision = false;
        int indexCorrection = 0;
        for (int i = 0; i < queue.getQueue().size(); i++) {
            UnifiedTrack ut = queue.getQueue().get(i);
            if (ut.getType()) {
                if (!checkTrack(ut.getLocalTrack())) {
                    if (i == queueCurrentIndex) {
                        queueCurrentIndexCollision = true;
                    } else if (i < queueCurrentIndex) {
                        indexCorrection++;
                    }
                    tmp.add(ut);
                }
            }
        }
        for (int i = 0; i < tmp.size(); i++) {
            queue.getQueue().remove(tmp.get(i));
        }
        if (queueCurrentIndexCollision) {
            if (queue.getQueue().size() > 0) {
                queueCurrentIndex = 0;
            } else {
                queue = new Queue();
            }
        } else {
            queueCurrentIndex -= indexCorrection;
        }

        tmp.clear();

        for (int i = 0; i < recentlyPlayed.getRecentlyPlayed().size(); i++) {
            UnifiedTrack ut = recentlyPlayed.getRecentlyPlayed().get(i);
            if (ut.getType()) {
                if (!checkTrack(ut.getLocalTrack())) {
                    tmp.add(ut);
                }
            }
        }
        for (int i = 0; i < tmp.size(); i++) {
            recentlyPlayed.getRecentlyPlayed().remove(tmp.get(i));
        }

        List<UnifiedTrack> temp = new ArrayList<>();
        List<Playlist> tmpPL = new ArrayList<>();

        for (int i = 0; i < allPlaylists.getPlaylists().size(); i++) {
            Playlist pl = allPlaylists.getPlaylists().get(i);
            for (int j = 0; j < pl.getSongList().size(); j++) {
                UnifiedTrack ut = pl.getSongList().get(j);
                if (ut.getType()) {
                    if (!checkTrack(ut.getLocalTrack())) {
                        temp.add(ut);
                    }
                }
            }
            for (int j = 0; j < temp.size(); j++) {
                pl.getSongList().remove(temp.get(j));
            }
            temp.clear();
            if (pl.getSongList().size() == 0) {
                tmpPL.add(pl);
            }
        }
        for (int i = 0; i < tmpPL.size(); i++) {
            allPlaylists.getPlaylists().remove(tmpPL.get(i));
        }
        tmpPL.clear();
    }

    public boolean checkTrack(LocalTrack lt) {
        for (int i = 0; i < localTrackList.size(); i++) {
            LocalTrack localTrack = localTrackList.get(i);
            if (localTrack.getTitle().equals(lt.getTitle())) {
                return true;
            }
        }
        return false;
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
        PlayerFragment plFrag = playerFragment;
        FullLocalMusicFragment flmFrag = (FullLocalMusicFragment) fragMan.findFragmentByTag("local");
        LocalMusicFragment lFrag = null;
        if (flmFrag != null) {
            lFrag = (LocalMusicFragment) flmFrag.getFragmentByPosition(0);
        }
        QueueFragment qFrag = (QueueFragment) fragMan.findFragmentByTag("queue");
        EqualizerFragment eqFrag = (EqualizerFragment) fragMan.findFragmentByTag("equalizer");
        ViewSavedDNA vsdFrag = (ViewSavedDNA) fragMan.findFragmentByTag("allSavedDNAs");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (showCase != null && showCase.isShowing()) {
            showCase.hide();
        } else if (plFrag != null && plFrag.isShowcaseVisible()) {
            plFrag.hideShowcase();
        } else if (lFrag != null && lFrag.isShowcaseVisible()) {
            lFrag.hideShowcase();
        } else if (qFrag != null && qFrag.isShowcaseVisible()) {
            qFrag.hideShowcase();
        } else if (eqFrag != null && eqFrag.isShowcaseVisible()) {
            eqFrag.hideShowcase();
        } else if (vsdFrag != null && vsdFrag.isShowcaseVisible()) {
            vsdFrag.hideShowcase();
        } else if (isFullScreenEnabled) {
            isFullScreenEnabled = false;
            plFrag.bottomContainer.setVisibility(View.VISIBLE);
            plFrag.seekBarContainer.setVisibility(View.VISIBLE);
            plFrag.toggleContainer.setVisibility(View.VISIBLE);
            spToolbar.setVisibility(View.VISIBLE);
            plFrag.fullscreenExtraSpaceOccupier.getLayoutParams().height = 0;
            onFullScreen();
        } else if (!searchView.isIconified()) {
            searchView.setQuery("", true);
            searchView.setIconified(true);
            new Thread(new CancelCall()).start();
            if (localRecyclerContainer.getVisibility() == View.VISIBLE || streamRecyclerContainer.getVisibility() == View.VISIBLE) {
                localRecyclerContainer.setVisibility(GONE);
                streamRecyclerContainer.setVisibility(GONE);
            }
        } else if (localRecyclerContainer.getVisibility() == View.VISIBLE || streamRecyclerContainer.getVisibility() == View.VISIBLE) {
            localRecyclerContainer.setVisibility(GONE);
            streamRecyclerContainer.setVisibility(GONE);
        } else {
            if (isEqualizerVisible) {
                showPlayer2();
            } else if (isQueueVisible) {
                showPlayer3();
            } else if (isPlayerVisible && !isPlayerTransitioning) {
                hidePlayer();
                showTabs();
                isPlayerVisible = false;
            } else if (isEditVisible) {
                hideFragment("Edit");
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
                } else if (isNewPlaylistVisible) {
                    hideFragment("newPlaylist");
                    setTitle("Music DNA");
                } else if (isEqualizerVisible) {
                    finalSelectedTracks.clear();
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
                } else if (isAboutVisible) {
                    hideFragment("About");
                    setTitle("Settings");
                } else if (isSettingsVisible) {
                    hideFragment("settings");
                    new SaveSettings().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    setTitle("Music DNA");
                } else if (!isPlayerTransitioning) {
                    startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                try {
                    final Uri imageUri = data.getData();
                    String path = imageUri.getPath();
                    Toast.makeText(this, path + "", Toast.LENGTH_SHORT).show();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    selectedImage = BitmapFactory.decodeStream(imageStream);

                    EditLocalSongFragment editSongFragment = (EditLocalSongFragment) getSupportFragmentManager().findFragmentByTag("Edit");
                    if (editSongFragment != null) {
                        editSongFragment.updateCoverArt(selectedImage, imageUri);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
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
        if (id == R.id.action_sleep) {
            showSleepDialog();
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
        updateStreamingList(query.trim());
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

        FullLocalMusicFragment flmFrag = (FullLocalMusicFragment) fragMan.findFragmentByTag("local");
        LocalMusicFragment lFrag = null;
        if (flmFrag != null)
            lFrag = (LocalMusicFragment) flmFrag.getFragmentByPosition(0);

        if (lFrag != null)
            lFrag.hideShuffleFab();

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
            localsongsRecyclerView.setVisibility(GONE);
            localNothingText.setVisibility(View.VISIBLE);
        } else {
            localsongsRecyclerView.setVisibility(View.VISIBLE);
            localNothingText.setVisibility(View.INVISIBLE);
        }

        (localsongsRecyclerView.getAdapter()).notifyDataSetChanged();
        if (lFrag != null)
            lFrag.updateAdapter();
        if (query.equals("")) {
            localRecyclerContainer.setVisibility(GONE);
        }
        if (query.equals("") && isLocalVisible) {
            if (lFrag != null)
                lFrag.showShuffleFab();
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
        FullLocalMusicFragment flmFrag = (FullLocalMusicFragment) fragMan.findFragmentByTag("local");
        if (flmFrag != null) {
            AlbumFragment aFrag = (AlbumFragment) flmFrag.getFragmentByPosition(1);
            if (aFrag != null) {
                aFrag.updateAdapter();
            }
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

        FullLocalMusicFragment flmFrag = (FullLocalMusicFragment) fragMan.findFragmentByTag("local");
        if (flmFrag != null) {
            ArtistFragment aFrag = (ArtistFragment) flmFrag.getFragmentByPosition(2);
            if (aFrag != null) {
                aFrag.updateAdapter();
            }
        }
    }

    private void updateStreamingList(String query) {

        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if ((settings.isStreamOnlyOnWifiEnabled() && mWifi.isConnected()) || (!settings.isStreamOnlyOnWifiEnabled())) {
            new Thread(new CancelCall()).start();

            if (isPlayerVisible) {
                hidePlayer();
            }

        /*Update the Streaming List*/

            if (!query.equals("")) {
            /*streamingTrackList.clear();
            if(sAdapter!=null){
                soundcloudRecyclerView.getAdapter().notifyDataSetChanged();
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
                            streamingTrackList = response.body();
                            sAdapter = new StreamTracksHorizontalAdapter(streamingTrackList, ctx);
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                            soundcloudRecyclerView.setLayoutManager(mLayoutManager);
                            soundcloudRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            soundcloudRecyclerView.setAdapter(sAdapter);

                            if (streamingTrackList.size() == 0) {
                                streamRecyclerContainer.setVisibility(GONE);
                            } else {
                                streamRecyclerContainer.setVisibility(View.VISIBLE);
                            }

                            stopLoadingIndicator();
                            (soundcloudRecyclerView.getAdapter()).notifyDataSetChanged();

                            StreamMusicFragment sFrag = (StreamMusicFragment) fragMan.findFragmentByTag("stream");
                            if (sFrag != null) {
                                sFrag.dataChanged();
                            }
                        } else {
                            stopLoadingIndicator();
                        }
                        Log.d("RETRO", response.body() + "");
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("RETRO1", t.getMessage());
                    }
                });

            } else {
                stopLoadingIndicator();
                streamRecyclerContainer.setVisibility(GONE);
            }
        } else {
            stopLoadingIndicator();
            streamRecyclerContainer.setVisibility(GONE);
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


        if (!isFullScreenEnabled)
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
                        spToolbar.setVisibility(GONE);
                    }
                });

        if (isFullScreenEnabled) {
            toolbar.setVisibility(View.INVISIBLE);
            fragmentToolbar.setVisibility(View.INVISIBLE);
        }

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

        if (playerFragment.mVisualizerView != null)
            playerFragment.mVisualizerView.setVisibility(View.INVISIBLE);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = ((Activity) (ctx)).getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getDarkColor(themeColor));
        }

        isPlayerVisible = false;

        if (playerFragment.cpb != null) {
            playerFragment.cpb.setAlpha(0.0f);
            playerFragment.cpb.setVisibility(View.VISIBLE);
            playerFragment.cpb.animate()
                    .alpha(1.0f);
        }
        if (playerFragment.smallPlayer != null) {
            playerFragment.smallPlayer.setAlpha(0.0f);
            playerFragment.smallPlayer.setVisibility(View.VISIBLE);
            playerFragment.smallPlayer.animate()
                    .alpha(1.0f);
        }

        playerContainer.setVisibility(View.VISIBLE);

        playerContainer.animate()
                .translationY(playerContainer.getHeight() - playerFragment.smallPlayer.getHeight())
                .setDuration(300);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAllSavedDnaVisisble) {
                    ViewSavedDNA vsdFrag = (ViewSavedDNA) fragMan.findFragmentByTag("allSavedDNAs");
                    if (vsdFrag != null)
                        vsdFrag.setVisualizerVisibility(View.VISIBLE);
                }
            }
        }, 350);

        playerFragment.player_controller.setAlpha(0.0f);
        playerFragment.player_controller.setImageDrawable(playerFragment.mainTrackController.getDrawable());

        playerFragment.player_controller.animate()
                .alpha(1.0f);

        playerFragment.snappyRecyclerView.animate()
                .alpha(0.0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        playerFragment.snappyRecyclerView.setVisibility(GONE);
                    }
                });
    }

    public void hidePlayer2() {

        isEqualizerVisible = true;

        if (playerFragment.mVisualizerView != null)
            playerFragment.mVisualizerView.setVisibility(View.INVISIBLE);

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

        if (playerFragment.mVisualizerView != null)
            playerFragment.mVisualizerView.setVisibility(View.INVISIBLE);

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


        playerContainer.setVisibility(View.VISIBLE);
        if (playerFragment.mVisualizerView != null)
            playerFragment.mVisualizerView.setVisibility(View.INVISIBLE);

        if (playerFragment.player_controller != null) {
            playerFragment.player_controller.setAlpha(1.0f);
            playerFragment.player_controller.animate()
                    .setDuration(300)
                    .alpha(0.0f);
        }

        if (playerFragment.cpb != null) {
            playerFragment.cpb.animate()
                    .alpha(0.0f);
        }
        if (playerFragment.smallPlayer != null) {
            playerFragment.smallPlayer.animate()
                    .alpha(0.0f);
        }

        isPlayerTransitioning = true;

        playerContainer.animate()
                .setDuration(300)
                .translationY(0)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        isPlayerTransitioning = false;
                    }
                });

//        if (settings.isAlbumArtBackgroundEnabled()) {
//            if (playerFragment.currentAlbumArtHolder != null) {
//                playerFragment.currentAlbumArtHolder.setVisibility(View.VISIBLE);
//                playerFragment.currentAlbumArtHolder.animate()
//                        .alpha(0.1f)
//                        .setDuration(300);
//            }
//        }

        if (playerFragment.snappyRecyclerView != null) {
            playerFragment.snappyRecyclerView.setVisibility(View.VISIBLE);
            playerFragment.snappyRecyclerView.animate()
                    .alpha(1.0f)
                    .setDuration(300);
        }

        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (playerFragment.mVisualizerView != null)
                    playerFragment.mVisualizerView.setVisibility(View.VISIBLE);
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
        if (playerFragment.mVisualizerView != null)
            playerFragment.mVisualizerView.setVisibility(View.INVISIBLE);

        isPlayerTransitioning = true;

        playerContainer.animate()
                .setDuration(300)
                .translationX(0)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        isPlayerTransitioning = false;
                    }
                });

        switchToolbar(equalizerToolbar, spToolbar, "left");

        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (playerFragment.mVisualizerView != null)
                    playerFragment.mVisualizerView.setVisibility(View.VISIBLE);
            }
        }, 400);


    }

    public void showPlayer3() {

        isPlayerVisible = true;
        isEqualizerVisible = false;
        isQueueVisible = false;

        if (playerFragment.mVisualizerView != null)
            playerFragment.mVisualizerView.setVisibility(View.INVISIBLE);

        isPlayerTransitioning = true;

        playerContainer.animate()
                .setDuration(300)
                .translationX(0)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        isPlayerTransitioning = false;
                    }
                });

        switchToolbar(queueToolbar, spToolbar, "right");

        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (playerFragment.mVisualizerView != null)
                    playerFragment.mVisualizerView.setVisibility(View.VISIBLE);
                hideFragment("queue");
            }
        }, 400);


    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        try {
            new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePoints() {

        try {
            playerFragment.mVisualizerView.outerRadius = (float) (Math.min(playerFragment.mVisualizerView.width, playerFragment.mVisualizerView.height) * 0.42);
            playerFragment.mVisualizerView.normalizedPosition = ((float) (playerFragment.mMediaPlayer.getCurrentPosition()) / (float) (playerFragment.durationInMilliSec));
            if (mBytes == null) {
                return;
            }
            playerFragment.mVisualizerView.angle = (float) (Math.PI - playerFragment.mVisualizerView.normalizedPosition * playerFragment.mVisualizerView.TAU);
            playerFragment.mVisualizerView.color = 0;
            playerFragment.mVisualizerView.lnDataDistance = 0;
            playerFragment.mVisualizerView.distance = 0;
            playerFragment.mVisualizerView.size = 0;
            playerFragment.mVisualizerView.volume = 0;
            playerFragment.mVisualizerView.power = 0;
        } catch (Exception e) {

        }

        float x, y;

        int midx = (int) (playerFragment.mVisualizerView.width / 2);
        int midy = (int) (playerFragment.mVisualizerView.height / 2);

        // calculate min and max amplitude for current byte array
        float max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        for (int a = 16; a < (mBytes.length / 2); a++) {
            float amp = mBytes[(a * 2) + 0] * mBytes[(a * 2) + 0] + mBytes[(a * 2) + 1] * mBytes[(a * 2) + 1];
            if (amp > max) {
                max = amp;
            }
            if (amp < min) {
                min = amp;
            }
        }

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

            playerFragment.mVisualizerView.volume = (amp);

            // converting polar to cartesian (distance calculated afterwards acts as radius for polar co-ords)
            x = (float) Math.sin(playerFragment.mVisualizerView.angle);
            y = (float) Math.cos(playerFragment.mVisualizerView.angle);

            // filtering low amplitude
            if (playerFragment.mVisualizerView.volume < minAudioStrength) {
                continue;
            }

            // color ( value of hue inn HSV ) calculated based on current progress of the song or audio clip
            playerFragment.mVisualizerView.color = (float) (playerFragment.mVisualizerView.normalizedPosition - 0.12 + Math.random() * 0.24);
            playerFragment.mVisualizerView.color = Math.round(playerFragment.mVisualizerView.color * 360);
            seekBarColor = (float) (playerFragment.mVisualizerView.normalizedPosition);
            seekBarColor = Math.round(seekBarColor * 360);

            // calculating distance from center ( 'r' in polar coordinates)
            playerFragment.mVisualizerView.lnDataDistance = (float) ((Math.log(a - 4) / playerFragment.mVisualizerView.LOG_MAX) - playerFragment.mVisualizerView.BASE);
            playerFragment.mVisualizerView.distance = playerFragment.mVisualizerView.lnDataDistance * playerFragment.mVisualizerView.outerRadius;


            // size of the circle to be rendered at the calculated position
            playerFragment.mVisualizerView.size = ratio * ((float) (4.5 * playerFragment.mVisualizerView.volume * playerFragment.mVisualizerView.MAX_DOT_SIZE + Math.random() * 2));

            // alpha also based on volume ( amplitude )
            playerFragment.mVisualizerView.alpha = (float) (playerFragment.mVisualizerView.volume * 0.09);

            // final cartesian coordinates for drawing on canvas
            x = x * playerFragment.mVisualizerView.distance;
            y = y * playerFragment.mVisualizerView.distance;


            float[] hsv = new float[3];
            hsv[0] = playerFragment.mVisualizerView.color;
            hsv[1] = (float) 0.9;
            hsv[2] = (float) 0.9;

            // setting color of the Paint
            playerFragment.mVisualizerView.mForePaint.setColor(Color.HSVToColor(hsv));

            if (playerFragment.mVisualizerView.size >= 8.0 && playerFragment.mVisualizerView.size < 29.0) {
                playerFragment.mVisualizerView.mForePaint.setAlpha(17);
            } else if (playerFragment.mVisualizerView.size >= 29.0 && playerFragment.mVisualizerView.size <= 60.0) {
                playerFragment.mVisualizerView.mForePaint.setAlpha(9);
            } else if (playerFragment.mVisualizerView.size > 60.0) {
                playerFragment.mVisualizerView.mForePaint.setAlpha(3);
            } else {
                playerFragment.mVisualizerView.mForePaint.setAlpha((int) (playerFragment.mVisualizerView.alpha * 1000));
            }

            // Add points and paint config to lists for redraw
            playerFragment.mVisualizerView.pts.add(Pair.create(midx + x, midy + y));
            playerFragment.mVisualizerView.ptPaint.add(Pair.create(playerFragment.mVisualizerView.size, Pair.create(playerFragment.mVisualizerView.mForePaint.getColor(), playerFragment.mVisualizerView.mForePaint.getAlpha())));

            cacheCanvas.drawCircle(midx + x, midy + y, playerFragment.mVisualizerView.size, playerFragment.mVisualizerView.mForePaint);
        }
    }

    @Override
    public void onComplete() {

        // Check for sleep timer and whether it has timed out
        if (isSleepTimerEnabled && isSleepTimerTimeout) {
            Toast.makeText(ctx, "Sleep timer timed out, closing app", Toast.LENGTH_SHORT).show();

            if (playerFragment != null && playerFragment.t != null)
                playerFragment.t.cancel();

            // Remove the notification
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1);

            // Finish the activity
            finish();
            return;
        }

        // Save the DNA if saving is enabled
        if (isSaveDNAEnabled) {
            new SaveTheDNAs().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        QueueFragment qFrag = (QueueFragment) fragMan.findFragmentByTag("queue");
        queueCall = true;

        PlayerFragment plFrag = playerFragment;

        if (repeatOnceEnabled && !nextControllerClicked) {

            /*
            * Executed if repeat once is enabled and user did not click the next button from player.
            */

            // Set Progress bar to 0
            plFrag.progressBar.setProgress(0);
            plFrag.progressBar.setSecondaryProgress(0);

            // Get Visualizer and Visualizer View to initial state
            plFrag.mVisualizer.setEnabled(true);
            VisualizerView.w = screen_width;
            VisualizerView.h = screen_width;
            VisualizerView.conf = Bitmap.Config.ARGB_8888;
            VisualizerView.bmp = Bitmap.createBitmap(VisualizerView.w, VisualizerView.h, VisualizerView.conf);
            cacheCanvas = new Canvas(VisualizerView.bmp);
            plFrag.mVisualizerView.clear();

            // Play the song again by seeking media player to 0
            plFrag.mMediaPlayer.seekTo(0);

            // Setup the icons
            plFrag.mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
            plFrag.isReplayIconVisible = false;
            plFrag.player_controller.setImageResource(R.drawable.ic_pause_white_48dp);

            // Resume the MediaPlayer
            plFrag.isPrepared = true;
            plFrag.mMediaPlayer.start();
        } else {

            /*
            * Executed if repeat once is disabled.
            * Execution depends on the current position in queue and whether the next button was clicked or not.
            * If current position is at the end of the queue, then number of elements in the queue are checked.
            */

            if (queueCurrentIndex < queue.getQueue().size() - 1) {
                queueCurrentIndex++;
                nextControllerClicked = false;
                hasQueueEnded = false;
                if (qFrag != null) {
                    qFrag.updateQueueAdapter();
                }
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
                if ((repeatEnabled || repeatOnceEnabled) && (queue.getQueue().size() > 1)) {
                    nextControllerClicked = false;
                    hasQueueEnded = false;
                    queueCurrentIndex = 0;
                    if (qFrag != null) {
                        qFrag.updateQueueAdapter();
                    }
                    onQueueItemClicked(0);
                } else if ((repeatEnabled || repeatOnceEnabled) && (queue.getQueue().size() == 1)) {
                    nextControllerClicked = false;
                    hasQueueEnded = false;
                    plFrag.progressBar.setProgress(0);
                    plFrag.progressBar.setSecondaryProgress(0);
                    plFrag.mVisualizer.setEnabled(true);
                    plFrag.mVisualizerView.clear();
                    plFrag.mMediaPlayer.seekTo(0);
                    plFrag.mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                    plFrag.isReplayIconVisible = false;
                    plFrag.player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                    plFrag.isPrepared = true;
                    plFrag.mMediaPlayer.start();
                } else {
                    if ((nextControllerClicked || hasQueueEnded) && (queue.getQueue().size() > 1)) {
                        nextControllerClicked = false;
                        hasQueueEnded = false;
                        queueCurrentIndex = 0;
                        if (qFrag != null) {
                            qFrag.updateQueueAdapter();
                        }
                        onQueueItemClicked(0);
                    } else if ((nextControllerClicked || hasQueueEnded) && (queue.getQueue().size() == 1)) {
                        nextControllerClicked = false;
                        hasQueueEnded = false;
                        plFrag.progressBar.setProgress(0);
                        plFrag.progressBar.setSecondaryProgress(0);
                        plFrag.mVisualizer.setEnabled(true);
                        plFrag.mVisualizerView.clear();
                        plFrag.mMediaPlayer.seekTo(0);
                        plFrag.mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                        plFrag.isReplayIconVisible = false;
                        plFrag.player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                        plFrag.isPrepared = true;
                        plFrag.mMediaPlayer.start();
                    } else {
                        // keep queue at last track
                    }
                }
            }
        }

    }

    @Override
    public void onPreviousTrack() {

        QueueFragment qFrag = (QueueFragment) fragMan.findFragmentByTag("queue");

        /*
        * Execution depends on the current position in the queue
        */

        if (queueCurrentIndex > 0) {
            queueCall = true;
            queueCurrentIndex--;
            if (qFrag != null) {
                qFrag.updateQueueAdapter();
            }
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
            // keep queue at 0
        }
    }

    @Override
    public void onQueueItemClicked(final int position) {

        if (isPlayerVisible && isQueueVisible)
            showPlayer3();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                queueCurrentIndex = position;
                UnifiedTrack ut = queue.getQueue().get(position);
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

    public void onQueueItemClicked2(int position) {
        if (position <= (queue.getQueue().size() - 1)) {
            queueCurrentIndex = position;
            UnifiedTrack ut = queue.getQueue().get(position);
            if (ut.getType()) {
                LocalTrack track = ut.getLocalTrack();
                localSelectedTrack = track;
                streamSelected = false;
                localSelected = true;
                queueCall = true;
                isReloaded = false;
                onLocalTrackSelected(position);
            } else {
                Track track = ut.getStreamTrack();
                selectedTrack = track;
                streamSelected = true;
                localSelected = false;
                queueCall = true;
                isReloaded = false;
                onTrackSelected(position);
            }
        }
    }

    @Override
    public void onPLaylistItemClicked(int position) {
        UnifiedTrack ut = tempPlaylist.getSongList().get(position);
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
        UnifiedTrack ut = favouriteTracks.getFavourite().get(position);
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
        setTitle("Music DNA");
    }

    @Override
    public void onFavouritePlayAll() {
        if (queue.getQueue().size() > 0) {
            onQueueItemClicked(0);
            hideFragment("favourite");
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
            Toast.makeText(this, "Long Press to Exit", Toast.LENGTH_SHORT).show();
            View decorView = getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

//            decorView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//            decorView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = getSupportActionBar();
            actionBar.show();
        }
    }

    @Override
    public void onArtistClick() {
        searchView.setQuery("", true);
        searchView.setIconified(true);
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
    public void onPlaylsitRename() {
        renamePlaylistDialog(allPlaylists.getPlaylists().get(renamePlaylistNumber).getPlaylistName());
    }

    @Override
    public void addToPlaylist(UnifiedTrack ut) {
        showAddToPlaylistDialog(ut);
    }

    @Override
    public PlayerFragment getPlayerFragmentFromHome() {
        return getPlayerFragment();
    }

    @Override
    public void onColorChanged() {
        navigationView.setItemIconTintList(ColorStateList.valueOf(themeColor));
    }

    @Override
    public void onAlbumArtBackgroundChangedVisibility(int visibility) {
        PlayerFragment plFrag = getPlayerFragment();
        if (plFrag != null) {
            plFrag.toggleAlbumArtBackground(visibility);
        }
    }

    @Override
    public void newPlaylistListener() {
        showFragment("newPlaylist");
    }

    @Override
    public void onPlaylistEmpty() {
        PlayListFragment plFrag = (PlayListFragment) fragMan.findFragmentByTag("allPlaylists");
        if (plFrag != null && plFrag.vpAdapter != null) {
            plFrag.vpAdapter.notifyItemRemoved(tempPlaylistNumber);
        }
        if (pAdapter != null) {
            pAdapter.notifyItemRemoved(tempPlaylistNumber);
        }
    }

    @Override
    public void onCancel() {
        finalSelectedTracks.clear();
    }

    @Override
    public void onDone() {
        if (finalSelectedTracks.size() == 0) {
            finalSelectedTracks.clear();
            onBackPressed();
        } else {
            newPlaylistNameDialog();
        }
    }

    @Override
    public void onAddedtoFavfromPlayer() {
        FavouritesFragment favouritesFragment = (FavouritesFragment) fragMan.findFragmentByTag("favourite");
        if (favouritesFragment != null) {
            favouritesFragment.updateData();
        }
    }

    @Override
    public void onRemovedfromFavfromPlayer() {
        FavouritesFragment favouritesFragment = (FavouritesFragment) fragMan.findFragmentByTag("favourite");
        if (favouritesFragment != null) {
            favouritesFragment.updateData();
        }
    }

    @Override
    public void onHeadsetRemoved() {
        PlayerFragment pFrag = getPlayerFragment();
        if (pFrag != null) {
            if (pFrag.mMediaPlayer != null && pFrag.mMediaPlayer.isPlaying()) {
                if (!pFrag.pauseClicked) {
                    pFrag.pauseClicked = true;
                }
                pFrag.togglePlayPause();
            }
        }
    }

    @Override
    public void onShuffleEnabled() {
        originalQueue = new Queue();
        for (UnifiedTrack ut : queue.getQueue()) {
            originalQueue.addToQueue(ut);
        }
        originalQueueIndex = queueCurrentIndex;
        UnifiedTrack ut = queue.getQueue().get(queueCurrentIndex);
        Collections.shuffle(queue.getQueue());
        for (int i = 0; i < queue.getQueue().size(); i++) {
            if (ut.equals(queue.getQueue().get(i))) {
                queue.getQueue().remove(i);
                break;
            }
        }
        queue.getQueue().add(0, ut);
        queueCurrentIndex = 0;

        new SaveQueue().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onShuffleDisabled() {
        UnifiedTrack ut1 = queue.getQueue().get(queueCurrentIndex);
        for (int i = 0; i < queue.getQueue().size(); i++) {
            UnifiedTrack ut = queue.getQueue().get(i);
            if (!originalQueue.getQueue().contains(ut)) {
                originalQueue.getQueue().add(ut);
            }
        }
        queue.getQueue().clear();
        for (UnifiedTrack ut : originalQueue.getQueue()) {
            queue.addToQueue(ut);
        }
        for (int i = 0; i < queue.getQueue().size(); i++) {
            if (ut1.equals(queue.getQueue().get(i))) {
                queueCurrentIndex = i;
                break;
            }
        }

        new SaveQueue().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onAboutClicked() {
        showFragment("About");
    }

    @Override
    public void onEditSongSave(boolean wasSaveSuccessful) {
        hideFragment("Edit");
        if (!wasSaveSuccessful) {
            Toast.makeText(this, "Error in saving Data", Toast.LENGTH_SHORT).show();
            return;
        }

        updateMediaCache(editSong.getTitle(), editSong.getArtist(), editSong.getAlbum(), editSong.getId());

        refreshAlbumAndArtists();

        if (isAlbumVisible) {
            hideFragment("viewAlbum");

        } else if (isArtistVisible) {
            hideFragment("viewArtist");
        }
        FullLocalMusicFragment flmFrag = (FullLocalMusicFragment) fragMan.findFragmentByTag("local");
        LocalMusicFragment lFrag = null;
        if (flmFrag != null) {
            lFrag = (LocalMusicFragment) flmFrag.getFragmentByPosition(0);
        }
        if (lFrag != null) {
            lFrag.updateAdapter();
        }

        ArtistFragment artFrag = null;
        if (flmFrag != null) {
            artFrag = (ArtistFragment) flmFrag.getFragmentByPosition(2);
        }
        if (artFrag != null) {
            artFrag.updateAdapter();
        }

        AlbumFragment albFrag = null;
        if (flmFrag != null) {
            albFrag = (AlbumFragment) flmFrag.getFragmentByPosition(1);
        }
        if (albFrag != null) {
            albFrag.updateAdapter();
        }
    }

    @Override
    public void getNewBitmap() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);

    }

    @Override
    public void deleteMediaStoreCache() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.android.providers.media/albumthumbs");
//        Toast.makeText(this, Environment.getExternalStorageDirectory() + "/Android/data/com.android.providers.media/albumthumbs/", Toast.LENGTH_SHORT).show();
        if (dir.isDirectory()) {
            Toast.makeText(this, "Clearing cache", Toast.LENGTH_SHORT).show();
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

    @Override
    public PlayerFragment getPlayerFragment() {
        return playerFragment;
    }

    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

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
                    playerFragment.mVisualizerView.updateVisualizer(mBytes);
                    if (playerFragment.mVisualizerView.bmp != null) {
                        if (navImageView != null)
                            navImageView.setImageBitmap(playerFragment.mVisualizerView.bmp);
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        new SaveVersionCode().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new SaveData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new SaveSettings().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new SaveQueue().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        try {
            prefsEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(headSetReceiver);
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(this);
        refWatcher.watch(this);
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (bound) {
            myService.setCallbacks(null); // unregister
            unbindService(serviceConnection);
            bound = false;
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

    public void newPlaylistNameDialog() {
        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.save_image_dialog);
        dialog.setTitle("Playlist Name");

        Button btn = (Button) dialog.findViewById(R.id.save_image_btn);
        final EditText newName = (EditText) dialog.findViewById(R.id.save_image_filename_text);

        CheckBox cb = (CheckBox) dialog.findViewById(R.id.text_checkbox);
        cb.setVisibility(GONE);

        btn.setBackgroundColor(themeColor);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNameRepeat = false;
                if (newName.getText().toString().trim().equals("")) {
                    newName.setError("Enter Playlist Name!");
                } else {
                    for (int i = 0; i < allPlaylists.getPlaylists().size(); i++) {
                        if (newName.getText().toString().equals(allPlaylists.getPlaylists().get(i).getPlaylistName())) {
                            isNameRepeat = true;
                            newName.setError("Playlist with same name exists!");
                            break;
                        }
                    }
                    if (!isNameRepeat) {
                        UnifiedTrack ut;
                        Playlist pl = new Playlist(newName.getText().toString());
                        for (int i = 0; i < finalSelectedTracks.size(); i++) {
                            ut = new UnifiedTrack(true, finalSelectedTracks.get(i), null);
                            pl.getSongList().add(ut);
                        }
                        allPlaylists.addPlaylist(pl);
                        finalSelectedTracks.clear();
                        if (pAdapter != null) {
                            pAdapter.notifyDataSetChanged();
                            if (allPlaylists.getPlaylists().size() > 0) {
                                playlistsRecycler.setVisibility(View.VISIBLE);
                                playlistNothingText.setVisibility(View.INVISIBLE);
                            }
                        }
                        PlayListFragment plFrag = (PlayListFragment) fragMan.findFragmentByTag("allPlaylists");
                        if (plFrag != null) {
                            plFrag.dataChanged();
                        }
                        new SavePlaylists().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        dialog.dismiss();
                        onBackPressed();
                    }
                }
            }
        });

        dialog.show();

    }

    public void renamePlaylistDialog(String oldName) {
        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.save_image_dialog);
        dialog.setTitle("Rename");

        Button btn = (Button) dialog.findViewById(R.id.save_image_btn);
        final EditText newName = (EditText) dialog.findViewById(R.id.save_image_filename_text);

        CheckBox cb = (CheckBox) dialog.findViewById(R.id.text_checkbox);
        cb.setVisibility(GONE);

        newName.setText(oldName);
        btn.setBackgroundColor(themeColor);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNameRepeat = false;
                if (newName.getText().toString().trim().equals("")) {
                    newName.setError("Enter Playlist Name!");
                } else {
                    for (int i = 0; i < allPlaylists.getPlaylists().size(); i++) {
                        if (newName.getText().toString().equals(allPlaylists.getPlaylists().get(i).getPlaylistName())) {
                            isNameRepeat = true;
                            newName.setError("Playlist with same name exists!");
                            break;
                        }
                    }
                    if (!isNameRepeat) {
                        allPlaylists.getPlaylists().get(renamePlaylistNumber).setPlaylistName(newName.getText().toString());
                        if (pAdapter != null) {
                            pAdapter.notifyItemChanged(renamePlaylistNumber);
                        }
                        PlayListFragment plFrag = (PlayListFragment) fragMan.findFragmentByTag("allPlaylists");
                        if (plFrag != null) {
                            plFrag.itemChanged(renamePlaylistNumber);
                        }
                        new SavePlaylists().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        dialog.dismiss();
                    }
                }
            }
        });

        dialog.show();

    }

    public void showAddToPlaylistDialog(final UnifiedTrack track) {
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
                    new SavePlaylists().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    dialog.dismiss();
                }
            });
        } else {
            lv.setVisibility(GONE);
        }

        // set the custom dialog components - text, image and button
        final EditText text = (EditText) dialog.findViewById(R.id.new_playlist_name);
        ImageView image = (ImageView) dialog.findViewById(R.id.confirm_button);
        // if button is clicked, close the custom dialog
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNameRepeat = false;
                if (text.getText().toString().trim().equals("")) {
                    text.setError("Enter Playlist Name!");
                } else {
                    for (int i = 0; i < allPlaylists.getPlaylists().size(); i++) {
                        if (text.getText().toString().equals(allPlaylists.getPlaylists().get(i).getPlaylistName())) {
                            isNameRepeat = true;
                            text.setError("Playlist with same name exists!");
                            break;
                        }
                    }
                    if (!isNameRepeat) {
                        List<UnifiedTrack> l = new ArrayList<UnifiedTrack>();
                        l.add(track);
                        Playlist pl = new Playlist(l, text.getText().toString().trim());
                        allPlaylists.addPlaylist(pl);
                        playlistsRecycler.setVisibility(View.VISIBLE);
                        playlistNothingText.setVisibility(View.INVISIBLE);
                        pAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        new SavePlaylists().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
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
        lv.setVisibility(GONE);

        // set the custom dialog components - text, image and button
        final EditText text = (EditText) dialog.findViewById(R.id.new_playlist_name);
        ImageView image = (ImageView) dialog.findViewById(R.id.confirm_button);
        // if button is clicked, close the custom dialog
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNameRepeat = false;
                if (text.getText().toString().trim().equals("")) {
                    text.setError("Enter Playlist Name!");
                } else {
                    for (int i = 0; i < allPlaylists.getPlaylists().size(); i++) {
                        if (text.getText().toString().equals(allPlaylists.getPlaylists().get(i).getPlaylistName())) {
                            isNameRepeat = true;
                            text.setError("Playlist with same name exists!");
                            break;
                        }
                    }
                    if (!isNameRepeat) {
                        Playlist pl = new Playlist(text.getText().toString());
                        for (int i = 0; i < queue.getQueue().size(); i++) {
                            pl.getSongList().add(queue.getQueue().get(i));
                        }
                        allPlaylists.addPlaylist(pl);
                        playlistsRecycler.setVisibility(View.VISIBLE);
                        playlistNothingText.setVisibility(View.INVISIBLE);
                        pAdapter.notifyDataSetChanged();
                        new SavePlaylists().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        Toast.makeText(HomeActivity.this, "Queue saved!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();
    }

    public void startLoadingIndicator() {
        findViewById(R.id.loadingIndicator).setVisibility(View.VISIBLE);
        soundcloudRecyclerView.setVisibility(View.INVISIBLE);
        streamNothingText.setVisibility(View.INVISIBLE);
    }

    public void stopLoadingIndicator() {
        findViewById(R.id.loadingIndicator).setVisibility(View.INVISIBLE);
        soundcloudRecyclerView.setVisibility(View.VISIBLE);
        if (streamingTrackList.size() == 0) {
            streamNothingText.setVisibility(View.VISIBLE);
        }
    }

    public void showFragment(String type) {

        if (!type.equals("viewAlbum") && !type.equals("folderContent") && !type.equals("viewArtist") && !type.equals("playlist") && !type.equals("newPlaylist") && !type.equals("About") && !type.equals("Edit"))
            hideAllFrags();

        if (!searchView.isIconified()) {
            searchView.setQuery("", true);
            searchView.setIconified(true);
            streamRecyclerContainer.setVisibility(GONE);
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
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
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
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
                    .add(R.id.fragContainer, newFragment, "stream")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("playlist") && !isPlaylistVisible) {
            setTitle(tempPlaylist.getPlaylistName());
            setUpFragmentToolbar(themeColor, (String) getTitle());
            if (!isAllPlaylistVisible)
                switchToolbar(toolbar, fragmentToolbar, "left");
            isPlaylistVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            ViewPlaylistFragment newFragment = (ViewPlaylistFragment) fm.findFragmentByTag("playlist");
            if (newFragment == null) {
                newFragment = new ViewPlaylistFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
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
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
                    .add(R.id.fragContainer, newFragment, "favourite")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("newPlaylist") && !isNewPlaylistVisible) {
            setTitle("Add to Playlist");
            setUpFragmentToolbar(themeColor, (String) getTitle());
            if (!isAllPlaylistVisible)
                switchToolbar(toolbar, fragmentToolbar, "left");
            navigationView.setCheckedItem(R.id.nav_playlists);
            isNewPlaylistVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            AddToPlaylistFragment newFragment = (AddToPlaylistFragment) fm.findFragmentByTag("newPlaylist");
            if (newFragment == null) {
                newFragment = new AddToPlaylistFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
                    .add(R.id.content_frag, newFragment, "newPlaylist")
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
            PlayListFragment newFragment = (PlayListFragment) fm.findFragmentByTag("allPlaylists");
            if (newFragment == null) {
                newFragment = new PlayListFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
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
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
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
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
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
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
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
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
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
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
                    .add(R.id.fragContainer, newFragment, "viewArtist")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("recent") && !isRecentVisible) {
            setTitle("Recently Played");
            setUpFragmentToolbar(themeColor, (String) getTitle());
            navigationView.setCheckedItem(R.id.nav_recent);
            switchToolbar(toolbar, fragmentToolbar, "left");
            isRecentVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            RecentsFragment newFragment = (RecentsFragment) fm.findFragmentByTag("recent");
            if (newFragment == null) {
                newFragment = new RecentsFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
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
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
                    .add(R.id.fragContainer, newFragment, "settings")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("About") && !isAboutVisible) {
            setTitle("About");
            setUpFragmentToolbar(themeColor, (String) getTitle());
            isAboutVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            AboutFragment newFragment = (AboutFragment) fm.findFragmentByTag("About");
            if (newFragment == null) {
                newFragment = new AboutFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
                    .add(R.id.content_frag, newFragment, "About")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (type.equals("Edit") && !isEditVisible) {
            setTitle("Edit");
            if (isAlbumVisible || isArtistVisible) {
                setUpFragmentToolbar(themeColor, (String) getTitle());
            } else {
                setUpFragmentToolbar(themeColor, (String) getTitle());
                switchToolbar(toolbar, fragmentToolbar, "left");
            }
            isEditVisible = true;
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            EditLocalSongFragment newFragment = (EditLocalSongFragment) fm.findFragmentByTag("Edit");
            if (newFragment == null) {
                newFragment = new EditLocalSongFragment();
            }
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_left,
                            R.anim.slide_right,
                            R.anim.slide_left,
                            R.anim.slide_right)
                    .add(R.id.content_frag, newFragment, "Edit")
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
            if (!isAllPlaylistVisible) {
                switchToolbar(fragmentToolbar, toolbar, "instant");
            } else {
                setUpFragmentToolbar(themeColor, "All Playlists");
            }
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
        } else if (type.equals("newPlaylist")) {
            isNewPlaylistVisible = false;
            if (!isAllPlaylistVisible) {
                switchToolbar(fragmentToolbar, toolbar, "instant");
                setTitle("Music DNA");
            } else {
                setUpFragmentToolbar(themeColor, "All Playlists");
            }
            navigationView.setCheckedItem(R.id.nav_home);
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("newPlaylist");
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
            navigationView.setCheckedItem(R.id.nav_home);
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
        } else if (type.equals("About")) {
            isAboutVisible = false;
            setUpFragmentToolbar(themeColor, "Settings");
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("About");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commitAllowingStateLoss();
            }
        } else if (type.equals("Edit")) {
            isEditVisible = false;
            if (isAlbumVisible) {
                setUpFragmentToolbar(themeColor, tempAlbum.getName());
            } else if (isArtistVisible) {
                setUpFragmentToolbar(themeColor, tempArtist.getName());
            } else if (isLocalVisible) {
                setTitle("Local");
                switchToolbar(fragmentToolbar, toolbar, "instant");
            } else {
                setTitle("Music DNA");
                switchToolbar(fragmentToolbar, toolbar, "instant");
            }
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentByTag("Edit");
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
        hideFragment("newPlaylist");
        hideFragment("equalizer");
        hideFragment("favourite");
        hideFragment("folderContent");
        hideFragment("allFolders");
        hideFragment("allSavedDNAs");
        hideFragment("viewAlbum");
        hideFragment("viewArtist");
        hideFragment("recent");
        hideFragment("settings");
        hideFragment("About");

        navigationView.setCheckedItem(R.id.nav_home);

        setTitle("Music DNA");

    }

    public void showNotification() {
        if (!isNotificationVisible) {
            Intent intent = new Intent(this, MediaPlayerService.class);
            intent.setAction(Constants.ACTION_PLAY);
//            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            startService(intent);
            isNotificationVisible = true;
        }
    }

    public void setNotification() {
        Notification notification;
        String ns = Context.NOTIFICATION_SERVICE;
        notificationManager = (NotificationManager) getSystemService(ns);
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification_view);
        RemoteViews notificationViewSmall = new RemoteViews(getPackageName(), R.layout.notification_view_small);
        Intent notificationIntent = new Intent(this, getClass());
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Intent switchIntent = new Intent("com.sdsmdg.harjot.MusicDNA.ACTION_PLAY_PAUSE");
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 100, switchIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.btn_pause_play_in_notification, pendingSwitchIntent);
        try {
            if (playerFragment.mMediaPlayer.isPlaying()) {
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
            if (playerFragment.mMediaPlayer.isPlaying()) {
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
                .setLargeIcon(((BitmapDrawable) playerFragment.selected_track_image.getDrawable()).getBitmap())
                .build();
        notification.priority = Notification.PRIORITY_MAX;
        notification.bigContentView = notificationView;
        notification.contentView = notificationViewSmall;
        notification.contentIntent = pendingNotificationIntent;
        if (playerFragment.mMediaPlayer.isPlaying()) {
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
        }
        notificationView.setImageViewBitmap(R.id.image_in_notification, ((BitmapDrawable) playerFragment.selected_track_image.getDrawable()).getBitmap());
        if (playerFragment.localIsPlaying) {
            notificationView.setTextViewText(R.id.title_in_notification, playerFragment.localTrack.getTitle());
            notificationView.setTextViewText(R.id.artist_in_notification, playerFragment.localTrack.getArtist());
        } else {
            notificationView.setTextViewText(R.id.title_in_notification, playerFragment.track.getTitle());
            notificationView.setTextViewText(R.id.artist_in_notification, "");
        }
        notificationViewSmall.setImageViewBitmap(R.id.image_in_notification, ((BitmapDrawable) playerFragment.selected_track_image.getDrawable()).getBitmap());
        if (playerFragment.localIsPlaying) {
            notificationViewSmall.setTextViewText(R.id.title_in_notification, playerFragment.localTrack.getTitle());
            notificationViewSmall.setTextViewText(R.id.artist_in_notification, playerFragment.localTrack.getArtist());
        } else {
            notificationViewSmall.setTextViewText(R.id.title_in_notification, playerFragment.track.getTitle());
            notificationViewSmall.setTextViewText(R.id.artist_in_notification, "");
        }
        getPlayerFragment().isStart = false;
        notificationManager.notify(1, notification);
    }

    public void HideBottomFakeToolbar() {
        bottomToolbar.setVisibility(View.INVISIBLE);
    }

    public static void addToFavourites(UnifiedTrack ut) {
        boolean isRepeat = false;
        for (int i = 0; i < favouriteTracks.getFavourite().size(); i++) {
            UnifiedTrack ut1 = favouriteTracks.getFavourite().get(i);
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
            Log.e("DNA Save ERROR", e.getMessage());
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

    public void shareLocalSong(String path) {
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
            return lhs.getTitle().toString().compareTo(rhs.getTitle().toString());
//            if (lhs.getTitle().toString().charAt(0) < rhs.getTitle().toString().charAt(0)) {
//                return -1;
//            } else {
//                return 1;
//            }
        }
    }

    public class albumComparator implements Comparator<Album> {

        @Override
        public int compare(Album lhs, Album rhs) {
            return lhs.getName().toString().compareTo(rhs.getName().toString());
//            if (lhs.getName().toString().charAt(0) < rhs.getName().toString().charAt(0)) {
//                return -1;
//            } else {
//                return 1;
//            }
        }
    }

    public class artistComparator implements Comparator<Artist> {

        @Override
        public int compare(Artist lhs, Artist rhs) {
            return lhs.getName().toString().compareTo(rhs.getName().toString());
//            if (lhs.getName().toString().charAt(0) < rhs.getName().toString().charAt(0)) {
//                return -1;
//            } else {
//                return 1;
//            }
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

                    if (settings == null) {
                        settings = new Settings();
                    }

                    themeColor = settings.getThemeColor();
                    minAudioStrength = settings.getMinAudioStrength();


                    navigationView.setItemIconTintList(ColorStateList.valueOf(themeColor));

                    toolbar.setBackgroundColor(themeColor);

                    try {
                        if (Build.VERSION.SDK_INT >= 21) {
                            Window window = ((Activity) ctx).getWindow();
                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(getDarkColor(themeColor));
                        }
                    } catch (Exception e) {

                    }

                    if (prevVersionCode == -1 || prevVersionCode <= 30) {
                        savedDNAs = null;
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
                    if (allMusicFolders == null) {
                        allMusicFolders = new AllMusicFolders();
                    }
                    if (savedDNAs == null) {
                        savedDNAs = new AllSavedDNA();
                    }

                    new SaveVersionCode().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    getLocalSongs();

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
                        bottomToolbar.setVisibility(GONE);
                    }

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
//                            PopupMenu popup = new PopupMenu(ctx, view);
//                            popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
//
//                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                                public boolean onMenuItemClick(MenuItem item) {
//                                    if (item.getTitle().equals("Add to Playlist")) {
//                                        showAddToPlaylistDialog(ut);
//                                        pAdapter.notifyDataSetChanged();
//                                    }
//                                    if (item.getTitle().equals("Add to Queue")) {
//                                        queue.getQueue().add(ut);
//                                    }
//                                    if (item.getTitle().equals("Play")) {
//                                        boolean isRepeat = false;
//                                        int pos = 0;
//                                        for (int i = 0; i < queue.getQueue().size(); i++) {
//                                            UnifiedTrack ut1 = queue.getQueue().get(i);
//                                            if (ut1.getType() && ut.getType() && ut1.getLocalTrack().getTitle().equals(ut.getLocalTrack().getTitle())) {
//                                                isRepeat = true;
//                                                pos = i;
//                                                break;
//                                            }
//                                            if (!ut1.getType() && !ut.getType() && ut1.getStreamTrack().getTitle().equals(ut.getStreamTrack().getTitle())) {
//                                                isRepeat = true;
//                                                pos = i;
//                                                break;
//                                            }
//                                        }
//                                        if (!isRepeat && isReloaded) {
//                                            if (ut.getType()) {
//                                                LocalTrack track = ut.getLocalTrack();
//                                                if (queue.getQueue().size() == 0) {
//                                                    queueCurrentIndex = 0;
//                                                    queue.getQueue().add(new UnifiedTrack(true, track, null));
//                                                } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
//                                                    queueCurrentIndex++;
//                                                    queue.getQueue().add(new UnifiedTrack(true, track, null));
//                                                } else if (isReloaded) {
//                                                    isReloaded = false;
//                                                    queueCurrentIndex = queue.getQueue().size();
//                                                    queue.getQueue().add(new UnifiedTrack(true, track, null));
//                                                } else {
//                                                    queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(true, track, null));
//                                                }
//                                                localSelectedTrack = track;
//                                                streamSelected = false;
//                                                localSelected = true;
//                                                queueCall = false;
//                                                isReloaded = false;
//                                                onLocalTrackSelected(position);
//                                            } else {
//                                                Track track = ut.getStreamTrack();
//                                                if (queue.getQueue().size() == 0) {
//                                                    queueCurrentIndex = 0;
//                                                    queue.getQueue().add(new UnifiedTrack(false, null, track));
//                                                } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
//                                                    queueCurrentIndex++;
//                                                    queue.getQueue().add(new UnifiedTrack(false, null, track));
//                                                } else if (isReloaded) {
//                                                    isReloaded = false;
//                                                    queueCurrentIndex = queue.getQueue().size();
//                                                    queue.getQueue().add(new UnifiedTrack(false, null, track));
//                                                } else {
//                                                    queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(false, null, track));
//                                                }
//                                                selectedTrack = track;
//                                                streamSelected = true;
//                                                localSelected = false;
//                                                queueCall = false;
//                                                isReloaded = false;
//                                                onTrackSelected(position);
//                                            }
//                                        } else {
//                                            onQueueItemClicked(pos);
//                                        }
//                                    }
//                                    if (item.getTitle().equals("Play Next")) {
//                                        if (ut.getType()) {
//                                            LocalTrack track = ut.getLocalTrack();
//                                            if (queue.getQueue().size() == 0) {
//                                                queueCurrentIndex = 0;
//                                                queue.getQueue().add(new UnifiedTrack(true, track, null));
//                                                localSelectedTrack = track;
//                                                streamSelected = false;
//                                                localSelected = true;
//                                                queueCall = false;
//                                                isReloaded = false;
//                                                onLocalTrackSelected(position);
//                                            } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
//                                                queue.getQueue().add(new UnifiedTrack(true, track, null));
//                                            } else if (isReloaded) {
//                                                isReloaded = false;
//                                                queueCurrentIndex = queue.getQueue().size();
//                                                queue.getQueue().add(new UnifiedTrack(true, track, null));
//                                                localSelectedTrack = track;
//                                                streamSelected = false;
//                                                localSelected = true;
//                                                queueCall = false;
//                                                isReloaded = false;
//                                                onLocalTrackSelected(position);
//                                            } else {
//                                                queue.getQueue().add(queueCurrentIndex + 1, new UnifiedTrack(true, track, null));
//                                            }
//                                        } else {
//                                            Track track = ut.getStreamTrack();
//                                            if (queue.getQueue().size() == 0) {
//                                                queueCurrentIndex = 0;
//                                                queue.getQueue().add(new UnifiedTrack(false, null, track));
//                                                selectedTrack = track;
//                                                streamSelected = true;
//                                                localSelected = false;
//                                                queueCall = false;
//                                                isReloaded = false;
//                                                onTrackSelected(position);
//                                            } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
//                                                queue.getQueue().add(new UnifiedTrack(false, null, track));
//                                            } else if (isReloaded) {
//                                                isReloaded = false;
//                                                queueCurrentIndex = queue.getQueue().size();
//                                                queue.getQueue().add(new UnifiedTrack(false, null, track));
//                                                selectedTrack = track;
//                                                streamSelected = true;
//                                                localSelected = false;
//                                                queueCall = false;
//                                                isReloaded = false;
//                                                onTrackSelected(position);
//                                            } else {
//                                                queue.getQueue().add(queueCurrentIndex + 1, new UnifiedTrack(false, null, track));
//                                            }
//                                        }
//                                    }
//                                    if (item.getTitle().equals("Add to Favourites")) {
//                                        addToFavourites(ut);
//                                    }
//                                    return true;
//                                }
//                            });
//
//                            popup.show();
                            CustomGeneralBottomSheetDialog generalBottomSheetDialog = new CustomGeneralBottomSheetDialog();
                            generalBottomSheetDialog.setPosition(position);
                            generalBottomSheetDialog.setTrack(ut);
                            generalBottomSheetDialog.setFragment("RecentHorizontalList");
                            generalBottomSheetDialog.show(getSupportFragmentManager(), "general_bottom_sheet_dialog");
                            return true;
                        }

                        @Override
                        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                        }
                    });

                    pAdapter = new PlayListsHorizontalAdapter(allPlaylists.getPlaylists(), ctx);
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
                                    } else if (item.getTitle().equals("Add to Queue")) {
                                        Playlist pl = allPlaylists.getPlaylists().get(position);
                                        for (UnifiedTrack ut : pl.getSongList()) {
                                            queue.addToQueue(ut);
                                        }
                                    } else if (item.getTitle().equals("Delete")) {
                                        allPlaylists.getPlaylists().remove(position);
                                        PlayListFragment plFrag = (PlayListFragment) fragMan.findFragmentByTag("allPlaylists");
                                        if (plFrag != null) {
                                            plFrag.itemRemoved(position);
                                        }
                                        pAdapter.notifyItemRemoved(position);
                                        new SavePlaylists().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

                    adapter = new LocalTracksHorizontalAdapter(finalLocalSearchResultList, ctx);
                    localsongsRecyclerView = (RecyclerView) findViewById(R.id.localMusicList_home);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                    localsongsRecyclerView.setLayoutManager(mLayoutManager);
                    localsongsRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
                    alphaAdapter.setFirstOnly(false);
                    localsongsRecyclerView.setAdapter(alphaAdapter);

                    localsongsRecyclerView.addOnItemTouchListener(new ClickItemTouchListener(localsongsRecyclerView) {
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
//                            PopupMenu popup = new PopupMenu(ctx, view);
//                            popup.getMenuInflater().inflate(R.menu.popup_local, popup.getMenu());
//
//                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                                public boolean onMenuItemClick(MenuItem item) {
//                                    if (item.getTitle().equals("Add to Playlist")) {
//                                        showAddToPlaylistDialog(new UnifiedTrack(true, finalLocalSearchResultList.get(position), null));
//                                        pAdapter.notifyDataSetChanged();
//                                    }
//                                    if (item.getTitle().equals("Add to Queue")) {
//                                        queue.getQueue().add(new UnifiedTrack(true, finalLocalSearchResultList.get(position), null));
//                                    }
//                                    if (item.getTitle().equals("Play")) {
//                                        LocalTrack track = finalLocalSearchResultList.get(position);
//                                        if (queue.getQueue().size() == 0) {
//                                            queueCurrentIndex = 0;
//                                            queue.getQueue().add(new UnifiedTrack(true, track, null));
//                                        } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
//                                            queueCurrentIndex++;
//                                            queue.getQueue().add(new UnifiedTrack(true, track, null));
//                                        } else if (isReloaded) {
//                                            isReloaded = false;
//                                            queueCurrentIndex = queue.getQueue().size();
//                                            queue.getQueue().add(new UnifiedTrack(true, track, null));
//                                        } else {
//                                            queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(true, track, null));
//                                        }
//                                        localSelectedTrack = track;
//                                        streamSelected = false;
//                                        localSelected = true;
//                                        queueCall = false;
//                                        isReloaded = false;
//                                        onLocalTrackSelected(position);
//                                    }
//                                    if (item.getTitle().equals("Play Next")) {
//                                        LocalTrack track = finalLocalSearchResultList.get(position);
//                                        if (queue.getQueue().size() == 0) {
//                                            queueCurrentIndex = 0;
//                                            queue.getQueue().add(new UnifiedTrack(true, track, null));
//                                            localSelectedTrack = track;
//                                            streamSelected = false;
//                                            localSelected = true;
//                                            queueCall = false;
//                                            isReloaded = false;
//                                            onLocalTrackSelected(position);
//                                        } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
//                                            queue.getQueue().add(new UnifiedTrack(true, track, null));
//                                        } else if (isReloaded) {
//                                            isReloaded = false;
//                                            queueCurrentIndex = queue.getQueue().size();
//                                            queue.getQueue().add(new UnifiedTrack(true, track, null));
//                                            localSelectedTrack = track;
//                                            streamSelected = false;
//                                            localSelected = true;
//                                            queueCall = false;
//                                            isReloaded = false;
//                                            onLocalTrackSelected(position);
//                                        } else {
//                                            queue.getQueue().add(queueCurrentIndex + 1, new UnifiedTrack(true, track, null));
//                                        }
//                                    }
//                                    if (item.getTitle().equals("Add to Favourites")) {
//                                        UnifiedTrack ut = new UnifiedTrack(true, finalLocalSearchResultList.get(position), null);
//                                        addToFavourites(ut);
//                                    }
//                                    if (item.getTitle().equals("Share")) {
//                                        shareLocalSong(finalLocalSearchResultList.get(position).getPath());
//                                    }
//                                    return true;
//                                }
//                            });
//
//                            popup.show();
//                            return true;
                            CustomLocalBottomSheetDialog localBottomSheetDialog = new CustomLocalBottomSheetDialog();
                            localBottomSheetDialog.setPosition(position);
                            localBottomSheetDialog.setLocalTrack(finalLocalSearchResultList.get(position));
                            localBottomSheetDialog.show(getSupportFragmentManager(), "local_song_bottom_sheet");
                            return true;
                        }

                        @Override
                        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                        }
                    });

                    soundcloudRecyclerView = (RecyclerView) findViewById(R.id.trackList_home);

                    soundcloudRecyclerView.addOnItemTouchListener(new ClickItemTouchListener(soundcloudRecyclerView) {
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
//                            PopupMenu popup = new PopupMenu(ctx, view);
//                            popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
//
//                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                                public boolean onMenuItemClick(MenuItem item) {
//                                    if (item.getTitle().equals("Add to Playlist")) {
//                                        showAddToPlaylistDialog(new UnifiedTrack(false, null, streamingTrackList.get(position)));
//                                        pAdapter.notifyDataSetChanged();
//                                    }
//                                    if (item.getTitle().equals("Add to Queue")) {
//                                        queue.getQueue().add(new UnifiedTrack(false, null, streamingTrackList.get(position)));
//                                    }
//                                    if (item.getTitle().equals("Play")) {
//                                        Track track = streamingTrackList.get(position);
//                                        if (queue.getQueue().size() == 0) {
//                                            queueCurrentIndex = 0;
//                                            queue.getQueue().add(new UnifiedTrack(false, null, track));
//                                        } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
//                                            queueCurrentIndex++;
//                                            queue.getQueue().add(new UnifiedTrack(false, null, track));
//                                        } else if (isReloaded) {
//                                            isReloaded = false;
//                                            queueCurrentIndex = queue.getQueue().size();
//                                            queue.getQueue().add(new UnifiedTrack(false, null, track));
//                                        } else {
//                                            queue.getQueue().add(++queueCurrentIndex, new UnifiedTrack(false, null, track));
//                                        }
//                                        selectedTrack = track;
//                                        streamSelected = true;
//                                        localSelected = false;
//                                        queueCall = false;
//                                        isReloaded = false;
//                                        onTrackSelected(position);
//                                    }
//                                    if (item.getTitle().equals("Play Next")) {
//                                        Track track = streamingTrackList.get(position);
//                                        if (queue.getQueue().size() == 0) {
//                                            queueCurrentIndex = 0;
//                                            queue.getQueue().add(new UnifiedTrack(false, null, track));
//                                            selectedTrack = track;
//                                            streamSelected = true;
//                                            localSelected = false;
//                                            queueCall = false;
//                                            isReloaded = false;
//                                            onTrackSelected(position);
//                                        } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
//                                            queue.getQueue().add(new UnifiedTrack(false, null, track));
//                                        } else if (isReloaded) {
//                                            isReloaded = false;
//                                            queueCurrentIndex = queue.getQueue().size();
//                                            queue.getQueue().add(new UnifiedTrack(false, null, track));
//                                            selectedTrack = track;
//                                            streamSelected = true;
//                                            localSelected = false;
//                                            queueCall = false;
//                                            isReloaded = false;
//                                            onTrackSelected(position);
//                                        } else {
//                                            queue.getQueue().add(queueCurrentIndex + 1, new UnifiedTrack(false, null, track));
//                                        }
//                                    }
//                                    if (item.getTitle().equals("Add to Favourites")) {
//                                        UnifiedTrack ut = new UnifiedTrack(false, null, streamingTrackList.get(position));
//                                        addToFavourites(ut);
//                                    }
//                                    return true;
//                                }
//                            });
//                            popup.show();
                            CustomGeneralBottomSheetDialog generalBottomSheetDialog = new CustomGeneralBottomSheetDialog();
                            generalBottomSheetDialog.setPosition(position);
                            generalBottomSheetDialog.setTrack(new UnifiedTrack(false, null, streamingTrackList.get(position)));
                            generalBottomSheetDialog.setFragment("Stream");
                            generalBottomSheetDialog.show(getSupportFragmentManager(), "general_bottom_sheet_dialog");
                            return true;
                        }

                        @Override
                        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                        }
                    });

                    playerContainer = findViewById(R.id.player_frag_container);

                    if (finalLocalSearchResultList.size() == 0) {
                        localsongsRecyclerView.setVisibility(GONE);
                        localNothingText.setVisibility(View.VISIBLE);
                    } else {
                        localsongsRecyclerView.setVisibility(View.VISIBLE);
                        localNothingText.setVisibility(View.INVISIBLE);
                    }

                    if (recentlyPlayed.getRecentlyPlayed().size() == 0) {
                        recentsRecycler.setVisibility(GONE);
                        recentsNothingText.setVisibility(View.VISIBLE);
                    } else {
                        recentsRecycler.setVisibility(View.VISIBLE);
                        recentsNothingText.setVisibility(View.INVISIBLE);
                    }

                    if (streamingTrackList.size() == 0) {
                        streamRecyclerContainer.setVisibility(GONE);
                        streamNothingText.setVisibility(View.VISIBLE);
                    } else {
                        streamRecyclerContainer.setVisibility(View.VISIBLE);
                        streamNothingText.setVisibility(View.INVISIBLE);
                    }

                    if (allPlaylists.getPlaylists().size() == 0) {
                        playlistsRecycler.setVisibility(GONE);
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

            try {
                String json6 = gson.toJson(queueCurrentIndex);
                prefsEditor.putString("queueCurrentIndex", json6);
            } catch (Exception e) {

            }
            return null;
        }
    }

    public class SaveVersionCode extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                String json7 = gson.toJson(versionCode);
                prefsEditor.putString("versionCode", json7);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class SaveRecents extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            if (!isSaveRecentsRunning) {
                isSaveRecentsRunning = true;
                try {
                    String json4 = gson.toJson(recentlyPlayed);
                    prefsEditor.putString("recentlyPlayed", json4);
                } catch (Exception e) {

                }
                isSaveRecentsRunning = false;
            }
            return null;
        }
    }

    public static class SaveFavourites extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (!isSaveFavouritesRunning) {
                isSaveFavouritesRunning = true;
                try {
                    String json5 = gson.toJson(favouriteTracks);
                    prefsEditor.putString("favouriteTracks", json5);
                } catch (Exception e) {

                }
                isSaveFavouritesRunning = false;
            }
            return null;
        }
    }

    public static class SaveSettings extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (!isSaveSettingsRunning) {
                isSaveSettingsRunning = true;
                try {
                    String json8 = gson.toJson(settings);
                    prefsEditor.putString("settings", json8);
                } catch (Exception e) {

                }
                isSaveSettingsRunning = false;
            }
            return null;
        }
    }

    public static class SaveTheDNAs extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (!isSaveDNAsRunning) {
                isSaveDNAsRunning = true;
                try {
                    String json = gson.toJson(savedDNAs);
                    prefsEditor.putString("savedDNAs", json);
                } catch (Exception e) {

                }
                isSaveDNAsRunning = false;
            }
            return null;
        }
    }

    public static class SaveQueue extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (!isSaveQueueRunning) {
                isSaveQueueRunning = true;
                try {
                    String json3 = gson.toJson(queue);
                    prefsEditor.putString("queue", json3);
                    String json6 = gson.toJson(queueCurrentIndex);
                    prefsEditor.putString("queueCurrentIndex", json6);
                } catch (Exception e) {

                }
                isSaveQueueRunning = false;
            }
            return null;
        }
    }

    public static class SavePlaylists extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (!isSavePLaylistsRunning) {
                isSavePLaylistsRunning = true;
                try {
                    String json2 = gson.toJson(allPlaylists);
                    prefsEditor.putString("allPlaylists", json2);
                } catch (Exception e) {

                }
                isSavePLaylistsRunning = false;
            }
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
                            t1.setVisibility(GONE);
                        }
                    });
            if (!isFullScreenEnabled)
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
                            t1.setVisibility(GONE);
                        }
                    });
            if (!isFullScreenEnabled)
                t2.setVisibility(View.VISIBLE);
            t2.setX(t2.getWidth());
            t2.setAlpha(0.0f);
            t2.animate()
                    .alpha(1.0f)
                    .translationX(0);
        } else {
            t1.setVisibility(GONE);
            t2.setX(0);
            t2.setAlpha(1.0f);
            if (!isFullScreenEnabled)
                t2.setVisibility(View.VISIBLE);
        }
    }

    public void setUpFragmentToolbar(int color, String title) {
        fragmentToolbar.setBackgroundColor(color);
        fragmentToolbarTitle.setText(title);
    }

    public void clearQueue() {
        QueueFragment qFrag = (QueueFragment) fragMan.findFragmentByTag("queue");
        for (int i = 0; i < queue.getQueue().size(); i++) {
            if (i < queueCurrentIndex) {
                queue.getQueue().remove(i);
                queueCurrentIndex--;
                if (qFrag != null) {
                    qFrag.notifyAdapterItemRemoved(i);
                }
                i--;
            } else if (i > queueCurrentIndex) {
                queue.getQueue().remove(i);
                if (qFrag != null) {
                    qFrag.notifyAdapterItemRemoved(i);
                }
                i--;
            }
        }


    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return (result);
    }

    public int getNavBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
//        return (pxToDp(result));
        return (result);
    }

    public boolean hasNavBar(Resources resources) {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }

    public static Pair<String, String> getTime(int millsec) {
        int min, sec;
        sec = millsec / 1000;
        min = sec / 60;
        sec = sec % 60;
        String minS, secS;
        minS = String.valueOf(min);
        secS = String.valueOf(sec);
        if (sec < 10) {
            secS = "0" + secS;
        }
        Pair<String, String> pair = Pair.create(minS, secS);
        return pair;
    }

    public int getDarkColor(int color) {
        int darkColor = 0;

        int r = Math.max(Color.red(color) - 25, 0);
        int g = Math.max(Color.green(color) - 25, 0);
        int b = Math.max(Color.blue(color) - 25, 0);

        darkColor = Color.rgb(r, g, b);

        return darkColor;
    }

    public void bottomSheetListener(int position, String action, String fragment, boolean type) {

        UnifiedTrack ut = null;
        if (fragment == null) {
            ut = new UnifiedTrack(true, finalLocalSearchResultList.get(position), null);
        } else if (fragment.equals("Artist")) {
            ut = new UnifiedTrack(true, tempArtist.getArtistSongs().get(position), null);
        } else if (fragment.equals("Album")) {
            ut = new UnifiedTrack(true, tempAlbum.getAlbumSongs().get(position), null);
        } else if (fragment.equals("Folder")) {
            ut = new UnifiedTrack(true, tempMusicFolder.getLocalTracks().get(position), null);
        } else if (fragment.equals("Recents")) {
            ut = recentlyPlayed.getRecentlyPlayed().get(position);
        } else if (fragment.equals("RecentHorizontalList")) {
            ut = continuePlayingList.get(position);
        } else if (fragment.equals("Stream")) {
            ut = new UnifiedTrack(false, null, streamingTrackList.get(position));
        }

        if (action.equals("Add to Playlist")) {
            showAddToPlaylistDialog(ut);
            pAdapter.notifyDataSetChanged();
        }
        if (action.equals("Add to Queue")) {
            queue.getQueue().add(ut);
        }
        if (action.equals("Play")) {
            if (queue.getQueue().size() == 0) {
                queueCurrentIndex = 0;
                queue.getQueue().add(ut);
            } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                queueCurrentIndex++;
                queue.getQueue().add(ut);
            } else if (isReloaded) {
                isReloaded = false;
                queueCurrentIndex = queue.getQueue().size();
                queue.getQueue().add(ut);
            } else {
                queue.getQueue().add(++queueCurrentIndex, ut);
            }
            streamSelected = !type;
            localSelected = type;
            queueCall = false;
            isReloaded = false;
            if (type) {
                localSelectedTrack = ut.getLocalTrack();
                onLocalTrackSelected(position);
            } else {
                selectedTrack = ut.getStreamTrack();
                onTrackSelected(position);
            }
        }
        if (action.equals("Play Next")) {
            if (queue.getQueue().size() == 0) {
                queueCurrentIndex = 0;
                queue.getQueue().add(ut);
                streamSelected = !type;
                localSelected = type;
                queueCall = false;
                isReloaded = false;
                if (type) {
                    localSelectedTrack = ut.getLocalTrack();
                    onLocalTrackSelected(position);
                } else {
                    selectedTrack = ut.getStreamTrack();
                    onTrackSelected(position);
                }
            } else if (queueCurrentIndex == queue.getQueue().size() - 1) {
                queue.getQueue().add(ut);
            } else if (isReloaded) {
                isReloaded = false;
                queueCurrentIndex = queue.getQueue().size();
                queue.getQueue().add(ut);
                streamSelected = !type;
                localSelected = type;
                queueCall = false;
                isReloaded = false;
                if (type) {
                    localSelectedTrack = ut.getLocalTrack();
                    onLocalTrackSelected(position);
                } else {
                    selectedTrack = ut.getStreamTrack();
                    onTrackSelected(position);
                }
            } else {
                queue.getQueue().add(queueCurrentIndex + 1, ut);
            }
        }
        if (action.equals("Add to Favourites")) {
            addToFavourites(ut);
        }
        if (action.equals("Share")) {
            shareLocalSong(ut.getLocalTrack().getPath());
        }
        if (action.equals("Edit")) {
            editSong = ut.getLocalTrack();
            showFragment("Edit");
        }
    }

    public void refreshAlbumAndArtists() {

        albums.clear();
        finalAlbums.clear();
        artists.clear();
        finalArtists.clear();

        for (int i = 0; i < localTrackList.size(); i++) {
            LocalTrack lt = localTrackList.get(i);

            String thisAlbum = lt.getAlbum();

            int pos = checkAlbum(thisAlbum);

            if (pos != -1) {
                albums.get(pos).getAlbumSongs().add(lt);
            } else {
                List<LocalTrack> llt = new ArrayList<>();
                llt.add(lt);
                Album ab = new Album(thisAlbum, llt);
                albums.add(ab);
            }

            if (pos != -1) {
                finalAlbums.get(pos).getAlbumSongs().add(lt);
            } else {
                List<LocalTrack> llt = new ArrayList<>();
                llt.add(lt);
                Album ab = new Album(thisAlbum, llt);
                finalAlbums.add(ab);
            }

            String thisArtist = lt.getArtist();

            pos = checkArtist(thisArtist);

            if (pos != -1) {
                artists.get(pos).getArtistSongs().add(lt);
            } else {
                List<LocalTrack> llt = new ArrayList<>();
                llt.add(lt);
                Artist ab = new Artist(thisArtist, llt);
                artists.add(ab);
            }

            if (pos != -1) {
                finalArtists.get(pos).getArtistSongs().add(lt);
            } else {
                List<LocalTrack> llt = new ArrayList<>();
                llt.add(lt);
                Artist ab = new Artist(thisArtist, llt);
                finalArtists.add(ab);
            }

        }

        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        try {
            if (localTrackList.size() > 0) {
                Collections.sort(localTrackList, new localMusicComparator());
                Collections.sort(finalLocalSearchResultList, new localMusicComparator());
            }
            if (albums.size() > 0) {
                Collections.sort(albums, new albumComparator());
                Collections.sort(finalAlbums, new albumComparator());
            }
            if (artists.size() > 0) {
                Collections.sort(artists, new artistComparator());
                Collections.sort(finalArtists, new artistComparator());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateMediaCache(String title, String artist, String album, long id) {

        ContentResolver musicResolver = this.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        ContentValues newValues = new ContentValues();
        newValues.put(android.provider.MediaStore.Audio.Media.TITLE, title);
        newValues.put(android.provider.MediaStore.Audio.Media.ARTIST, artist);
        newValues.put(android.provider.MediaStore.Audio.Media.ALBUM, album);

        int res = musicResolver.update(musicUri, newValues, android.provider.MediaStore.Audio.Media._ID + "=?", new String[]{String.valueOf(id)});

        if (res > 0) {
//            Toast.makeText(this, "Updated MediaStore cache", Toast.LENGTH_SHORT).show();
        }

    }

    public void showSleepDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sleep_timer_dialog);

        final WheelView wheelPicker = (WheelView) dialog.findViewById(R.id.wheelPicker);
        wheelPicker.setItems(minuteList);

        Button setBtn = (Button) dialog.findViewById(R.id.set_button);
        Button cancelBtn = (Button) dialog.findViewById(R.id.cancel_button);
        final Button removerBtn = (Button) dialog.findViewById(R.id.remove_timer_button);

        final LinearLayout buttonWrapper = (LinearLayout) dialog.findViewById(R.id.button_wrapper);

        final TextView timerSetText = (TextView) dialog.findViewById(R.id.timer_set_text);

        setBtn.setBackgroundColor(themeColor);
        removerBtn.setBackgroundColor(themeColor);
        cancelBtn.setBackgroundColor(Color.WHITE);

        if (isSleepTimerEnabled) {
            wheelPicker.setVisibility(View.GONE);
            buttonWrapper.setVisibility(View.GONE);
            removerBtn.setVisibility(View.VISIBLE);
            timerSetText.setVisibility(View.VISIBLE);

            long currentTime = System.currentTimeMillis();
            long difference = currentTime - timerSetTime;

            int minutesLeft = (int) (timerTimeOutDuration - ((difference / 1000) / 60));
            if (minutesLeft > 1) {
                timerSetText.setText("Timer set for " + minutesLeft + " minutes from now.");
            } else if (minutesLeft == 1) {
                timerSetText.setText("Timer set for " + 1 + " minute from now.");
            } else {
                timerSetText.setText("Music will stop after completion of current song");
            }

        } else {
            wheelPicker.setVisibility(View.VISIBLE);
            buttonWrapper.setVisibility(View.VISIBLE);
            removerBtn.setVisibility(View.GONE);
            timerSetText.setVisibility(View.GONE);
        }

        removerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSleepTimerEnabled = false;
                isSleepTimerTimeout = false;
                timerTimeOutDuration = 0;
                timerSetTime = 0;
                sleepHandler.removeCallbacksAndMessages(null);
                Toast.makeText(ctx, "Timer removed", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSleepTimerEnabled = true;
                int minutes = Integer.parseInt(wheelPicker.getItems().get(wheelPicker.getSelectedPosition()));
                timerTimeOutDuration = minutes;
                timerSetTime = System.currentTimeMillis();
                sleepHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isSleepTimerTimeout = true;
                        if (playerFragment.mMediaPlayer == null || !playerFragment.mMediaPlayer.isPlaying()) {
                            main.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ctx, "Sleep timer timed out, closing app", Toast.LENGTH_SHORT).show();
                                    if (playerFragment != null && playerFragment.t != null)
                                        playerFragment.t.cancel();
                                    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    try {
                                        notificationManager.cancel(1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                }, minutes * 60 * 1000);
                Toast.makeText(ctx, "Timer set for " + minutes + " minutes", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSleepTimerEnabled = false;
                isSleepTimerTimeout = false;
                dialog.dismiss();
            }
        });

        dialog.show();

    }

}
