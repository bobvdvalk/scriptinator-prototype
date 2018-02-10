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
package io.chapp.scriptinator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.model.Link;
import io.chapp.scriptinator.model.PageResult;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.services.JobService;
import io.chapp.scriptinator.services.ScriptService;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

@RestController
@RequestMapping("scripts")
public class ScriptController {
    private final ScriptService scriptService;
    private final JobService jobService;
    private final ObjectMapper objectMapper;

    public ScriptController(ScriptService scriptService, JobService jobService, ObjectMapper objectMapper) {
        this.scriptService = scriptService;
        this.jobService = jobService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("")
    public PageResult<Script> getScripts(HttpServletRequest request) {
        return PageResult.of(
                new Link("/scripts"),
                scriptService.get(
                        PageResult.request(request)
                )
        );
    }

    /**
     * Get script by ID
     *
     * The reason you request a script by id is because a script id is always unique and a script name is only unique
     * within the project.
     *
     * @param scriptId long value
     * @return PageResult of a script id.
     */
    @GetMapping("{scriptId}")
    public Script getScriptById(@PathVariable long scriptId) {
        return scriptService.get(scriptId);
    }

    @GetMapping("{scriptId}/jobs")
    public PageResult<Job> getJobs(@PathVariable long scriptId, HttpServletRequest request) {
        return PageResult.of(
                new Link("/scripts/" + scriptId + "/jobs"),
                jobService.get(
                        scriptId,
                        PageResult.request(request)
                )
        );
    }

    @PostMapping("{scriptId}/jobs")
    @ResponseStatus(HttpStatus.CREATED)
    public Job runScript(@PathVariable long scriptId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (InputStream data = request.getInputStream()) {
            Job result = scriptService.run(
                    scriptService.get(scriptId),
                    null,
                    StreamUtils.copyToString(data, Charset.defaultCharset())
            );
            response.setHeader("Location", objectMapper.convertValue(
                    result.getUrl(),
                    String.class
            ));
            return result;
        }
    }
}
