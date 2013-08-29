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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MjpegInputStream extends DataInputStream {
    private static final byte[] SOI_MARKER = { (byte) 0xFF, (byte) 0xD8 };
    private static final byte[] EOF_MARKER = { (byte) 0xFF, (byte) 0xD9 };
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final int HEADER_MAX_LENGTH = 100;
    private static final int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;

    public MjpegInputStream(InputStream stream) {
        super(new BufferedInputStream(stream, FRAME_MAX_LENGTH));
    }

    private int getEndOfSequence(DataInputStream stream, byte[] sequence) throws IOException {
        int index = 0;
        byte b;
        for (int i = 0; i < FRAME_MAX_LENGTH; i++) {
            b = (byte) stream.readUnsignedByte();
            if (b == sequence[index]) {
                index++;
                if (index == sequence.length) {
                    return i + 1;
                }
            } else {
                index = 0;
            }
        }
        return -1;
    }

    private int getStartOfSequence(DataInputStream stream, byte[] sequence) throws IOException {
        int end = getEndOfSequence(stream, sequence);
        return (end < 0) ? (-1) : (end - sequence.length);
    }

    private int parseContentLength(byte[] header) throws IOException, NumberFormatException {
        ByteArrayInputStream stream = new ByteArrayInputStream(header);
        Properties props = new Properties();
        props.load(stream);
        return Integer.parseInt(props.getProperty(CONTENT_LENGTH));
    }

    public Bitmap readMjpegFrame() throws IOException {
        mark(FRAME_MAX_LENGTH);
        int headerLength = getStartOfSequence(this, SOI_MARKER);
        reset();
        byte[] header = new byte[headerLength];
        readFully(header);
        int contentLength;
        try {
            contentLength = parseContentLength(header);
        } catch (NumberFormatException nfe) {
            contentLength = getEndOfSequence(this, EOF_MARKER);
        }
        reset();
        byte[] frame = new byte[contentLength];
        skipBytes(headerLength);
        readFully(frame);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(frame));
    }
}
