package com.sdsmdg.harjot.MusicDNA.CustomBottomSheetDialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.imageLoader.ImageLoader;

/**
 * Created by Harjot on 21-Oct-16.
 */

public class CustomLocalBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    TextView playText, playNextText, addToQueueText, addToPlaylistText, addToFavouriteText, shareText;

    ImageView localSongImage;
    TextView localSongTitle, localSongArtist;

    HomeActivity activity;

    int position = 0;
    String fragment;
    LocalTrack localTrack;

    ImageLoader imgLoader;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (HomeActivity) activity;
        imgLoader = new ImageLoader(activity);
    }

    public void setPosition(int pos) {
        position = pos;
    }

    public void setLocalTrack(LocalTrack localTrack) {
        this.localTrack = localTrack;
    }

    public void setFragment(String frag) {
        fragment = frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.local_song_bottom_sheet, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        localSongImage = (ImageView) view.findViewById(R.id.local_song_bottom_sheet_image);
        localSongTitle = (TextView) view.findViewById(R.id.local_song_bottom_sheet_title);
        localSongArtist = (TextView) view.findViewById(R.id.local_song_bottom_sheet_artist);

        imgLoader.DisplayImage(localTrack.getPath(), localSongImage);
        localSongTitle.setText(localTrack.getTitle());
        localSongArtist.setText(localTrack.getArtist());

        playText = (TextView) view.findViewById(R.id.local_song_bottom_sheet_play);
        playText.setOnClickListener(this);
        playNextText = (TextView) view.findViewById(R.id.local_song_bottom_sheet_play_next);
        playNextText.setOnClickListener(this);
        addToQueueText = (TextView) view.findViewById(R.id.local_song_bottom_sheet_add_to_queue);
        addToQueueText.setOnClickListener(this);
        addToPlaylistText = (TextView) view.findViewById(R.id.local_song_bottom_sheet_add_to_playlist);
        addToPlaylistText.setOnClickListener(this);
        addToFavouriteText = (TextView) view.findViewById(R.id.local_song_bottom_sheet_add_to_fav);
        addToFavouriteText.setOnClickListener(this);
        shareText = (TextView) view.findViewById(R.id.local_song_bottom_sheet_share);
        shareText.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_song_bottom_sheet_play:
                activity.bottomSheetListener(position, "Play", fragment, true);
                break;
            case R.id.local_song_bottom_sheet_play_next:
                activity.bottomSheetListener(position, "Play Next", fragment, true);
                break;
            case R.id.local_song_bottom_sheet_add_to_queue:
                activity.bottomSheetListener(position, "Add to Queue", fragment, true);
                break;
            case R.id.local_song_bottom_sheet_add_to_playlist:
                activity.bottomSheetListener(position, "Add to Playlist", fragment, true);
                break;
            case R.id.local_song_bottom_sheet_add_to_fav:
                activity.bottomSheetListener(position, "Add to Favourites", fragment, true);
                break;
            case R.id.local_song_bottom_sheet_share:
                activity.bottomSheetListener(position, "Share", fragment, true);
                break;
        }
        dismiss();
    }
}

