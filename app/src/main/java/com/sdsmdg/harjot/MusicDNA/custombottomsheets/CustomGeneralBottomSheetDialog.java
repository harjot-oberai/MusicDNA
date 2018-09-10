package com.sdsmdg.harjot.MusicDNA.custombottomsheets;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.imageloader.ImageLoader;
import com.sdsmdg.harjot.MusicDNA.models.UnifiedTrack;

/**
 * Created by Harjot on 21-Oct-16.
 */

public class CustomGeneralBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    LinearLayout playText, playNextText, addToQueueText, addToPlaylistText, addToFavouriteText;

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
        return inflater.inflate(R.layout.general_song_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        generalSongImage = view.findViewById(R.id.general_song_bottom_sheet_image);
        generalSongTitle = view.findViewById(R.id.general_song_bottom_sheet_title);
        generalSongArtist = view.findViewById(R.id.general_song_bottom_sheet_artist);

        if (generalTrack.getType()) {
            imgLoader.DisplayImage(generalTrack.getLocalTrack().getPath(), generalSongImage);
            generalSongTitle.setText(generalTrack.getLocalTrack().getTitle());
            generalSongArtist.setText(generalTrack.getLocalTrack().getArtist());
        } else {
            imgLoader.DisplayImage(generalTrack.getStreamTrack().getArtworkURL(), generalSongImage);
            generalSongTitle.setText(generalTrack.getStreamTrack().getTitle());
            generalSongArtist.setText("");
        }

        playText = view.findViewById(R.id.general_song_bottom_sheet_play_wrapper);
        playText.setOnClickListener(this);
        playNextText = view.findViewById(R.id.general_song_bottom_sheet_play_next_wrapper);
        playNextText.setOnClickListener(this);
        addToQueueText = view.findViewById(R.id.general_song_bottom_sheet_add_to_queue_wrapper);
        addToQueueText.setOnClickListener(this);
        addToPlaylistText = view.findViewById(R.id.general_song_bottom_sheet_add_to_playlist_wrapper);
        addToPlaylistText.setOnClickListener(this);
        addToFavouriteText = view.findViewById(R.id.general_song_bottom_sheet_add_to_fav_wrapper);
        addToFavouriteText.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.general_song_bottom_sheet_play_wrapper:
                activity.bottomSheetListener(position, getString(R.string.play), fragment, generalTrack.getType());
                break;
            case R.id.general_song_bottom_sheet_play_next_wrapper:
                activity.bottomSheetListener(position, getString(R.string.play_next), fragment, generalTrack.getType());
                break;
            case R.id.general_song_bottom_sheet_add_to_queue_wrapper:
                activity.bottomSheetListener(position, getString(R.string.add_to_queue), fragment, generalTrack.getType());
                break;
            case R.id.general_song_bottom_sheet_add_to_playlist_wrapper:
                activity.bottomSheetListener(position, getString(R.string.add_to_playlist), fragment, generalTrack.getType());
                break;
            case R.id.general_song_bottom_sheet_add_to_fav_wrapper:
                activity.bottomSheetListener(position, getString(R.string.add_to_favourites), fragment, generalTrack.getType());
                break;
        }
        dismiss();
    }
}
