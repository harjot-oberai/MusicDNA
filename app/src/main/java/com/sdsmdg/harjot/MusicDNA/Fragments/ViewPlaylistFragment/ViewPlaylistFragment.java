package com.sdsmdg.harjot.MusicDNA.fragments.ViewPlaylistFragment;


import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.clickitemtouchlistener.ClickItemTouchListener;
import com.sdsmdg.harjot.MusicDNA.itemtouchhelpers.SimpleItemTouchHelperCallback;
import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.models.Track;
import com.sdsmdg.harjot.MusicDNA.models.UnifiedTrack;
import com.sdsmdg.harjot.MusicDNA.MusicDNAApplication;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.activities.SplashActivity;
import com.sdsmdg.harjot.MusicDNA.utilities.CommonUtils;
import com.sdsmdg.harjot.MusicDNA.imageloader.ImageLoader;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPlaylistFragment extends Fragment implements
        PlaylistTrackAdapter.OnDragStartListener,
        PlaylistTrackAdapter.OnMoveRemoveListener {

    RecyclerView playlistRecyler;
    PlaylistTrackAdapter plAdapter;
    FloatingActionButton playAll;

    ImageView backdrop, backBtn, renameIcon, addToQueueIcon;
    TextView title, songsText, fragmentTitle;

    ImageLoader imgLoader;

    ItemTouchHelper mItemTouchHelper;

    LinearLayoutManager mLayoutManager2;

    View bottomMarginLayout;

    playlistCallbackListener mCallback;

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void updateViewPlaylistFragment() {

        title.setText(HomeActivity.tempPlaylist.getPlaylistName());

        String s = "";
        if (HomeActivity.tempPlaylist.getSongList().size() > 1)
            s = "Songs";
        else
            s = "Song";
        songsText.setText(HomeActivity.tempPlaylist.getSongList().size() + " " + s);

        UnifiedTrack ut = HomeActivity.tempPlaylist.getSongList().get(0);
        if (ut.getType()) {
            LocalTrack lt = ut.getLocalTrack();
            imgLoader.DisplayImage(lt.getPath(), backdrop);
        } else {
            Track t = ut.getStreamTrack();
            Picasso.with(getContext())
                    .load(t.getArtworkURL())
                    .resize(100, 100)
                    .error(R.drawable.ic_default)
                    .placeholder(R.drawable.ic_default)
                    .into(backdrop);
        }

    }

    public interface playlistCallbackListener {
        void onPlaylistPlayAll();

        void onPlaylistItemClicked(int position);

        void playlistRename();

        void playlistAddToQueue();
    }

    public ViewPlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        imgLoader = new ImageLoader(context);
        try {
            mCallback = (playlistCallbackListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_playlist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = CommonUtils.dpTopx(65, getContext());

        fragmentTitle = (TextView) view.findViewById(R.id.playlist_fragment_title);
        if (SplashActivity.tf4 != null)
            fragmentTitle.setTypeface(SplashActivity.tf4);

        title = (TextView) view.findViewById(R.id.playlist_title);
        title.setText(HomeActivity.tempPlaylist.getPlaylistName());

        songsText = (TextView) view.findViewById(R.id.playlist_tracks_text);
        String s = "";
        if (HomeActivity.tempPlaylist.getSongList().size() > 1)
            s = "Songs";
        else
            s = "Song";
        songsText.setText(HomeActivity.tempPlaylist.getSongList().size() + " " + s);

        backBtn = (ImageView) view.findViewById(R.id.view_playlist_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        renameIcon = (ImageView) view.findViewById(R.id.rename_playlist_icon);
        renameIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.playlistRename();
            }
        });

        addToQueueIcon = (ImageView) view.findViewById(R.id.add_playlist_to_queue_icon);
        addToQueueIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.playlistAddToQueue();
            }
        });

        backdrop = (ImageView) view.findViewById(R.id.playlist_backdrop);
        UnifiedTrack ut = HomeActivity.tempPlaylist.getSongList().get(0);
        if (ut.getType()) {
            LocalTrack lt = ut.getLocalTrack();
            imgLoader.DisplayImage(lt.getPath(), backdrop);
        } else {
            Track t = ut.getStreamTrack();
            Picasso.with(getContext())
                    .load(t.getArtworkURL())
                    .resize(100, 100)
                    .error(R.drawable.ic_default)
                    .placeholder(R.drawable.ic_default)
                    .into(backdrop);
        }

        playlistRecyler = (RecyclerView) view.findViewById(R.id.view_playlist_recycler);
        plAdapter = new PlaylistTrackAdapter(HomeActivity.tempPlaylist.getSongList(), this, getContext());
        mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        playlistRecyler.setLayoutManager(mLayoutManager2);
        playlistRecyler.setItemAnimator(new DefaultItemAnimator());
        playlistRecyler.setAdapter(plAdapter);

        playlistRecyler.addOnItemTouchListener(new ClickItemTouchListener(playlistRecyler) {
            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                mCallback.onPlaylistItemClicked(position);
                return true;
            }

            @Override
            public boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        playAll = (FloatingActionButton) view.findViewById(R.id.play_all_fab);
        if (HomeActivity.tempPlaylist.getSongList().size() == 0) {
            playAll.setVisibility(View.GONE);
        }
        playAll.setBackgroundTintList(ColorStateList.valueOf(HomeActivity.themeColor));
        playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = HomeActivity.tempPlaylist.getSongList().size();
                HomeActivity.queue.getQueue().clear();
                for (int i = 0; i < size; i++) {
                    HomeActivity.queue.addToQueue(HomeActivity.tempPlaylist.getSongList().get(i));
                }
                HomeActivity.queueCurrentIndex = 0;
                mCallback.onPlaylistPlayAll();
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(plAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(playlistRecyler);

    }

    @Override
    public void onResume() {
        super.onResume();
        mLayoutManager2.scrollToPositionWithOffset(0, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                playAll.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new OvershootInterpolator());
            }
        }, 500);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }

}
