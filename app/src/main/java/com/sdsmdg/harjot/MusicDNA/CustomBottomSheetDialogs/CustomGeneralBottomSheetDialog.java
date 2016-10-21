package com.sdsmdg.harjot.MusicDNA.CustomBottomSheetDialogs;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.Models.UnifiedTrack;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.imageLoader.ImageLoader;

/**
 * Created by Harjot on 21-Oct-16.
 */

public class CustomGeneralBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    TextView playText, playNextText, addToQueueText, addToPlaylistText, addToFavouriteText;

    ImageView generalSongImage;
    TextView generalSongTitle, generalSongArtist;

    HomeActivity activity;

    int position = 0;
    String fragment;
    UnifiedTrack generalTrack;

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

    public void setTrack(UnifiedTrack generalTrack) {
        this.generalTrack = generalTrack;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.general_song_bottom_sheet, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        generalSongImage = (ImageView) view.findViewById(R.id.general_song_bottom_sheet_image);
        generalSongTitle = (TextView) view.findViewById(R.id.general_song_bottom_sheet_title);
        generalSongArtist = (TextView) view.findViewById(R.id.general_song_bottom_sheet_artist);

        if (generalTrack.getType()) {
            imgLoader.DisplayImage(generalTrack.getLocalTrack().getPath(), generalSongImage);
            generalSongTitle.setText(generalTrack.getLocalTrack().getTitle());
            generalSongArtist.setText(generalTrack.getLocalTrack().getArtist());
        } else {
            imgLoader.DisplayImage(generalTrack.getStreamTrack().getArtworkURL(), generalSongImage);
            generalSongTitle.setText(generalTrack.getStreamTrack().getTitle());
            generalSongArtist.setText("");
        }

        playText = (TextView) view.findViewById(R.id.general_song_bottom_sheet_play);
        playText.setOnClickListener(this);
        playNextText = (TextView) view.findViewById(R.id.general_song_bottom_sheet_play_next);
        playNextText.setOnClickListener(this);
        addToQueueText = (TextView) view.findViewById(R.id.general_song_bottom_sheet_add_to_queue);
        addToQueueText.setOnClickListener(this);
        addToPlaylistText = (TextView) view.findViewById(R.id.general_song_bottom_sheet_add_to_playlist);
        addToPlaylistText.setOnClickListener(this);
        addToFavouriteText = (TextView) view.findViewById(R.id.general_song_bottom_sheet_add_to_fav);
        addToFavouriteText.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.general_song_bottom_sheet_play:
                activity.bottomSheetListener(position, "Play", fragment, generalTrack.getType());
                break;
            case R.id.general_song_bottom_sheet_play_next:
                activity.bottomSheetListener(position, "Play Next", fragment, generalTrack.getType());
                break;
            case R.id.general_song_bottom_sheet_add_to_queue:
                activity.bottomSheetListener(position, "Add to Queue", fragment, generalTrack.getType());
                break;
            case R.id.general_song_bottom_sheet_add_to_playlist:
                activity.bottomSheetListener(position, "Add to Playlist", fragment, generalTrack.getType());
                break;
            case R.id.general_song_bottom_sheet_add_to_fav:
                activity.bottomSheetListener(position, "Add to Favourites", fragment, generalTrack.getType());
                break;
        }
        dismiss();
    }
}
