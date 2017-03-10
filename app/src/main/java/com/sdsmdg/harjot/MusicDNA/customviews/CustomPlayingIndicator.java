package com.sdsmdg.harjot.MusicDNA.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;

/**
 * Created by Harjot on 28-Sep-16.
 */
public class CustomPlayingIndicator extends View implements Runnable {

    int drawColor;
    Paint drawPaint;

    float maxHeight = 60;
    float width = 4;
    float separation = 20;

    float midx, midy;

    float[] newHeight = new float[5];
    double[] angle = new double[5];

    boolean isPaused = false;

    public CustomPlayingIndicator(Context context) {
        super(context);
        init();
    }

    public CustomPlayingIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomPlayingIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        drawColor = Color.BLACK;
        drawPaint = new Paint();
        drawPaint.setColor(drawColor);
        maxHeight *= HomeActivity.ratio;
        width *= HomeActivity.ratio;
        separation *= HomeActivity.ratio;
        for (int i = 0; i < 5; i++) {
            newHeight[i] = 0;
            angle[i] = i * Math.PI / 6.0;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        midx = canvas.getWidth() / 2;
        midy = canvas.getHeight() / 2;
        separation = canvas.getWidth() / 6.0f;
        maxHeight = canvas.getHeight() * 0.72f;

        if (getVisibility() == VISIBLE) {
            if (!isPaused) {
                for (int i = 0; i < 5; i++) {
                    canvas.drawRect(midx - width + ((i - 2) * separation), midy - newHeight[i], midx + width + ((i - 2) * separation), midy + newHeight[i], drawPaint);
                }
                postDelayed(this, 10);
            } else {
                for (int i = 0; i < 5; i++) {
                    canvas.drawRect(midx - width + ((i - 2) * separation), midy - (maxHeight / 4.0f), midx + width + ((i - 2) * separation), midy + (maxHeight / 4.0f), drawPaint);
                }
            }
        }

    }


    @Override
    public void run() {

        for (int i = 0; i < 5; i++) {
            angle[i] += 0.09;
            newHeight[i] = (float) (maxHeight * (Math.abs(Math.sin(angle[i]) / 2.5f) + 0.15));
        }
        invalidate();
    }

    public void pause() {
        isPaused = true;
        invalidate();
    }

    public void play() {
        isPaused = false;
        invalidate();
    }

    public void setDrawColor(int color){
        drawColor = color;
        drawPaint.setColor(color);
        invalidate();
    }

}
