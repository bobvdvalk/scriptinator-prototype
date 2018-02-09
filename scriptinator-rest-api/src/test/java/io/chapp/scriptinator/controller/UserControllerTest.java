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
package io.chapp.scriptinator.controller;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.repositories.ProjectRepository;
import io.chapp.scriptinator.repositories.UserRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import static io.chapp.scriptinator.controller.ScriptinatorTestCase.DEFAULT_USERNAME;
import static org.testng.AssertJUnit.assertEquals;

@Listeners(ScriptinatorTestCase.class)
public class UserControllerTest {
    private final OkHttpClient client = new OkHttpClient();

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Test
    public void testWhenUserIsRequestedItIsReturned() throws IOException {
        // Precondition
        User user = new User();
        user.setDisplayName("UserTestUser");
        user.setEmail("testuser@evil.incoperated");
        user.setUsername("Superusertest93");
        user.setPassword("thisistest");
        userRepository.save(user);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/users/Superusertest93")
                        .build()
        ).execute();

        // Validation
        ReadContext json = JsonPath.parse(response.body().string());

        assertEquals(
                json.read("$.displayName"),
                "UserTestUser"
        );

        assertEquals(
                json.read("$.email"),
                "testuser@evil.incoperated"
        );

        assertEquals(
                json.read("$.url"),
                "http://localhost:8080/users/Superusertest93"
        );

        assertEquals(
                json.read("$.avatarUrl"),
                "https://www.gravatar.com/avatar/b18d01a30f85969ec426927c7453c5ea"
        );
    }

    @Test
    public void testWhenProjectListIsEmpty() throws IOException {
        // Precondition
        User user = new User();
        user.setDisplayName("UserTestUser");
        user.setEmail("testuser@evil.incoperated");
        user.setUsername("Superusertest93");
        user.setPassword("thisistest");
        userRepository.save(user);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/users/Superusertest93/projects")
                        .build()
        ).execute();

        // Validation
        ReadContext json = JsonPath.parse(response.body().string());

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
    public void testWithProjects() throws IOException {
        // Precondition
        User user = new User();
        user.setDisplayName("UserTestUser");
        user.setEmail("testuser@evil.incoperated");
        user.setUsername("Superusertest93");
        user.setPassword("thisistest");
        userRepository.save(user);

        Project project = new Project();
        project.setName("HelloWorld");
        project.setDescription("This is a description.");
        project.setOwner(user);
        projectRepository.save(project);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/users/Superusertest93/projects")
                        .build()
        ).execute();

        //Validation
        ReadContext json = JsonPath.parse(response.body().string());

        assertEquals(
                json.read("$.hasItems"),
                Boolean.valueOf("true")
        );

        assertEquals(
                json.read("$.hasNext"),
                Boolean.valueOf("false")
        );

        assertEquals(
                json.read("$.totalItemCount"),
                (Integer) 1
        );
    }

    @Test
    public void testListingUsersReturnsAllUsers() throws IOException {
        // Precondition
        User user = new User();
        user.setUsername("superSecretSpecialSir");
        user.setPassword("sssssss");
        userRepository.save(user);
        user = new User();
        user.setUsername("megaMasterMagicalMisses");
        user.setPassword("mmmmmm");
        userRepository.save(user);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/users")
                        .build()
        ).execute();

        //Validation
        ReadContext json = JsonPath.parse(response.body().string());

        assertEquals(
                new HashSet<>(json.read("$.items[*].username")),
                new HashSet<>(Arrays.asList(
                        DEFAULT_USERNAME,
                        "superSecretSpecialSir",
                        "megaMasterMagicalMisses"
                ))
        );

        assertEquals(
                json.read("$.hasNext"),
                Boolean.valueOf("false")
        );

        assertEquals(
                json.read("$.totalItemCount"),
                (Integer) 3
        );
    }
}
