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

import io.chapp.scriptinator.ScriptinatorRestApi;
import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.repositories.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.testng.*;

public class ScriptinatorTestCase implements IClassListener, IInvokedMethodListener {
    public static final String DEFAULT_USERNAME = "test_user";
    public static final String DEFAULT_PASSWORD = "thisispassword";

    private ApplicationContext context;

    @Override
    public void onBeforeClass(ITestClass testClass) {
        context = SpringApplication.run(ScriptinatorRestApi.class);

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
        User user = new User();
        user.setDisplayName("Test User: " + method.getTestMethod().getMethodName());
        user.setUsername(DEFAULT_USERNAME);
        user.setPassword(DEFAULT_PASSWORD);
        context.getBean(UserRepository.class).save(user);
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        context.getBean(UserRepository.class).deleteAll();
    }
}
