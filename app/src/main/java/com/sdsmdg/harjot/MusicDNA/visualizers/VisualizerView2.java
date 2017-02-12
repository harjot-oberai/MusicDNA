package com.sdsmdg.harjot.MusicDNA.visualizers;

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
import com.sdsmdg.harjot.MusicDNA.activities.SplashActivity;

/**
 * Created by Harjot on 04-Jun-16.
 */
public class VisualizerView2 extends View {

    public Bitmap bmp;
    public Paint mForePaint = new Paint();
    public Paint mTextPaint = new Paint();
    boolean textEnabled = false;
    String text;

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

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    private void init() {
        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(0, 128, 255));
        mForePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(40.0f * HomeActivity.ratio);
        if (SplashActivity.tf3 != null)
            mTextPaint.setTypeface(SplashActivity.tf3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bmp != null) {
            canvas.drawBitmap(bmp, 0, -1 * (canvas.getHeight() / 13), null);
        }
        if (textEnabled && HomeActivity.tempSavedDNA != null) {
            canvas.drawText(text, canvas.getWidth() / 2, (canvas.getHeight() * 16) / 17, mTextPaint);
        }
    }

    public void update() {
        invalidate();
    }

    public void drawText(String str, boolean addTexttoImage) {
        textEnabled = addTexttoImage;
        text = str;
        invalidate();
    }

    public void dropText() {
        textEnabled = false;
        invalidate();
    }

}
