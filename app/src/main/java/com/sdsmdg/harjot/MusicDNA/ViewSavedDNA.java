package com.sdsmdg.harjot.MusicDNA;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.sdsmdg.harjot.MusicDNA.Models.Playlist;
import com.sdsmdg.harjot.MusicDNA.Models.SavedDNA;
import com.sdsmdg.harjot.MusicDNA.Models.UnifiedTrack;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewSavedDNA extends Fragment {

    static RecyclerView viewDnaRecycler;
    static ViewSavedDnaRecyclerAdapter vdAdapter;

    static VisualizerView2 mVisualizerView2;

    ImageView shareIcon, saveToStorageIcon;

    static onShareListener mCallback;

    static int selectedDNA = 0;

    public ViewSavedDNA() {
        // Required empty public constructor
    }

    public interface onShareListener {
        void onShare(Bitmap bmp, String fileName);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (onShareListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_saved_dn, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewDnaRecycler = (RecyclerView) view.findViewById(R.id.saved_dna_recycler);
        vdAdapter = new ViewSavedDnaRecyclerAdapter(HomeActivity.savedDNAs.getSavedDNAs());
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(HomeActivity.ctx, LinearLayoutManager.HORIZONTAL, false);
        viewDnaRecycler.setLayoutManager(mLayoutManager2);
        viewDnaRecycler.setItemAnimator(new DefaultItemAnimator());
        viewDnaRecycler.setAdapter(vdAdapter);

        mVisualizerView2 = (VisualizerView2) view.findViewById(R.id.saved_dna_visualizer);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (HomeActivity.savedDNAs.getSavedDNAs().size() > 0) {
                    SavedDNA dna = HomeActivity.savedDNAs.getSavedDNAs().get(0);
                    selectedDNA = 0;
                    HomeActivity.tempSavedDNA = dna;
                    mVisualizerView2.setPts(dna.getModel().getPts());
                    mVisualizerView2.setPtPaint(dna.getModel().getPtPaint());
                    mVisualizerView2.setBmp(dna.getModel().getBmp());
                    mVisualizerView2.update();
                }
            }
        }, 350);

        viewDnaRecycler.addOnItemTouchListener(new ClickItemTouchListener(viewDnaRecycler) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                vdAdapter.notifyItemChanged(selectedDNA);
                selectedDNA = position;
                vdAdapter.notifyItemChanged(selectedDNA);
                SavedDNA dna = HomeActivity.savedDNAs.getSavedDNAs().get(position);
                selectedDNA = position;
                HomeActivity.tempSavedDNA = dna;
                mVisualizerView2.setPts(dna.getModel().getPts());
                mVisualizerView2.setPtPaint(dna.getModel().getPtPaint());
                mVisualizerView2.update();
                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                PopupMenu popup = new PopupMenu(HomeActivity.ctx, view);
                popup.getMenuInflater().inflate(R.menu.save_dna_popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("View")) {
                            vdAdapter.notifyItemChanged(selectedDNA);
                            selectedDNA = position;
                            vdAdapter.notifyItemChanged(selectedDNA);
                            SavedDNA dna = HomeActivity.savedDNAs.getSavedDNAs().get(position);
                            selectedDNA = position;
                            HomeActivity.tempSavedDNA = dna;
                            mVisualizerView2.setPts(dna.getModel().getPts());
                            mVisualizerView2.setPtPaint(dna.getModel().getPtPaint());
                            mVisualizerView2.update();
                        } else if (item.getTitle().equals("Delete")) {
                            HomeActivity.savedDNAs.getSavedDNAs().remove(position);
                            vdAdapter.notifyItemRemoved(position);
                        }
                        return true;
                    }
                });
                popup.show();
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        shareIcon = (ImageView) view.findViewById(R.id.share_icon);
        saveToStorageIcon = (ImageView) view.findViewById(R.id.save_to_storage_icon);

        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1);
            }
        });

        saveToStorageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HomeActivity.tempSavedDNA != null) {
                    showDialog(0);
                }
            }
        });

    }

    public void showDialog(int type) {
        if (type == 0) {
            final Dialog dialog = new Dialog(HomeActivity.ctx);
            dialog.setContentView(R.layout.save_image_dialog);
            dialog.setTitle("Save as Image");

            // set the custom dialog components - text, image and button
            final EditText text = (EditText) dialog.findViewById(R.id.save_image_filename_text);
            text.setText(HomeActivity.tempSavedDNA.getName());
            Button btn = (Button) dialog.findViewById(R.id.save_image_btn);
            // if button is clicked, close the custom dialog
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (text.getText().toString().trim().equals("")) {
                        text.setError("Enter Filename");
                    } else {
                        mVisualizerView2.drawText(text.getText().toString());
                        mVisualizerView2.setDrawingCacheEnabled(true);
                        HomeActivity.saveBitmapAsImage(mVisualizerView2.getDrawingCache(), text.getText().toString());
                        mVisualizerView2.dropText();
                        mVisualizerView2.setDrawingCacheEnabled(false);
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        } else if (type == 1) {
            final Dialog dialog = new Dialog(HomeActivity.ctx);
            dialog.setContentView(R.layout.save_image_dialog);
            dialog.setTitle("Share as Image");

            // set the custom dialog components - text, image and button
            final EditText text = (EditText) dialog.findViewById(R.id.save_image_filename_text);
            text.setText(HomeActivity.tempSavedDNA.getName());
            Button btn = (Button) dialog.findViewById(R.id.save_image_btn);
            btn.setText("SHARE");
            // if button is clicked, close the custom dialog
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (text.getText().toString().trim().equals("")) {
                        text.setError("Enter Filename");
                    } else {
                        mVisualizerView2.drawText(text.getText().toString());
                        mVisualizerView2.setDrawingCacheEnabled(true);
                        mCallback.onShare(mVisualizerView2.getDrawingCache(), text.getText().toString());
                        mVisualizerView2.dropText();
                        mVisualizerView2.setDrawingCacheEnabled(false);
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        }
    }

}
