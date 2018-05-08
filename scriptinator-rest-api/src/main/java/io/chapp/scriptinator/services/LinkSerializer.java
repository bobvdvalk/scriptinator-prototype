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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.chapp.scriptinator.model.Link;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LinkSerializer extends StdSerializer<Link> {
    private final String contextPath;

    protected LinkSerializer(@Value("${server.servlet.context-path:}") String contextPath) {
        super(Link.class);
        this.contextPath = contextPath;
    }

    @Override
    public void serialize(Link link, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        URIBuilder resourceUrl = getRequestUrl()
                .removeQuery()
                .clearParameters()
                .setPath(contextPath + link.getHref());

        List<NameValuePair> parameters = link.getParameters().entrySet().stream().map(
                entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())
        ).collect(Collectors.toList());

        if (!parameters.isEmpty()) {
            resourceUrl.setParameters(parameters);
        }

        jsonGenerator.writeString(resourceUrl.toString());
    }

    private URIBuilder getRequestUrl() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String requestUrl = request.getRequestURL().toString();

        try {
            return new URIBuilder(requestUrl);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid request url", e);
        }
    }
}
