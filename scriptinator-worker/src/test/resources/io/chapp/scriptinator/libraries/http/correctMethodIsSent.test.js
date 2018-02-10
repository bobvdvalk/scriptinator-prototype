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


Assert.equal(
    "Method is GET",
    HTTP.get({url: "https://httpbin.org/anything"}).body().asJson().method,
    "GET"
);

Assert.equal(
    "Method is POST",
    HTTP.post({url: "https://httpbin.org/anything"}).body().asJson().method,
    "POST"
);

Assert.equal(
    "Method is DELETE",
    HTTP.delete({url: "https://httpbin.org/anything"}).body().asJson().method,
    "DELETE"
);

Assert.equal(
    "Method is PUT",
    HTTP.put({url: "https://httpbin.org/anything"}).body().asJson().method,
    "PUT"
);

Assert.equal(
    "Method is PATCH",
    HTTP.patch({url: "https://httpbin.org/anything"}).body().asJson().method,
    "PATCH"
);
