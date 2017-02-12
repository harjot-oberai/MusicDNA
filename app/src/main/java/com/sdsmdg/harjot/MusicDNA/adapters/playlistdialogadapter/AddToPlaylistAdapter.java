package com.sdsmdg.harjot.MusicDNA.adapters.playlistdialogadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.models.Playlist;
import com.sdsmdg.harjot.MusicDNA.R;

import java.util.List;

/**
 * Created by Harjot on 15-May-16.
 */
public class AddToPlaylistAdapter extends BaseAdapter {

    List<Playlist> allPlaylists;
    Context ctx;

    public AddToPlaylistAdapter(List<Playlist> allPlaylists, Context ctx) {
        this.allPlaylists = allPlaylists;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return allPlaylists.size();
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
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.playlist_list_row, parent, false);

        Playlist pl = allPlaylists.get(position);

        TextView playlistName = (TextView) v.findViewById(R.id.playlist_name_holder);
        playlistName.setText(pl.getPlaylistName());

        return v;
    }
}
