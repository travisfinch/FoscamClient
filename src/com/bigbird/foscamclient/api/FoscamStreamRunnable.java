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

package com.bigbird.foscamclient.api;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.bigbird.foscamclient.MjpegInputStream;

public class FoscamStreamRunnable implements Runnable {
    private FoscamCamera camera;
    private FoscamStreamReadyListener listener;
    private MjpegInputStream stream;

    public FoscamStreamRunnable(FoscamCamera camera, FoscamStreamReadyListener listener) {
        this.camera = camera;
        this.listener = listener;
    }

    public void run() {
        String url = FoscamUrlFactory.getStreamUrl(camera);
        DefaultHttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(new HttpGet(URI.create(url)));
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                stream = new MjpegInputStream(entity.getContent());
            }
        } catch (Exception e) {
            /* Do nothing */
        } finally {
            listener.onFoscamStreamReady(stream);
        }
    }
}
