package com.example.harjot.musicstreamer;

import android.graphics.Canvas;

/**
 * Created by Harjot on 11-May-16.
 */
public class DrawThread extends Thread {
    OptimizedVisualizerView myView;
    private boolean running = false;

    public DrawThread(OptimizedVisualizerView view) {
        myView = view;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void run() {
        while(running){

            Canvas canvas = myView.getHolder().lockCanvas(null);

            if(canvas != null){
                synchronized (myView.getHolder()) {
                    myView.drawSomething(canvas);
                }
                myView.getHolder().unlockCanvasAndPost(canvas);
            }

            try {
                sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}
