package com.sdsmdg.harjot.MusicDNA.fragments.NewPlaylistFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.imageloader.ImageLoader;

import java.util.List;

/**
 * Created by Harjot on 30-Sep-16.
 */
public class NewPlaylistRecyclerAdapter extends RecyclerView.Adapter<NewPlaylistRecyclerAdapter.MyViewHolder> {

    private List<LocalTrack> localTracks;
    private Context ctx;
    ImageLoader imgLoader;

    public NewPlaylistRecyclerAdapter(List<LocalTrack> localTracks, Context ctx) {
        this.localTracks = localTracks;
        this.ctx = ctx;
        this.imgLoader = new ImageLoader(ctx);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_5, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LocalTrack track = localTracks.get(position);
        holder.title.setText(track.getTitle());
        holder.artist.setText(track.getArtist());

        LocalTrack lt = localTracks.get(position);
        if (HomeActivity.finalSelectedTracks.contains(lt)) {
            holder.cb.setChecked(true);
        } else {
            holder.cb.setChecked(false);
        }

        imgLoader.DisplayImage(track.getPath(), holder.art);
    }

    @Override
    public int getItemCount() {
        return localTracks.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView art;
        TextView title, artist;
        CheckBox cb;

        public MyViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.img_2);
            title = (TextView) view.findViewById(R.id.title_2);
            artist = (TextView) view.findViewById(R.id.url_2);
            cb = (CheckBox) view.findViewById(R.id.is_selected_checkbox);
        }
    }
}
