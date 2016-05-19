package com.sdsmdg.harjot.MusicDNA;


import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BounceEase;
import com.db.chart.view.animation.easing.CubicEase;
import com.db.chart.view.animation.easing.ElasticEase;
import com.db.chart.view.animation.easing.ExpoEase;
import com.db.chart.view.animation.easing.LinearEase;
import com.db.chart.view.animation.easing.SineEase;


/**
 * A simple {@link Fragment} subclass.
 */
public class EqualizerFragment extends Fragment {

    LineSet dataset;
    LineChartView chart;
    Paint paint;

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

        paint = new Paint();
        paint.setColor(Color.parseColor("#777777"));
        chart = (LineChartView) view.findViewById(R.id.lineChart);

        dataset = new LineSet();
        dataset.setColor(Color.parseColor("#3F334D"));
        dataset.addPoint("first", (float) 10.0);
        dataset.addPoint("second", (float) 17.0);
        dataset.addPoint("third", (float) 8.0);
        dataset.addPoint("fourth", (float) 15.0);
        dataset.addPoint("fifth", (float) 12.0);

        dataset.setSmooth(true);

        chart.setXAxis(false);
        chart.setYAxis(false);

        chart.setYLabels(AxisController.LabelPosition.NONE);
        chart.setGrid(ChartView.GridType.HORIZONTAL,paint);

        Animation anim = new Animation();
        anim.setDuration(1500);
        anim.setEasing(new ExpoEase());
        anim.setAlpha(1);

        chart.addData(dataset);
        chart.show(anim);

    }
}
