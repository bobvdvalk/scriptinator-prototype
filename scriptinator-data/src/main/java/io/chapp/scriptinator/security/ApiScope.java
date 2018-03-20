/*
 * Copyright © 2018 Scriptinator (support@scriptinator.io)
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
package io.chapp.scriptinator.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ApiScope {
    JOB_READ,
    PROJECT_READ,
    SCHEDULE_READ,
    SCRIPT,
    SCRIPT_READ,
    SCRIPT_RUN;

    @Override
    @JsonValue
    public String toString() {
        return name().toLowerCase().replace('_', ':');
    }

    @JsonCreator
    public static ApiScope fromString(String value) {
        return ApiScope.valueOf(value.toUpperCase().replace(':', '_'));
    }

    public static List<String> allScopes() {
        return Arrays.stream(ApiScope.values())
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
