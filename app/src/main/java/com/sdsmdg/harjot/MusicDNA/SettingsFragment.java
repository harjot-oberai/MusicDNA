package com.sdsmdg.harjot.MusicDNA;


import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    CardView densitycard, themeCard;
    ImageView themeColorImg;
    SeekBar densitySeekbar;
    TextView densityText;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        densitycard = (CardView) view.findViewById(R.id.density_card);
        densitySeekbar = (SeekBar) view.findViewById(R.id.density_seekbar);
        densityText = (TextView) view.findViewById(R.id.density_text);
        densitySeekbar.setMax(100);
        densitySeekbar.setProgress(100 - (int)(HomeActivity.minAudioStrength * 100));
        densityText.setText(String.valueOf(densitySeekbar.getProgress()));
        densitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                HomeActivity.minAudioStrength = 1.0f - ((float) progress / (float) 100);
                densityText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        themeCard = (CardView) view.findViewById(R.id.theme_card);
        themeColorImg = (ImageView) view.findViewById(R.id.theme_color_img);
        themeColorImg.setBackgroundColor(HomeActivity.themeColor);
        themeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChromaDialog.Builder()
                        .initialColor(((ColorDrawable) themeColorImg.getBackground()).getColor())
                        .colorMode(ColorMode.RGB) // There's also ARGB and HSV
                        .onColorSelected(new ColorSelectListener() {
                            @Override
                            public void onColorSelected(int color) {
                                HomeActivity.themeColor = color;
                                HomeActivity.toolbar.setBackgroundColor(color);
                                themeColorImg.setBackgroundColor(color);
                                if (Build.VERSION.SDK_INT >= 21) {
                                    Window window = ((Activity)(HomeActivity.ctx)).getWindow();
                                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                    window.setStatusBarColor(color);
                                }
                            }
                        })
                        .create()
                        .show(getFragmentManager(), "ChromaDialog");
            }
        });
    }
}
