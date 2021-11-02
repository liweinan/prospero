/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.prospero.api;

import java.time.Instant;
import java.util.Locale;

public class SavedState {

    public enum Type { UPDATE, INSTALL, ROLLBACK;}

    private String hash;
    private Instant timestamp;
    private Type type;

    public SavedState(String hash, Instant timestamp, Type type) {
        this.hash = hash;
        this.timestamp = timestamp;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return this.hash;
    }

    public Manifest getMetadata() {
        return null;
    }

    public String shortDescription() {
        return String.format("[%s] %s - %s", hash, timestamp.toString(), type.toString().toLowerCase(Locale.ROOT));
    }
}
