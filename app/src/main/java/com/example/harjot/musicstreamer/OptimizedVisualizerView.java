package com.example.harjot.musicstreamer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harjot on 10-May-16.
 */
public class OptimizedVisualizerView extends SurfaceView implements Runnable {

    Thread thread = null;
    SurfaceHolder surfaceHolder;
    volatile boolean running = false;

    private DrawThread myThread;

    private byte[] mBytes;
    private float[] mPoints;
    private float[] mCirclePoints;
    private float[] mCirclePoints1;
    private Rect mRect = new Rect();
    private Paint mForePaint = new Paint();
    private Paint mForePaint1 = new Paint();

    View root;

    Canvas c;

    private float width, height, angle, color, lnDataDistance, distance, size, volume, power, outerRadius, alpha;
    private int time;
    private float normalizedPosition;

    private List<Pair<Float, Float>> pts;
    private List<Pair<Float, Pair<Integer, Integer>>> ptPaint;

    double LOG_MAX = Math.log(64);
    double TAU = Math.PI * 2;
    double MAX_DOT_SIZE = 0.5;
    double BASE = Math.log(4) / LOG_MAX;

    public OptimizedVisualizerView(Context context) {
        super(context);
        init();

    }

    public OptimizedVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
//        surfaceHolder = getHolder();
//        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//            }
//
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                c = holder.lockCanvas(null);
//                onDraw(c);
//                holder.unlockCanvasAndPost(c);
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format,
//                                       int width, int height) {
//            }
//        });
    }

    public OptimizedVisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
//        surfaceHolder = getHolder();
//        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//            }
//
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                setWillNotDraw(false);
//                Canvas c = holder.lockCanvas(null);
//                onDraw(c);
//                holder.unlockCanvasAndPost(c);
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format,
//                                       int width, int height) {
//            }
//        });
    }

    private void init() {
        mBytes = null;
        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(0, 128, 255));
        mForePaint1.setStrokeWidth(1f);
        mForePaint1.setAntiAlias(true);
        mForePaint1.setColor(Color.rgb(255, 128, 0));

        root = this;

        pts = new ArrayList<>();
        ptPaint = new ArrayList<>();

        myThread = new DrawThread(this);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                myThread.setRunning(false);
                while (retry) {
                    try {
                        myThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }
            }


            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                myThread.setRunning(true);
                myThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        //invalidate();
//        c = surfaceHolder.lockCanvas(null);
//        onDraw(c);
//        surfaceHolder.unlockCanvasAndPost(c);
    }

    public void clear() {
        pts.clear();
        ptPaint.clear();
    }

    public void onResumeMySurfaceView() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void onPauseMySurfaceView() {
        boolean retry = true;
        running = false;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (running) {
            if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas();
                //... actual drawing on canvas

                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }

    protected void drawSomething(Canvas canvas){
        width = canvas.getWidth();
        height = canvas.getHeight();

        int midx = (int) (width / 2);
        int midy = (int) (height / 2);

//        mForePaint.setColor(Color.WHITE);
//        canvas.drawCircle(midx, midy, 50, mForePaint);

        Log.d("SIZE", width + ":" + height);

        outerRadius = (float) (Math.min(width, height) * 0.47);
        normalizedPosition = ((float) ((System.currentTimeMillis() - PlayerFragment.startTime) + PlayerFragment.totalElapsedTime)) / (float) (PlayerFragment.durationInMilliSec);
        if (mBytes == null) {
            return;
        }
        angle = (float) (Math.PI - normalizedPosition * TAU);
        Log.d("ANGLE", angle + "");
        color = 0;
        lnDataDistance = 0;
        distance = 0;
        size = 0;
        volume = 0;
        power = 0;

        float x, y;

        // Redraw previous points
        /*for (int i = 0; i < pts.size(); i++) {
            mForePaint.setColor(ptPaint.get(i).second.first);
            mForePaint.setAlpha(ptPaint.get(i).second.second);
            canvas.drawCircle(pts.get(i).first, pts.get(i).second, ptPaint.get(i).first, mForePaint);
        }*/

        // calculate min and max amplitude for current byte array
        float max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        for (int a = 16; a < (mBytes.length / 2); a++) {
            Log.d("BYTE", mBytes[a] + "");
            float amp = mBytes[(a * 2) + 0] * mBytes[(a * 2) + 0] + mBytes[(a * 2) + 1] * mBytes[(a * 2) + 1];
            if (amp > max) {
                max = amp;
            }
            if (amp < min) {
                min = amp;
            }
        }

        Log.d("SIZE2", width + ":" + height);

        /**
         * Number Fishing is all that is used here to get the best looking DNA
         * Number fishing is HOW YOU WIN AT LIFE. -- paullewis :)
         * **/

        for (int a = 16; a < (mBytes.length / 2); a++) {
            Log.d("BYTE", mBytes[a] + "");

            // scale the amplitude to the range [0,1]
            float amp = mBytes[(a * 2) + 0] * mBytes[(a * 2) + 0] + mBytes[(a * 2) + 1] * mBytes[(a * 2) + 1];
            if (max != min)
                amp = (amp - min) / (max - min);
            else {
                amp = 0;
            }

            volume = ((float) amp);             // REDUNDANT :P

            // converting polar to cartesian (distance calculated afterwards acts as radius for polar co-ords)
            x = (float) Math.sin(angle);
            y = (float) Math.cos(angle);

            // filtering low amplitude
            if (volume < 0.39) {
                continue;
            }

            // color ( value of hue inn HSV ) calculated based on current progress of the song or audio clip
            color = (float) (normalizedPosition - 0.12 + Math.random() * 0.24);
            color = Math.round(color * 360);

            // calculating distance from center ( 'r' in polar coordinates)
            lnDataDistance = (float) ((Math.log(a - 4) / LOG_MAX) - BASE);
            distance = lnDataDistance * outerRadius;

            // size of the circle to be rendered at the calculated position
            size = (float) (4.5 * volume * MAX_DOT_SIZE + Math.random() * 2);

            // alpha also based on volume ( amplitude )
            alpha = (float) (volume * 0.09);

            // final cartesian coordinates for drawing on canvas
            x = x * distance;
            y = y * distance;


            float[] hsv = new float[3];
            hsv[0] = color;
            hsv[1] = (float) 0.8;
            hsv[2] = (float) 0.5;

            // setting color of the Paint
            mForePaint.setColor(Color.HSVToColor(hsv));

            if (size >= 10.0 && size < 17.0) {
                mForePaint.setAlpha(9);
            } else if (size >= 17.0) {
                mForePaint.setAlpha(0);
            } else {
                mForePaint.setAlpha((int) (alpha * 1000));
//                mForePaint.setAlpha((int) 30);
            }

            // Setting alpha of the Paint
            //mForePaint.setAlpha((int) (alpha * 1000));

            // Draw the circles at correct position
            canvas.drawCircle(midx + x, midy + y, size, mForePaint);


            // Add points and paint config to lists for redraw
            pts.add(Pair.create(midx + x, midy + y));
            ptPaint.add(Pair.create(size, Pair.create(mForePaint.getColor(), mForePaint.getAlpha())));
        }
    }

}
