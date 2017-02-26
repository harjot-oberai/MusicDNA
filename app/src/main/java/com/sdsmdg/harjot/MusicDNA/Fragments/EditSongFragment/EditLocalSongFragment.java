package com.sdsmdg.harjot.MusicDNA.fragments.EditSongFragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.activities.SplashActivity;
import com.sdsmdg.harjot.MusicDNA.lyrics.Lyrics;
import com.sdsmdg.harjot.MusicDNA.utilities.CommonUtils;
import com.sdsmdg.harjot.MusicDNA.imageloader.ImageLoader;
import com.sdsmdg.harjot.MusicDNA.utilities.DownloadThread;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import java.io.File;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditLocalSongFragment extends Fragment {

    EditText titleText, artistText, albumText;
    Button saveButton;

    ImageView backBtn, backdrop;
    TextView fragTitle;

    Context ctx;

    ImageLoader imgLoader;

    boolean isTitleNotNull = false;
    boolean isArtistNotNull = false;
    boolean isAlbumNotNull = false;

    MP3File mp3File;
    Tag tag;
    ID3v1Tag id3v1Tag;
    AbstractID3v2Tag id3v2Tag;
    ID3v24Tag id3v24Tag;

    EditFragmentCallbackListener mCallback;

    View bottomMarginLayout;

    public interface EditFragmentCallbackListener {
        void onEditSongSave(boolean wasSaveSuccessful);

        void getNewBitmap();

        void deleteMediaStoreCache();
    }

    public EditLocalSongFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = context;
        try {
            imgLoader = new ImageLoader(context);
            mCallback = (EditFragmentCallbackListener) context;
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

        backBtn = (ImageView) view.findViewById(R.id.edit_song_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        fragTitle = (TextView) view.findViewById(R.id.edit_song_fragment_title);
        if (SplashActivity.tf4 != null)
            fragTitle.setTypeface(SplashActivity.tf4);

        backdrop = (ImageView) view.findViewById(R.id.edit_song_backdrop);
        imgLoader.DisplayImage(HomeActivity.editSong.getPath(), backdrop);

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = CommonUtils.dpTopx(65, getContext());
        ;

        titleText = (EditText) view.findViewById(R.id.edit_song_title);
        artistText = (EditText) view.findViewById(R.id.edit_song_artist);
        albumText = (EditText) view.findViewById(R.id.edit_song_album);


        saveButton = (Button) view.findViewById(R.id.edit_song_save_button);
        saveButton.setBackgroundColor(HomeActivity.themeColor);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!titleText.getText().toString().trim().equals("")) {
                    HomeActivity.editSong.setTitle(titleText.getText().toString().trim());
                    isTitleNotNull = true;
                } else {
                    titleText.setError("Enter a valid Title");
                    isTitleNotNull = false;
                }
                if (!artistText.getText().toString().trim().equals("")) {
                    HomeActivity.editSong.setTitle(artistText.getText().toString().trim());
                    isArtistNotNull = true;
                } else {
                    artistText.setError("Enter a valid Artist name");
                    isArtistNotNull = false;
                }
                if (!albumText.getText().toString().trim().equals("")) {
                    HomeActivity.editSong.setTitle(albumText.getText().toString().trim());
                    isAlbumNotNull = true;
                } else {
                    albumText.setError("Enter a valid Album name");
                    isAlbumNotNull = false;
                }
                if (isTitleNotNull && isArtistNotNull && isAlbumNotNull) {

                    boolean error = false;

                    try {
                        if (tag != null) {
                            tag.setField(FieldKey.TITLE, titleText.getText().toString().trim());
                            tag.setField(FieldKey.ARTIST, artistText.getText().toString().trim());
                            tag.setField(FieldKey.ALBUM, albumText.getText().toString().trim());
                        }
                    } catch (FieldDataInvalidException e) {
                        error = true;
                        e.printStackTrace();
                    }

                    try {
                        if (id3v1Tag != null) {
                            id3v1Tag.setField(FieldKey.TITLE, titleText.getText().toString().trim());
                            id3v1Tag.setField(FieldKey.ARTIST, artistText.getText().toString().trim());
                            id3v1Tag.setField(FieldKey.ALBUM, albumText.getText().toString().trim());
                        }
                    } catch (FieldDataInvalidException e) {
                        error = true;
                        e.printStackTrace();
                    }

                    try {
                        if (id3v2Tag != null) {
                            id3v2Tag.setField(FieldKey.TITLE, titleText.getText().toString().trim());
                            id3v2Tag.setField(FieldKey.ARTIST, artistText.getText().toString().trim());
                            id3v2Tag.setField(FieldKey.ALBUM, albumText.getText().toString().trim());
                        }
                    } catch (FieldDataInvalidException e) {
                        error = true;
                        e.printStackTrace();
                    }

                    try {
                        if (id3v24Tag != null) {
                            id3v24Tag.setField(FieldKey.TITLE, titleText.getText().toString().trim());
                            id3v24Tag.setField(FieldKey.ARTIST, artistText.getText().toString().trim());
                            id3v24Tag.setField(FieldKey.ALBUM, albumText.getText().toString().trim());
                        }
                    } catch (FieldDataInvalidException e) {
                        error = true;
                        e.printStackTrace();
                    }

                    try {
                        mp3File.commit();
                    } catch (CannotWriteException e) {
                        error = true;
                        e.printStackTrace();
                    }

                    if (!error) {
                        Toast.makeText(ctx, "Saved", Toast.LENGTH_SHORT).show();
                        HomeActivity.editSong.setTitle(titleText.getText().toString().trim());
                        HomeActivity.editSong.setArtist(artistText.getText().toString().trim());
                        HomeActivity.editSong.setAlbum(albumText.getText().toString().trim());
                    }

                    mCallback.onEditSongSave(!error);

                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        mp3File = null;

        try {
            File f = new File(HomeActivity.editSong.getPath());
            mp3File = (MP3File) AudioFileIO.read(f);
        } catch (IOException | CannotReadException | InvalidAudioFrameException | TagException | ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            mp3File = null;
            e.printStackTrace();
        }

        if (mp3File == null) {
            Toast.makeText(ctx, "Error in loading the file", Toast.LENGTH_SHORT).show();
            mCallback.onEditSongSave(false);
        }
        if (mp3File != null && !mp3File.hasID3v2Tag()) {
            Toast.makeText(ctx, "No Tags Found", Toast.LENGTH_SHORT).show();
            mCallback.onEditSongSave(false);
        }

        if (mp3File != null && mp3File.hasID3v2Tag()) {
            titleText.setText(HomeActivity.editSong.getTitle());
            artistText.setText(HomeActivity.editSong.getArtist());
            albumText.setText(HomeActivity.editSong.getAlbum());
        }

        try {
            tag = mp3File.getTag();
            id3v1Tag = mp3File.getID3v1Tag();
            id3v2Tag = mp3File.getID3v2Tag();
            id3v24Tag = mp3File.getID3v2TagAsv24();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ctx, "Error in finding tags", Toast.LENGTH_SHORT).show();
            mCallback.onEditSongSave(false);
        }

    }

    public Bitmap getBitmap(String url) {
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(url);
        Bitmap bitmap;

        byte[] data = mmr.getEmbeddedPicture();

        if (data != null) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            return bitmap;
        } else {
            return null;
        }
    }

    public void updateCoverArt(Bitmap bmp, Uri artUri) {
//        if (bmp != null) {
//            songImage.setImageBitmap(bmp);
//            backImage.setImageBitmap(bmp);
//        }
//
//        File file = new File(artUri.getPath());
//
//        if (file.exists()) {
//            Artwork cover = null;
//            try {
//                cover = ArtworkFactory.createArtworkFromFile(file);
//                tag.deleteArtworkField();
//                tag.createField(cover);
//                tag.setField(cover);
//                mp3File.commit();
//            } catch (FieldDataInvalidException e) {
//                e.printStackTrace();
//            } catch (CannotWriteException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        mCallback2.deleteMediaStoreCache();

    }

}
