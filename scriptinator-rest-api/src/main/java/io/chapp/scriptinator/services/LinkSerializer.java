package io.chapp.scriptinator.services;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.chapp.scriptinator.model.Link;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

@Service
public class LinkSerializer extends StdSerializer<Link> {

    protected LinkSerializer() {
        super(Link.class);
    }

    @Override
    public void serialize(Link link, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String resourceUrl = getRequestUrl()
                .removeQuery()
                .clearParameters()
                .setPath(link.getHref())
                .setParameters(
                        link.getParameters().entrySet()
                                .stream()
                                .map(
                                        entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                                .collect(Collectors.toList())
                )
                .toString();

        jsonGenerator.writeString(resourceUrl);
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
