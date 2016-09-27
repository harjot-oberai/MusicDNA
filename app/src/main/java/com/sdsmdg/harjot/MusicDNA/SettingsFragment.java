package com.sdsmdg.harjot.MusicDNA;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.squareup.leakcanary.RefWatcher;

import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    CardView densitycard, themeCard, aboutCard;
    ImageView themeColorImg;
    SeekBar densitySeekbar;
    TextView densityText;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public interface onColorChangedListener {
        public void onColorChanged();
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
        densitySeekbar.setProgress(100 - (int) (HomeActivity.minAudioStrength * 100));
        densityText.setText(String.valueOf(densitySeekbar.getProgress()));
        densitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                HomeActivity.minAudioStrength = 1.0f - ((float) progress / (float) 100);
                HomeActivity.settings.setMinAudioStrength(HomeActivity.minAudioStrength);
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
                ColorPickerDialogBuilder
                        .with(getContext())
                        .setTitle("Choose color")
                        .initialColor(((ColorDrawable) themeColorImg.getBackground()).getColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(9)
                        .showColorPreview(true)
                        .lightnessSliderOnly()
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int color) {

                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int color, Integer[] allColors) {
                                HomeActivity.settings.setThemeColor(color);
                                HomeActivity.themeColor = color;
                                HomeActivity.toolbar.setBackgroundColor(color);
                                HomeActivity.fragmentToolbar.setBackgroundColor(color);
                                themeColorImg.setBackgroundColor(color);
                                if (Build.VERSION.SDK_INT >= 21) {
                                    Window window = ((Activity) (getContext())).getWindow();
                                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                    window.setStatusBarColor(color);
                                }
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .build()
                        .show();
            }
        });

        aboutCard = (CardView) view.findViewById(R.id.about_card);
        aboutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("About");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setMessage("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                alertDialog.show();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }
}
