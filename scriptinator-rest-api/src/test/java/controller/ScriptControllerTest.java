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
