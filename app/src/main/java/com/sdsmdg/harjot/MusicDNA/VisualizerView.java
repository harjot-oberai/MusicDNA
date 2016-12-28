package com.sdsmdg.harjot.MusicDNA;

/**
 * Created by Harjot on 01-May-16.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class VisualizerView extends View {

    static double LOG_MAX = Math.log(64);
    static double TAU = Math.PI * 2;
    static double MAX_DOT_SIZE = 0.5;
    static double BASE = Math.log(4) / LOG_MAX;
    public static Paint mForePaint = new Paint();
    public static Paint mForePaint1 = new Paint();
    public static float width, height, angle, color, lnDataDistance, distance, size, volume, power, outerRadius, alpha;
    public static float normalizedPosition;
    public static List<Pair<Float, Float>> pts;
    public static List<Pair<Float, Pair<Integer, Integer>>> ptPaint;

    public static List<Pair<Float, Float>> pts2;
    public static List<Pair<Float, Pair<Integer, Integer>>> ptPaint2;

    static int w;
    static int h;
    static Bitmap.Config conf;
    static Bitmap bmp;

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(0, 128, 255));
        mForePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        mForePaint1.setStrokeWidth(1f);
        mForePaint1.setAntiAlias(true);
        mForePaint1.setColor(Color.rgb(255, 128, 0));
        pts = new ArrayList<>();
        ptPaint = new ArrayList<>();
    }

    public void updateVisualizer(byte[] bytes) {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = canvas.getWidth();
        height = canvas.getHeight();

        // Redraw previous points
        if (HomeActivity.isPlayerVisible) {
            if (PlayerFragment.mVisualizerView != null && (PlayerFragment.mVisualizerView.getVisibility() == View.VISIBLE)) {
                if (bmp != null)
                    canvas.drawBitmap(bmp, 0, 0, null);
                pts.clear();
                ptPaint.clear();
            }
        }
    }

    public void clear() {
        pts.clear();
        ptPaint.clear();
    }
}
