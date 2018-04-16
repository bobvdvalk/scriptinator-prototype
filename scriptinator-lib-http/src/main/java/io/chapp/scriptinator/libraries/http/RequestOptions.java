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
package io.chapp.scriptinator.libraries.http;


import com.fasterxml.jackson.annotation.JsonSetter;
import io.chapp.scriptinator.libraries.core.DataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestOptions extends DataValue {
    private BasicAuthentication basicAuth;
    private BearerAuthentication bearerAuth;
    private List<String> headers = new ArrayList<>();

    public RequestOptions() {
    }

    public RequestOptions(RequestOptions clientOptions) {
        basicAuth = clientOptions.basicAuth;
        bearerAuth = clientOptions.bearerAuth;
        headers = new ArrayList<>(clientOptions.headers);
    }

    public BasicAuthentication getBasicAuth() {
        return basicAuth;
    }

    public void setBasicAuth(BasicAuthentication basicAuth) {
        this.basicAuth = basicAuth;
    }

    public BearerAuthentication getBearerAuth() {
        return bearerAuth;
    }

    public void setBearerAuth(BearerAuthentication bearerAuth) {
        this.bearerAuth = bearerAuth;
    }

    public List<String> getHeaders() {
        return headers;
    }

    // Due to the way we handle json serialization, arrays are mapped to maps with "$index" for keys.
    @JsonSetter
    public void setHeaders(Map<String, String> headers) {
        if (headers == null) {
            this.headers = null;
        } else {
            this.headers = new ArrayList<>(headers.values());
        }
    }

    @Override
    protected void inspect(Map<String, Object> target) {
        if (basicAuth != null) {
            target.put("basicAuth", basicAuth);
        }
        if (bearerAuth != null) {
            target.put("bearerAuth", bearerAuth);
        }
    }
}
