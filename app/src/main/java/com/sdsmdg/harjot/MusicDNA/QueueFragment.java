package com.sdsmdg.harjot.MusicDNA;


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

import com.sdsmdg.harjot.MusicDNA.Helpers.SimpleItemTouchHelperCallback;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class QueueFragment extends Fragment implements QueueRecyclerAdapter.OnDragStartListener {

    RecyclerView queueRecycler;
    static QueueRecyclerAdapter qAdapter;

    static ItemTouchHelper mItemTouchHelper;

    FloatingActionButton saveQueue;

    static onQueueItemClickedListener mCallback;
    static onQueueSaveListener mCallback2;

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

        qAdapter = new QueueRecyclerAdapter(HomeActivity.queue.getQueue(), HomeActivity.ctx, this);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(HomeActivity.ctx, LinearLayoutManager.VERTICAL, false);
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
        saveQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback2.onQueueSave();
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(qAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(queueRecycler);

    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

}
