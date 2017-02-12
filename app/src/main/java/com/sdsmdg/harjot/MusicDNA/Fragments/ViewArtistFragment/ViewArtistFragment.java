package com.sdsmdg.harjot.MusicDNA.fragments.ViewArtistFragment;


import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.clickitemtouchlistener.ClickItemTouchListener;
import com.sdsmdg.harjot.MusicDNA.custombottomsheets.CustomLocalBottomSheetDialog;
import com.sdsmdg.harjot.MusicDNA.fragments.LocalMusicFragments.LocalTrackRecyclerAdapter;
import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.models.UnifiedTrack;
import com.sdsmdg.harjot.MusicDNA.MusicDNAApplication;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.activities.SplashActivity;
import com.sdsmdg.harjot.MusicDNA.utilities.CommonUtils;
import com.sdsmdg.harjot.MusicDNA.imageloader.ImageLoader;
import com.squareup.leakcanary.RefWatcher;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewArtistFragment extends Fragment {

    RecyclerView rv;
    LocalTrackRecyclerAdapter lAdapter;

    artistCallbackListener mCallback;

    FloatingActionButton playAllfab;
    Context ctx;

    HomeActivity activity;

    TextView title;

    View bottomMarginLayout;

    ImageLoader imgLoader;

    ImageView backBtn, backdrop, addToQueueIcon;
    TextView fragTitle, artistTitle, artistTrackText;

    public ViewArtistFragment() {
        // Required empty public constructor
    }

    public interface artistCallbackListener {
        void onArtistSongClick();

        void onArtistPlayAll();

        void addArtistToQueue();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = context;
        activity = (HomeActivity) context;
        try {
            imgLoader = new ImageLoader(context);
            mCallback = (artistCallbackListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_artist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backBtn = (ImageView) view.findViewById(R.id.view_artist_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        backdrop = (ImageView) view.findViewById(R.id.artist_backdrop);
        imgLoader.DisplayImage(HomeActivity.tempArtist.getArtistSongs().get(0).getPath(), backdrop);

        fragTitle = (TextView) view.findViewById(R.id.artist_fragment_title);
        if (SplashActivity.tf4 != null)
            fragTitle.setTypeface(SplashActivity.tf4);

        artistTitle = (TextView) view.findViewById(R.id.artist_title);
        artistTitle.setText(HomeActivity.tempArtist.getName());

        artistTrackText = (TextView) view.findViewById(R.id.artist_tracks_text);
        int tmp = HomeActivity.tempArtist.getArtistSongs().size();
        String details1;
        if (tmp == 1) {
            details1 = "1 Song ";
        } else {
            details1 = tmp + " Songs ";
        }
        artistTrackText.setText(details1);

        addToQueueIcon = (ImageView) view.findViewById(R.id.add_artist_to_queue_icon);
        addToQueueIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.addArtistToQueue();
            }
        });

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = CommonUtils.dpTopx(65, getContext());

        rv = (RecyclerView) view.findViewById(R.id.artist_songs_recycler);
        lAdapter = new LocalTrackRecyclerAdapter(HomeActivity.tempArtist.getArtistSongs(), getContext());
        LinearLayoutManager llManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(lAdapter);

        rv.addOnItemTouchListener(new ClickItemTouchListener(rv) {
            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                LocalTrack track = HomeActivity.tempArtist.getArtistSongs().get(position);
                if (HomeActivity.queue.getQueue().size() == 0) {
                    HomeActivity.queueCurrentIndex = 0;
                    HomeActivity.queue.getQueue().add(new UnifiedTrack(true, track, null));
                } else if (HomeActivity.queueCurrentIndex == HomeActivity.queue.getQueue().size() - 1) {
                    HomeActivity.queueCurrentIndex++;
                    HomeActivity.queue.getQueue().add(new UnifiedTrack(true, track, null));
                } else if (HomeActivity.isReloaded) {
                    HomeActivity.isReloaded = false;
                    HomeActivity.queueCurrentIndex = HomeActivity.queue.getQueue().size();
                    HomeActivity.queue.getQueue().add(new UnifiedTrack(true, track, null));
                } else {
                    HomeActivity.queue.getQueue().add(++HomeActivity.queueCurrentIndex, new UnifiedTrack(true, track, null));
                }
                HomeActivity.localSelectedTrack = track;
                HomeActivity.streamSelected = false;
                HomeActivity.localSelected = true;
                HomeActivity.queueCall = false;
                HomeActivity.isReloaded = false;
                mCallback.onArtistSongClick();
                return true;
            }

            @Override
            public boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                CustomLocalBottomSheetDialog localBottomSheetDialog = new CustomLocalBottomSheetDialog();
                localBottomSheetDialog.setPosition(position);
                localBottomSheetDialog.setLocalTrack(activity.tempArtist.getArtistSongs().get(position));
                localBottomSheetDialog.setFragment("Artist");
                localBottomSheetDialog.show(activity.getSupportFragmentManager(), "local_song_bottom_sheet");
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        playAllfab = (FloatingActionButton) view.findViewById(R.id.play_all_fab_artist);
        playAllfab.setBackgroundTintList(ColorStateList.valueOf(HomeActivity.themeColor));
        playAllfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.queue.getQueue().clear();
                for (int i = 0; i < HomeActivity.tempArtist.getArtistSongs().size(); i++) {
                    UnifiedTrack ut = new UnifiedTrack(true, HomeActivity.tempArtist.getArtistSongs().get(i), null);
                    HomeActivity.queue.getQueue().add(ut);
                }
                mCallback.onArtistPlayAll();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                playAllfab.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new OvershootInterpolator());
            }
        }, 500);
    }

    public void updateData() {
        if (lAdapter != null) {
            lAdapter.notifyDataSetChanged();
        }
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
