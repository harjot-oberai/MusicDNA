package com.sdsmdg.harjot.MusicDNA;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.Models.SavedDNA;
import com.sdsmdg.harjot.MusicDNA.Models.Track;
import com.sdsmdg.harjot.MusicDNA.imageLoader.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Harjot on 03-Jun-16.
 */
public class ViewSavedDnaRecyclerAdapter extends RecyclerView.Adapter<ViewSavedDnaRecyclerAdapter.MyViewHolder> {

    private List<SavedDNA> savedDNAs;
    ImageLoader imgLoader;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        SavedDNA dna = savedDNAs.get(position);
        if (dna.getModel().getType()) {
            LocalTrack lt = dna.getModel().getLocalTrack();
            imgLoader.DisplayImage(lt.getPath(), holder.art);
            holder.title.setText(lt.getTitle());
            holder.artist.setText(lt.getArtist());
        } else {
            Track t = dna.getModel().getTrack();
            Picasso.with(HomeActivity.ctx)
                    .load(t.getArtworkURL())
                    .resize(100, 100)
                    .error(R.drawable.ic_default)
                    .placeholder(R.drawable.ic_default)
                    .into(holder.art);
            holder.title.setText(t.getTitle());
            holder.artist.setText("");
        }

        if (position == ViewSavedDNA.selectedDNA) {
            holder.title.setTextColor(Color.parseColor("#DE1A1A"));
        } else {
            holder.title.setTextColor(Color.parseColor("#444444"));
        }

    }

    @Override
    public int getItemCount() {
        return savedDNAs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView art;
        TextView title, artist;
        RelativeLayout bottomHolder;

        public MyViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.backImage);
            title = (TextView) view.findViewById(R.id.card_title);
            artist = (TextView) view.findViewById(R.id.card_artist);
            bottomHolder = (RelativeLayout) view.findViewById(R.id.bottomHolder);
        }
    }

    public ViewSavedDnaRecyclerAdapter(List<SavedDNA> savedDNAs) {
        this.savedDNAs = savedDNAs;
        imgLoader = new ImageLoader(HomeActivity.ctx);
    }


}
