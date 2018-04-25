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
package io.chapp.scriptinator.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.chapp.scriptinator.model.WebhookArgument;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WebhookArgumentParser {
    private final ObjectMapper objectMapper;

    public WebhookArgumentParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Parse a request to a webhook argument.
     *
     * @param request The request to parse.
     * @return The webhook argument.
     * @throws IOException If the request body could not be read.
     */
    public WebhookArgument parseRequest(HttpServletRequest request) throws IOException {
        return new WebhookArgument(
                request.getMethod(),
                parseRequestBody(request),
                Collections.list(request.getHeaderNames()).stream()
                        .collect(Collectors.toMap(Function.identity(), request::getHeader))
        );
    }

    /**
     * Parse the request body as an appropriate object.
     * For <code>multipart/form-data</code>, <code>application/json</code>
     * and <code>application/x-www-form-urlencoded</code> this returns the body parsed to a map.
     * For <code>text/*</code> and <code>application/xml</code> this returns the plain text from the body.
     * For anything else this returns <code>null</code>.
     *
     * @param request The request to parse.
     * @return An object representing the request  body.
     * @throws IOException If the stream could not be read.
     */
    private Object parseRequestBody(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType();
        String characterEncoding = request.getCharacterEncoding();

        // Check if the content type is given.
        if (contentType == null) {
            return null;
        }

        if (contentType.startsWith("multipart/form-data")) {
            // Create a map from all parts.
            Collection<Part> bodyParts = getBodyParts(request);
            Map<String, String> partsMap = new HashMap<>();

            // Add the name and content of each part to the map.
            for (Part part : bodyParts) {
                // Ignore file parts.
                if (part.getSubmittedFileName() == null) {
                    partsMap.put(part.getName(), IOUtils.toString(part.getInputStream(), characterEncoding));
                }
            }

            return partsMap;
        } else if (contentType.startsWith("application/x-www-form-urlencoded")) {
            return Collections.list(request.getParameterNames()).stream()
                    .collect(Collectors.toMap(
                            Function.identity(),
                            request::getParameter
                    ));
        } else if (isSupportedType(contentType)) {
            try (InputStream data = request.getInputStream()) {
                String stringData = IOUtils.toString(data, characterEncoding);
                if (StringUtils.isEmpty(stringData) || isText(contentType)) {
                    return stringData;
                } else if (isJson(contentType)) {
                    return readJsonValueAsMap(stringData);
                }
            }
        }
        return null;
    }

    private Map readJsonValueAsMap(String value) throws IOException {
        try {
            return objectMapper.readValue(value, Map.class);
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("Invalid json body: " + e.getMessage(), e);
        }
    }

    private boolean isSupportedType(String contentType) {
        return isJson(contentType) || isText(contentType);
    }

    private boolean isJson(String contentType) {
        return contentType.startsWith("application/json");
    }

    private boolean isText(String contentType) {
        return contentType.startsWith("text/") || contentType.startsWith("application/xml");
    }

    private Collection<Part> getBodyParts(HttpServletRequest request) throws IOException {
        try {
            return request.getParts();
        } catch (ServletException e) {
            throw new IllegalStateException("Could not get request body parts.", e);
        }
    }
}
