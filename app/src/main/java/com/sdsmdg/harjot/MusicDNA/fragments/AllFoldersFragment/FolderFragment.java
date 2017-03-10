package com.sdsmdg.harjot.MusicDNA.fragments.AllFoldersFragment;


import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.clickitemtouchlistener.ClickItemTouchListener;
import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.models.MusicFolder;
import com.sdsmdg.harjot.MusicDNA.MusicDNAApplication;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.activities.SplashActivity;
import com.sdsmdg.harjot.MusicDNA.utilities.CommonUtils;
import com.sdsmdg.harjot.MusicDNA.imageloader.ImageLoader;
import com.squareup.leakcanary.RefWatcher;


/**
 * A simple {@link Fragment} subclass.
 */
public class FolderFragment extends Fragment {

    RecyclerView allFoldersRecycler;
    FolderRecyclerAdapter mfAdapter;
    LinearLayoutManager mLayoutManager2;

    onFolderClickedListener mCallback;

    View bottomMarginLayout;

    ImageView backBtn;
    TextView fragmentTitle;
    ImageView[] imgView = new ImageView[10];

    ImageView fragIcon;

    ImageLoader imgLoader;

    public FolderFragment() {
        // Required empty public constructor
    }

    public interface onFolderClickedListener {
        void onFolderClicked(int pos);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            imgLoader = new ImageLoader(context);
            imgLoader.type = "folder";
            mCallback = (onFolderClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_folder, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backBtn = (ImageView) view.findViewById(R.id.all_folder_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        fragmentTitle = (TextView) view.findViewById(R.id.all_folder_fragment_title);
        if (SplashActivity.tf4 != null)
            fragmentTitle.setTypeface(SplashActivity.tf4);

        fragIcon = (ImageView) view.findViewById(R.id.all_folder_frag_icon);
        fragIcon.setImageTintList(ColorStateList.valueOf(HomeActivity.themeColor));

        initializeHeaderImages(view);

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = CommonUtils.dpTopx(65, getContext());

        allFoldersRecycler = (RecyclerView) view.findViewById(R.id.all_folders_recycler);
        mfAdapter = new FolderRecyclerAdapter(HomeActivity.allMusicFolders.getMusicFolders(), getContext());
        mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        allFoldersRecycler.setLayoutManager(mLayoutManager2);
        allFoldersRecycler.setItemAnimator(new DefaultItemAnimator());
        allFoldersRecycler.setAdapter(mfAdapter);

        allFoldersRecycler.addOnItemTouchListener(new ClickItemTouchListener(allFoldersRecycler) {
            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                mCallback.onFolderClicked(position);
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

    }

    @Override
    public void onResume() {
        super.onResume();
        mLayoutManager2.scrollToPositionWithOffset(0, 0);
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

    public void initializeHeaderImages(View v) {

        imgView[0] = (ImageView) v.findViewById(R.id.all_folder_img_1);
        imgView[1] = (ImageView) v.findViewById(R.id.all_folder_img_2);
        imgView[2] = (ImageView) v.findViewById(R.id.all_folder_img_3);
        imgView[3] = (ImageView) v.findViewById(R.id.all_folder_img_4);
        imgView[4] = (ImageView) v.findViewById(R.id.all_folder_img_5);
        imgView[5] = (ImageView) v.findViewById(R.id.all_folder_img_6);
        imgView[6] = (ImageView) v.findViewById(R.id.all_folder_img_7);
        imgView[7] = (ImageView) v.findViewById(R.id.all_folder_img_8);
        imgView[8] = (ImageView) v.findViewById(R.id.all_folder_img_9);
        imgView[9] = (ImageView) v.findViewById(R.id.all_folder_img_10);

        int numFolders = HomeActivity.allMusicFolders.getMusicFolders().size();
        MusicFolder mf1, mf2;
        if (numFolders == 0) {
            for (int i = 0; i < 10; i++) {
                imgLoader.DisplayImage("folder" + i, imgView[i]);
            }
        } else if (numFolders == 1) {
            mf1 = HomeActivity.allMusicFolders.getMusicFolders().get(0);
            for (int i = 0; i < Math.min(10, mf1.getLocalTracks().size()); i++) {
                imgLoader.DisplayImage(mf1.getLocalTracks().get(i).getPath(), imgView[i]);
            }
            if (mf1.getLocalTracks().size() < 10) {
                for (int i = mf1.getLocalTracks().size(); i < 10; i++) {
                    imgLoader.DisplayImage("folder" + i, imgView[i]);
                }
            }
        } else {
            mf1 = HomeActivity.allMusicFolders.getMusicFolders().get(0);
            mf2 = HomeActivity.allMusicFolders.getMusicFolders().get(1);
            for (int i = 0; i < Math.min(10, mf1.getLocalTracks().size()); i++) {
                imgLoader.DisplayImage(mf1.getLocalTracks().get(i).getPath(), imgView[i]);
            }
            if (mf1.getLocalTracks().size() < 10) {
                if (mf2.getLocalTracks().size() >= (10 - mf1.getLocalTracks().size())) {
                    for (int i = mf1.getLocalTracks().size(); i < 10; i++) {
                        LocalTrack lt = mf2.getLocalTracks().get(i - mf1.getLocalTracks().size());
                        imgLoader.DisplayImage(lt.getPath(), imgView[i]);
                    }
                } else {
                    for (int i = mf1.getLocalTracks().size(); i < mf1.getLocalTracks().size() + mf2.getLocalTracks().size(); i++) {
                        LocalTrack lt = mf2.getLocalTracks().get(i - mf1.getLocalTracks().size());
                        imgLoader.DisplayImage(lt.getPath(), imgView[i]);
                    }
                    for (int i = mf1.getLocalTracks().size() + mf2.getLocalTracks().size(); i < 10; i++) {
                        imgLoader.DisplayImage("folder" + i, imgView[i]);
                    }
                }
            }
        }
    }

}
