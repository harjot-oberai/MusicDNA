package com.sdsmdg.harjot.MusicDNA;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.Fragment;
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
        if (dna.getModel().getType()) {
            LocalTrack lt = dna.getModel().getLocalTrack();
            imgLoader.DisplayImage(lt.getPath(), holder.art);
            holder.title.setText(lt.getTitle());
            holder.artist.setText(lt.getArtist());
        } else {
            Track t = dna.getModel().getTrack();
            Picasso.with(ctx)
                    .load(t.getArtworkURL())
                    .resize(100, 100)
                    .error(R.drawable.ic_default)
                    .placeholder(R.drawable.ic_default)
                    .into(holder.art);
            holder.title.setText(t.getTitle());
            holder.artist.setText("");
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
        RelativeLayout bottomHolder;

        public MyViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.backImage);
            title = (TextView) view.findViewById(R.id.card_title);
            artist = (TextView) view.findViewById(R.id.card_artist);
            bottomHolder = (RelativeLayout) view.findViewById(R.id.bottomHolder);
        }
    }

    public ViewSavedDnaRecyclerAdapter(List<SavedDNA> savedDNAs, Context ctx, ViewSavedDNA vsdFrag) {
        this.savedDNAs = savedDNAs;
        this.ctx = ctx;
        this.vsdFrag = vsdFrag;
        imgLoader = new ImageLoader(ctx);
    }


}
