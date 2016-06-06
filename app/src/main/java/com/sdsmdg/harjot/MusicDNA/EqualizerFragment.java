package com.sdsmdg.harjot.MusicDNA;


import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class EqualizerFragment extends Fragment {

    LineSet dataset;
    LineChartView chart;
    Paint paint;
    float[] points;

    static int y;

    static short reverbPreset = -1, bassStrength = -1;

    short numberOfFrequencyBands;
    LinearLayout mLinearLayout;

    SeekBar[] seekBarFinal = new SeekBar[5];

    AnalogController bassController, reverbController;

    Spinner presetSpinner;

    public EqualizerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_equalizer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presetSpinner = (Spinner) view.findViewById(R.id.equalizer_preset_spinner);

        chart = (LineChartView) view.findViewById(R.id.lineChart);
        paint = new Paint();
        dataset = new LineSet();

        bassController = (AnalogController) view.findViewById(R.id.controllerBass);
        reverbController = (AnalogController) view.findViewById(R.id.controller3D);

        bassController.setLabel("BASS");
        reverbController.setLabel("3D");

        int x = (int) ((PlayerFragment.bassBoost.getRoundedStrength() * 19) / 1000);
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

        bassController.setOnProgressChangedListener(new AnalogController.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                bassStrength = (short) (((float) 1000 / 19) * (progress));
                PlayerFragment.bassBoost.setStrength(bassStrength);
            }
        });

        reverbController.setOnProgressChangedListener(new AnalogController.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                reverbPreset = (short) ((progress * 6) / 19);
                PlayerFragment.presetReverb.setPreset(reverbPreset);
                y = progress;
            }
        });

        mLinearLayout = (LinearLayout) view.findViewById(R.id.equalizerContainer);
//        mLinearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView equalizerHeading = new TextView(HomeActivity.ctx);
        equalizerHeading.setText("Equalizer");
        equalizerHeading.setTextSize(20);
        equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);

        numberOfFrequencyBands = PlayerFragment.mEqualizer.getNumberOfBands();

        points = new float[numberOfFrequencyBands];

        final short lowerEqualizerBandLevel = PlayerFragment.mEqualizer.getBandLevelRange()[0];
        final short upperEqualizerBandLevel = PlayerFragment.mEqualizer.getBandLevelRange()[1];

        for (short i = 0; i < numberOfFrequencyBands; i++) {
            final short equalizerBandIndex = i;
            final TextView frequencyHeaderTextView = new TextView(HomeActivity.ctx);
            frequencyHeaderTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            frequencyHeaderTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            frequencyHeaderTextView.setTextColor(Color.parseColor("#FFFFFF"));
            frequencyHeaderTextView.setText((PlayerFragment.mEqualizer.getCenterFreq(equalizerBandIndex) / 1000) + "Hz");
//            mLinearLayout.addView(frequencyHeaderTextView);

            LinearLayout seekBarRowLayout = new LinearLayout(HomeActivity.ctx);
            seekBarRowLayout.setOrientation(LinearLayout.VERTICAL);

            TextView lowerEqualizerBandLevelTextView = new TextView(HomeActivity.ctx);
            lowerEqualizerBandLevelTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            lowerEqualizerBandLevelTextView.setTextColor(Color.parseColor("#FFFFFF"));
            lowerEqualizerBandLevelTextView.setText((lowerEqualizerBandLevel / 100) + "dB");

            TextView upperEqualizerBandLevelTextView = new TextView(HomeActivity.ctx);
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

            SeekBar seekBar = new SeekBar(HomeActivity.ctx);
            TextView textView = new TextView(HomeActivity.ctx);
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
            seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#FFA036"), PorterDuff.Mode.SRC_IN));
            seekBar.setId(i);
//            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);

            textView.setText(frequencyHeaderTextView.getText());
            textView.setTextColor(Color.WHITE);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            points[i] = PlayerFragment.mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel;

            dataset.addPoint(frequencyHeaderTextView.getText().toString(), points[i]);

            seekBar.setProgress(PlayerFragment.mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    PlayerFragment.mEqualizer.setBandLevel(equalizerBandIndex, (short) (progress + lowerEqualizerBandLevel));
                    points[seekBar.getId()] = PlayerFragment.mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel;
                    dataset.updateValues(points);
                    chart.notifyDataUpdate();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    presetSpinner.setSelection(0);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            /*seekBarRowLayout.addView(upperEqualizerBandLevelTextView);
            seekBarRowLayout.addView(seekBar);
            seekBarRowLayout.addView(lowerEqualizerBandLevelTextView);

            mLinearLayout.addView(seekBarRowLayout);*/

            equalizeSound();

        }

        paint.setColor(Color.parseColor("#555555"));
        paint.setStrokeWidth((float) (1.33 * HomeActivity.ratio));

        dataset.setColor(Color.parseColor("#FFA036"));
        dataset.setSmooth(true);
        dataset.setThickness(5);

        chart.setXAxis(false);
        chart.setYAxis(false);

        chart.setYLabels(AxisController.LabelPosition.NONE);
        chart.setGrid(ChartView.GridType.FULL, 7, 10, paint);

        chart.setAxisBorderValues(-300, 3300);

        chart.addData(dataset);
        chart.show();

    }

    public void equalizeSound() {
        ArrayList<String> equalizerPresetNames = new ArrayList<>();
        ArrayAdapter<String> equalizerPresetSpinnerAdapter = new ArrayAdapter<String>(HomeActivity.ctx,
                R.layout.spinner_item,
                equalizerPresetNames);
        equalizerPresetSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        equalizerPresetNames.add("Custom");

        for (short i = 0; i < PlayerFragment.mEqualizer.getNumberOfPresets(); i++) {
            equalizerPresetNames.add(PlayerFragment.mEqualizer.getPresetName(i));
        }

        presetSpinner.setAdapter(equalizerPresetSpinnerAdapter);
        presetSpinner.setDropDownWidth((HomeActivity.screen_width * 3) / 4);

        presetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    PlayerFragment.mEqualizer.usePreset((short) (position - 1));

                    short numberOfFreqBands = PlayerFragment.mEqualizer.getNumberOfBands();

                    final short lowerEqualizerBandLevel = PlayerFragment.mEqualizer.getBandLevelRange()[0];

                    for (short i = 0; i < numberOfFreqBands; i++) {
                        seekBarFinal[i].setProgress(PlayerFragment.mEqualizer.getBandLevel(i) - lowerEqualizerBandLevel);
                        points[i] = PlayerFragment.mEqualizer.getBandLevel(i) - lowerEqualizerBandLevel;
                        dataset.updateValues(points);
                        chart.notifyDataUpdate();
                    }
                } else {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
