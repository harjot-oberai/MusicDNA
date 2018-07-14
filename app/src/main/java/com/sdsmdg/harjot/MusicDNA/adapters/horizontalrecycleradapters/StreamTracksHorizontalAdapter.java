package com.sdsmdg.harjot.MusicDNA.adapters.horizontalrecycleradapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.imageLoader.ImageLoader;
import com.sdsmdg.harjot.MusicDNA.models.Track;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Harjot on 15-May-16.
 */
public class StreamTracksHorizontalAdapter extends RecyclerView.Adapter<StreamTracksHorizontalAdapter.MyViewHolder> {

    List<Track> streamList;
    Context ctx;
    ImageLoader imgLoader;

    @Override
    public void onBindViewHolder(StreamTracksHorizontalAdapter.MyViewHolder holder, int position) {

        Track track = streamList.get(position);
        try {
            if (track.getArtworkURL() != null) {
                Log.d("ARTWORK_URL", track.getArtworkURL());
                Picasso.get()
                        .load(track.getArtworkURL())
                        .error(R.drawable.ic_default)
                        .placeholder(R.drawable.ic_default)
                        .into(holder.art);
            } else {
                holder.art.setImageResource(R.drawable.ic_default);
            }

        } catch (Exception e) {
            Log.e("AdapterError", e.getMessage());
        }
        holder.title.setText(track.getTitle());
        holder.artist.setText("");
    }

    public StreamTracksHorizontalAdapter(List<Track> streamList, Context ctx) {
        this.streamList = streamList;
        this.ctx = ctx;
        imgLoader = new ImageLoader(ctx);
    }

    @Override
    public StreamTracksHorizontalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_layout2, parent, false);

        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView art;
        TextView title, artist;
        RelativeLayout bottomHolder;

        public MyViewHolder(View view) {
            super(view);
            art = view.findViewById(R.id.backImage);
            title = view.findViewById(R.id.card_title);
            artist = view.findViewById(R.id.card_artist);
            bottomHolder = view.findViewById(R.id.bottomHolder);
        }
    }

    @Override
    public int getItemCount() {
        return streamList.size();
    }
}
