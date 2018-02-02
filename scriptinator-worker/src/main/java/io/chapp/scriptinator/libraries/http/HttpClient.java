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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.chapp.scriptinator.ClosableContext;
import io.chapp.scriptinator.workerservices.ObjectConverter;
import okhttp3.*;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.chapp.scriptinator.libraries.EncodeUtils.toBase64;
import static io.chapp.scriptinator.libraries.EncodeUtils.urlEncode;

public class HttpClient extends HttpRequestExecutor {
    private static final Set<String> REQUIRE_BODY;

    static {
        HashSet<String> methods = new HashSet<>();
        methods.add("POST");
        methods.add("PUT");
        methods.add("PATCH");
        REQUIRE_BODY = Collections.unmodifiableSet(methods);
    }

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final ClosableContext closableContext;

    public HttpClient(OkHttpClient client, ObjectConverter converter, ObjectMapper objectMapper, ClosableContext closableContext) {
        super(converter);
        this.client = client;
        this.objectMapper = objectMapper;
        this.closableContext = closableContext;
    }

    @Override
    protected HttpResponse request(HttpRequest request) {
        try {
            Headers headers = buildHeaders(request);

            RequestBody body = buildBody(request);
            if (body == null && REQUIRE_BODY.contains(request.getMethod())) {
                MediaType mediaType = MediaType.parse(request.getContentType());
                if (mediaType == null) {
                    mediaType = MediaType.parse("application/x-www-form-urlencoded");
                }
                body = RequestBody.create(
                        mediaType,
                        ""
                );
            }

            Response response = client.newCall(
                    new Request.Builder()
                            .url(request.getUrl())
                            .headers(headers)
                            .method(request.getMethod(), body)
                            .build()
            ).execute();

            if(response.body() != null) {
                closableContext.register(response);
            }

            return new HttpResponse(
                    this,
                    request,
                    response
            );
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Headers buildHeaders(HttpRequest request) {
        Headers.Builder headers = new Headers.Builder();
        if (request.getHeaders() != null) {
            request.getHeaders().forEach(headers::add);
        }

        if (request.getBasicAuthentication() != null) {
            headers.add("Authorization", "Basic " + toBase64(
                    request.getBasicAuthentication().getUsername() + ":" +
                            request.getBasicAuthentication().getPassword()
            ));
        }

        return headers.build();
    }

    private RequestBody buildBody(HttpRequest request) {
        String contentType = request.getContentType();
        Object requestBody = request.getBody();
        if (StringUtils.isEmpty(contentType)) {
            if (requestBody == null) {
                return null;
            }
            contentType = "application/json";
        }

        MediaType mediaType = MediaType.parse(contentType);

        if (mediaType == null) {
            throw new IllegalArgumentException("Invalid contentType: " + request.getContentType());
        }

        if (mediaType.toString().contains("json")) {
            return jsonBody(mediaType, request.getBody());
        }

        if (mediaType.subtype().equals("x-www-form-urlencoded") && request.getBody() instanceof Map) {
            Map<String, Object> bodyParts = (Map<String, Object>) request.getBody();

            return RequestBody.create(
                    mediaType,
                    bodyParts.entrySet().stream()
                            .map(entry -> entry.getKey() + "=" + urlEncode(entry.getValue()))
                            .collect(Collectors.joining("&"))
            );
        }

        return RequestBody.create(
                mediaType,
                String.valueOf(request.getBody())
        );
    }

    private RequestBody jsonBody(MediaType mediaType, Object body) {
        try {
            return RequestBody.create(
                    mediaType,
                    objectMapper.writeValueAsString(body)
            );
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    ObjectMapper getMapper() {
        return objectMapper;
    }
}
