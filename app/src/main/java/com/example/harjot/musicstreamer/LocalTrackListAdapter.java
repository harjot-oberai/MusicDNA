package com.example.harjot.musicstreamer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.harjot.musicstreamer.Models.LocalTrack;

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
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.list_item, parent, false);
        TextView tr_title = (TextView) v.findViewById(R.id.title);
        ImageView tr_img = (ImageView) v.findViewById(R.id.img);
        LocalTrack track = localTracks.get(position);
        tr_title.setText(track.getTitle());
        Bitmap img = getAlbumArt(track.getPath());
        if(img!=null){
            tr_img.setImageBitmap(img);
        }
        return v;
    }

    private Bitmap getAlbumArt(String path){
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        Bitmap bitmap = null;

        byte [] data = mmr.getEmbeddedPicture();
        //coverart is an Imageview object

        // convert the byte array to a bitmap
        if(data != null) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        else {

        }
        return bitmap;
    }

}
