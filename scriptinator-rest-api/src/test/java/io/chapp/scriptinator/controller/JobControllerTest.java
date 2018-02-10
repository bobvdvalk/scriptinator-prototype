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
import io.chapp.scriptinator.repositories.JobRepository;
import io.chapp.scriptinator.repositories.ProjectRepository;
import io.chapp.scriptinator.repositories.ScriptRepository;
import io.chapp.scriptinator.repositories.UserRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static io.chapp.scriptinator.controller.ScriptinatorTestCase.DEFAULT_USERNAME;
import static org.testng.Assert.assertEquals;

@Listeners(ScriptinatorTestCase.class)
public class JobControllerTest {
    private final OkHttpClient client = new OkHttpClient();

    @Autowired
    UserRepository userRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ScriptRepository scriptRepository;
    @Autowired
    JobRepository jobRepository;

    @Test
    public void testListingJobsReturnsAllJobs() throws IOException {
        // Precondition
        Script script = createTestScript();

        Job jobOne = new Job();
        jobOne.setQueuedTime(new Date());
        jobOne.setStatus(Job.Status.QUEUED);
        jobOne.setScript(script);
        jobOne = jobRepository.save(jobOne);

        Job jobTwo = new Job();
        jobTwo.setQueuedTime(new Date());
        jobTwo.setStatus(Job.Status.QUEUED);
        jobTwo.setScript(script);
        jobTwo = jobRepository.save(jobTwo);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/jobs")
                        .build()
        ).execute();


        // Validation
        String body = response.body().string();
        ReadContext json = JsonPath.parse(body);

        assertEquals(
                new HashSet<>(json.read("$.items[*].id")),
                new HashSet<>(Arrays.asList(
                        jobOne.getId().intValue(),
                        jobTwo.getId().intValue()
                ))
        );
    }

    @Test
    public void testGetJobByIdReturnsValidInformation() throws IOException {
        // Precondition
        Script script = createTestScript();

        Job job = new Job();
        job.setQueuedTime(new Date());
        job.setStatus(Job.Status.QUEUED);
        job.setScript(script);
        job = jobRepository.save(job);


        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/jobs/" + job.getId())
                        .build()
        ).execute();


        // Validation
        String body = response.body().string();
        ReadContext json = JsonPath.parse(body);

        assertEquals(
                json.read("$.url"),
                "http://localhost:8080/jobs/" + job.getId()
        );
        assertEquals(
                json.read("$.status"),
                "QUEUED"
        );
        assertEquals(
                (String) json.read("$.finishedTime"),
                null
        );
        assertEquals(
                json.<Long>read("$.queuedTime"),
                (Long) job.getQueuedTime().getTime()
        );
    }

    private Script createTestScript() {
        Project project = new Project();
        project.setName("jobTestProject");
        project.setOwner(userRepository.findByUsername(DEFAULT_USERNAME));
        project = projectRepository.save(project);

        Script script = new Script();
        script.setName("testScript");
        script.setProject(project);
        script = scriptRepository.save(script);

        return script;
    }
}
