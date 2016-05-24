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
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;


/**
 * A simple {@link Fragment} subclass.
 */
public class EqualizerFragment extends Fragment {

    LineSet dataset;
    LineChartView chart;
    Paint paint;
    float[] points;

    int y;

    static short reverbPreset = -1, bassStrength = -1;

    short numberOfFrequencyBands;
    LinearLayout mLinearLayout;

    AnalogController bassController, reverbController;

    public EqualizerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_equalizer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView equalizerHeading = new TextView(HomeActivity.ctx);
        equalizerHeading.setText("Equalizer");
        equalizerHeading.setTextSize(20);
        equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);

//        mLinearLayout.addView(equalizerHeading);

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
            mLinearLayout.addView(frequencyHeaderTextView);

            LinearLayout seekBarRowLayout = new LinearLayout(HomeActivity.ctx);
            seekBarRowLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView lowerEqualizerBandLevelTextView = new TextView(HomeActivity.ctx);
            lowerEqualizerBandLevelTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
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
            seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#FFA036"), PorterDuff.Mode.SRC_IN));
            seekBar.setId(i);
//            seekBar.setRotation(270);
            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);

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

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            seekBarRowLayout.addView(lowerEqualizerBandLevelTextView);
            seekBarRowLayout.addView(seekBar);
            seekBarRowLayout.addView(upperEqualizerBandLevelTextView);

//            seekBarRowLayout.setRotation(270);

            mLinearLayout.addView(seekBarRowLayout);
        }

        paint.setColor(Color.parseColor("#555555"));
        paint.setStrokeWidth(2 * HomeActivity.ratio);

        dataset.setColor(Color.parseColor("#FFA036"));
        dataset.setSmooth(true);

        chart.setXAxis(false);
        chart.setYAxis(false);

        chart.setYLabels(AxisController.LabelPosition.NONE);
        chart.setGrid(ChartView.GridType.HORIZONTAL, 7, 1, paint);

        chart.setAxisBorderValues(-300, 3300);

//        Animation anim = new Animation();
//        anim.setDuration(500);
//        anim.setEasing(new ExpoEase());
//        anim.setAlpha(1);

        chart.addData(dataset);
        chart.show();

    }
}
