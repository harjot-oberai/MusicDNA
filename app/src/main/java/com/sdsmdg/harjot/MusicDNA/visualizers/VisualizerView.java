package com.sdsmdg.harjot.MusicDNA.visualizers;

/**
 * Created by Harjot on 01-May-16.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.fragments.PlayerFragment.PlayerFragment;

public class VisualizerView extends View {

    public static Paint mForePaint = new Paint();
    public static Paint mForePaint1 = new Paint();
    public static float width, height, angle, color, lnDataDistance, distance, size, volume, power, outerRadius, alpha;
    public static float normalizedPosition;
    public static double LOG_MAX = Math.log(64);
    public static double TAU = Math.PI * 2;
    public static double MAX_DOT_SIZE = 0.5;
    public static double BASE = Math.log(4) / LOG_MAX;
    public static int w;
    public static int h;
    public static Bitmap.Config conf;
    public static Bitmap bmp;

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
            }
        }
    }
}
