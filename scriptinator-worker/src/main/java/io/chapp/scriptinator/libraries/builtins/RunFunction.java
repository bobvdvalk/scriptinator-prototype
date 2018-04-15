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

import io.chapp.scriptinator.libraries.core.ScriptinatorExecutionException;
import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.services.ScriptService;
import org.apache.commons.lang3.StringUtils;

import java.util.NoSuchElementException;

import static io.chapp.scriptinator.libraries.core.EncodeUtils.toNativeJsonOrNull;

public class RunFunction implements BuiltIn {
    private final Job job;
    private final ScriptService scriptService;

    public RunFunction(Job job, ScriptService scriptService) {
        this.job = job;
        this.scriptService = scriptService;
    }

    @Override
    public Object run(Object... args) {
        if (args.length == 0) {
            throw new ScriptinatorExecutionException("Please provide the name of the script which you want to run");
        }

        String fullName = args[0].toString();

        String[] nameParts = StringUtils.split(fullName, '/');
        String projectName = job.getScript().getProject().getName();
        String scriptName = "";

        switch (nameParts.length) {
            case 1:
                scriptName = nameParts[0];
                break;
            case 2:
                projectName = nameParts[0];
                scriptName = nameParts[1];
                break;
            default:
                throw new IllegalArgumentException("It seems like '" + fullName + "' is not a valid script name.");
        }

        // Get the script.
        try {
            Script script = scriptService.getOwnedBy(
                    job.getScript().getProject().getOwner().getUsername(),
                    projectName,
                    scriptName
            );

            scriptService.run(script, job, toNativeJsonOrNull(
                    args.length > 1 ? args[1] : null
            ));
        } catch (NoSuchElementException e) {
            throw new ScriptinatorExecutionException("Could not start the script '" + fullName + "' because it could not be found.");
        }
        return null;
    }
}
