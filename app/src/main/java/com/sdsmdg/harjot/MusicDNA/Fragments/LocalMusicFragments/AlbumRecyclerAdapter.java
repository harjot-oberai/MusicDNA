package com.sdsmdg.harjot.MusicDNA.fragments.LocalMusicFragments;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.models.Album;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.imageloader.ImageLoader;

import java.util.List;

/**
 * Created by Harjot on 16-Jun-16.
 */
public class AlbumRecyclerAdapter extends RecyclerView.Adapter<AlbumRecyclerAdapter.MyViewHolder> {

    List<Album> albumList;
    Context ctx;
    ImageLoader imgLoader;

    public AlbumRecyclerAdapter(List<Album> albumList, Context ctx) {
        this.albumList = albumList;
        this.ctx = ctx;
        imgLoader = new ImageLoader(ctx);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_layout3, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Album ab = albumList.get(position);
        holder.title.setText(ab.getName());
        if (ab.getAlbumSongs().size() > 1)
            holder.artist.setText(ab.getAlbumSongs().size() + " Songs");
        else
            holder.artist.setText(ab.getAlbumSongs().size() + " Song");
        holder.title.setTextColor(Color.parseColor("#DDDDDD"));
        holder.artist.setTextColor(Color.parseColor("#BBBBBB"));
        imgLoader.DisplayImage(ab.getAlbumSongs().get(0).getPath(), holder.art);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
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

}
