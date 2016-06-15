package com.sdsmdg.harjot.MusicDNA;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harjot on 04-Jun-16.
 */
public class VisualizerView2 extends View {

    public List<Pair<Float, Float>> pts;
    public List<Pair<Float, Pair<Integer, Integer>>> ptPaint;
    public Paint mForePaint = new Paint();
    public Paint mTextPaint = new Paint();
    boolean textEnabled = false;
    String text;


    public void setPts(List<Pair<Float, Float>> pts) {
        this.pts = pts;
    }

    public void setPtPaint(List<Pair<Float, Pair<Integer, Integer>>> ptPaint) {
        this.ptPaint = ptPaint;
    }

    public VisualizerView2(Context context) {
        super(context);
        init();
    }

    public VisualizerView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VisualizerView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(0, 128, 255));
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(40.0f * HomeActivity.ratio);
        mTextPaint.setTypeface(SplashActivity.tf);
        pts = new ArrayList<>();
        ptPaint = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < pts.size(); i++) {
            mForePaint.setColor(ptPaint.get(i).second.first);
            mForePaint.setAlpha(ptPaint.get(i).second.second);
            canvas.drawCircle(pts.get(i).first, pts.get(i).second - (canvas.getHeight() / 5), ptPaint.get(i).first, mForePaint);
        }
        if (textEnabled && HomeActivity.tempSavedDNA != null) {
            canvas.drawText(text, canvas.getWidth() / 2, (canvas.getHeight() * 15) / 16, mTextPaint);
        }
    }

    public void update() {
        invalidate();
    }

    public void drawText(String str) {
        textEnabled = true;
        text = str;
        invalidate();
    }

    public void dropText() {
        textEnabled = false;
        invalidate();
    }

}
