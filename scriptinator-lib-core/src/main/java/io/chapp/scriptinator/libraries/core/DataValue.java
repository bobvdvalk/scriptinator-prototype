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
package io.chapp.scriptinator.libraries.core;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class DataValue {
    private static final String INDENT = "  ";

    @Override
    public String toString() {
        Map<String, Object> introspect = new HashMap<>();
        inspect(introspect);

        String body = "[]";
        if (!introspect.isEmpty()) {
            body = introspect.entrySet().stream()
                    .map(entry -> "\n" + entry.getKey() + " = " + entry.getValue())
                    .map(value -> StringUtils.replace(value, "\n", "\n" + INDENT))
                    .collect(Collectors.joining(
                            ",",
                            "[",
                            "\n]"
                    ));
        }

        return getClass().getSimpleName() + ":" + Objects.hashCode(this) + " " + body;
    }

    protected void inspect(Map<String, Object> target) {
    }
}
