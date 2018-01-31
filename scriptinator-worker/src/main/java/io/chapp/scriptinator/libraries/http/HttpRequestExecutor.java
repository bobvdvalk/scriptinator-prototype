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
        return get(converter.read(request, HttpRequest.class));
    }

    public HttpResponse get(HttpRequest request) {
        request.setMethod("GET");
        return request(request);
    }

    public HttpResponse head(HttpRequest request) {
        request.setMethod("HEAD");
        return request(request);
    }

    public HttpResponse post(HttpRequest request) {
        request.setMethod("POST");
        return request(request);
    }

    public HttpResponse delete(HttpRequest request) {
        request.setMethod("DELETE");
        return request(request);
    }

    public HttpResponse put(HttpRequest request) {
        request.setMethod("PUT");
        return request(request);
    }

    public HttpResponse patch(HttpRequest request) {
        request.setMethod("PATCH");
        return request(request);
    }

    public abstract HttpResponse request(HttpRequest request);
}
