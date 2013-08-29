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
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.bigbird.foscamclient.utils.ToastManager;

public class FoscamCommandRunnable implements Runnable {
    private List<String> urls;
    private int length;

    public FoscamCommandRunnable(String start, String stop, int length) {
        urls = new ArrayList<String>();
        urls.add(start);
        urls.add(stop);
        this.length = length;
    }

    public void run() {
        ToastManager manager = ToastManager.getInstance();
        for (String url : urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            try {
                HttpResponse response = client.execute(new HttpGet(URI.create(url)));
                int status = response.getStatusLine().getStatusCode();
                if (status != HttpStatus.SC_OK) {
                    manager.makeToast("Unable to execute command: " + status);
                    continue;
                }
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    entity.consumeContent();
                }
                Thread.sleep(length);
            } catch (Exception e) {
                manager.makeToast("Unable to execute command: " + e.getMessage());
            } finally {
                client.getConnectionManager().shutdown();
            }
        }
    }
}
