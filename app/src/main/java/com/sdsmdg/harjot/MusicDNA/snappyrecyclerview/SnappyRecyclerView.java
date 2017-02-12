package com.sdsmdg.harjot.MusicDNA.snappyrecyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;

/**
 * Created by Harjot on 22-Nov-16.
 */

public class SnappyRecyclerView extends RecyclerView {

    private int currentPosition;
    HomeActivity homeActivity;
    Context ctx;

    // Use it with a horizontal LinearLayoutManager
    // Based on http://stackoverflow.com/a/29171652/4034572

    public SnappyRecyclerView(Context context) {
        super(context);
        ctx = context;
    }

    public SnappyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
    }

    public SnappyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ctx = context;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        // views on the screen
        int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
        View lastView = linearLayoutManager.findViewByPosition(lastVisibleItemPosition);
        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        View firstView = linearLayoutManager.findViewByPosition(firstVisibleItemPosition);

        // distance we need to scroll
        int leftMargin = (screenWidth - lastView.getWidth()) / 2;
        int rightMargin = (screenWidth - firstView.getWidth()) / 2 + firstView.getWidth();
        int leftEdge = lastView.getLeft();
        int rightEdge = firstView.getRight();
        int scrollDistanceLeft = leftEdge - leftMargin;
        int scrollDistanceRight = rightMargin - rightEdge;

        if (Math.abs(velocityX) < 1000) {
            // The fling is slow -> stay at the current page if we are less than half through,
            // or go to the next page if more than half through

            if (leftEdge > screenWidth / 2) {
                // go to next page
                smoothScrollBy(-scrollDistanceRight, 0);
            } else if (rightEdge < screenWidth / 2) {
                // go to next page
                smoothScrollBy(scrollDistanceLeft, 0);
            } else {
                // stay at current page
                if (velocityX > 0) {
                    smoothScrollBy(-scrollDistanceRight, 0);
                } else {
                    smoothScrollBy(scrollDistanceLeft, 0);
                }
            }
//            handleActions();
            return true;

        } else {
            // The fling is fast -> go to next page

            if (velocityX > 0) {
                smoothScrollBy(scrollDistanceLeft, 0);
            } else {
                smoothScrollBy(-scrollDistanceRight, 0);
            }
//            handleActions();
            return true;

        }

    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        // If you tap on the phone while the RecyclerView is scrolling it will stop in the middle.
        // This code fixes this. This code is not strictly necessary but it improves the behaviour.

        if (state == SCROLL_STATE_IDLE) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();

            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

            // views on the screen
            int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            View lastView = linearLayoutManager.findViewByPosition(lastVisibleItemPosition);
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            View firstView = linearLayoutManager.findViewByPosition(firstVisibleItemPosition);

            // distance we need to scroll
            int leftMargin = (screenWidth - lastView.getWidth()) / 2;
            int rightMargin = (screenWidth - firstView.getWidth()) / 2 + firstView.getWidth();
            int leftEdge = lastView.getLeft();
            int rightEdge = firstView.getRight();
            int scrollDistanceLeft = leftEdge - leftMargin;
            int scrollDistanceRight = rightMargin - rightEdge;

            if (leftEdge > screenWidth / 2) {
                smoothScrollBy(-scrollDistanceRight, 0);
            } else if (rightEdge < screenWidth / 2) {
                smoothScrollBy(scrollDistanceLeft, 0);
            }

            handleActions();

        }
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void handleActions() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

        currentPosition = firstVisibleItemPosition;
        if (currentPosition > HomeActivity.queueCurrentIndex) {
            getAdapter().notifyDataSetChanged();
            homeActivity.onQueueItemClicked2(currentPosition);
        } else if (currentPosition < HomeActivity.queueCurrentIndex) {
            getAdapter().notifyDataSetChanged();
            homeActivity.onQueueItemClicked2(currentPosition);
        } else {
//            setTransparency();
        }
    }

    public void setTransparency() {
        final CustomAdapter.ViewHolder viewHolder = (CustomAdapter.ViewHolder) findViewHolderForAdapterPosition(currentPosition);
        if (homeActivity.settings != null) {
            if (homeActivity.settings.isAlbumArtBackgroundEnabled()) {
                if (viewHolder != null && viewHolder.albumArt != null)
                    viewHolder.albumArt.animate()
                            .alpha(0.20f);
            } else {
                if (viewHolder != null && viewHolder.albumArt != null)
                    viewHolder.albumArt.animate()
                            .alpha(0.0f);
            }
        }
    }

    public void setActivity(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

}
