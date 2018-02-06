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

import io.chapp.scriptinator.libraries.core.DataValue;
import io.chapp.scriptinator.libraries.core.ScriptinatorExecutionException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

import java.io.IOException;

public class HttpResponseBody extends DataValue {
    private final ResponseBody body;
    private final HttpClient client;
    private final HttpResponse response;

    public HttpResponseBody(ResponseBody body, HttpClient client, HttpResponse response) {
        this.body = body;
        this.client = client;
        this.response = response;
    }

    public String contentType() {
        MediaType contentType = body.contentType();
        if (contentType == null) {
            return null;
        }
        return contentType.toString();
    }

    public String asString() {
        try {
            return body.string();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Object asJson() {
        try {
            return client.getMapper().readValue(
                    asString(),
                    Object.class
            );
        } catch (IOException e) {
            throw new ScriptinatorExecutionException(
                    "Could not parse response body from '" + response.request().getMethod() + " " + response.request().getUrl() + "' as json.",
                    e
            );
        }
    }


}
