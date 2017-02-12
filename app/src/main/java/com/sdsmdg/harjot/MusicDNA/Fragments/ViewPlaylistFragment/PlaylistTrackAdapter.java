package com.sdsmdg.harjot.MusicDNA.fragments.ViewPlaylistFragment;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.itemtouchhelpers.ItemTouchHelperAdapter;
import com.sdsmdg.harjot.MusicDNA.itemtouchhelpers.ItemTouchHelperViewHolder;
import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.models.Track;
import com.sdsmdg.harjot.MusicDNA.models.UnifiedTrack;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.imageloader.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Harjot on 20-May-16.
 */
public class PlaylistTrackAdapter extends RecyclerView.Adapter<PlaylistTrackAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {

    private List<UnifiedTrack> songList;
    private Context ctx;
    ImageLoader imgLoader;
    HomeActivity homeActivity;

    public interface OnDragStartListener {
        void onDragStarted(RecyclerView.ViewHolder viewHolder);
    }

    public interface OnMoveRemoveListener {
        void updateViewPlaylistFragment();
    }

    public interface onPlaylistEmptyListener {
        void onPlaylistEmpty();
    }

    private final OnDragStartListener mDragStartListener;
    public onPlaylistEmptyListener mCallback;
    public OnMoveRemoveListener mCallback2;

    public PlaylistTrackAdapter(List<UnifiedTrack> songList, ViewPlaylistFragment fragContext, Context ctx) {
        this.songList = songList;
        mDragStartListener = fragContext;
        mCallback2 = fragContext;

        mCallback = (onPlaylistEmptyListener) ctx;
        this.ctx = ctx;
        homeActivity = (HomeActivity) ctx;
        imgLoader = new ImageLoader(ctx);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        UnifiedTrack prev = songList.remove(fromPosition);
        songList.add(toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
        if (homeActivity.pAdapter != null)
            homeActivity.pAdapter.notifyDataSetChanged();

        homeActivity.updateAllPlaylistFragment();
        mCallback2.updateViewPlaylistFragment();

        new HomeActivity.SavePlaylists().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onItemDismiss(int position) {
        songList.remove(position);
        notifyItemRemoved(position);
        if (songList.size() == 0) {
            ((HomeActivity) ctx).onBackPressed();
            homeActivity.allPlaylists.getPlaylists().remove(homeActivity.tempPlaylistNumber);
            mCallback.onPlaylistEmpty();
        } else if (homeActivity.pAdapter != null) {
            homeActivity.pAdapter.notifyDataSetChanged();
            mCallback2.updateViewPlaylistFragment();
        }

        homeActivity.updateAllPlaylistFragment();

        new HomeActivity.SavePlaylists().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder {

        ImageView art;
        TextView title, artist;
        View indicator;
        ImageView holderImg;

        public MyViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.img);
            title = (TextView) view.findViewById(R.id.title);
            artist = (TextView) view.findViewById(R.id.url);
            indicator = view.findViewById(R.id.currently_playing_indicator);
            holderImg = (ImageView) view.findViewById(R.id.holderImage);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.parseColor("#444444"));
            if (Build.VERSION.SDK_INT >= 21) {
                itemView.setTranslationZ(12);
            }
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.parseColor("#111111"));
            if (Build.VERSION.SDK_INT >= 21) {
                itemView.setTranslationZ(0);
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlaylistTrackAdapter.MyViewHolder holder, int position) {
        UnifiedTrack ut = songList.get(position);
        if (ut.getType()) {
            LocalTrack lt = ut.getLocalTrack();
            imgLoader.DisplayImage(lt.getPath(), holder.art);
            holder.title.setText(lt.getTitle());
            holder.artist.setText(lt.getArtist());
        } else {
            Track t = ut.getStreamTrack();
            Picasso.with(ctx)
                    .load(t.getArtworkURL())
                    .resize(100, 100)
                    .error(R.drawable.ic_default)
                    .placeholder(R.drawable.ic_default)
                    .into(holder.art);
            holder.title.setText(t.getTitle());
            holder.artist.setText("");
        }

        holder.holderImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onDragStarted(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

}
