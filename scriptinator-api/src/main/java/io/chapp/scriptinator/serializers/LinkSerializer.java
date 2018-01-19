package io.chapp.scriptinator.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.chapp.scriptinator.model.Link;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@JsonComponent
public class LinkSerializer extends StdSerializer<Link> {

    protected LinkSerializer() {
        super(Link.class);
    }

    @Override
    public void serialize(Link link, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();
        builder.replacePath("/api" + link.getHref());
        builder.replaceQuery("");
        jsonGenerator.writeString(builder.toUriString());
    }
}
