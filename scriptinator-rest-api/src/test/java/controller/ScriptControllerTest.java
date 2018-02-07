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
package controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptControllerTest.class);

    @Before
    public void setupMockStuff() {
        LOGGER.info("Starting ScriptControllerTest");
        //todo: create mock database and then retrieve data
    }

    @Test
    public void testRetrieveScriptById() {
        int scriptId = 1;
    }

    @Test
    public void testRetrieveScriptByProjectIdAndQualifiedName() {
        int projectId = 1;
        String scriptName = "HelloWorld";
    }

    @After
    public void afterTest() {
        LOGGER.info("Stopping ScriptControllerTest");
        //todo: do stuff after the test.
    }
}
