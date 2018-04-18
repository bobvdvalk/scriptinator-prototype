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
package io.chapp.scriptinator.webcontrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.chapp.scriptinator.ScriptinatorWeb;
import io.chapp.scriptinator.model.*;
import io.chapp.scriptinator.repositories.*;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.singletonMap;

public class WebhookHandlerWebControllerTest {
    private ApplicationContext context;
    @Autowired
    private ScriptRepository scriptRepository;
    @Autowired
    private WebhookRepository webhookRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobRepository jobRepository;

    private OkHttpClient client = new OkHttpClient();
    private Webhook webhook;
    private long scriptId;

    @BeforeClass
    public void startServer() {
        System.setProperty("spring.devtools.restart.enabled", "false");
        context = SpringApplication.run(ScriptinatorWeb.class);
        context.getAutowireCapableBeanFactory().autowireBean(this);
        userRepository.deleteAll();
        User user = new User();
        user.setEmail("test@email.com");
        user.setUsername("webhook-test");
        user.setPassword("fre0gu439-0tgu-9543y");// Don't need this
        userRepository.save(user);
    }

    @AfterClass
    public void stopServer() {
        userRepository.deleteAll();
        SpringApplication.exit(context);
    }

    @BeforeMethod
    public void createWebhookAndScript(Method method) {
        User user = userRepository.findByUsername("webhook-test").get();

        Project project = new Project();
        project.setName(method.getName());
        project.setDescription("Webhook Integration Test");
        project.setOwner(user);
        project = projectRepository.save(project);

        Script script = new Script();
        script.setName("empty-script");
        script.setProject(project);
        script.setCode("");
        script = scriptRepository.save(script);
        scriptId = script.getId();

        webhook = new Webhook();
        webhook.setName("yea-sure");
        webhook.setScriptName("empty-script");
        webhook.setProject(project);
        webhook.setUuid(UUID.randomUUID().toString());
        webhook = webhookRepository.save(webhook);
    }

    @Test
    public void testJsonRequestIsParsedIntoBody() throws IOException {
        Response response = client.newCall(
                new Request.Builder()
                        .url("http://localhost:8081/webhook/" + webhook.getUuid())
                        .post(
                                RequestBody.create(
                                        MediaType.parse("application/json"),
                                        "{\"hello\": \"world\"}"
                                )
                        )
                        .build()
        ).execute();

        Assert.assertEquals(
                toMap(response.body().string()),
                singletonMap("success", true)
        );
        Assert.assertEquals(response.code(), 200);

        Page<Job> jobs = jobRepository.findAllByScriptProjectOwnerUsernameAndScriptId("webhook-test", scriptId, new PageRequest(0, 1));

        Assert.assertEquals(jobs.getTotalElements(), 1);
        Assert.assertEquals(
                toMap(jobs.getContent().get(0).getArgument()).get("body"),
                singletonMap("hello", "world")
        );
    }

    private Map toMap(String string) throws IOException {
        return new ObjectMapper().readValue(string, Map.class);
    }
}