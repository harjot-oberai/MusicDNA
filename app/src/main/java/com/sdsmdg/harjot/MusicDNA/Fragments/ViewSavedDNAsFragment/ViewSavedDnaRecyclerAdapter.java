package com.sdsmdg.harjot.MusicDNA.fragments.ViewSavedDNAsFragment;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.models.SavedDNA;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.imageloader.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Harjot on 03-Jun-16.
 */
public class ViewSavedDnaRecyclerAdapter extends RecyclerView.Adapter<ViewSavedDnaRecyclerAdapter.MyViewHolder> {

    private List<SavedDNA> savedDNAs;
    private Context ctx;
    ImageLoader imgLoader;
    ViewSavedDNA vsdFrag;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        SavedDNA dna = savedDNAs.get(position);
        if (dna.getType()) {
            imgLoader.DisplayImage(dna.getLocalPath(), holder.art);
            holder.title.setText(dna.getName());
            holder.artist.setText(dna.getArtist());
        } else {
            Picasso.with(ctx)
                    .load(dna.getTrackArtworkURL())
                    .resize(100, 100)
                    .error(R.drawable.ic_default)
                    .placeholder(R.drawable.ic_default)
                    .into(holder.art);
            holder.title.setText(dna.getName());
            holder.artist.setText(dna.getArtist());
        }

        if (position == vsdFrag.getSelectedDNAnumber()) {
            holder.title.setTextColor(HomeActivity.themeColor);
        } else {
            holder.title.setTextColor(Color.parseColor("#FFFFFF"));
        }

    }

    @Override
    public int getItemCount() {
        return savedDNAs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView art;
        TextView title, artist;

        public MyViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.backImage);
            title = (TextView) view.findViewById(R.id.card_title);
            artist = (TextView) view.findViewById(R.id.card_artist);
        }
    }

    public ViewSavedDnaRecyclerAdapter(List<SavedDNA> savedDNAs, Context ctx, ViewSavedDNA vsdFrag) {
        this.savedDNAs = savedDNAs;
        this.ctx = ctx;
        this.vsdFrag = vsdFrag;
        imgLoader = new ImageLoader(ctx);
    }


}
