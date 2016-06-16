package com.sdsmdg.harjot.MusicDNA;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Harjot on 28-May-16.
 */
public class CustomImageHolder extends View {

    int width, height;
    int startAngle = 270, sweepAngle = 360;
    Paint paint;
    float radius;
    int midX, midY;
    boolean isStarted = false;
    TextView text;
    RelativeLayout relativeLayout;

    public CustomImageHolder(Context context) {
        super(context);
        init();
    }

    public CustomImageHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomImageHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = canvas.getWidth();
        height = canvas.getHeight();
        radius = (float) width / (float) 2.0;
        midX = width / 2;
        midY = height / 2;
        if (isStarted) {
            RectF oval = new RectF();
            oval.set(midX - radius, midY - radius, midX + radius, midY + radius);
            canvas.drawArc(oval, startAngle, sweepAngle, true, paint);

            if (sweepAngle > 0) {
                startAngle += 3;
                if (startAngle > 359) {
                    startAngle -= 360;
                }
                sweepAngle -= 3;
                invalidate();
            }
        }
    }

    public void start(TextView tx,RelativeLayout relSplash) {
        isStarted = true;
        text = tx;
        relativeLayout = relSplash;
        invalidate();
    }

    public float getProgress() {
        return ((float) sweepAngle / (float) 360.0);
    }

}
