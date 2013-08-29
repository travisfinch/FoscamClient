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

import java.util.Locale;

public class FoscamUrlFactory {
    public static String getStreamUrl(FoscamCamera c) {
        return String.format(Locale.US, FoscamConsts.STREAM_FORMAT, c.getHost(), c.getPort(), c.getUser(), c.getPassword(),
                FoscamConsts.RESOLUTION);
    }

    public static String getControlUrl(FoscamCamera c, int command) {
        return String.format(Locale.US, FoscamConsts.CONTROL_FORMAT, c.getHost(), c.getPort(), c.getUser(), c.getPassword(),
                command);
    }

    public static String getSnapshotUrl(FoscamCamera c) {
        return String.format(Locale.US, FoscamConsts.SNAPSHOT_FORMAT, c.getHost(), c.getPort(), c.getUser(), c.getPassword());
    }
}
