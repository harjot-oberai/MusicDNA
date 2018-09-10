package com.sdsmdg.harjot.MusicDNA.adapters.horizontalrecycleradapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.imageloader.ImageLoader;
import com.sdsmdg.harjot.MusicDNA.models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.models.Track;
import com.sdsmdg.harjot.MusicDNA.models.UnifiedTrack;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Harjot on 15-May-16.
 */
public class RecentsListHorizontalAdapter extends RecyclerView.Adapter<RecentsListHorizontalAdapter.MyViewHolder> {

    private List<UnifiedTrack> recentlyPlayed;
    Context ctx;
    ImageLoader imgLoader;

    public RecentsListHorizontalAdapter(List<UnifiedTrack> recentlyPlayed, Context ctx) {
        this.recentlyPlayed = recentlyPlayed;
        this.ctx = ctx;
        imgLoader = new ImageLoader(ctx);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        UnifiedTrack ut = recentlyPlayed.get(position);
        if (ut.getType()) {
            LocalTrack lt = ut.getLocalTrack();
            imgLoader.DisplayImage(lt.getPath(), holder.art);
            holder.title.setText(lt.getTitle());
            holder.artist.setText(lt.getArtist());
        } else {
            Track t = ut.getStreamTrack();
            Picasso.get()
                    .load(t.getArtworkURL())
                    .resize(100, 100)
                    .error(R.drawable.ic_default)
                    .placeholder(R.drawable.ic_default)
                    .into(holder.art);
            holder.title.setText(t.getTitle());
            holder.artist.setText("");
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_layout2, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return recentlyPlayed.size();
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
}
