package com.example.harjot.musicstreamer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.harjot.musicstreamer.Models.LocalTrack;

import java.util.List;

/**
 * Created by Harjot on 14-May-16.
 */
public class LocalTracksHorizontalAdapter extends RecyclerView.Adapter<LocalTracksHorizontalAdapter.MyViewHolder> {

    private List<LocalTrack> localList;
    int def = 0x000000;

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

    public LocalTracksHorizontalAdapter(List<LocalTrack> localList) {
        this.localList = localList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        LocalTrack localTrack = localList.get(position);
        Bitmap img = getAlbumArt(localTrack.getPath());
        if (img != null) {
            holder.art.setImageBitmap(img);
            if (img != null && !img.isRecycled()) {
                Palette palette = Palette.from(img).generate();
                if (palette.getLightMutedColor(def) != 0) {
                    holder.bottomHolder.setBackgroundColor(palette.getLightMutedColor(def));
                } else {
                    holder.bottomHolder.setBackgroundColor(Color.WHITE);
                }
//                Log.d("MUTED", palette.getLightMutedColor(def) + "");
                if (palette.getDarkMutedColor(def) != 0) {
                    holder.title.setTextColor(palette.getDarkMutedColor(def));
                    holder.artist.setTextColor(palette.getDarkMutedColor(def));
                } else {
                    holder.title.setTextColor(Color.parseColor("#444444"));
                    holder.artist.setTextColor(Color.parseColor("#777777"));
                }
            }
        } else {
            holder.art.setImageResource(R.drawable.ic_default);
            holder.bottomHolder.setBackgroundColor(Color.WHITE);
            holder.title.setTextColor(Color.parseColor("#444444"));
            holder.artist.setTextColor(Color.parseColor("#777777"));
        }
        holder.title.setText(localTrack.getTitle());
        holder.artist.setText(localTrack.getArtist());

    }

    @Override
    public int getItemCount() {
        return localList.size();
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

    int ContrastColor(Color color) {
        int d = 0;

        // Counting the perceptive luminance - human eye favors green color...
        double a = 1 - (0.299 * color.RED + 0.587 * color.GREEN + 0.114 * color.BLUE) / 255;

        if (a < 0.5)
            d = 0; // bright colors - black font
        else
            d = 255; // dark colors - white font

        return Color.rgb(d, d, d);
    }

}
