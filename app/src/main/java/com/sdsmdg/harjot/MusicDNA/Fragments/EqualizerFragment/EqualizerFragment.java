package com.sdsmdg.harjot.MusicDNA.fragments.EqualizerFragment;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.sdsmdg.harjot.MusicDNA.customviews.AnalogController;
import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.MusicDNAApplication;
import com.sdsmdg.harjot.MusicDNA.fragments.PlayerFragment.PlayerFragment;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.activities.SplashActivity;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class EqualizerFragment extends Fragment {

    ImageView backBtn;
    TextView fragTitle;
    SwitchCompat equalizerSwitch;

    LineSet dataset;
    LineChartView chart;
    Paint paint;
    float[] points;

    int y = 0;

    ImageView spinnerDropDownIcon;

    short numberOfFrequencyBands;
    LinearLayout mLinearLayout;

    SeekBar[] seekBarFinal = new SeekBar[5];

    AnalogController bassController, reverbController;

    Spinner presetSpinner;

    FrameLayout equalizerBlocker;

    ShowcaseView showCase;

    Context ctx;

    onCheckChangedListener mCallback;

    public interface onCheckChangedListener {
        void onCheckChanged(boolean isChecked);
    }

    public EqualizerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = context;
        mCallback = (onCheckChangedListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_equalizer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backBtn = (ImageView) view.findViewById(R.id.equalizer_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        fragTitle = (TextView) view.findViewById(R.id.equalizer_fragment_title);
        if (SplashActivity.tf4 != null)
            fragTitle.setTypeface(SplashActivity.tf4);

        equalizerSwitch = (SwitchCompat) view.findViewById(R.id.equalizer_switch);
        equalizerSwitch.setChecked(HomeActivity.isEqualizerEnabled);
        equalizerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCallback.onCheckChanged(isChecked);
            }
        });

        spinnerDropDownIcon = (ImageView) view.findViewById(R.id.spinner_dropdown_icon);
        spinnerDropDownIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presetSpinner.performClick();
            }
        });

        presetSpinner = (Spinner) view.findViewById(R.id.equalizer_preset_spinner);

        equalizerBlocker = (FrameLayout) view.findViewById(R.id.equalizerBlocker);

        if (HomeActivity.isEqualizerEnabled) {
            equalizerBlocker.setVisibility(View.GONE);
        } else {
            equalizerBlocker.setVisibility(View.VISIBLE);
        }

        chart = (LineChartView) view.findViewById(R.id.lineChart);
        paint = new Paint();
        dataset = new LineSet();

        bassController = (AnalogController) view.findViewById(R.id.controllerBass);
        reverbController = (AnalogController) view.findViewById(R.id.controller3D);

        bassController.setLabel("BASS");
        reverbController.setLabel("3D");

        bassController.circlePaint2.setColor(HomeActivity.themeColor);
        bassController.linePaint.setColor(HomeActivity.themeColor);
        bassController.invalidate();
        reverbController.circlePaint2.setColor(HomeActivity.themeColor);
        bassController.linePaint.setColor(HomeActivity.themeColor);
        reverbController.invalidate();

        if (!HomeActivity.isEqualizerReloaded) {
            int x = 0;
            if (PlayerFragment.bassBoost != null) {
                try {
                    x = ((PlayerFragment.bassBoost.getRoundedStrength() * 19) / 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (PlayerFragment.presetReverb != null) {
                try {
                    y = (PlayerFragment.presetReverb.getPreset() * 19) / 6;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (x == 0) {
                bassController.setProgress(1);
            } else {
                bassController.setProgress(x);
            }

            if (y == 0) {
                reverbController.setProgress(1);
            } else {
                reverbController.setProgress(y);
            }
        } else {
            int x = ((HomeActivity.bassStrength * 19) / 1000);
            y = (HomeActivity.reverbPreset * 19) / 6;
            if (x == 0) {
                bassController.setProgress(1);
            } else {
                bassController.setProgress(x);
            }

            if (y == 0) {
                reverbController.setProgress(1);
            } else {
                reverbController.setProgress(y);
            }
        }

        bassController.setOnProgressChangedListener(new AnalogController.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                HomeActivity.bassStrength = (short) (((float) 1000 / 19) * (progress));
                try {
                    PlayerFragment.bassBoost.setStrength(HomeActivity.bassStrength);
                    HomeActivity.equalizerModel.setBassStrength(HomeActivity.bassStrength);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        reverbController.setOnProgressChangedListener(new AnalogController.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                HomeActivity.reverbPreset = (short) ((progress * 6) / 19);
                HomeActivity.equalizerModel.setReverbPreset(HomeActivity.reverbPreset);
                try {
                    PlayerFragment.presetReverb.setPreset(HomeActivity.reverbPreset);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                y = progress;
            }
        });

        mLinearLayout = (LinearLayout) view.findViewById(R.id.equalizerContainer);

        TextView equalizerHeading = new TextView(getContext());
        equalizerHeading.setText("Equalizer");
        equalizerHeading.setTextSize(20);
        equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);

        numberOfFrequencyBands = 5;

        points = new float[numberOfFrequencyBands];

        final short lowerEqualizerBandLevel = PlayerFragment.mEqualizer.getBandLevelRange()[0];
        final short upperEqualizerBandLevel = PlayerFragment.mEqualizer.getBandLevelRange()[1];

        for (short i = 0; i < numberOfFrequencyBands; i++) {
            final short equalizerBandIndex = i;
            final TextView frequencyHeaderTextView = new TextView(getContext());
            frequencyHeaderTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            frequencyHeaderTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            frequencyHeaderTextView.setTextColor(Color.parseColor("#FFFFFF"));
            frequencyHeaderTextView.setText((PlayerFragment.mEqualizer.getCenterFreq(equalizerBandIndex) / 1000) + "Hz");

            LinearLayout seekBarRowLayout = new LinearLayout(getContext());
            seekBarRowLayout.setOrientation(LinearLayout.VERTICAL);

            TextView lowerEqualizerBandLevelTextView = new TextView(getContext());
            lowerEqualizerBandLevelTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            lowerEqualizerBandLevelTextView.setTextColor(Color.parseColor("#FFFFFF"));
            lowerEqualizerBandLevelTextView.setText((lowerEqualizerBandLevel / 100) + "dB");

            TextView upperEqualizerBandLevelTextView = new TextView(getContext());
            lowerEqualizerBandLevelTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            upperEqualizerBandLevelTextView.setTextColor(Color.parseColor("#FFFFFF"));
            upperEqualizerBandLevelTextView.setText((upperEqualizerBandLevel / 100) + "dB");

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.weight = 1;

            SeekBar seekBar = new SeekBar(getContext());
            TextView textView = new TextView(getContext());
            switch (i) {
                case 0:
                    seekBar = (SeekBar) view.findViewById(R.id.seekBar1);
                    textView = (TextView) view.findViewById(R.id.textView1);
                    break;
                case 1:
                    seekBar = (SeekBar) view.findViewById(R.id.seekBar2);
                    textView = (TextView) view.findViewById(R.id.textView2);
                    break;
                case 2:
                    seekBar = (SeekBar) view.findViewById(R.id.seekBar3);
                    textView = (TextView) view.findViewById(R.id.textView3);
                    break;
                case 3:
                    seekBar = (SeekBar) view.findViewById(R.id.seekBar4);
                    textView = (TextView) view.findViewById(R.id.textView4);
                    break;
                case 4:
                    seekBar = (SeekBar) view.findViewById(R.id.seekBar5);
                    textView = (TextView) view.findViewById(R.id.textView5);
                    break;
            }
            seekBarFinal[i] = seekBar;
            seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN));
            seekBar.getThumb().setColorFilter(new PorterDuffColorFilter(HomeActivity.themeColor, PorterDuff.Mode.SRC_IN));
            seekBar.setId(i);
//            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);

            textView.setText(frequencyHeaderTextView.getText());
            textView.setTextColor(Color.WHITE);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            if (HomeActivity.isEqualizerReloaded) {
                points[i] = HomeActivity.seekbarpos[i] - lowerEqualizerBandLevel;
                dataset.addPoint(frequencyHeaderTextView.getText().toString(), points[i]);
                seekBar.setProgress(HomeActivity.seekbarpos[i] - lowerEqualizerBandLevel);
            } else {
                points[i] = PlayerFragment.mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel;
                dataset.addPoint(frequencyHeaderTextView.getText().toString(), points[i]);
                seekBar.setProgress(PlayerFragment.mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel);
                HomeActivity.seekbarpos[i] = PlayerFragment.mEqualizer.getBandLevel(equalizerBandIndex);
                HomeActivity.isEqualizerReloaded = true;
            }
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    PlayerFragment.mEqualizer.setBandLevel(equalizerBandIndex, (short) (progress + lowerEqualizerBandLevel));
                    points[seekBar.getId()] = PlayerFragment.mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel;
                    HomeActivity.seekbarpos[seekBar.getId()] = (progress + lowerEqualizerBandLevel);
                    HomeActivity.equalizerModel.getSeekbarpos()[seekBar.getId()] = (progress + lowerEqualizerBandLevel);
                    dataset.updateValues(points);
                    chart.notifyDataUpdate();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    presetSpinner.setSelection(0);
                    HomeActivity.presetPos = 0;
                    HomeActivity.equalizerModel.setPresetPos(0);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }

        equalizeSound();

        paint.setColor(Color.parseColor("#555555"));
        paint.setStrokeWidth((float) (1.10 * HomeActivity.ratio));

        dataset.setColor(HomeActivity.themeColor);
        dataset.setSmooth(true);
        dataset.setThickness(5);

        chart.setXAxis(false);
        chart.setYAxis(false);

        chart.setYLabels(AxisRenderer.LabelPosition.NONE);
        chart.setXLabels(AxisRenderer.LabelPosition.NONE);
        chart.setGrid(ChartView.GridType.NONE, 7, 10, paint);

        chart.setAxisBorderValues(-300, 3300);

        chart.addData(dataset);
        chart.show();

        Button mEndButton = new Button(getContext());
        mEndButton.setBackgroundColor(HomeActivity.themeColor);
        mEndButton.setTextColor(Color.WHITE);

        showCase = new ShowcaseView.Builder(getActivity())
                .blockAllTouches()
                .singleShot(4)
                .setStyle(R.style.CustomShowcaseTheme)
                .useDecorViewAsParent()
                .replaceEndButton(mEndButton)
                .setContentTitlePaint(HomeActivity.tp)
                .setTarget(new ViewTarget(R.id.showcase_view_equalizer, getActivity()))
                .setContentTitle("Presets")
                .setContentText("Use one of the available presets")
                .build();
        showCase.setButtonText("Next");
        showCase.setButtonPosition(HomeActivity.lps);
        showCase.overrideButtonClick(new View.OnClickListener() {
            int count1 = 0;

            @Override
            public void onClick(View v) {
                count1++;
                switch (count1) {
                    case 1:
                        showCase.setTarget(new ViewTarget(R.id.equalizerContainer, getActivity()));
                        showCase.setContentTitle("Equalizer Controls");
                        showCase.setContentText("Use the seekbars to control the Individual frequencies");
                        showCase.setButtonPosition(HomeActivity.lps);
                        showCase.setButtonText("Next");
                        break;
                    case 2:
                        showCase.setTarget(new ViewTarget(R.id.controllerBass, getActivity()));
                        showCase.setContentTitle("Bass and Reverb");
                        showCase.setContentText("Use these controls to control Bass and Reverb");
                        showCase.setButtonPosition(HomeActivity.lps);
                        showCase.setButtonText("Done");
                        break;
                    case 3:
                        showCase.hide();
                        break;
                }
            }

        });

    }

    public void equalizeSound() {
        ArrayList<String> equalizerPresetNames = new ArrayList<>();
        ArrayAdapter<String> equalizerPresetSpinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item,
                equalizerPresetNames);
        equalizerPresetSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        equalizerPresetNames.add("Custom");

        for (short i = 0; i < PlayerFragment.mEqualizer.getNumberOfPresets(); i++) {
            equalizerPresetNames.add(PlayerFragment.mEqualizer.getPresetName(i));
        }

        presetSpinner.setAdapter(equalizerPresetSpinnerAdapter);
        presetSpinner.setDropDownWidth((HomeActivity.screen_width * 3) / 4);
        if (HomeActivity.isEqualizerReloaded && HomeActivity.presetPos != 0) {
//            correctPosition = false;
            presetSpinner.setSelection(HomeActivity.presetPos);
        }

        presetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position != 0) {
                        PlayerFragment.mEqualizer.usePreset((short) (position - 1));
                        HomeActivity.presetPos = position;
                        short numberOfFreqBands = 5;

                        final short lowerEqualizerBandLevel = PlayerFragment.mEqualizer.getBandLevelRange()[0];

                        for (short i = 0; i < numberOfFreqBands; i++) {
                            seekBarFinal[i].setProgress(PlayerFragment.mEqualizer.getBandLevel(i) - lowerEqualizerBandLevel);
                            points[i] = PlayerFragment.mEqualizer.getBandLevel(i) - lowerEqualizerBandLevel;
                            HomeActivity.seekbarpos[i] = PlayerFragment.mEqualizer.getBandLevel(i);
                            HomeActivity.equalizerModel.getSeekbarpos()[i] = PlayerFragment.mEqualizer.getBandLevel(i);
                        }
                        dataset.updateValues(points);
                        chart.notifyDataUpdate();
                    }
                } catch (Exception e) {
                    Toast.makeText(ctx, "Error while updating Equalizer", Toast.LENGTH_SHORT).show();
                }
                HomeActivity.equalizerModel.setPresetPos(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

    public boolean isShowcaseVisible() {
        return (showCase != null && showCase.isShowing());
    }

    public void hideShowcase() {
        showCase.hide();
    }

    public void setBlockerVisibility(int visibility) {
        equalizerBlocker.setVisibility(visibility);
    }

}
