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
import org.testng.annotations.Test;

import java.util.*;

public class AssertLibraryTest {
    private final AssertLibrary assertLibrary = new AssertLibrary(new ObjectMapper());

    @Test(expectedExceptions = UnmetAssertionException.class, expectedExceptionsMessageRegExp = "Assertion not met: Expected message")
    public void testFailThrowsException() {
        assertLibrary.fail("Expected message");
    }

    @Test
    public void testNotNullWithValue() {
        assertLibrary.notNull("42", 42);
    }

    @Test(expectedExceptions = UnmetAssertionException.class)
    public void testNotNullWithNull() {
        assertLibrary.notNull("null", null);
    }

    @Test
    public void testNotNullOrEmptyWithValue() {
        assertLibrary.notNullOrEmpty("string", "not null");
        assertLibrary.notNullOrEmpty("list", Collections.emptyList());
    }

    @Test(expectedExceptions = UnmetAssertionException.class)
    public void testNotNullOrEmptyWithNull() {
        assertLibrary.notNullOrEmpty("null", null);
    }

    @Test(expectedExceptions = UnmetAssertionException.class)
    public void testNotNullOrEmptyWithEmptyString() {
        assertLibrary.notNullOrEmpty("empty", "");
    }

    @Test
    public void testEqualListAndMap() {
        List<String> list = Arrays.asList("foo", "bar", "baz");
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "foo");
        map.put(1, "bar");
        map.put(2, "baz");

        assertLibrary.equal("list and map", list, map);
        assertLibrary.equal("map and list", map, list);
    }

    @Test
    public void testEqualNull() {
        assertLibrary.equal("null and null", null, null);
    }
}