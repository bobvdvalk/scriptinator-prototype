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
package io.chapp.scriptinator.libraries.core;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.objects.NativeJSON;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;

public class EncodeUtils {
    private EncodeUtils() {
    }

    public static String urlEncode(Object input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Server does not support UTF-8", e);
        }
    }

    public static String toBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public static String toNativeJsonOrNull(Object argument) {
        if (argument == null || ScriptObjectMirror.isUndefined(argument)) {
            return null;
        }

        return NativeJSON.stringify(null, argument, null, null).toString();
    }
}
