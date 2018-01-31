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

import com.fasterxml.jackson.annotation.JsonSetter;
import io.chapp.scriptinator.libraries.DataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequest extends DataValue {
    private String method = "GET";
    private String url;
    private String contentType = "";
    private Object body;
    private BasicAuthentication basicAuthentication;
    private List<String> headers = new ArrayList<>();

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public BasicAuthentication getBasicAuthentication() {
        return basicAuthentication;
    }

    public void setBasicAuthentication(BasicAuthentication basicAuthentication) {
        this.basicAuthentication = basicAuthentication;
    }

    public List<String> getHeaders() {
        return headers;
    }

    @JsonSetter
    public void setHeaders(Map<String, String> headers) {
        this.headers = new ArrayList<>(headers.values());
    }

    @Override
    protected void inspect(Map<String, Object> target) {
        target.put("method", method);
        target.put("url", url);
        if (basicAuthentication != null) {
            target.put("basicAuthentication", basicAuthentication);
        }
    }
}
