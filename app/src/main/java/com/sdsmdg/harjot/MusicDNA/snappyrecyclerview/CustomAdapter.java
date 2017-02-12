package com.sdsmdg.harjot.MusicDNA.snappyrecyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.mmin18.widget.RealtimeBlurView;
import com.sdsmdg.harjot.MusicDNA.models.UnifiedTrack;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.imageloader.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Harjot on 22-Nov-16.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    Context ctx;
    SnappyRecyclerView recyclerView;
    List<UnifiedTrack> queue;
    ImageLoader imgLoader;

    public CustomAdapter(Context ctx, SnappyRecyclerView recyclerView, List<UnifiedTrack> queue) {
        this.ctx = ctx;
        this.recyclerView = recyclerView;
        this.queue = queue;
        imgLoader = new ImageLoader(ctx);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_visualizer_element, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UnifiedTrack ut = queue.get(position);
        holder.albumArt.setAlpha(0.35f);
        if (ut.getType()) {
            Bitmap bmp = getBitmap(ut.getLocalTrack().getPath());
            if (bmp != null) {
                holder.albumArt.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.albumArt.setImageBitmap(bmp);
            } else {
                holder.albumArt.setScaleType(ImageView.ScaleType.CENTER);
                holder.albumArt.setImageResource(R.drawable.ic_default);
            }
        } else {
            Picasso.with(ctx).load(ut.getStreamTrack().getArtworkURL()).into(holder.albumArt);
        }
    }

    @Override
    public int getItemCount() {
        return queue.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView albumArt;
        public RealtimeBlurView blurredAlbumArt;

        public ViewHolder(View itemView) {
            super(itemView);
            albumArt = (ImageView) itemView.findViewById(R.id.album_art_container_v);
            blurredAlbumArt = (RealtimeBlurView) itemView.findViewById(R.id.blurred_album_art);
        }
    }

    public Bitmap getBitmap(String url) {
        try {
            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(url);
            Bitmap bitmap = null;

            byte[] data = mmr.getEmbeddedPicture();

            if (data != null) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                return bitmap;
            } else {
                return null;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
