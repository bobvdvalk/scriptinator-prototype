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
package io.chapp.scriptinator.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.chapp.scriptinator.ScriptinatorRestApi;
import io.chapp.scriptinator.model.OAuthApp;
import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.repositories.OAuthAppRepository;
import io.chapp.scriptinator.repositories.UserRepository;
import okhttp3.OkHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.testng.*;

public class ScriptinatorTestCase implements IClassListener, IInvokedMethodListener {
    public static final String DEFAULT_USERNAME = "test_user";
    public static final String DEFAULT_CLIENT_ID = "012345678901234567890123456789";
    private static final String DEFAULT_PASSWORD = "thisispassword";
    private static final String DEFAULT_CLIENT_SECRET = "012345678901234567890123456789";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private ApplicationContext context;

    @Override
    public void onBeforeClass(ITestClass testClass) {
        context = SpringApplication.run(new Class[]{
                ScriptinatorRestApi.class,
                OkHTTPClientConfig.class
        }, new String[]{
                //"--debug"
        });

        for (Object instance : testClass.getInstances(true)) {
            context.getAutowireCapableBeanFactory().autowireBean(instance);
        }
    }

    @Override
    public void onAfterClass(ITestClass testClass) {
        SpringApplication.exit(context);
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        String methodName = method.getTestMethod().getMethodName();

        // Create a new user.
        User user = new User();
        user.setDisplayName("Test User: " + methodName);
        user.setUsername(DEFAULT_USERNAME);
        user.setPassword(DEFAULT_PASSWORD);
        user = context.getBean(UserRepository.class).save(user);

        // Create an OAuth app for the user.
        OAuthApp app = new OAuthApp();
        app.setName("Test App: " + methodName);
        app.setClientId(DEFAULT_CLIENT_ID);
        app.setClientSecret(DEFAULT_CLIENT_SECRET);
        app.setOwner(user);
        context.getBean(OAuthAppRepository.class).save(app);
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        context.getBean(UserRepository.class).deleteAll();
    }
}
