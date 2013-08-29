/**
 * Copyright 2013 Travis Finch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bigbird.foscamclient;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MjpegView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private MjpegViewRunnable runnable;
    private Thread thread;
    private MjpegInputStream stream;
    private int width;
    private int height;
    private boolean running;
    private boolean surfaceReady;

    public MjpegView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);
        runnable = new MjpegViewRunnable();
        width = getWidth();
        height = getHeight();
        running = false;
        surfaceReady = false;
    }

    private class MjpegViewRunnable implements Runnable {
        private Rect getDisplayRect(int w, int h) {
            int x = (width / 2) - (w / 2);
            int y = (height / 2) - (h / 2);
            return new Rect(x, y, w + x, h + y);
        }

        public void run() {
            Canvas canvas = null;
            Bitmap bitmap;
            Rect rect;
            Paint paint = new Paint();
            while (running) {
                if (surfaceReady) {
                    try {
                        canvas = holder.lockCanvas();
                        synchronized (holder) {
                            try {
                                bitmap = stream.readMjpegFrame();
                                rect = getDisplayRect(bitmap.getWidth(), bitmap.getHeight());
                                canvas.drawColor(Color.BLACK);
                                canvas.drawBitmap(bitmap, null, rect, paint);
                            } catch (IOException e) {
                                /* Do nothing */
                            }
                        }
                    } finally {
                        if (canvas != null) {
                            holder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        surfaceReady = true;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceReady = false;
        stop();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        synchronized (holder) {
            this.width = width;
            this.height = height;
        }
    }

    public void setSource(MjpegInputStream stream) {
        this.stream = stream;
    }

    public void start() {
        if (stream != null) {
            thread = new Thread(runnable);
            thread.start();
            running = true;
        }
    }

    public void stop() {
        if (running) {
            running = false;
            boolean retry = true;
            while (retry) {
                try {
                    thread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    /* Do nothing */
                }
            }
            try {
                stream.close();
            } catch (IOException e) {
                /* Do nothing */
            }
        }
    }
}
