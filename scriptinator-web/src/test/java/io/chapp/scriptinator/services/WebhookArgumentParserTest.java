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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.chapp.scriptinator.model.WebhookArgument;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

import static org.testng.Assert.*;

public class WebhookArgumentParserTest {
    private final WebhookArgumentParser parser = new WebhookArgumentParser(new ObjectMapper());

    @Test
    public void testRequestMethodAndHeaders() throws IOException {
        MockHttpServletRequest request = mockRequest("text/anything", "bodybody");
        request.setMethod("PATCH");
        request.addHeader("My-Header", "something");

        WebhookArgument argument = parser.parseRequest(request);
        assertEquals(argument.getMethod(), "PATCH");
        assertEquals(argument.getBody(), "bodybody");
        assertEquals(argument.getHeaders().get("My-Header"), "something");
    }

    @Test
    public void testContentTypeJson() throws IOException {
        MockHttpServletRequest request = mockRequest("application/json", "{\"ten\":10}");

        WebhookArgument argument = parser.parseRequest(request);
        assertEquals(((Map<String, Object>) argument.getBody()).get("ten"), 10);
    }

    @Test
    public void testContentTypeXml() throws IOException {
        MockHttpServletRequest request = mockRequest("application/xml", "<some xml=\"true\"><here/></some>");

        WebhookArgument argument = parser.parseRequest(request);
        assertEquals(argument.getBody(), "<some xml=\"true\"><here/></some>");
    }

    @Test
    public void testContentTypeMultipart() throws IOException {
        MockHttpServletRequest request = mockRequest("multipart/form-data", "");
        request.addPart(new MockPart("text", "plaintext".getBytes()));
        request.addPart(new MockPart("image", "mfw.png", "nope".getBytes()));

        WebhookArgument argument = parser.parseRequest(request);
        Map<String, Object> body = (Map<String, Object>) argument.getBody();
        assertEquals(body.get("text"), "plaintext");
        assertFalse(body.containsKey("image"));
    }

    @Test
    public void testContentTypeUnknown() throws IOException {
        MockHttpServletRequest request = mockRequest("application/unknown", "Just plain text");

        WebhookArgument argument = parser.parseRequest(request);
        assertNull(argument.getBody());
    }

    @Test
    public void testContentTypeNull() throws IOException {
        MockHttpServletRequest request = mockRequest(null, "Just plain text");

        WebhookArgument argument = parser.parseRequest(request);
        assertNull(argument.getBody());
    }

    @Test
    public void testContentTypeFormUrlencoded() throws IOException {
        MockHttpServletRequest request = mockRequest("application/x-www-form-urlencoded", "");
        request.setParameter("empty", "");
        request.setParameter("encoded!", "a b");

        WebhookArgument argument = parser.parseRequest(request);
        Map<String, Object> body = (Map<String, Object>) argument.getBody();
        assertEquals(body.get("empty"), "");
        assertEquals(body.get("encoded!"), "a b");
    }

    private MockHttpServletRequest mockRequest(String contentType, String body) {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/webhook");
        mockRequest.setContentType(contentType);
        mockRequest.setContent(body.getBytes());
        return mockRequest;
    }
}