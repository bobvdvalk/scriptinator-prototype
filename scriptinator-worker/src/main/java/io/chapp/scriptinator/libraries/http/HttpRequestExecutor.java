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
package io.chapp.scriptinator.libraries.http;

import io.chapp.scriptinator.libraries.DataValue;
import io.chapp.scriptinator.workerservices.ObjectConverter;

import java.util.Map;

public abstract class HttpRequestExecutor extends DataValue {
    private final ObjectConverter converter;

    protected HttpRequestExecutor(ObjectConverter converter) {
        this.converter = converter;
    }

    public HttpResponse get(Map<String, Object> request) {
        return request("GET", request);
    }

    public HttpResponse head(Map<String, Object> request) {
        return request("HEAD", request);
    }

    public HttpResponse post(Map<String, Object> request) {
        return request("POST", request);
    }

    public HttpResponse delete(Map<String, Object> request) {
        return request("DELETE", request);
    }

    public HttpResponse put(Map<String, Object> request) {
        return request("PUT", request);
    }

    public HttpResponse patch(Map<String, Object> request) {
        return request("PATCH", request);
    }

    public HttpResponse options(Map<String, Object> request) {
        return request("OPTIONS", request);
    }

    private HttpResponse request(String method, Map<String, Object> request) {
        HttpRequest httpRequest = converter.read(request, HttpRequest.class);
        httpRequest.setMethod(method);

        return request(httpRequest);
    }

    public abstract HttpResponse request(HttpRequest request);
}
