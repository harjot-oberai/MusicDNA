package com.sdsmdg.harjot.MusicDNA;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harjot on 12-May-16.
 */
public class OptimizedVisualizerView extends SurfaceView implements SurfaceHolder.Callback{

    private MySurfaceThread thread;

    static double LOG_MAX = Math.log(64);
    static double TAU = Math.PI * 2;
    static double MAX_DOT_SIZE = 0.5;
    static double BASE = Math.log(4) / LOG_MAX;
    private byte[] mBytes;
    private float[] mPoints;
    private float[] mCirclePoints;
    private float[] mCirclePoints1;
    private Rect mRect = new Rect();
    public static Paint mForePaint = new Paint();
    public static Paint mForePaint1 = new Paint();
    public static float width, height, angle, color, lnDataDistance, distance, size, volume, power, outerRadius, alpha;
    public static float normalizedPosition;
    public static List<Pair<Float, Float>> pts;
    public static List<Pair<Float, Pair<Integer, Integer>>> ptPaint;

    public void updateVisualizer(byte[] bytes) {
        //mBytes = bytes;
        invalidate();
    }

    public void clear() {
        pts.clear();
        ptPaint.clear();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        //super.onDraw(canvas);

        super.onDraw(canvas);
        width = canvas.getWidth();
        height = canvas.getHeight();
//        outerRadius = (float) (Math.min(width, height) * 0.47);
//        normalizedPosition = ((float) ((System.currentTimeMillis() - PlayerFragment.startTime) + PlayerFragment.totalElapsedTime + PlayerFragment.deltaTime)) / (float) (PlayerFragment.durationInMilliSec);
//        if (mBytes == null) {
//            return;
//        }
//        angle = (float) (Math.PI - normalizedPosition * TAU);
//        Log.d("ANGLE", angle + "");
//        color = 0;
//        lnDataDistance = 0;
//        distance = 0;
//        size = 0;
//        volume = 0;
//        power = 0;
//
//        float x, y;
//
//        int midx = (int) (width / 2);
//        int midy = (int) (height / 2);
//
        // Redraw previous points
        for (int i = 0; i < pts.size(); i++) {
            mForePaint.setColor(ptPaint.get(i).second.first);
            mForePaint.setAlpha(ptPaint.get(i).second.second);
            canvas.drawCircle(pts.get(i).first, pts.get(i).second, ptPaint.get(i).first, mForePaint);
        }
//
//        // calculate min and max amplitude for current byte array
//        float max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
//        for (int a = 16; a < (mBytes.length / 2); a++) {
//            Log.d("BYTE", mBytes[a] + "");
//            float amp = mBytes[(a * 2) + 0] * mBytes[(a * 2) + 0] + mBytes[(a * 2) + 1] * mBytes[(a * 2) + 1];
//            if (amp > max) {
//                max = amp;
//            }
//            if (amp < min) {
//                min = amp;
//            }
//        }
//
//        Log.d("MAXMIN", max + ":" + min);
//
//        /**
//         * Number Fishing is all that is used here to get the best looking DNA
//         * Number fishing is HOW YOU WIN AT LIFE. -- paullewis :)
//         * **/
//
//        for (int a = 16; a < (mBytes.length / 2); a++) {
//            Log.d("BYTE", mBytes[a] + "");
//            if (max <= 3.0) {
//                break;
//            }
//
//            // scale the amplitude to the range [0,1]
//            float amp = mBytes[(a * 2) + 0] * mBytes[(a * 2) + 0] + mBytes[(a * 2) + 1] * mBytes[(a * 2) + 1];
//            if (max != min)
//                amp = (amp - min) / (max - min);
//            else {
//                amp = 0;
//            }
//
//            volume = ((float) amp);             // REDUNDANT :P
//
//            // converting polar to cartesian (distance calculated afterwards acts as radius for polar co-ords)
//            x = (float) Math.sin(angle);
//            y = (float) Math.cos(angle);
//
//            // filtering low amplitude
//            if (volume < 0.39) {
//                continue;
//            }
//
//            // color ( value of hue inn HSV ) calculated based on current progress of the song or audio clip
//            color = (float) (normalizedPosition - 0.12 + Math.random() * 0.24);
//            color = Math.round(color * 360);
//
//            // calculating distance from center ( 'r' in polar coordinates)
//            lnDataDistance = (float) ((Math.log(a - 4) / LOG_MAX) - BASE);
//            distance = lnDataDistance * outerRadius;
//
//            // size of the circle to be rendered at the calculated position
//            size = (float) (4.5 * volume * MAX_DOT_SIZE + Math.random() * 2);
//
//            // alpha also based on volume ( amplitude )
//            alpha = (float) (volume * 0.09);
//
//            // final cartesian coordinates for drawing on canvas
//            x = x * distance;
//            y = y * distance;
//
//
//            float[] hsv = new float[3];
//            hsv[0] = color;
//            hsv[1] = (float) 0.8;
//            hsv[2] = (float) 0.5;
//
//            // setting color of the Paint
//            mForePaint.setColor(Color.HSVToColor(hsv));
//
//            if (size >= 15.0 && size < 29.0) {
//                mForePaint.setAlpha(15);
//            } else if (size >= 29.0 && size <= 60.0) {
//                mForePaint.setAlpha(9);
//            } else if (size > 60.0) {
//                mForePaint.setAlpha(0);
//            } else {
//                mForePaint.setAlpha((int) (alpha * 1000));
//            }
//
//            // Setting alpha of the Paint
//            //mForePaint.setAlpha((int) (alpha * 1000));
//
//            // Draw the circles at correct position
//            canvas.drawCircle(midx + x, midy + y, size, mForePaint);
//
//
//            // Add points and paint config to lists for redraw
//            pts.add(Pair.create(midx + x, midy + y));
//            ptPaint.add(Pair.create(size, Pair.create(mForePaint.getColor(), mForePaint.getAlpha())));
//        }

    }

    public OptimizedVisualizerView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init();
    }

    public OptimizedVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init();
    }

    public OptimizedVisualizerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init();
    }

    private void init(){
        mBytes = null;
        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        pts = new ArrayList<>();
        ptPaint = new ArrayList<>();
        getHolder().addCallback(this);
        thread = new MySurfaceThread(getHolder(), this);
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
                               int arg3) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}
