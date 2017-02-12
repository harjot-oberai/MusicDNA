package com.sdsmdg.harjot.MusicDNA.itemtouchhelpers;

/**
 * Created by Harjot on 18-May-16.
 */
public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
