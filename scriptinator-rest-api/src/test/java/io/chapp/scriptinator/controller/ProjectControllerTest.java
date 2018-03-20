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
package io.chapp.scriptinator.controller;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.Schedule;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.repositories.ProjectRepository;
import io.chapp.scriptinator.repositories.ScheduleRepository;
import io.chapp.scriptinator.repositories.ScriptRepository;
import io.chapp.scriptinator.repositories.UserRepository;
import io.chapp.scriptinator.utils.ScriptinatorTestCase;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import static java.util.Collections.emptyList;
import static org.testng.Assert.assertEquals;

@Listeners(ScriptinatorTestCase.class)
public class ProjectControllerTest {
    private final OkHttpClient client = new OkHttpClient();
    @Autowired
    Headers accessToken;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ScriptRepository scriptRepository;
    @Autowired
    ScheduleRepository scheduleRepository;

    @Test
    public void testWhenProjectIsRequestedItIsReturned() throws IOException {
        // Precondition
        Project project = new Project();
        project.setOwner(userRepository.findByUsername(ScriptinatorTestCase.DEFAULT_USERNAME).get());
        project.setName("hoiBobDitIsEenTest");
        project.setDescription("We can enter a nice description");
        projectRepository.save(project);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/projects/hoiBobDitIsEenTest")
                        .headers(accessToken)
                        .build()
        ).execute();

        // Validation
        ReadContext json = JsonPath.parse(response.body().string());

        assertEquals(
                json.read("$.name"),
                "hoiBobDitIsEenTest"
        );
        assertEquals(
                json.read("$.description"),
                "We can enter a nice description"
        );
        assertEquals(
                json.read("$.id"),
                (Integer) project.getId().intValue()
        );
        assertEquals(
                json.read("$.url"),
                "http://localhost:8080/projects/hoiBobDitIsEenTest"
        );
        assertEquals(
                json.read("$.scriptsUrl"),
                "http://localhost:8080/projects/hoiBobDitIsEenTest/scripts"
        );
    }

    @Test
    public void testWhenProjectListIsRequestedAllProjectsArePresent() throws IOException {
        // Precondition
        User defaultUser = userRepository.findByUsername(ScriptinatorTestCase.DEFAULT_USERNAME).get();
        Project project = new Project();
        project.setOwner(defaultUser);
        project.setName("thisIsProject1");
        projectRepository.save(project);
        project = new Project();
        project.setOwner(defaultUser);
        project.setName("thisIsProject2");
        projectRepository.save(project);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/projects")
                        .headers(accessToken)
                        .build()
        ).execute();

        // Validation
        ReadContext json = JsonPath.parse(response.body().string());

        assertEquals(
                json.read("$.items[*].name"),
                Arrays.asList(
                        "thisIsProject1",
                        "thisIsProject2"
                )
        );

    }

    @Test
    public void testWhenRequestingPageNumberTooHighNoDataIsReturned() throws IOException {
        // Precondition
        User defaultUser = userRepository.findByUsername(ScriptinatorTestCase.DEFAULT_USERNAME).get();
        Project project = new Project();
        project.setOwner(defaultUser);
        project.setName("thisIsProject1");
        projectRepository.save(project);
        project = new Project();
        project.setOwner(defaultUser);
        project.setName("thisIsProject2");
        projectRepository.save(project);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/projects?page=2")
                        .headers(accessToken)
                        .build()
        ).execute();

        // Validation
        ReadContext json = JsonPath.parse(response.body().string());

        assertEquals(
                json.read("$.items[*].name"),
                emptyList()
        );
        assertEquals(
                (int) json.read("$.itemCount"),
                0
        );
        assertEquals(
                (int) json.read("$.totalItemCount"),
                2
        );
    }

    @Test
    public void testListProjectScriptReturnsAllScripts() throws IOException {
        // Precondition
        User defaultUser = userRepository.findByUsername(ScriptinatorTestCase.DEFAULT_USERNAME).get();
        Project project = new Project();
        project.setOwner(defaultUser);
        project.setName("aNiceProjectWithScripts");
        project = projectRepository.save(project);

        Script script = new Script();
        script.setProject(project);
        script.setName("testScript");
        scriptRepository.save(script);
        script = new Script();
        script.setProject(project);
        script.setName("someOtherScript");
        scriptRepository.save(script);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/projects/aNiceProjectWithScripts/scripts")
                        .headers(accessToken)
                        .build()
        ).execute();

        // Validation
        String body = response.body().string();
        ReadContext json = JsonPath.parse(body);

        assertEquals(
                new HashSet<>(json.read("$.items[*].name")),
                new HashSet<>(Arrays.asList(
                        "testScript",
                        "someOtherScript"
                ))
        );
    }

    @Test
    public void testListProjectSchedulesReturnsAllSchedules() throws IOException {
        // Precondition
        Project project = new Project();
        project.setOwner(userRepository.findByUsername(ScriptinatorTestCase.DEFAULT_USERNAME).get());
        project.setName("aSuperScheduledProject");
        project = projectRepository.save(project);

        Script script = new Script();
        script.setProject(project);
        script.setName("triggered");
        scriptRepository.save(script);

        Schedule schedule = new Schedule();
        schedule.setProject(project);
        schedule.setName("scheduleMe");
        schedule.setScriptName("triggered");
        schedule.setCronString("* * * * * ?");
        scheduleRepository.save(schedule);
        schedule = new Schedule();
        schedule.setProject(project);
        schedule.setName("justInTime");
        schedule.setScriptName("triggered");
        schedule.setCronString("* * * * * ?");
        scheduleRepository.save(schedule);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/projects/aSuperScheduledProject/schedules")
                        .headers(accessToken)
                        .build()
        ).execute();

        // Validation
        String body = response.body().string();
        ReadContext json = JsonPath.parse(body);

        assertEquals(
                new HashSet<>(json.read("$.items[*].name")),
                new HashSet<>(Arrays.asList(
                        "scheduleMe",
                        "justInTime"
                ))
        );
    }
}
