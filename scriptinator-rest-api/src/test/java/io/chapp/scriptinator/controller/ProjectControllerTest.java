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
import io.chapp.scriptinator.model.*;
import io.chapp.scriptinator.repositories.*;
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
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.testng.Assert.assertEquals;

@Listeners(ScriptinatorTestCase.class)
public class ProjectControllerTest {
    private final OkHttpClient client = new OkHttpClient();
    @Autowired
    private Headers accessToken;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ScriptRepository scriptRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private WebhookRepository webhookRepository;

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
                        .url("http://localhost:8080/api/projects/hoiBobDitIsEenTest")
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
                "http://localhost:8080/api/projects/hoiBobDitIsEenTest"
        );
        assertEquals(
                json.read("$.scriptsUrl"),
                "http://localhost:8080/api/projects/hoiBobDitIsEenTest/scripts"
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
                        .url("http://localhost:8080/api/projects")
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
                        .url("http://localhost:8080/api/projects?page=2")
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
                        .url("http://localhost:8080/api/projects/aNiceProjectWithScripts/scripts")
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
                        .url("http://localhost:8080/api/projects/aSuperScheduledProject/schedules")
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

    @Test
    public void testListProjectWebhooksReturnsAllWebhooks() throws IOException {
        // Precondition
        Project project = new Project();
        project.setOwner(userRepository.findByUsername(ScriptinatorTestCase.DEFAULT_USERNAME).get());
        project.setName("sensitiveProject");
        project = projectRepository.save(project);

        Script script = new Script();
        script.setProject(project);
        script.setName("hashtagTriggered");
        scriptRepository.save(script);

        Webhook webhook = new Webhook();
        webhook.setUuid(UUID.randomUUID().toString());
        webhook.setProject(project);
        webhook.setScriptName("hashtagTriggered");
        webhook.setName("triggerMe");
        webhookRepository.save(webhook);
        webhook = new Webhook();
        webhook.setUuid(UUID.randomUUID().toString());
        webhook.setProject(project);
        webhook.setScriptName("hashtagTriggered");
        webhook.setName("triggerHappy");
        webhookRepository.save(webhook);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/api/projects/sensitiveProject/webhooks")
                        .headers(accessToken)
                        .build()
        ).execute();

        // Validation
        String body = response.body().string();
        ReadContext json = JsonPath.parse(body);

        assertEquals(
                response.code(),
                200
        );
        assertEquals(
                new HashSet<>(json.read("$.items[*].name")),
                new HashSet<>(Arrays.asList(
                        "triggerMe",
                        "triggerHappy"
                ))
        );
    }
}
