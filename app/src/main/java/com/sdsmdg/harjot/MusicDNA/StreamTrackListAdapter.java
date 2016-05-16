package com.sdsmdg.harjot.MusicDNA;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.Models.Track;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Harjot on 30-Apr-16.
 */
public class StreamTrackListAdapter extends BaseAdapter {

    private List<Track> tracks;
    private Context context;

    public StreamTrackListAdapter(Context ctx, List<Track> tracks) {
        super();
        context = ctx;
        this.tracks = tracks;
    }

    @Override
    public int getCount() {
        return this.tracks.size();
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.list_item, parent, false);
        TextView tr_title = (TextView) v.findViewById(R.id.title);
        //TextView tv_url = (TextView) v.findViewById(R.id.url);
        ImageView tr_img = (ImageView) v.findViewById(R.id.img);
        Track track = tracks.get(position);
        tr_title.setText(track.getTitle());
        try {
            if (track.getArtworkURL() != null)
                Picasso.with(context).load(track.getArtworkURL()).resize(100, 100).into(tr_img);
            else{
                tr_img.setImageResource(R.drawable.ic_default);
            }
            Log.d("URL", track.getArtworkURL());
        } catch (Exception e) {
            Log.e("AdapterError", e.getMessage());
        }
        return v;
    }
}
