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


import io.chapp.scriptinator.libraries.core.DataValue;
import io.chapp.scriptinator.libraries.core.ObjectConverter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpRequestExecutor extends DataValue {
    private final ObjectConverter converter;
    private final ClientOptions clientOptions;

    protected HttpRequestExecutor(ObjectConverter converter, ClientOptions clientOptions) {
        this.converter = converter;
        this.clientOptions = clientOptions;
    }

    public HttpResponse get(Map<String, Object> request) {
        return request("GET", request);
    }

    public HttpResponse get(String url) {
        return get(Collections.singletonMap("url", url));
    }

    public HttpResponse head(Map<String, Object> request) {
        return request("HEAD", request);
    }

    public HttpResponse head(String url) {
        return head(Collections.singletonMap("url", url));
    }

    public HttpResponse post(Map<String, Object> request) {
        return request("POST", request);
    }

    public HttpResponse post(String url) {
        return post(Collections.singletonMap("url", url));
    }

    public HttpResponse delete(Map<String, Object> request) {
        return request("DELETE", request);
    }

    public HttpResponse delete(String url) {
        return delete(Collections.singletonMap("url", url));
    }

    public HttpResponse put(Map<String, Object> request) {
        return request("PUT", request);
    }

    public HttpResponse put(String url) {
        return put(Collections.singletonMap("url", url));
    }

    public HttpResponse patch(Map<String, Object> request) {
        return request("PATCH", request);
    }

    public HttpResponse patch(String url) {
        return patch(Collections.singletonMap("url", url));
    }

    public HttpResponse options(Map<String, Object> request) {
        return request("OPTIONS", request);
    }

    public HttpResponse options(String url) {
        return options(Collections.singletonMap("url", url));
    }

    private HttpResponse request(String method, Map<String, Object> request) {
        Map<String, Object> requestParams = new HashMap<>(request);
        requestParams.put("method", method);
        return request(requestParams);
    }

    public HttpResponse request(Map<String, Object> request) {
        HttpRequest httpRequest = converter.readInto(request, new HttpRequest(clientOptions));
        return request(httpRequest);
    }

    protected abstract HttpResponse request(HttpRequest request);
}
