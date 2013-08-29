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

public class FoscamConsts {
    public static final String STREAM_FORMAT = "http://%s:%d/videostream.cgi?user=%s&pwd=%s&resolution=%d";
    public static final String CONTROL_FORMAT = "http://%s:%d/decoder_control.cgi?user=%s&pwd=%s&command=%s";
    public static final String SNAPSHOT_FORMAT = "http://%s:%d/snapshot.cgi?user=%s&pwd=%s";

    public static final int RESOLUTION = 32;

    public static final int UP = 0;
    public static final int STOP_UP = 1;
    public static final int DOWN = 2;
    public static final int STOP_DOWN = 3;
    public static final int LEFT = 4;
    public static final int STOP_LEFT = 5;
    public static final int RIGHT = 6;
    public static final int STOP_RIGHT = 7;
}
