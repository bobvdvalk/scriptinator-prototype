/*
 * Copyright © 2018 Thomas Biesaart (thomas.biesaart@gmail.com)
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

import static java.util.Collections.emptyMap;

public class ObjectConverter {
    private final ObjectMapper mapper;

    public ObjectConverter(ObjectMapper mapper) {
        this.mapper = mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public <T> T read(Map<String, Object> value, Class<T> targetClass) {
        return mapper.convertValue(value == null ? emptyMap() : value, targetClass);
    }

    public <T> T readInto(Map<String, Object> request, T target) {
        try {
            return mapper.readerForUpdating(target).readValue(
                    mapper.writeValueAsBytes(request)
            );
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
