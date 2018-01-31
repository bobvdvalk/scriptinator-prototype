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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.chapp.scriptinator.workerservices.ObjectConverter;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;

public class HttpClient extends HttpRequestExecutor {
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public HttpClient(OkHttpClient client, ObjectConverter converter, ObjectMapper objectMapper) {
        super(converter);
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Override
    public HttpResponse request(HttpRequest request) {
        try {
            return new HttpResponse(
                    this,
                    request,
                    client.newCall(
                            new Request.Builder()
                                    .url(request.getUrl())
                                    .headers(buildHeaders(request))
                                    .method(request.getMethod(), buildBody(request))
                                    .build()
                    ).execute()
            );
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Headers buildHeaders(HttpRequest request) {
        return Headers.of();
    }

    private RequestBody buildBody(HttpRequest request) {
        return null;
    }

    public ObjectMapper getMapper() {
        return objectMapper;
    }
}
