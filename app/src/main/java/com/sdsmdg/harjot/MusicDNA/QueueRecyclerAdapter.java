package com.sdsmdg.harjot.MusicDNA;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.Models.Track;
import com.sdsmdg.harjot.MusicDNA.Models.UnifiedTrack;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Harjot on 16-May-16.
 */


public class QueueRecyclerAdapter extends RecyclerView.Adapter<QueueRecyclerAdapter.MyViewHolder> {

    private List<UnifiedTrack> queue;
    Context ctx;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView art;
        TextView title, artist;

        public MyViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.img);
            title = (TextView) view.findViewById(R.id.title);
            artist = (TextView) view.findViewById(R.id.url);
        }
    }

    public QueueRecyclerAdapter(List<UnifiedTrack> queue, Context ctx) {
        this.queue = queue;
        this.ctx = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if(HomeActivity.queueCurrentIndex == position){
            holder.title.setTextColor(Color.RED);
        }
        else{
            holder.title.setTextColor(Color.BLACK);
        }

        UnifiedTrack ut = queue.get(position);
        if (ut.getType()) {
            LocalTrack lt = ut.getLocalTrack();
            Bitmap img = getAlbumArt(lt.getPath());
            if (img != null) {
                holder.art.setImageBitmap(img);
            } else {
                holder.art.setImageResource(R.drawable.ic_default);
            }
            holder.title.setText(lt.getTitle());
            holder.artist.setText(lt.getArtist());
        } else {
            Track t = ut.getStreamTrack();
            if(t.getArtworkURL()!=null){
                Picasso.with(ctx).load(t.getArtworkURL()).into(holder.art);
            }
            else{
                holder.art.setImageResource(R.drawable.ic_default);
            }
            holder.title.setText(t.getTitle());
            holder.artist.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return queue.size();
    }

    public static Bitmap getAlbumArt(String path) {
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        Bitmap bitmap = null;

        byte[] data = mmr.getEmbeddedPicture();
        if (data != null) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            return bitmap;
        } else {
            return null;
        }
    }

}
