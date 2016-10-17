package com.sdsmdg.harjot.MusicDNA;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddToPlaylistFragment extends Fragment {

    RecyclerView rv;
    AddToPlaylistAdapter atpAdapter;

    EditText searchBox;

    TextView doneText, numberSelectedSongs, cancelText;

    List<LocalTrack> finalList;

    ImageView clearSearchBtn;

    LinearLayoutManager mLayoutManager2;

    int numberSelected = 0;

    public newPlaylistListener mCallback;

    public interface newPlaylistListener {
        public void onCancel();

        public void onDone();
    }

    public AddToPlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_to_playlist, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (newPlaylistListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
        finalList = new ArrayList<>();
        for (int i = 0; i < HomeActivity.localTrackList.size(); i++) {
            finalList.add(HomeActivity.localTrackList.get(i));
        }

        atpAdapter = new AddToPlaylistAdapter(finalList, getContext());

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        numberSelected = 0;
        HomeActivity.finalSelectedTracks.clear();
        atpAdapter.notifyDataSetChanged();
        clearSearchBtn = (ImageView) view.findViewById(R.id.clear_search_btn);
        clearSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                searchBox.setText("");
                finalList.clear();
                for (int i = 0; i < HomeActivity.localTrackList.size(); i++) {
                    finalList.add(HomeActivity.localTrackList.get(i));
                }
                atpAdapter.notifyDataSetChanged();
            }
        });

        searchBox = (EditText) view.findViewById(R.id.add_to_playlist_search);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    finalList.clear();
                    for (int i = 0; i < HomeActivity.localTrackList.size(); i++) {
                        finalList.add(HomeActivity.localTrackList.get(i));
                    }
                } else {
                    finalList.clear();
                    for (int i = 0; i < HomeActivity.localTrackList.size(); i++) {
                        LocalTrack lt = HomeActivity.localTrackList.get(i);
                        if (lt.getTitle().toLowerCase().contains(s.toString().trim().toLowerCase())) {
                            finalList.add(lt);
                        }
                    }
                }
                atpAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        doneText = (TextView) view.findViewById(R.id.done_text);
        cancelText = (TextView) view.findViewById(R.id.cancel_text);
        numberSelectedSongs = (TextView) view.findViewById(R.id.number_selected_songs);

        doneText.setBackgroundColor(HomeActivity.themeColor);

        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onCancel();
                atpAdapter.notifyDataSetChanged();
                numberSelected = 0;
                numberSelectedSongs.setText(String.valueOf(numberSelected) + " selected");
            }
        });

        doneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onDone();
            }
        });

        rv = (RecyclerView) view.findViewById(R.id.add_to_playlist_recycler);
        mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager2);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(atpAdapter);

        rv.addOnItemTouchListener(new ClickItemTouchListener(rv) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {

                LocalTrack lt = finalList.get(position);
                if (HomeActivity.finalSelectedTracks.contains(lt)) {
                    HomeActivity.finalSelectedTracks.remove(lt);
                    numberSelected--;
                } else {
                    HomeActivity.finalSelectedTracks.add(lt);
                    numberSelected++;
                }

                numberSelectedSongs.setText(String.valueOf(numberSelected) + " selected");

                CheckBox cb = (CheckBox) view.findViewById(R.id.is_selected_checkbox);
                cb.setChecked(!cb.isChecked());

                atpAdapter.notifyItemChanged(position);
                return false;
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

    @Override
    public void onResume() {
        super.onResume();
        mLayoutManager2.scrollToPositionWithOffset(0, 0);
    }

    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
