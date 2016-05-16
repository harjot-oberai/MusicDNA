package com.example.harjot.musicstreamer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.harjot.musicstreamer.Models.LocalTrack;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by Harjot on 09-May-16.
 */
public class LocalTrackListAdapter extends BaseAdapter {

    private List<LocalTrack> localTracks;
    private Context ctx;

    public LocalTrackListAdapter(List<LocalTrack> localTracks, Context ctx) {
        this.localTracks = localTracks;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return localTracks.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) HomeActivity.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.list_item, parent, false);
        TextView tr_title = (TextView) v.findViewById(R.id.title);
        ImageView tr_img = (ImageView) v.findViewById(R.id.img);
        TextView artist = (TextView) v.findViewById(R.id.url);
        LocalTrack track = localTracks.get(position);
        tr_title.setText(track.getTitle());
        artist.setText(track.getArtist());
//        Picasso.with(ctx).load(new File(track.getPath())).into(tr_img);
        Bitmap img = getAlbumArt(track.getPath());
        if(img!=null){
            tr_img.setImageBitmap(img);
        }
        else{
            tr_img.setImageResource(R.drawable.ic_default);
        }
        return v;
    }

    public static Bitmap getAlbumArt(String path){
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        Bitmap bitmap = null;

        byte [] data = mmr.getEmbeddedPicture();
        if(data != null) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            return bitmap;
        }
        else {
            return null;
        }
    }

}
