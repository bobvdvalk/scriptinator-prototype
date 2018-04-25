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

import static org.testng.Assert.assertEquals;

@Listeners(ScriptinatorTestCase.class)
public class ScheduleControllerTest {
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
    public void testGetScheduleByIdReturnsValidInformation() throws IOException {
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
        schedule.setName("itIsTime");
        schedule.setScriptName("triggered");
        schedule.setCronString("0 0/10 * * * ?");
        scheduleRepository.save(schedule);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/api/schedules/" + schedule.getId())
                        .headers(accessToken)
                        .build()
        ).execute();


        // Validation
        String body = response.body().string();
        ReadContext json = JsonPath.parse(body);

        assertEquals(
                json.read("$.url"),
                "http://localhost:8080/api/schedules/" + schedule.getId()
        );
        assertEquals(
                json.read("$.name"),
                "itIsTime"
        );
        assertEquals(
                json.read("$.scriptName"),
                "triggered"
        );
        assertEquals(
                json.read("$.cronString"),
                "0 0/10 * * * ?"
        );
    }
}
