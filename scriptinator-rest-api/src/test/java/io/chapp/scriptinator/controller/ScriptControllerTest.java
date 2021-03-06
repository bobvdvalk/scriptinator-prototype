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
import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.repositories.JobRepository;
import io.chapp.scriptinator.repositories.ProjectRepository;
import io.chapp.scriptinator.repositories.ScriptRepository;
import io.chapp.scriptinator.repositories.UserRepository;
import io.chapp.scriptinator.utils.ScriptinatorTestCase;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static io.chapp.scriptinator.utils.ScriptinatorTestCase.DEFAULT_USERNAME;
import static org.testng.Assert.assertEquals;

@Listeners(ScriptinatorTestCase.class)
public class ScriptControllerTest {
    private final OkHttpClient client = new OkHttpClient();
    @Autowired
    private Headers accessToken;

    @Autowired
    private ScriptRepository scriptRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private JobRepository jobRepository;

    @Test
    public void testWhenScriptIsRequestedItReturned() throws IOException {
        // Precondition
        User user = userRepository.findByUsername(DEFAULT_USERNAME).get();

        Project project = new Project();
        project.setName("HelloWorld");
        project.setDescription("This is a description.");
        project.setOwner(user);
        projectRepository.save(project);

        Script script = new Script();
        script.setProject(project);
        script.setCode("info('Hello World');");
        script.setName("greet");
        script.setDescription("Prints hello world.");
        scriptRepository.save(script);


        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/api/scripts/" + script.getId())
                        .headers(accessToken)
                        .build()
        ).execute();

        // Validation
        ReadContext json = JsonPath.parse(response.body().string());

        assertEquals(
                json.read("$.name"),
                "greet"
        );

        assertEquals(
                json.read("$.code"),
                "info('Hello World');"
        );

        assertEquals(
                json.read("$.project.name"),
                "HelloWorld"
        );

        assertEquals(
                json.read("$.project.owner.username"),
                DEFAULT_USERNAME
        );
    }

    @Test
    public void testWhenScriptListIsRequestedItReturned() throws IOException {
        // Precondition
        User user = userRepository.findByUsername(DEFAULT_USERNAME).get();

        Project project = new Project();
        project.setName("HelloWorld");
        project.setDescription("This is a description.");
        project.setOwner(user);
        projectRepository.save(project);

        Script script = new Script();
        script.setProject(project);
        script.setCode("info('Hello World');");
        script.setName("greet");
        script.setDescription("Prints hello world.");
        scriptRepository.save(script);


        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/api/scripts/")
                        .headers(accessToken)
                        .build()
        ).execute();

        // Validation
        String body = response.body().string();
        ReadContext json = JsonPath.parse(body);

        assertEquals(
                json.read("$.totalItemCount"),
                (Integer) 1
        );

        assertEquals(
                json.read("$.items.[0].name"),
                "greet"
        );

        assertEquals(
                json.read("$.items.[0].project.name"),
                "HelloWorld"
        );

        assertEquals(
                json.read("$.hasItems"),
                Boolean.valueOf("true")
        );

        assertEquals(
                json.read("$.hasNext"),
                Boolean.valueOf("false")
        );

        assertEquals(
                json.read("$.hasPrevious"),
                Boolean.valueOf("false")
        );
    }

    @Test
    public void scriptReturnEmptyScriptList() throws IOException {
        // Precondition
        User user = userRepository.findByUsername(DEFAULT_USERNAME).get();

        Project project = new Project();
        project.setName("HelloWorld");
        project.setDescription("This is a description.");
        project.setOwner(user);
        projectRepository.save(project);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/api/scripts")
                        .headers(accessToken)
                        .build()
        ).execute();

        // Validation
        ReadContext json = JsonPath.parse(response.body().string());

        assertEquals(
                json.read("$.totalItemCount"),
                (Integer) 0
        );

        assertEquals(
                json.read("$.hasItems"),
                Boolean.valueOf("false")
        );

        assertEquals(
                json.read("$.hasNext"),
                Boolean.valueOf("false")
        );

        assertEquals(
                json.read("$.hasPrevious"),
                Boolean.valueOf("false")
        );

    }

    @Test
    public void testGettingJobsFromScriptListsAllJobs() throws IOException {
        // Precondition
        User defaultUser = userRepository.findByUsername(DEFAULT_USERNAME).get();

        Project project = new Project();
        project.setName("testJobListing");
        project.setOwner(defaultUser);
        project = projectRepository.save(project);

        Script script = new Script();
        script.setName("testScript");
        script.setProject(project);
        script = scriptRepository.save(script);

        Job jobOne = new Job();
        jobOne.setScript(script);
        jobOne.setStatus(Job.Status.QUEUED);
        jobOne.setQueuedTime(new Date());
        jobOne = jobRepository.save(jobOne);

        Job jobTwo = new Job();
        jobTwo.setScript(script);
        jobTwo.setStatus(Job.Status.RUNNING);
        jobTwo.setQueuedTime(new Date());
        jobTwo.setStartedTime(new Date());
        jobTwo = jobRepository.save(jobTwo);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/api/scripts/" + script.getId() + "/jobs")
                        .headers(accessToken)
                        .build()
        ).execute();

        // Validation
        ReadContext json = JsonPath.parse(response.body().string());

        assertEquals(
                new HashSet<>(json.read("$.items[*].id")),
                new HashSet<>(Arrays.asList(
                        jobOne.getId().intValue(),
                        jobTwo.getId().intValue()
                ))
        );
    }

    @Test
    public void testRunningScriptCreatesAndReturnsJob() throws IOException {
        // Precondition
        User defaultUser = userRepository.findByUsername(DEFAULT_USERNAME).get();

        Project project = new Project();
        project.setName("testScriptRunning");
        project.setOwner(defaultUser);
        project = projectRepository.save(project);

        Script script = new Script();
        script.setName("testScript");
        script.setProject(project);
        script.setCode("info('Hello!');");
        script = scriptRepository.save(script);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .post(RequestBody.create(MediaType.parse("application/json"), "{\"argument\": {\"Hello\": \"World\"}}"))
                        .url("http://localhost:8080/api/scripts/" + script.getId() + "/jobs")
                        .headers(accessToken)
                        .build()
        ).execute();

        // Validation
        ReadContext json = JsonPath.parse(response.body().string());


        assertEquals(
                response.code(),
                201
        );

        // Pull the id from the location header
        String location = response.header("Location");
        int jobId = Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));

        assertEquals(
                (int) json.read("$.id"),
                jobId
        );
        assertEquals(
                json.read("$.scriptUrl"),
                "http://localhost:8080/api/scripts/" + script.getId()
        );
        assertEquals(
                json.read("$.status"),
                "QUEUED"
        );
        assertEquals(
                json.read("$.argument"),
                "{\"Hello\":\"World\"}"
        );
    }
}