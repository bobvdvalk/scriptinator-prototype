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
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpResponse extends DataValue {
    private final HttpClient client;
    private final HttpRequest request;
    private final Response response;

    public HttpResponse(HttpClient client, HttpRequest request, Response response) {
        this.client = client;
        this.request = request;
        this.response = response;
    }

    public HttpRequest request() {
        return request;
    }

    public int code() {
        return response.code();
    }

    public String message() {
        return response.message();
    }

    public String protocol() {
        return response.protocol().toString();
    }

    public List<String> headerNames() {
        return new ArrayList<>(response.headers().names());
    }

    public Map<String, List<String>> headers() {
        return response.headers().toMultimap();
    }

    public List<String> headers(String name) {
        return response.headers(name);
    }

    public String header(String name) {
        return response.header(name);
    }

    public HttpResponseBody body() {
        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }
        return new HttpResponseBody(body, client, this);
    }

    @Override
    protected void inspect(Map<String, Object> target) {
        headerNames().forEach(name -> target.put(name, header(name)));
        target.put("request", request);
    }
}
