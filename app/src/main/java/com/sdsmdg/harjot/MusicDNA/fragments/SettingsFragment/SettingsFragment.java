package com.sdsmdg.harjot.MusicDNA.fragments.SettingsFragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.support.v7.widget.SwitchCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.MusicDNAApplication;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.activities.SplashActivity;
import com.sdsmdg.harjot.MusicDNA.models.Settings;
import com.sdsmdg.harjot.MusicDNA.utilities.CommonUtils;
import com.squareup.leakcanary.RefWatcher;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    RelativeLayout densitycard, themeCard, aboutCard, albumArtCard, wifiCard, resetSettings;
    SwitchCompat albumArtToggle;
    SwitchCompat wifiToggle;
    ImageView themeColorImg;
    SeekBar densitySeekbar;
    TextView densityTextDialog, densityText;

    HomeActivity homeActivity;

    View bottomMarginLayout;

    SettingsFragmentCallbackListener mCallback;

    ImageView backBtn;
    TextView fragTitle;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public interface SettingsFragmentCallbackListener {
        void onColorChanged();

        void onAlbumArtBackgroundChangedVisibility(int visibility);

        void onAboutClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        homeActivity = (HomeActivity) context;
        try {
            mCallback = (SettingsFragmentCallbackListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
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

        backBtn = (ImageView) view.findViewById(R.id.settings_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        fragTitle = (TextView) view.findViewById(R.id.settings_fragment_title);
        if (SplashActivity.tf4 != null)
            fragTitle.setTypeface(SplashActivity.tf4);

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = CommonUtils.dpTopx(65, getContext());

        resetSettings = (RelativeLayout) view.findViewById(R.id.reset_settings_card);
        resetSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(view.getContext()).create();
                alertDialog.setTitle("Reset settings?");
                alertDialog.setMessage("This operation will reset all settings but playlist and other content will remain unchanged.");
                alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Reset",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            resetAllSettings();
                            Toast.makeText(getContext(), "Settings have been reset", Toast.LENGTH_LONG).show();
                            }
                        });
                alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(HomeActivity.themeColor);
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(HomeActivity.themeColor);
                    }
                });

                alertDialog.show();
            }
        });


        densitycard = (RelativeLayout) view.findViewById(R.id.density_card);
        densityText = (TextView) view.findViewById(R.id.density_value);
        densityText.setText(String.valueOf(100 - (int) (homeActivity.minAudioStrength * 100)));

        densitycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.density_dialog);
                densitySeekbar = (SeekBar) dialog.findViewById(R.id.density_dialog_seekbar);
                densitySeekbar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(homeActivity.themeColor, PorterDuff.Mode.SRC_IN));
                densityTextDialog = (TextView) dialog.findViewById(R.id.density_dialog_value);
                densitySeekbar.setMax(100);
                densitySeekbar.setProgress(Integer.parseInt(densityText.getText().toString()));
                densityTextDialog.setText(String.valueOf(densitySeekbar.getProgress()));
                densitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        setHomeActivityDNADensity(progress);
                        densityTextDialog.setText(String.valueOf(progress));
                        densityText.setText(String.valueOf(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                dialog.show();

            }
        });

        themeCard = (RelativeLayout) view.findViewById(R.id.theme_card);
        themeColorImg = (ImageView) view.findViewById(R.id.theme_color_img);
        themeColorImg.setBackgroundColor(homeActivity.themeColor);
        themeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = ColorPickerDialogBuilder
                        .with(getContext())
                        .setTitle("Choose color")
                        .initialColor(HomeActivity.themeColor)
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
                                setHomeActivityColor(color);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .build();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(HomeActivity.themeColor);
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(HomeActivity.themeColor);
                    }
                });

                dialog.show();
            }
        });

        albumArtCard = (RelativeLayout) view.findViewById(R.id.album_art_card);
        albumArtToggle = (SwitchCompat) view.findViewById(R.id.album_art_toggle);
        albumArtToggle.setChecked(homeActivity.settings.isAlbumArtBackgroundEnabled());
        albumArtCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumArtToggle.toggle();
            }
        });
        albumArtToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setHomeActivityAlbumBg(isChecked);
            }
        });

        wifiCard = (RelativeLayout) view.findViewById(R.id.wifi_card);
        wifiToggle = (SwitchCompat) view.findViewById(R.id.wifi_stream_toggle);
        wifiToggle.setChecked(homeActivity.settings.isStreamOnlyOnWifiEnabled());
        wifiCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiToggle.toggle();
            }
        });
        wifiToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setHomeActivityStreamingOnWifi(isChecked);
            }
        });


        aboutCard = (RelativeLayout) view.findViewById(R.id.about_card);
        aboutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onAboutClicked();
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

    public int getDarkColor(int color) {
        int darkColor = 0;

        int r = Math.max(Color.red(color) - 25, 0);
        int g = Math.max(Color.green(color) - 25, 0);
        int b = Math.max(Color.blue(color) - 25, 0);

        darkColor = Color.rgb(r, g, b);

        return darkColor;
    }

    public void resetAllSettings(){
        setHomeActivityColor(Settings.DEF_THEME_COLOR);
        setHomeActivityDNADensity(Settings.DEF_DNA_DENSITY);
        setHomeActivityAlbumBg(Settings.DEF_ALBUM_ART_BG);
        setHomeActivityStreamingOnWifi(Settings.DEF_STREAM_ONLY_ON_WIFI);
    }

    public void setHomeActivityColor(int color){
        homeActivity.settings.setThemeColor(color);
        homeActivity.themeColor = color;
        homeActivity.collapsingToolbar.setContentScrimColor(color);
        homeActivity.customLinearGradient.setStartColor(color);
        homeActivity.customLinearGradient.invalidate();
        themeColorImg.setBackgroundColor(color);
        mCallback.onColorChanged();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = ((Activity) (getContext())).getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getDarkColor(color));
        }
    }

    public void setHomeActivityDNADensity(float progress) {
        homeActivity.minAudioStrength = (100 - progress)/(float)100;
        homeActivity.settings.setMinAudioStrength(homeActivity.minAudioStrength);
        densityText.setText(String.valueOf(progress));
        densityText.setText(String.valueOf(100 - (int) (homeActivity.minAudioStrength * 100)));
    }

    public void setHomeActivityAlbumBg(boolean enabled) {
        homeActivity.settings.setAlbumArtBackgroundEnabled(enabled);
        mCallback.onAlbumArtBackgroundChangedVisibility(enabled ? View.VISIBLE : View.GONE);
        albumArtToggle.setChecked(enabled);
    }

    public void setHomeActivityStreamingOnWifi(boolean enabled) {
        homeActivity.settings.setStreamOnlyOnWifiEnabled(enabled);
        wifiToggle.setChecked(enabled);
    }
}