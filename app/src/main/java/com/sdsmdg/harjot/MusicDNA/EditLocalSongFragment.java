package com.sdsmdg.harjot.MusicDNA;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.sdsmdg.harjot.MusicDNA.imageLoader.ImageLoader;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditLocalSongFragment extends Fragment {

    EditText titleText, artistText, albumText;
    ImageView songImage;
    Button saveButton;

    Context ctx;

    onEditSongSaveListener mCallback;

    public interface onEditSongSaveListener {
        public void onEditSongSave();
    }

    public EditLocalSongFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = context;
        try {
            mCallback = (onEditSongSaveListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_local_song, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saveButton = (Button) view.findViewById(R.id.edit_song_save_button);
        saveButton.setBackgroundColor(HomeActivity.themeColor);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onEditSongSave();
            }
        });

        titleText = (EditText) view.findViewById(R.id.edit_song_title);
        titleText.setText(HomeActivity.editSong.getTitle());
        artistText = (EditText) view.findViewById(R.id.edit_song_artist);
        artistText.setText(HomeActivity.editSong.getArtist());
        albumText = (EditText) view.findViewById(R.id.edit_song_album);
        albumText.setText(HomeActivity.editSong.getAlbum());

        songImage = (ImageView) view.findViewById(R.id.edit_song_image);

        Bitmap bmp = null;
        try {
            bmp = getBitmap(HomeActivity.editSong.getPath());
        } catch (Exception e) {

        }

        if (bmp != null) {
            songImage.setImageBitmap(bmp);
        } else {
            songImage.setImageResource(R.drawable.ic_default);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        titleText.setText(HomeActivity.editSong.getTitle());
        artistText.setText(HomeActivity.editSong.getArtist());
        albumText.setText(HomeActivity.editSong.getAlbum());
    }

    public Bitmap getBitmap(String url) {
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(url);
        Bitmap bitmap = null;

        byte[] data = mmr.getEmbeddedPicture();

        if (data != null) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            return bitmap;
        } else {
            return null;
        }
    }

}
