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


import io.chapp.scriptinator.security.ApiScope;
import io.chapp.scriptinator.utils.OkHTTPClientConfig;
import io.chapp.scriptinator.utils.ScriptinatorTestCase;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Listeners(ScriptinatorTestCase.class)
public class ApiSecurityTest {
    @Autowired
    private OkHTTPClientConfig clientConfig;

    @DataProvider
    public Object[][] endpoints() {
        return new Object[][]{
                new Object[]{"GET", "/projects", null, 200, Collections.singletonList(ApiScope.PROJECT_READ)},
                new Object[]{"GET", "/projects/by-name", null, 404, Collections.singletonList(ApiScope.PROJECT_READ)},
                new Object[]{"GET", "/projects/name/scripts", null, 200, Arrays.asList(ApiScope.SCRIPT, ApiScope.SCRIPT_READ)},
                new Object[]{"GET", "/projects/name/schedules", null, 200, Collections.singletonList(ApiScope.SCHEDULE_READ)},
                new Object[]{"GET", "/jobs", null, 200, Collections.singletonList(ApiScope.JOB_READ)},
                new Object[]{"GET", "/jobs/23534", null, 404, Collections.singletonList(ApiScope.JOB_READ)},
                new Object[]{"GET", "/schedules", null, 200, Collections.singletonList(ApiScope.SCHEDULE_READ)},
                new Object[]{"GET", "/schedules/3254", null, 404, Collections.singletonList(ApiScope.SCHEDULE_READ)},
                new Object[]{"GET", "/scripts", null, 200, Arrays.asList(ApiScope.SCRIPT, ApiScope.SCRIPT_READ)},
                new Object[]{"GET", "/scripts/5436", null, 404, Arrays.asList(ApiScope.SCRIPT, ApiScope.SCRIPT_READ)},
                new Object[]{"GET", "/scripts/5436/jobs", null, 200, Collections.singletonList(ApiScope.JOB_READ)},
                new Object[]{"POST", "/scripts/5436/jobs", RequestBody.create(MediaType.parse("application/json"), "{}"), 404, Arrays.asList(ApiScope.SCRIPT, ApiScope.SCRIPT_RUN)}
        };
    }

    @Test(dataProvider = "endpoints")
    public void testOnlyRequiredScopesGrantAccess(String method, String endpoint, RequestBody body, int successCode, Collection<ApiScope> requiresOneOf) throws IOException {
        OkHttpClient client = new OkHttpClient();


        for (ApiScope testScope : ApiScope.values()) {
            String token = clientConfig.generateFakeToken(Collections.singletonList(testScope));

            Response response = client.newCall(
                    new Request.Builder()
                            .url("http://localhost:8080/api" + endpoint)
                            .method(method, body)
                            .header("Authorization", "Bearer " + token)
                            .build()
            ).execute();

            Assert.assertEquals(
                    response.code(),
                    requiresOneOf.contains(testScope) ? successCode : 403,
                    method + " " + endpoint + " should require scope '" + StringUtils.join(requiresOneOf, "' or '") + "' but returned an unexpected code with scope '" + testScope + "':"
            );
        }
    }
}
