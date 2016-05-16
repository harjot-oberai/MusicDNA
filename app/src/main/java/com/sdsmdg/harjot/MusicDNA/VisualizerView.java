package com.sdsmdg.harjot.MusicDNA;

/**
 * Created by Harjot on 01-May-16.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class VisualizerView extends View {

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
        mBytes = null;
        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(0, 128, 255));
        mForePaint1.setStrokeWidth(1f);
        mForePaint1.setAntiAlias(true);
        mForePaint1.setColor(Color.rgb(255, 128, 0));
        pts = new ArrayList<>();
        ptPaint = new ArrayList<>();
          // deprecated
    }

    public void updateVisualizer(byte[] bytes) {
        //mBytes = bytes;
        invalidate();
    }

//    ***** Wave visualizer using wWavelet Transform ******
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (mBytes == null) {
//            return;
//        }
//        if (mPoints == null || mPoints.length < mBytes.length * 4) {
//            mPoints = new float[mBytes.length * 4];
//            mCirclePoints = new float[mBytes.length * 4];
//            mCirclePoints1 = new float[mBytes.length * 4];
//        }
//        mRect.set(0, 0, getWidth(), getHeight());
//        float centerX = mRect.width() / 2;
//        float centerY = mRect.height() / 2;
//        float theta = (float) (360.0 / (float) (mBytes.length - 1));
//        //Log.d("BYTES","" +  (mBytes.length - 1));
//        float RADIUS = Math.min(centerX / 2, centerY / 2);
//        //float RADIUS = 100;
//        for (int i = 0; i < mBytes.length - 1; i++) {
//            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
//            mPoints[i * 4 + 1] = ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 512;
//            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
//            mPoints[i * 4 + 3] = ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 512;
//
//            mCirclePoints[i * 4] = (float) (centerX + (RADIUS + mPoints[i * 4 + 1]) * Math.sin((i * theta *Math.PI)/ (180)));
//            mCirclePoints[i * 4 + 1] = (float) (centerY + (RADIUS + mPoints[i * 4 + 1]) * Math.cos((i * theta *Math.PI)/ (180)));
//            mCirclePoints[i * 4 + 2] = (float) (centerX + (RADIUS + mPoints[i * 4 + 3]) * Math.sin(((i+1) * theta *Math.PI)/ (180)));
//            mCirclePoints[i * 4 + 3] = (float) (centerY + (RADIUS + mPoints[i * 4 + 3]) * Math.cos(((i+1) * theta *Math.PI)/ (180)));
//
//            mCirclePoints1[i * 4] = (float) (centerX - (RADIUS + mPoints[i * 4 + 1]) * Math.sin((i * theta *Math.PI)/ (180)));
//            mCirclePoints1[i * 4 + 1] = (float) (centerY - (RADIUS + mPoints[i * 4 + 1]) * Math.cos((i * theta *Math.PI)/ (180)));
//            mCirclePoints1[i * 4 + 2] = (float) (centerX - (RADIUS + mPoints[i * 4 + 3]) * Math.sin(((i+1) * theta *Math.PI)/ (180)));
//            mCirclePoints1[i * 4 + 3] = (float) (centerY - (RADIUS + mPoints[i * 4 + 3]) * Math.cos(((i+1) * theta *Math.PI)/ (180)));
//
//        }
//        canvas.drawLines(mCirclePoints, mForePaint);
//        canvas.drawLines(mCirclePoints1, mForePaint1);
//    }

//    ***** DNA Visualizer using Wavelet Transform *****
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        width = canvas.getWidth();
//        height = canvas.getHeight();
//        outerRadius = (float) (Math.min(width, height) * 0.44);
//        normalizedPosition = ((float) (System.currentTimeMillis() - StreamMusicFragment.startTime)) / (float) (StreamMusicFragment.durationInMilliSec);
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
//        float x = (float) Math.sin(angle);
//        float y = (float) Math.cos(angle);
//
//        int midx = (int) (width / 2);
//        int midy = (int) (height / 2);
//
//        for (int i = 0; i < pts.size(); i++) {
//            mForePaint.setColor(ptPaint.get(i).second.first);
//            mForePaint.setAlpha(ptPaint.get(i).second.second);
//            canvas.drawCircle(pts.get(i).first, pts.get(i).second, ptPaint.get(i).first, mForePaint);
//        }
//
//        for (int a = 16; a < mBytes.length / 2; a++) {
//            Log.d("BYTE", mBytes[a] + "");
//            /*float amp = mBytes[(a*2) + 0]*mBytes[(a*2) + 0] + mBytes[(a*2) + 1]*mBytes[(a*2) + 1];
//            Log.d("AMP", amp + "");*/
//            volume = ((float) (mBytes[a] + 128)) / (float) 255;
//            //volume = ((float) amp) / (float) 32768.0;
//            Log.d("VOLUME", volume + "");
//            x = (float) Math.sin(angle);
//            y = (float) Math.cos(angle);
//            if (volume < 0.73)
//
//            {
//                continue;
//            }
//
//            color = (float) (normalizedPosition - 0.12 + Math.random() * 0.24);
//            color = Math.round(color * 360);
//
//            lnDataDistance = (float) ((Math.log(a - 4) / LOG_MAX) - BASE);
//            Log.d("LNDIS", lnDataDistance + "");
//
//            distance = lnDataDistance * outerRadius;
//            size = (float) (1.5 * volume * MAX_DOT_SIZE + Math.random() * 2);
//
//
//            if (Math.random() > 0.995)
//
//            {
//                size *= ((mBytes[a] + 128) * 0.2) * Math.random();
//                volume *= Math.random() * 0.25;
//            }
//
//            Log.d("SIZE", size + "");
//
//            alpha = (float) (volume * 0.09);
//            Log.d("ALPHA2", alpha + "");
//            x = x * distance;
//            y = y * distance;
//
//            float[] hsv = new float[3];
//            hsv[0] = color;
//            hsv[1] = (float) 0.8;
//            hsv[2] = (float) 0.5;
//
//            // mForePaint.setAlpha((int) (155 - alpha * 1000));
//
//            mForePaint.setColor(Color.HSVToColor(hsv));
//
//            /*if (size > 2.5) {
//                mForePaint.setAlpha(0);
//            } else {
//                mForePaint.setAlpha(248);
//            }*/
//
//            mForePaint.setAlpha((int) (alpha * 900));
//            Log.d("ALPHA3", mForePaint.getAlpha() + "");
//
//            canvas.drawCircle(midx + x, midy + y, size, mForePaint);
//            pts.add(Pair.create(midx + x, midy + y));
//            ptPaint.add(Pair.create(size, Pair.create(mForePaint.getColor(), mForePaint.getAlpha())));
//        }
//    }

    //  ***** DNA Visualizer using Fast Fourier transform (FFT) *****
    @Override
    protected void onDraw(Canvas canvas) {
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

    public void clear() {
        pts.clear();
        ptPaint.clear();
    }
}
