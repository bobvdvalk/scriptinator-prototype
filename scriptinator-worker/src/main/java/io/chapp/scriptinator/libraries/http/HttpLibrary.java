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

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class HttpLibrary {
    private final OkHttpClient client;

    public HttpLibrary() {
        this(new OkHttpClient());
    }

    public HttpLibrary(OkHttpClient client) {
        this.client = client;
    }

    public Request get(String url) {
        return request("GET", url, null);
    }

    public Request head(String url) {
        return request("HEAD", url, null);
    }

    public Request delete(String url) {
        return request("DELETE", url, null);
    }

    public Request post(String url, RequestBody body) {
        return request("POST", url, body);
    }
    public Request put(String url, RequestBody body) {
        return request("PUT", url, body);
    }

    public Request patch(String url, RequestBody body) {
        return request("PATCH", url, body);
    }

    public Request request(String method, String url, RequestBody body) {
        return new Request(
                client,
                new okhttp3.Request.Builder()
                        .url(url)
                        .method(method, body)
        );
    }
}
