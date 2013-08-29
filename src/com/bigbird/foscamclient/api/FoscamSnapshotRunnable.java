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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.bigbird.foscamclient.utils.ToastManager;

public class FoscamSnapshotRunnable implements Runnable {
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String FILENAME_HEADER = "filename=\"";

    private FoscamCamera camera;

    public FoscamSnapshotRunnable(FoscamCamera camera) {
        this.camera = camera;
    }

    private String getSnapshotName(Header header) {
        String value = header.getValue();
        value = value.substring(FILENAME_HEADER.length(), value.length() - 1);
        return value;
    }

    private void writeSnapshot(String name, InputStream stream) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        ByteArrayOutputStream byteStream = null;
        FileOutputStream fileStream = null;
        try {
            byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + name);
            file.createNewFile();
            fileStream = new FileOutputStream(file);
            fileStream.write(byteStream.toByteArray());
        } finally {
            if (byteStream != null) {
                byteStream.close();
            }
            if (fileStream != null) {
                fileStream.close();
            }
        }
    }

    public void run() {
        ToastManager manager = ToastManager.getInstance();
        String url = FoscamUrlFactory.getSnapshotUrl(camera);
        DefaultHttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(new HttpGet(URI.create(url)));
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                manager.makeToast("Unable to take snapshot: " + status);
                return;
            }
            Header header = response.getFirstHeader(CONTENT_DISPOSITION);
            String name = getSnapshotName(header);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                writeSnapshot(name, entity.getContent());
                manager.makeToast("Saved \"" + name + "\"");
            }
        } catch (Exception e) {
            ToastManager.getInstance().makeToast("Unable to take snapshot: " + e.getMessage());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}
