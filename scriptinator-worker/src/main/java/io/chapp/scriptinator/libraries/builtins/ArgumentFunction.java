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
package io.chapp.scriptinator.libraries.builtins;

import io.chapp.scriptinator.model.Job;
import jdk.nashorn.internal.runtime.JSONFunctions;

import java.util.function.Supplier;

public class ArgumentFunction implements Supplier<Object> {
    private final Job job;

    public ArgumentFunction(Job job) {
        this.job = job;
    }

    @Override
    public Object get() {
        if (job.getArgument() == null) {
            return null;
        }

        return JSONFunctions.parse(job.getArgument(), null);
    }
}
