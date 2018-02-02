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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.chapp.scriptinator.ClosableContext;
import io.chapp.scriptinator.workerservices.ObjectConverter;
import okhttp3.OkHttpClient;

public class HttpLibrary extends HttpRequestExecutor {
    private final ObjectConverter converter;
    private final ClosableContext closableContext;

    public HttpLibrary(ObjectConverter converter, ClosableContext closableContext) {
        super(converter);
        this.converter = converter;
        this.closableContext = closableContext;
    }

    public HttpClient client() {
        return new HttpClient(new OkHttpClient(), converter, new ObjectMapper(), closableContext);
    }

    @Override
    protected HttpResponse request(HttpRequest request) {
        return client().request(request);
    }
}
