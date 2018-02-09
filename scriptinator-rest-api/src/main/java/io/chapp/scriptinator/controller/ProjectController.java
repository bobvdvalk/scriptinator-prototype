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
package io.chapp.scriptinator.controller;

import io.chapp.scriptinator.model.Link;
import io.chapp.scriptinator.model.PageResult;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.services.ProjectService;
import io.chapp.scriptinator.services.ScriptService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * ProjectController
 * <p>
 * Rest controller for the ProjectDtos entity.
 *
 * @author bobvdvalk
 */
@RestController
@RequestMapping("projects")
public class ProjectController {
    private final ProjectService projectService;
    private final ScriptService scriptService;

    public ProjectController(ProjectService projectService, ScriptService scriptService) {
        this.projectService = projectService;
        this.scriptService = scriptService;
    }

    @GetMapping
    public PageResult<Project> projectList(HttpServletRequest request) {
        Page<Project> result = projectService.get(
                PageResult.request(request)
        );

        return PageResult.of(
                new Link("/projects"),
                result
        );
    }

    @GetMapping("{projectName}")
    public Project getProjectByName(@PathVariable String projectName) {
        return projectService.get(projectName);
    }

    @GetMapping("{projectName}/scripts")
    public PageResult<Script> getProjectScripts(@PathVariable String projectName, HttpServletRequest request) {
        return PageResult.of(
                new Link("/projects/" + projectName + "/scripts"),
                scriptService.get(
                        projectName,
                        PageResult.request(request)
                )
        );
    }
}
