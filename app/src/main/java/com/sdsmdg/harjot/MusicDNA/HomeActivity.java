package com.sdsmdg.harjot.MusicDNA;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.harjot.MusicDNA.Interfaces.StreamService;
import com.sdsmdg.harjot.MusicDNA.Models.AllPlaylists;
import com.sdsmdg.harjot.MusicDNA.Models.Favourite;
import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.Models.Playlist;
import com.sdsmdg.harjot.MusicDNA.Models.Queue;
import com.sdsmdg.harjot.MusicDNA.Models.RecentlyPlayed;
import com.sdsmdg.harjot.MusicDNA.Models.Track;
import com.sdsmdg.harjot.MusicDNA.Models.UnifiedTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bz.tsung.android.objectify.NoSuchPreferenceFoundException;
import bz.tsung.android.objectify.ObjectPreferenceLoader;
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
        ViewPlaylistFragment.onPlaylistPlayAllListener {

    public static List<LocalTrack> localTrackList = new ArrayList<>();
    public static List<LocalTrack> finalLocalSearchResultList = new ArrayList<>();
    public static List<Track> streamingTrackList = new ArrayList<>();

    RecentlyPlayed recentlyPlayed;
    static Favourite favouriteTracks;
    static Queue queue;
    static Playlist tempPlaylist;
    static AllPlaylists allPlaylists;

    static boolean repeatEnabled = false;
    static boolean shuffleEnabled = false;

    static boolean isFavourite = false;

    static boolean isReloaded = false;

    public static int queueCurrentIndex = 0;

    static Context ctx;

    static boolean queueCall = false;

    DrawerLayout drawer;

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

    int max_x = Integer.MIN_VALUE, min_x = Integer.MAX_VALUE;

    static Toolbar toolbar;

    static Activity main;

    static float seekBarColor;

    static byte[] mBytes;

    View playerContainer;

    static android.app.FragmentManager fragMan;

    Animation slideUp, slideDown;

    static boolean isPlayerVisible = false;
    static boolean isLocalVisible = false;
    static boolean isStreamVisible = false;
    static boolean isQueueVisible = false;
    static boolean isPlaylistVisible = false;
    static boolean isEqualizerVisible = false;
    static boolean isFavouriteVisible = false;

    static LocalTrack localSelectedTrack;
    static Track selectedTrack;

    static boolean localSelected = false;
    static boolean streamSelected = false;

    public void onTrackSelected(int position) {

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
                        .commit();
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
            boolean isRepeat = false;
            for (int i = 0; i < recentlyPlayed.getRecentlyPlayed().size(); i++) {
                if (!recentlyPlayed.getRecentlyPlayed().get(i).getType() && recentlyPlayed.getRecentlyPlayed().get(i).getStreamTrack().getTitle().equals(track.getStreamTrack().getTitle())) {
                    isRepeat = true;
                }
            }
            if (!isRepeat) {
                recentlyPlayed.addSong(track);
                recentsRecycler.setVisibility(View.VISIBLE);
                recentsNothingText.setVisibility(View.INVISIBLE);
                rAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onLocalTrackSelected(int position) {

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
                        .commit();
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
            boolean isRepeat = false;
            for (int i = 0; i < recentlyPlayed.getRecentlyPlayed().size(); i++) {
                if (recentlyPlayed.getRecentlyPlayed().get(i).getType() && recentlyPlayed.getRecentlyPlayed().get(i).getLocalTrack().getTitle().equals(track.getLocalTrack().getTitle())) {
                    isRepeat = true;
                }
            }
            if (!isRepeat) {
                recentlyPlayed.addSong(track);
                recentsRecycler.setVisibility(View.VISIBLE);
                recentsNothingText.setVisibility(View.INVISIBLE);
                rAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ctx = this;

        setContentView(R.layout.activity_home);

        fragMan = getFragmentManager();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setShowHideAnimationEnabled(true);

        VisualizerView.act = this;

        main = this;

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screen_width = display.getWidth();
        screen_height = display.getHeight();

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
            allPlaylists = new ObjectPreferenceLoader(ctx, "AllPlayLists", AllPlaylists.class).load();
            queue = new ObjectPreferenceLoader(ctx, "Queue", Queue.class).load();
            favouriteTracks = new ObjectPreferenceLoader(ctx, "Favourites", Favourite.class).load();
            recentlyPlayed = new ObjectPreferenceLoader(ctx, "RecentlyPlayed", RecentlyPlayed.class).load();
            isReloaded = new ObjectPreferenceLoader(ctx, "isReloaded", Boolean.class).load();
        } catch (NoSuchPreferenceFoundException e) {
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


        slideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        slideUp.setFillAfter(true);

        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        slideDown.setFillAfter(true);

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
//                queue.setQueue(tempPlaylist.getSongList());
                showFragment("playlist");
//                queueCurrentIndex = 0;
//                onQueueItemClicked(0);
                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
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
            streamingListView.setVisibility(View.GONE);
            streamNothingText.setVisibility(View.VISIBLE);
        } else {
            streamingListView.setVisibility(View.VISIBLE);
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
//        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%Music%"}, null);
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
        if (id == R.id.action_queue) {
            showFragment("queue");
        }
        if (id == R.id.action_equalizer) {
            showFragment("equalizer");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_stream) {
            // Handle the camera action
        } else if (id == R.id.nav_local) {

        } else if (id == R.id.nav_playlists) {

        } else if (id == R.id.nav_fav) {
            showFragment("favourite");
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
                        // request successful (status code 200, 201)
                        Log.d("RETRO", response.body() + "");
                        streamingTrackList = response.body();
                        sAdapter = new StreamTracksHorizontalAdapter(streamingTrackList, ctx);
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                        streamingListView.setLayoutManager(mLayoutManager);
                        streamingListView.setItemAnimator(new DefaultItemAnimator());
                        streamingListView.setAdapter(sAdapter);

                        if (streamingTrackList.size() == 0) {
                            streamingListView.setVisibility(View.GONE);
                        } else {
                            streamingListView.setVisibility(View.VISIBLE);
                        }

                        stopLoadingIndicator();

                        (streamingListView.getAdapter()).notifyDataSetChanged();
                        if ((StreamMusicFragment.lv) != null)
                            (StreamMusicFragment.lv.getAdapter()).notifyDataSetChanged();
                    } else {
                        //request not successful (like 400,401,403 etc)
                        //Handle errors
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
//        getSupportActionBar().hide();
    }

    public void showTabs() {
//        toolbar.setVisibility(View.VISIBLE);
//        toolbar.setAlpha(1.0f);
//
//        toolbar.animate()
//                .translationY(0)
//                .alpha(1.0f);
//        getSupportActionBar().show();
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
            if (PlayerFragment.mVisualizerView.volume < 0.83) {
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


            float ratio = (float) screen_height / (float) 1920;
            float ratio2 = (float) screen_width / (float) 1080;
            ratio = Math.min(ratio, ratio2);

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
            hsv[2] = (float) 0.5;

            // setting color of the Paint
            PlayerFragment.mVisualizerView.mForePaint.setColor(Color.HSVToColor(hsv));

            if (PlayerFragment.mVisualizerView.size >= 10.0 && PlayerFragment.mVisualizerView.size < 29.0) {
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

    @Override
    public void onComplete() {
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
        new ObjectPreferenceLoader(ctx, "AllPlayLists", AllPlaylists.class).save(allPlaylists);
        new ObjectPreferenceLoader(ctx, "Queue", Queue.class).save(queue);
        new ObjectPreferenceLoader(ctx, "RecentlyPlayed", RecentlyPlayed.class).save(recentlyPlayed);
        new ObjectPreferenceLoader(ctx, "Favourites", Favourite.class).save(favouriteTracks);
        new ObjectPreferenceLoader(ctx, "queueCurrentIndex", Integer.class).save(queueCurrentIndex);
        new ObjectPreferenceLoader(ctx, "isReloaded", Boolean.class).save(true);
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
                    .commit();
        } else if (type.equals("queue") && !isQueueVisible) {
            setTitle("Queue");
            isQueueVisible = true;
            android.app.FragmentManager fm = getFragmentManager();
            QueueFragment newFragment = new QueueFragment();
            QueueFragment.mCallback = this;
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.fragContainer, newFragment, "queue")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commit();
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
                    .commit();
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
                    .commit();
        } else if (type.equals("equalizer") && !isEqualizerVisible) {
            setTitle("Equalizer");
            isEqualizerVisible = true;
            android.app.FragmentManager fm = getFragmentManager();
            EqualizerFragment newFragment = new EqualizerFragment();
//            QueueFragment.mCallback = this;
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.fragContainer, newFragment, "equalizer")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (type.equals("favourite") && !isFavouriteVisible) {
            setTitle("Favourites");
            isFavouriteVisible = true;
            android.app.FragmentManager fm = getFragmentManager();
            FavouritesFragment newFragment = new FavouritesFragment();
            FavouritesFragment.mCallback = this;
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.fragContainer, newFragment, "favourite")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commit();
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
                        .commit();
            }
        } else if (type.equals("queue")) {
            isQueueVisible = false;
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("queue");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commit();
            }
        } else if (type.equals("stream")) {
            isStreamVisible = false;
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("stream");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commit();
            }
        } else if (type.equals("playlist")) {
            isPlaylistVisible = false;
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("playlist");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commit();
            }
        } else if (type.equals("equalizer")) {
            isEqualizerVisible = false;
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("equalizer");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commit();
            }
        } else if (type.equals("favourite")) {
            isFavouriteVisible = false;
            android.app.FragmentManager fm = getFragmentManager();
            android.app.Fragment frag = fm.findFragmentByTag("favourite");
            if (frag != null) {
                fm.beginTransaction()
                        .remove(frag)
                        .commit();
            }
        }
    }

}
