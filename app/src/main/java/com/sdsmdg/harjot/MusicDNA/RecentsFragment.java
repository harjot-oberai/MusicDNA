package com.sdsmdg.harjot.MusicDNA;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdsmdg.harjot.MusicDNA.Helpers.SimpleItemTouchHelperCallback;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecentsFragment extends Fragment implements RecentsTrackAdapter.OnDragStartListener {

    RecyclerView recentRecycler;
    RecentsTrackAdapter rtAdpater;

    static ItemTouchHelper mItemTouchHelper;

    public RecentsFragment() {
        // Required empty public constructor
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
        recentRecycler = (RecyclerView) view.findViewById(R.id.view_recent_recycler);
        rtAdpater = new RecentsTrackAdapter(HomeActivity.recentlyPlayed.getRecentlyPlayed(), this);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(HomeActivity.ctx, LinearLayoutManager.VERTICAL, false);
        recentRecycler.setLayoutManager(mLayoutManager2);
        recentRecycler.setItemAnimator(new DefaultItemAnimator());
        recentRecycler.setAdapter(rtAdpater);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(rtAdpater);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recentRecycler);

    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {

    }
}
