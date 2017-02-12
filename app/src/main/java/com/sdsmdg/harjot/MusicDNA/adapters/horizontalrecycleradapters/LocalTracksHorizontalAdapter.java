package com.sdsmdg.harjot.MusicDNA.adapters.horizontalrecycleradapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.imageloader.ImageLoader;

import java.util.List;

/**
 * Created by Harjot on 14-May-16.
 */
public class LocalTracksHorizontalAdapter extends RecyclerView.Adapter<LocalTracksHorizontalAdapter.MyViewHolder> {

    private List<LocalTrack> localList;
    private Context ctx;
    ImageLoader imgLoader;

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

    public LocalTracksHorizontalAdapter(List<LocalTrack> localList, Context ctx) {
        this.localList = localList;
        this.ctx = ctx;
        imgLoader = new ImageLoader(ctx);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_layout2, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        LocalTrack localTrack = localList.get(position);
        imgLoader.DisplayImage(localTrack.getPath(), holder.art);
        holder.title.setText(localTrack.getTitle());
        holder.artist.setText(localTrack.getArtist());
    }

    @Override
    public int getItemCount() {
        return localList.size();
    }

}
