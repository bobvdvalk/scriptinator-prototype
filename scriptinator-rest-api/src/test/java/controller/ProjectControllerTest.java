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

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import io.chapp.scriptinator.ScriptinatorRestApi;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.repositories.ProjectRepository;
import io.chapp.scriptinator.repositories.UserRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;

public class ProjectControllerTest {
    private final OkHttpClient client = new OkHttpClient();

    ApplicationContext context;
    UserRepository userRepository;
    ProjectRepository projectRepository;
    User user;

    @Test
    public void testWhenProjectIsRequestedItIsReturned() throws IOException {
        // Precondition
        Project project = new Project();
        project.setOwner(user);
        project.setName("hoiBobDitIsEenTest");
        project.setDescription("We can enter a nice description");
        projectRepository.save(project);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/projects/hoiBobDitIsEenTest")
                        .build()
        ).execute();

        // Validation
        String body = response.body().string();
        ReadContext json = JsonPath.parse(body);

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

    @BeforeClass
    public void startServer() {
        context = SpringApplication.run(ScriptinatorRestApi.class);
        userRepository = context.getBean(UserRepository.class);
        projectRepository = context.getBean(ProjectRepository.class);
    }

    @AfterClass
    public void stopServer() {
        SpringApplication.exit(context);
    }

    @BeforeMethod
    public void createUser(Method method) {
        user = new User();
        user.setDisplayName("Test User: " + method.getName());
        user.setUsername(method.getName());
        user.setPassword("thisistest");
        user = userRepository.save(user);
    }

    @AfterMethod
    public void cleanUp() {
        userRepository.deleteAll();
    }
}
