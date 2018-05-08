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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.model.Link;
import io.chapp.scriptinator.model.PageResult;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.services.JobService;
import io.chapp.scriptinator.services.ScriptService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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

    @GetMapping
    @PreAuthorize("#oauth2.hasAnyScope('script', 'script:read')")
    public PageResult<Script> getScripts(HttpServletRequest request) {
        return PageResult.of(
                new Link("/scripts"),
                scriptService.findAllOwnedByPrincipal(
                        PageResult.request(request)
                )
        );
    }

    /*
     * Get script by ID
     *
     * The reason you request a script by id is because a script id is always unique and a script name is only unique
     * within the project.
     */
    @GetMapping("{scriptId}")
    @PreAuthorize("#oauth2.hasAnyScope('script', 'script:read')")
    public Script getScriptById(@PathVariable long scriptId) {
        return scriptService.getOwnedByPrincipal(scriptId);
    }

    @GetMapping("{scriptId}/jobs")
    @PreAuthorize("#oauth2.hasScope('job:read')")
    public PageResult<Job> getJobs(@PathVariable long scriptId, HttpServletRequest request) {
        return PageResult.of(
                new Link("/scripts/" + scriptId + "/jobs"),
                jobService.findAllForScriptOwnedByPrincipal(
                        scriptId,
                        PageResult.request(request)
                )
        );
    }

    @PostMapping(value = "{scriptId}/jobs", consumes = "application/json")
    @PreAuthorize("#oauth2.hasAnyScope('script', 'script:run')")
    public ResponseEntity<Job> runScript(@PathVariable long scriptId, @RequestBody Job job) throws IOException {
        try {
            Script script = scriptService.getOwnedByPrincipal(scriptId);
            Object argument = StringUtils.isEmpty(job.getArgument()) ? null : objectMapper.readValue(job.getArgument(), Object.class);
            Job result = scriptService.run(script, argument);

            var headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, objectMapper.convertValue(result.getUrl(), String.class));

            return new ResponseEntity<>(
                    result,
                    headers,
                    HttpStatus.CREATED
            );
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("Invalid 'argument' field. Must be a json string.", e);
        }
    }
}
