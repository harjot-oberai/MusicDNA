package com.sdsmdg.harjot.MusicDNA;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
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
public class FavouritesFragment extends Fragment implements FavouriteTrackAdapter.OnDragStartListener {


    RecyclerView favouriteRecycler;
    static FavouriteTrackAdapter fAdapter;

    static ItemTouchHelper mItemTouchHelper;

    static onFavouriteItemClickedListener mCallback;

    public interface onFavouriteItemClickedListener {
        public void onFavouriteItemClicked(int position);
    }

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (onFavouriteItemClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favouriteRecycler = (RecyclerView) view.findViewById(R.id.favouriteRecycler);

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
