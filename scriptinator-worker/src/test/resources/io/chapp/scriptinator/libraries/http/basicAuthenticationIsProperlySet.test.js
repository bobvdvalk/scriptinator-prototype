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
var HTTP = Script.library("HTTP");
var Assert = Script.library("Assert");

var authenticatedResponse = HTTP.get({
    url: "https://httpbin.org/basic-auth/someuser/secret123",
    basicAuth: {
        username: "someuser",
        password: "secret123"
    }
});

Assert.equal(
    "Authenticated response contains username",
    {
        authenticated: true,
        user: "someuser"
    },
    authenticatedResponse.body().asJson()
);

Assert.equal(
    "Authenticated response has status code 200",
    200,
    authenticatedResponse.code()
);


var missingAuthentication = HTTP.get({
    url: "https://httpbin.org/basic-auth/someuser/secret123"
});

Assert.equal(
    "Missing authentication has status code 401",
    401,
    missingAuthentication.code()
);
