package com.sdsmdg.harjot.MusicDNA;


import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.sdsmdg.harjot.MusicDNA.Helpers.SimpleItemTouchHelperCallback;
import com.squareup.leakcanary.RefWatcher;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class QueueFragment extends Fragment implements QueueRecyclerAdapter.OnDragStartListener {

    RecyclerView queueRecycler;
    QueueRecyclerAdapter qAdapter;

    ItemTouchHelper mItemTouchHelper;

    FloatingActionButton saveQueue;

    onQueueItemClickedListener mCallback;
    onQueueSaveListener mCallback2;

    ShowcaseView showCase;

    public interface onQueueItemClickedListener {
        public void onQueueItemClicked(int position);
    }

    public interface onQueueSaveListener {
        public void onQueueSave();
    }

    public QueueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (onQueueItemClickedListener) context;
            mCallback2 = (onQueueSaveListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_queue, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queueRecycler = (RecyclerView) view.findViewById(R.id.queueRecycler);

        qAdapter = new QueueRecyclerAdapter(HomeActivity.queue.getQueue(), getContext(), this);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        queueRecycler.setLayoutManager(mLayoutManager2);
        queueRecycler.setItemAnimator(new DefaultItemAnimator());
        queueRecycler.setAdapter(qAdapter);

        mLayoutManager2.scrollToPositionWithOffset(HomeActivity.queueCurrentIndex, 2);

        queueRecycler.addOnItemTouchListener(new ClickItemTouchListener(queueRecycler) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                mCallback.onQueueItemClicked(position);
                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        saveQueue = (FloatingActionButton) view.findViewById(R.id.save_queue);
        saveQueue.setColorFilter(HomeActivity.themeColor);
        saveQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback2.onQueueSave();
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(qAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(queueRecycler);

        Button mEndButton = new Button(getContext());
        mEndButton.setBackgroundColor(Color.parseColor("#FFA036"));
        mEndButton.setTextColor(Color.WHITE);

        showCase = new ShowcaseView.Builder(getActivity())
                .blockAllTouches()
                .singleShot(3)
                .setStyle(R.style.CustomShowcaseTheme)
                .useDecorViewAsParent()
                .replaceEndButton(mEndButton)
                .setTarget(new ViewTarget(R.id.showcase_view, getActivity()))
                .setContentTitle("Queue")
                .setContentText("Here all songs that are currently in queue are listed." +
                        " Use handle to reorder the Queue and swipe the song to remove from queue")
                .build();
        showCase.setButtonText("Next");
        showCase.setButtonPosition(HomeActivity.lps);
        showCase.overrideButtonClick(new View.OnClickListener() {
            int count1 = 0;

            @Override
            public void onClick(View v) {
                count1++;
                switch (count1) {
                    case 1:
                        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
                        lps.setMargins(margin, margin, margin, 5 + HomeActivity.navBarHeightSizeinDp);
                        showCase.setButtonPosition(lps);
                        showCase.setTarget(new ViewTarget(saveQueue.getId(), getActivity()));
                        showCase.setContentTitle("Save Queue");
                        showCase.setContentText("Save the queue as a playlist");
                        showCase.setButtonText("Done");
                        break;
                    case 2:
                        showCase.hide();
                        break;
                }
            }

        });

    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
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

    public boolean isShowcaseVisible() {
        return (showCase != null && showCase.isShowing());
    }

    public void hideShowcase() {
        showCase.hide();
    }

    public void updateQueueAdapter() {
        if (qAdapter != null)
            qAdapter.notifyDataSetChanged();
    }

    public void notifyAdapterItemRemoved(int i) {
        if (qAdapter != null) {
            qAdapter.notifyItemRemoved(i);
        }
    }
}
