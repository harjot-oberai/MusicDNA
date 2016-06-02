package com.sdsmdg.harjot.MusicDNA;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.Models.MusicFolder;
import com.sdsmdg.harjot.MusicDNA.Models.Playlist;
import com.sdsmdg.harjot.MusicDNA.Models.UnifiedTrack;
import com.sdsmdg.harjot.MusicDNA.imageLoader.ImageLoader;

import java.util.List;

/**
 * Created by Harjot on 02-Jun-16.
 */
public class FolderRecyclerAdapter extends RecyclerView.Adapter<FolderRecyclerAdapter.MyViewHolder> {

    List<MusicFolder> musicFolders;
    ImageLoader imgLoader;

    public FolderRecyclerAdapter(List<MusicFolder> musicFolders) {
        this.musicFolders = musicFolders;
        imgLoader = new ImageLoader(HomeActivity.ctx);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView[] img = new ImageView[4];
        TextView playListName;
        TextView continuedSymbol;
        TextView[] name = new TextView[4];

        public MyViewHolder(View itemView) {
            super(itemView);
            img[0] = (ImageView) itemView.findViewById(R.id.image1);
            img[1] = (ImageView) itemView.findViewById(R.id.image2);
            img[2] = (ImageView) itemView.findViewById(R.id.image3);
            img[3] = (ImageView) itemView.findViewById(R.id.image4);
            playListName = (TextView) itemView.findViewById(R.id.playlist_name);
            name[0] = (TextView) itemView.findViewById(R.id.name1);
            name[1] = (TextView) itemView.findViewById(R.id.name2);
            name[2] = (TextView) itemView.findViewById(R.id.name3);
            name[3] = (TextView) itemView.findViewById(R.id.name4);
            continuedSymbol = (TextView) itemView.findViewById(R.id.name5);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_custom_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        MusicFolder mf = musicFolders.get(position);
        List<LocalTrack> list = mf.getLocalTracks();
        holder.playListName.setText(mf.getFolderName() + " (" + mf.getLocalTracks().size() + ")");
        if (list.size() >= 4) {
            String[] names = new String[4];
            for (int i = 0; i < 4; i++) {
                names[i] = list.get(i).getTitle();
                imgLoader.DisplayImage(list.get(i).getPath(), holder.img[i]);
                holder.name[i].setText(names[i]);
            }
            holder.continuedSymbol.setVisibility(View.VISIBLE);
        } else {
            int sz = list.size();
            String[] names = new String[4];
            for (int i = 0; i < sz; i++) {
                names[i] = list.get(i).getTitle();
                imgLoader.DisplayImage(list.get(i).getPath(), holder.img[i]);
                holder.name[i].setText(names[i]);
            }
        }
    }

    @Override
    public int getItemCount() {
        return musicFolders.size();
    }

}
