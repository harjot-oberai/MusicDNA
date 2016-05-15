package com.example.harjot.musicstreamer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.harjot.musicstreamer.Models.Playlist;

import java.util.List;

/**
 * Created by Harjot on 15-May-16.
 */
public class PlayListsHorizontalAdapter extends RecyclerView.Adapter<PlayListsHorizontalAdapter.MyViewHolder> {

    List<Playlist> playlists;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView playlistBack;
        TextView playlistName, playlistSize;
        RelativeLayout bottomHolder;

        public MyViewHolder(View view) {
            super(view);
            playlistBack = (ImageView) view.findViewById(R.id.backImage);
            playlistName = (TextView) view.findViewById(R.id.card_title);
            playlistSize = (TextView) view.findViewById(R.id.card_artist);
            bottomHolder = (RelativeLayout) view.findViewById(R.id.bottomHolder);
        }
    }

    public PlayListsHorizontalAdapter(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Playlist pl = playlists.get(position);
        holder.playlistBack.setImageResource(R.drawable.ic_play_arrow_black_48dp);
        holder.playlistName.setText(pl.getPlaylistName());
        holder.playlistSize.setText(String.valueOf(pl.getSongList().size()));

    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }
}
