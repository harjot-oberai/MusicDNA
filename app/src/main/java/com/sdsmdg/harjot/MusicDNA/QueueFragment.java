package com.sdsmdg.harjot.MusicDNA;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class QueueFragment extends Fragment {

    RecyclerView queueRecycler;
    static QueueRecyclerAdapter qAdapter;

    static onQueueItemClickedListener mCallback;

    public interface onQueueItemClickedListener{
        public void onQueueItemClicked(int position);
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

        qAdapter = new QueueRecyclerAdapter(HomeActivity.queue.getQueue(), HomeActivity.ctx);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(HomeActivity.ctx, LinearLayoutManager.VERTICAL, false);
        queueRecycler.setLayoutManager(mLayoutManager2);
        queueRecycler.setItemAnimator(new DefaultItemAnimator());
        AlphaInAnimationAdapter alphaAdapter2 = new AlphaInAnimationAdapter(qAdapter);
        alphaAdapter2.setFirstOnly(false);
        ScaleInAnimationAdapter scaleAdapter2 = new ScaleInAnimationAdapter(alphaAdapter2);
        scaleAdapter2.setFirstOnly(false);
        queueRecycler.setAdapter(scaleAdapter2);

        queueRecycler.addOnItemTouchListener(new ClickItemTouchListener(queueRecycler) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                mCallback.onQueueItemClicked(position);
                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                return false;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

    }

}
