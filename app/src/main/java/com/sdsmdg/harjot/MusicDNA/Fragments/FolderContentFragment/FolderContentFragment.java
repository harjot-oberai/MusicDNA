package com.sdsmdg.harjot.MusicDNA.fragments.FolderContentFragment;


import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.design.widget.FloatingActionButton;
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
import com.sdsmdg.harjot.MusicDNA.custombottomsheets.CustomGeneralBottomSheetDialog;
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
public class FolderContentFragment extends Fragment {

    RecyclerView folderContentRecycler;
    LocalTrackRecyclerAdapter fContentAdapter;
    LinearLayoutManager mLayoutManager2;

    FloatingActionButton playAllFAB;

    ImageLoader imgLoader;

    ImageView backBtn, addToQueueIcon, backdrop;
    TextView title, songsText, fragmentTitle;

    View bottomMarginLayout;

    folderCallbackListener mCallback;

    public FolderContentFragment() {
        // Required empty public constructor
    }

    public interface folderCallbackListener {
        void onFolderContentPlayAll();

        void onFolderContentItemClick(int position);

        void folderAddToQueue();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        imgLoader = new ImageLoader(context);
        try {
            mCallback = (folderCallbackListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_folder_content, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = CommonUtils.dpTopx(65, getContext());

        fragmentTitle = (TextView) view.findViewById(R.id.folder_fragment_title);
        if (SplashActivity.tf4 != null)
            fragmentTitle.setTypeface(SplashActivity.tf4);

        title = (TextView) view.findViewById(R.id.folder_title);
        title.setText(HomeActivity.tempMusicFolder.getFolderName());

        songsText = (TextView) view.findViewById(R.id.folder_tracks_text);
        String s = "";
        if (HomeActivity.tempMusicFolder.getLocalTracks().size() > 1)
            s = "Songs";
        else
            s = "Song";
        songsText.setText(HomeActivity.tempMusicFolder.getLocalTracks().size() + " " + s);

        backBtn = (ImageView) view.findViewById(R.id.folder_content_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        addToQueueIcon = (ImageView) view.findViewById(R.id.add_folder_to_queue_icon);
        addToQueueIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.folderAddToQueue();
            }
        });

        backdrop = (ImageView) view.findViewById(R.id.folder_backdrop);
        LocalTrack lt = HomeActivity.tempFolderContent.get(0);
        imgLoader.DisplayImage(lt.getPath(), backdrop);

        folderContentRecycler = (RecyclerView) view.findViewById(R.id.folder_content_recycler);
        fContentAdapter = new LocalTrackRecyclerAdapter(HomeActivity.tempFolderContent, getContext());
        mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        folderContentRecycler.setLayoutManager(mLayoutManager2);
        folderContentRecycler.setItemAnimator(new DefaultItemAnimator());
        folderContentRecycler.setAdapter(fContentAdapter);

        folderContentRecycler.addOnItemTouchListener(new ClickItemTouchListener(folderContentRecycler) {
            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                LocalTrack track = HomeActivity.tempMusicFolder.getLocalTracks().get(position);
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
                mCallback.onFolderContentItemClick(position);
                return true;
            }

            @Override
            public boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                UnifiedTrack ut = new UnifiedTrack(true, HomeActivity.tempMusicFolder.getLocalTracks().get(position), null);
                CustomGeneralBottomSheetDialog generalBottomSheetDialog = new CustomGeneralBottomSheetDialog();
                generalBottomSheetDialog.setPosition(position);
                generalBottomSheetDialog.setTrack(ut);
                generalBottomSheetDialog.setFragment("Folder");
                generalBottomSheetDialog.show(getActivity().getSupportFragmentManager(), "general_bottom_sheet_dialog");
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        playAllFAB = (FloatingActionButton) view.findViewById(R.id.folder_play_all_fab);
        playAllFAB.setBackgroundTintList(ColorStateList.valueOf(HomeActivity.themeColor));
        playAllFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFolderContentPlayAll();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mLayoutManager2.scrollToPositionWithOffset(0, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                playAllFAB.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new OvershootInterpolator());
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
