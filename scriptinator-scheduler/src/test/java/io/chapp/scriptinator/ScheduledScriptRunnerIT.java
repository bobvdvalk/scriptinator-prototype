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
package io.chapp.scriptinator;

import io.chapp.scriptinator.model.*;
import io.chapp.scriptinator.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Date;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

@ContextConfiguration(classes = ScriptinatorScheduler.class)
public class ScheduledScriptRunnerIT extends AbstractTestNGSpringContextTests {
    @Autowired
    private ScriptRepository scriptRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private ScriptinatorSchedulerConfiguration configuration;

    private Project project;
    private Script script;

    @BeforeClass
    public void setup() {
        configuration.setSchedulerInterval(1);

        User user = new User();
        user.setDisplayName("Schedule User");
        user.setEmail("schedule@me.com");
        user.setUsername("SU12");
        user.setPassword("eludehcs");
        userRepository.save(user);

        project = new Project();
        project.setName("ScheduledProject");
        project.setOwner(user);
        project = projectRepository.save(project);

        script = new Script();
        script.setProject(project);
        script.setName("schedule-me");
        script = scriptRepository.save(script);
    }

    @AfterClass
    public void teardown() {
        userRepository.deleteAll();
    }

    @AfterMethod
    public void removeSchedulesAndJobs() {
        scheduleRepository.deleteAll();
        jobRepository.deleteAll();
    }

    @Test
    public void testSchedulerRunsSchedules() {
        long scheduleId = createSchedule("* * * * * ?", script.getName());

        /*
        Since this is a very timing-sensitive test with such short timings,
        asserting that there were e.g. exactly 2 jobs resulted in inconsistent test results.
        */
        await("Wait until schedule is processed.")
                .atMost(3, TimeUnit.SECONDS) // Less than 3 seconds resulted in inconsistency as well. *sigh*
                .until(() -> scheduleRepository.findOne(scheduleId).getLastRun() != null);

        Page<Job> jobs = jobRepository.findByScriptId(script.getId(), new PageRequest(0, 1));
        for (Job job : jobs) {
            assertEquals((long) job.getTriggeredByScheduleId(), scheduleId);
            assertEquals(job.getArgument(), "true");
        }
    }

    @Test
    public void testInvalidScriptReferenceDisablesSchedule() {
        long scheduleId = createSchedule("* * * * * ?", "invalid");

        await("Wait until schedule is processed.")
                .atMost(2, TimeUnit.SECONDS)
                .until(() -> scheduleRepository.findOne(scheduleId).getLastRun() != null);

        Schedule schedule = scheduleRepository.findOne(scheduleId);
        assertFalse(schedule.isEnabled());
        assertEquals(schedule.getStatus(), Schedule.Status.INVALID_SCRIPT);
    }

    @Test
    public void testInvalidCronDisablesSchedule() {
        long scheduleId = createSchedule("that's not a cron, that's a chicken", script.getName());

        await("Wait until schedule is processed.")
                .atMost(2, TimeUnit.SECONDS)
                .until(() -> scheduleRepository.findOne(scheduleId).getLastRun() != null);

        Schedule schedule = scheduleRepository.findOne(scheduleId);
        assertFalse(schedule.isEnabled());
        assertEquals(schedule.getStatus(), Schedule.Status.INVALID_CRON);
    }

    private long createSchedule(String cron, String script) {
        Schedule schedule = new Schedule();
        schedule.setName("schedule");
        schedule.setDescription("Tick tock tick tock");
        schedule.setCronString(cron);
        schedule.setProject(project);
        schedule.setScriptName(script);
        schedule.setEnabled(true);
        schedule.setArgument("true");
        schedule.setNextRun(Date.from(Instant.EPOCH));
        return scheduleRepository.save(schedule).getId();
    }
}