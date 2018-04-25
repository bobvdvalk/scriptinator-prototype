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
package io.chapp.scriptinator.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Link {
    private final String href;
    private final Map<String, String> parameters;

    public Link(String href) {
        this(href, Collections.emptyMap());
    }

    public Link(String href, Map<String, String> parameters) {
        if (!href.startsWith("/")) {
            throw new IllegalArgumentException("Links must start with a '/'");
        }

        this.href = href;
        this.parameters = parameters;
    }

    public String getHref() {
        return href;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Link withParameter(String name, String value) {
        Link result = copy();
        result.parameters.put(name, value);
        return result;
    }

    public Link withParameter(String name, int value) {
        return withParameter(name, Integer.toString(value));
    }

    private Link copy() {
        return new Link(href, new HashMap<>(parameters));
    }
}
