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
package io.chapp.scriptinator.libraries.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AssertLibrary {
    private final ObjectMapper comparisonMapper;

    public AssertLibrary(ObjectMapper comparisonMapper) {
        this.comparisonMapper = comparisonMapper;
    }

    public void fail(String message) {
        throw new UnmetAssertionException("Assertion not met: " + message);
    }

    public void notNull(String property, Object value) {
        if (value == null) {
            fail("Expected '" + property + "' to be not null.");
        }
    }

    public void notNullOrEmpty(String property, Object value) {
        if (value == null || "".equals(String.valueOf(value))) {
            fail("Expected '" + property + "' to not be null or empty.");
        }
    }

    @SuppressWarnings("squid:S1221") // This is an assertion
    public void equal(String message, Object expectedInput, Object actualInput) {
        Object expected = comparisonMapper.convertValue(expectedInput, Object.class);
        Object actual = comparisonMapper.convertValue(actualInput, Object.class);

        // Convert lists to linked hashmaps, because that is what a list from the script get converted to.
        if (expected instanceof List) {
            expected = listToMap((List) expected);
        }
        if (actual instanceof List) {
            actual = listToMap((List) actual);
        }

        if (!deepEquals(message, expected, actual)) {
            fail(message + "\nExpected '" + expected + "'\nFound: '" + actual + "'");
        }
    }

    private boolean deepEquals(String message, Object expected, Object actual) {
        if (expected instanceof Map && actual instanceof Map) {
            Map<Object, Object> expectedMap = (Map<Object, Object>) expected;
            Map<Object, Object> actualMap = (Map<Object, Object>) actual;

            if (expectedMap.size() != actualMap.size()) {
                return false;
            }

            expectedMap.forEach((key, value) -> equal(message, value, actualMap.get(key)));
            return true;
        } else {
            return Objects.equals(expected, actual);
        }
    }

    private Map<String, Object> listToMap(List list) {
        Map<String, Object> map = new LinkedHashMap<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            map.put(Integer.toString(i), list.get(i));
        }
        return map;
    }
}
