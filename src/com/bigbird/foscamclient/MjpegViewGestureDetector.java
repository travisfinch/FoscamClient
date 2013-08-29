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

import org.apache.http.client.ClientProtocolException;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import com.bigbird.foscamclient.api.FoscamController;

public class MjpegViewGestureDetector extends SimpleOnGestureListener {
    private static final int FLING_MAX_OFF_PATH = 250;
    private static final int FLING_MIN_DISTANCE = 120;
    private static final int FLING_VELOCITY_THRESHOLD = 200;
    private static final int LENGTH_MIN = 300;
    private static final int LENGTH_MAX = 1000;

    private FoscamController controller;

    public MjpegViewGestureDetector(FoscamController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean value = false;
        try {
            if (Math.abs(e1.getY() - e2.getY()) < FLING_MAX_OFF_PATH) {
                if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_VELOCITY_THRESHOLD) {
                    onFlingLeft(e1.getX() - e2.getX());
                } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_VELOCITY_THRESHOLD) {
                    onFlingRight(e2.getX() - e1.getX());
                }
                value = true;
            } else if (Math.abs(e1.getX() - e2.getX()) < FLING_MAX_OFF_PATH) {
                if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(velocityY) > FLING_VELOCITY_THRESHOLD) {
                    onFlingUp(e1.getY() - e2.getY());
                } else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE && Math.abs(velocityY) > FLING_VELOCITY_THRESHOLD) {
                    onFlingDown(e2.getY() - e1.getY());
                }
                value = true;
            }
        } catch (Exception e) {
            /* Do nothing */
        }
        return value;
    }

    private int sanitizeLength(float length) {
        int value;
        if (length < LENGTH_MIN) {
            value = LENGTH_MIN;
        } else if (length > LENGTH_MAX) {
            value = LENGTH_MAX;
        } else {
            value = (int) length;
        }
        return value;
    }

    public void onFlingLeft(float length) throws ClientProtocolException, IOException {
        controller.moveLeft(sanitizeLength(length));
    }

    public void onFlingRight(float length) throws ClientProtocolException, IOException {
        controller.moveRight(sanitizeLength(length));
    }

    public void onFlingUp(float length) throws ClientProtocolException, IOException {
        controller.moveUp(sanitizeLength(length));
    }

    public void onFlingDown(float length) throws ClientProtocolException, IOException {
        controller.moveDown(sanitizeLength(length));
    }
}
