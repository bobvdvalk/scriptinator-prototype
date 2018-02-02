package controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectControllerTest.class);

    @Before
    public void setupMockStuff() {
        LOGGER.info("Starting ProjectControllerTest");
        //todo: create mock database and then retrieve data
    }

    @Test
    public void testRetrieveProjectByProjectId() {
        int projectId = 1;
    }

    @Test
    public void testProjectListByPageId() {
        int projectId = 1;

    }

    @After
    public void afterTest() {
        LOGGER.info("Stopping ProjectControllerTest");
        //todo: do stuff after the test.
    }
}
