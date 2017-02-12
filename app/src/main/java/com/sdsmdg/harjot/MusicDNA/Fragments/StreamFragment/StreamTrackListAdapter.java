package com.sdsmdg.harjot.MusicDNA.fragments.StreamFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.models.Track;
import com.sdsmdg.harjot.MusicDNA.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Harjot on 30-Apr-16.
 */
public class StreamTrackListAdapter extends RecyclerView.Adapter<StreamTrackListAdapter.MyViewHolder> {

    private List<Track> tracks;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView art;
        TextView title, artist;

        public MyViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.img_2);
            title = (TextView) view.findViewById(R.id.title_2);
            artist = (TextView) view.findViewById(R.id.url_2);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_2, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.title.setText(track.getTitle());
        try {
            Picasso.with(context)
                    .load(track.getArtworkURL())
                    .resize(100, 100)
                    .error(R.drawable.ic_default)
                    .placeholder(R.drawable.ic_default)
                    .into(holder.art);
            Log.d("URL", track.getArtworkURL());
        } catch (Exception e) {
            Log.e("AdapterError", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }


    public StreamTrackListAdapter(Context ctx, List<Track> tracks) {
        super();
        context = ctx;
        this.tracks = tracks;
    }
}
