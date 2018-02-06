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
package io.chapp.scriptinator.libraries.test;

import com.fasterxml.jackson.databind.ObjectMapper;

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

        if (!expected.equals(actual)) {
            fail(message + "\nExpected '" + expected + "'\nFound: '" + actual + "'");
        }
    }
}
