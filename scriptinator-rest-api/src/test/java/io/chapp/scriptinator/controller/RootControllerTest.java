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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;

import static org.testng.Assert.assertEquals;

@Listeners(ScriptinatorTestCase.class)
public class RootControllerTest {
    private final OkHttpClient client = new OkHttpClient();
    @Autowired
    Headers accessToken;

    @Test
    public void testUrlsAreSetCorrectly() throws IOException {
        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080")
                        .headers(accessToken)
                        .build()
        ).execute();

        // Validation
        Map<String, String> urls = new ObjectMapper().readValue(response.body().string(), new TypeReference<Map<String, String>>() {
        });

        // The response must contain only these urls
        assertEquals(
                urls.keySet(),
                new HashSet<>(Arrays.asList(
                        "jobsUrl",
                        "scriptsUrl",
                        "projectsUrl",
                        "webhooksUrl"
                ))
        );
        assertEquals(urls.get("jobsUrl"), "http://localhost:8080/jobs");
        assertEquals(urls.get("scriptsUrl"), "http://localhost:8080/scripts");
        assertEquals(urls.get("projectsUrl"), "http://localhost:8080/projects");
        assertEquals(urls.get("webhooksUrl"), "http://localhost:8080/webhooks");
    }

    @Test
    public void testUrlHostnameIsOverriddenByHostsHeader() throws IOException {
        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080")
                        .headers(accessToken)
                        .header("Host", "custom_host")
                        .build()
        ).execute();

        // Validation
        Map<String, String> urls = new ObjectMapper().readValue(response.body().string(), new TypeReference<Map<String, String>>() {
        });

        // The response must contain only these urls
        assertEquals(
                urls.keySet(),
                new HashSet<>(Arrays.asList(
                        "jobsUrl",
                        "scriptsUrl",
                        "projectsUrl",
                        "webhooksUrl"
                ))
        );
        assertEquals(urls.get("jobsUrl"), "http://custom_host/jobs");
        assertEquals(urls.get("scriptsUrl"), "http://custom_host/scripts");
        assertEquals(urls.get("projectsUrl"), "http://custom_host/projects");
        assertEquals(urls.get("webhooksUrl"), "http://custom_host/webhooks");
    }
}
