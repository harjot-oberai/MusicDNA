package com.sdsmdg.harjot.MusicDNA;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class FolderFragment extends Fragment {

    RecyclerView allFoldersRecycler;
    FolderRecyclerAdapter mfAdapter;

    static onFolderClickedListener mCallback;

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
        allFoldersRecycler = (RecyclerView) view.findViewById(R.id.all_folders_recycler);
        mfAdapter = new FolderRecyclerAdapter(HomeActivity.allMusicFolders.getMusicFolders());
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(HomeActivity.ctx, LinearLayoutManager.VERTICAL, false);
        allFoldersRecycler.setLayoutManager(mLayoutManager2);
        allFoldersRecycler.setItemAnimator(new DefaultItemAnimator());
        allFoldersRecycler.setAdapter(mfAdapter);

        allFoldersRecycler.addOnItemTouchListener(new ClickItemTouchListener(allFoldersRecycler) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                mCallback.onFolderClicked(position);
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

    }
}
