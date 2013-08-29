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

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.bigbird.foscamclient.api.FoscamCamera;
import com.bigbird.foscamclient.api.FoscamController;
import com.bigbird.foscamclient.api.FoscamStreamReadyListener;
import com.bigbird.foscamclient.api.FoscamStreamRunnable;
import com.bigbird.foscamclient.utils.ToastManager;

public class FoscamViewer extends Activity implements FoscamStreamReadyListener {
    private MjpegView view;
    private FoscamCamera camera;
    private FoscamController controller;
    private GestureDetector detector;
    private ProgressDialog dialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.foscam_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean value = false;
        switch (item.getItemId()) {
        case R.id.snapshot:
            controller.takeSnapshot();
            break;
        case R.id.restart:
            startStream();
            value = true;
            break;
        case R.id.quit:
            finish();
            value = true;
            break;
        }
        return value;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ToastManager.getInstance().init(this, new Handler());
        view = new MjpegView(this);
        setContentView(view);
        String host = getString(R.string.foscam_hostname);
        int port = Integer.parseInt(getString(R.string.foscam_port));
        String user = getString(R.string.foscam_user);
        String password = getString(R.string.foscam_password);
        camera = new FoscamCamera(host, port, user, password);
        controller = new FoscamController(camera);
        detector = new GestureDetector(this, new MjpegViewGestureDetector(controller));
        view.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });
        startStream();
    }

    @Override
    public void onPause() {
        super.onPause();
        view.stop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    public void startStream() {
        view.stop();
        dialog = ProgressDialog.show(this, "", "Loading stream...");
        FoscamStreamRunnable runnable = new FoscamStreamRunnable(camera, this);
        new Thread(runnable).start();
    }

    public void onFoscamStreamReady(MjpegInputStream stream) {
        dialog.dismiss();
        if (stream != null) {
            view.setSource(stream);
            view.start();
        } else {
            ToastManager.getInstance().makeToast("Unable to load MJPEG stream");
        }
    }
}
