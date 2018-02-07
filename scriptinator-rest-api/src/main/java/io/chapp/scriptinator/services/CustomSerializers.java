package io.chapp.scriptinator.services;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.stereotype.Service;

@Service
public class CustomSerializers extends SimpleModule {

    public CustomSerializers(LinkSerializer linkSerializer) {
        addSerializer(linkSerializer);
    }
}
