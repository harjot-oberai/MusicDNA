package com.sdsmdg.harjot.MusicDNA;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Harjot on 28-May-16.
 */
public class CustomImageHolder extends View {

    Drawable d1, d2, d3, d4;
    int width, height;
    boolean isReady = false;

    public CustomImageHolder(Context context) {
        super(context);
    }

    public CustomImageHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = canvas.getWidth();
        height = canvas.getHeight();

        Drawable d = getResources().getDrawable(R.drawable.ic_default);

        int x = Math.min(width, height);

        if (isReady) {
            d1.setBounds(width / 2 - x / 2, height / 2 - x / 2, width / 2, height / 2);
            d1.draw(canvas);

            d2.setBounds(width / 2, height / 2 - x / 2, width / 2 + x / 2, height / 2);
            d2.draw(canvas);

            d3.setBounds(width / 2 - x / 2, height / 2, width / 2, height / 2 + x / 2);
            d3.draw(canvas);

            d4.setBounds(width / 2, height / 2, width / 2 + x / 2, height / 2 + x / 2);
            d4.draw(canvas);
        } else {
            d.setBounds(width / 2 - x / 2, height / 2 - x / 2, width / 2, height / 2);
            d.draw(canvas);

            d.setBounds(width / 2, height / 2 - x / 2, width / 2 + x / 2, height / 2);
            d.draw(canvas);

            d.setBounds(width / 2 - x / 2, height / 2, width / 2, height / 2 + x / 2);
            d.draw(canvas);

            d.setBounds(width / 2, height / 2, width / 2 + x / 2, height / 2 + x / 2);
            d.draw(canvas);
        }


    }

    public void setDrawables(Drawable d1, Drawable d2, Drawable d3, Drawable d4) {
        this.d1 = d1;
        this.d2 = d2;
        this.d3 = d3;
        this.d4 = d4;

        isReady = true;

        invalidate();

    }

    public void setD1(Drawable d1) {
        this.d1 = d1;
    }

    public void setD2(Drawable d2) {
        this.d2 = d2;
    }

    public void setD3(Drawable d3) {
        this.d3 = d3;
    }

    public void setD4(Drawable d4) {
        this.d4 = d4;
    }
}
