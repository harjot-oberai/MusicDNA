package com.sdsmdg.harjot.MusicDNA.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.fragments.PlayerFragment.PlayerFragment;

/**
 * Created by Harjot on 14-May-16.
 */
public class CustomProgressBar extends View {

    Paint forePaint;

    public CustomProgressBar(Context context) {
        super(context);
        init();
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        forePaint = new Paint();
        forePaint.setStrokeWidth(1.0f);
        forePaint.setAntiAlias(true);
        forePaint.setColor(Color.rgb(0, 128, 255));
    }

    public void update() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        forePaint.setColor(HomeActivity.themeColor);
        forePaint.setAlpha(248);
        float right = ((float) canvas.getWidth() / (float) PlayerFragment.durationInMilliSec) * (float) PlayerFragment.mMediaPlayer.getCurrentPosition();
        canvas.drawRect(0, 0, right, canvas.getHeight(), forePaint);
    }
}
