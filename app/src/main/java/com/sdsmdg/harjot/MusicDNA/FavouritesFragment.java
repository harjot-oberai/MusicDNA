package com.sdsmdg.harjot.MusicDNA;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.Helpers.SimpleItemTouchHelperCallback;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment implements FavouriteTrackAdapter.OnDragStartListener {


    RecyclerView favouriteRecycler;
    static FavouriteTrackAdapter fAdapter;

    static ItemTouchHelper mItemTouchHelper;

    static onFavouriteItemClickedListener mCallback;
    static onFavouritePlayAllListener mCallback2;

    LinearLayout noFavouriteContent;

    FloatingActionButton playAll;

    public interface onFavouriteItemClickedListener {
        public void onFavouriteItemClicked(int position);
    }

    public interface onFavouritePlayAllListener {
        public void onFavouritePlayAll();
    }

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (onFavouriteItemClickedListener) context;
            mCallback2 = (onFavouritePlayAllListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favouriteRecycler = (RecyclerView) view.findViewById(R.id.favouriteRecycler);
        noFavouriteContent = (LinearLayout) view.findViewById(R.id.noFavouriteContent);
        playAll = (FloatingActionButton) view.findViewById(R.id.fav_play_all_fab);
        ((TextView)view.findViewById(R.id.favNoContentText)).setTypeface(SplashActivity.tf2);

        if (HomeActivity.favouriteTracks.getFavourite().size() == 0) {
            favouriteRecycler.setVisibility(View.INVISIBLE);
            playAll.setVisibility(View.INVISIBLE);
            noFavouriteContent.setVisibility(View.VISIBLE);
        } else {
            favouriteRecycler.setVisibility(View.VISIBLE);
            playAll.setVisibility(View.VISIBLE);
            noFavouriteContent.setVisibility(View.INVISIBLE);
        }

        fAdapter = new FavouriteTrackAdapter(HomeActivity.favouriteTracks.getFavourite(), this);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(HomeActivity.ctx, LinearLayoutManager.VERTICAL, false);
        favouriteRecycler.setLayoutManager(mLayoutManager2);
        favouriteRecycler.setItemAnimator(new DefaultItemAnimator());
        favouriteRecycler.setAdapter(fAdapter);

        favouriteRecycler.addOnItemTouchListener(new ClickItemTouchListener(favouriteRecycler) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                mCallback.onFavouriteItemClicked(position);
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

        if (HomeActivity.favouriteTracks.getFavourite().size() == 0) {
            playAll.setVisibility(View.INVISIBLE);
        } else {
            playAll.setVisibility(View.VISIBLE);
        }
        playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.queue.setQueue(HomeActivity.favouriteTracks.getFavourite());
                HomeActivity.queueCurrentIndex = 0;
                mCallback2.onFavouritePlayAll();
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(fAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(favouriteRecycler);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }


    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
