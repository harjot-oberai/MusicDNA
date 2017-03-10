package com.sdsmdg.harjot.MusicDNA.fragments.RecentsFragment;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.clickitemtouchlistener.ClickItemTouchListener;
import com.sdsmdg.harjot.MusicDNA.custombottomsheets.CustomGeneralBottomSheetDialog;
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

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecentsFragment extends Fragment implements
        RecentsTrackAdapter.OnDragStartListener,
        RecentsTrackAdapter.onRemoveListener {

    RecyclerView recentRecycler;
    public RecentsTrackAdapter rtAdpater;
    LinearLayoutManager mLayoutManager2;

    LinearLayout noContent;

    ItemTouchHelper mItemTouchHelper;

    recentsCallbackListener mCallback;

    FloatingActionButton shuffleFab;

    View bottomMarginLayout;

    ImageView backdrop;
    TextView fragTitle;
    ImageView backBtn, addToQueueIcon, fragIcon;

    ImageLoader imgLoader;

    public RecentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void updateRecentsFragment() {
        if (HomeActivity.recentlyPlayed.getRecentlyPlayed().size() > 0) {
            UnifiedTrack ut = HomeActivity.recentlyPlayed.getRecentlyPlayed().get(0);
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
        } else {
            backdrop.setBackground(new ColorDrawable(Color.parseColor("#111111")));
        }
    }

    public interface recentsCallbackListener {
        void onRecentItemClicked(boolean isLocal);

        void onRecentFromQueue(int pos);

        void addRecentsToQueue();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            imgLoader = new ImageLoader(context);
            mCallback = (recentsCallbackListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recents, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backBtn = (ImageView) view.findViewById(R.id.recents_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        fragIcon = (ImageView) view.findViewById(R.id.recents_frag_icon);
        fragIcon.setImageTintList(ColorStateList.valueOf(HomeActivity.themeColor));

        fragTitle = (TextView) view.findViewById(R.id.recents_fragment_title);
        if (SplashActivity.tf4 != null)
            fragTitle.setTypeface(SplashActivity.tf4);

        addToQueueIcon = (ImageView) view.findViewById(R.id.add_recents_to_queue_icon);
        addToQueueIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.addRecentsToQueue();
            }
        });

        backdrop = (ImageView) view.findViewById(R.id.recents_backdrop);
        if (HomeActivity.recentlyPlayed.getRecentlyPlayed().size() > 0) {
            UnifiedTrack ut = HomeActivity.recentlyPlayed.getRecentlyPlayed().get(0);
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

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = CommonUtils.dpTopx(65, getContext());

        noContent = (LinearLayout) view.findViewById(R.id.no_recents_content);

        recentRecycler = (RecyclerView) view.findViewById(R.id.view_recent_recycler);
        rtAdpater = new RecentsTrackAdapter(HomeActivity.recentlyPlayed.getRecentlyPlayed(), this, getContext());
        mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recentRecycler.setLayoutManager(mLayoutManager2);
        recentRecycler.setItemAnimator(new DefaultItemAnimator());
        recentRecycler.setAdapter(rtAdpater);

        recentRecycler.addOnItemTouchListener(new ClickItemTouchListener(recentRecycler) {
            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                UnifiedTrack ut = HomeActivity.recentlyPlayed.getRecentlyPlayed().get(position);
                boolean isRepeat = false;
                int pos = 0;
                for (int i = 0; i < HomeActivity.queue.getQueue().size(); i++) {
                    UnifiedTrack ut1 = HomeActivity.queue.getQueue().get(i);
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
                        mCallback.onRecentItemClicked(true);
                    } else {
                        Track track = ut.getStreamTrack();
                        if (HomeActivity.queue.getQueue().size() == 0) {
                            HomeActivity.queueCurrentIndex = 0;
                            HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
                        } else if (HomeActivity.queueCurrentIndex == HomeActivity.queue.getQueue().size() - 1) {
                            HomeActivity.queueCurrentIndex++;
                            HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
                        } else if (HomeActivity.isReloaded) {
                            HomeActivity.isReloaded = false;
                            HomeActivity.queueCurrentIndex = HomeActivity.queue.getQueue().size();
                            HomeActivity.queue.getQueue().add(new UnifiedTrack(false, null, track));
                        } else {
                            HomeActivity.queue.getQueue().add(++HomeActivity.queueCurrentIndex, new UnifiedTrack(false, null, track));
                        }
                        HomeActivity.selectedTrack = track;
                        HomeActivity.streamSelected = true;
                        HomeActivity.localSelected = false;
                        HomeActivity.queueCall = false;
                        HomeActivity.isReloaded = false;
                        mCallback.onRecentItemClicked(false);
                    }
                } else {
                    mCallback.onRecentFromQueue(pos);
                }

                return true;
            }

            @Override
            public boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                if (position != -1) {
                    final UnifiedTrack ut = HomeActivity.recentlyPlayed.getRecentlyPlayed().get(position);
                    CustomGeneralBottomSheetDialog generalBottomSheetDialog = new CustomGeneralBottomSheetDialog();
                    generalBottomSheetDialog.setPosition(position);
                    generalBottomSheetDialog.setTrack(ut);
                    generalBottomSheetDialog.setFragment("Recents");
                    generalBottomSheetDialog.show(getActivity().getSupportFragmentManager(), "general_bottom_sheet_dialog");
                }
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        shuffleFab = (FloatingActionButton) view.findViewById(R.id.play_all_fab_recent);

        if (HomeActivity.recentlyPlayed != null && HomeActivity.recentlyPlayed.getRecentlyPlayed().size() > 0) {
            noContent.setVisibility(View.INVISIBLE);
            shuffleFab.setVisibility(View.VISIBLE);
        } else {
            noContent.setVisibility(View.VISIBLE);
            shuffleFab.setVisibility(View.INVISIBLE);
        }

        shuffleFab.setBackgroundTintList(ColorStateList.valueOf(HomeActivity.themeColor));
        shuffleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HomeActivity.recentlyPlayed.getRecentlyPlayed().size() > 0) {
                    HomeActivity.queue.getQueue().clear();
                    for (int i = 0; i < HomeActivity.recentlyPlayed.getRecentlyPlayed().size(); i++) {
                        HomeActivity.queue.getQueue().add(HomeActivity.recentlyPlayed.getRecentlyPlayed().get(i));
                    }
                    Random r = new Random();
                    mCallback.onRecentFromQueue(r.nextInt(HomeActivity.queue.getQueue().size()));
                }
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(rtAdpater);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recentRecycler);

    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mLayoutManager2.scrollToPositionWithOffset(0, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shuffleFab.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new OvershootInterpolator());
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
