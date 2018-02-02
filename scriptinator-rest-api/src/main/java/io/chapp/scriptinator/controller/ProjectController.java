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

import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.ProjectDtos;
import io.chapp.scriptinator.services.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectController
 * <p>
 * Rest controller for the ProjectDtos entity.
 *
 * @author bobvdvalk
 */
@RestController
@RequestMapping("/projects/")
public class ProjectController {
    private final Logger logger = LoggerFactory.getLogger(ProjectController.class);
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @RequestMapping("/list")
    @ResponseBody
    public List<ProjectDtos> projects(@RequestParam("page-id") int pageId) {
        logger.info("retrieving all projects");
        List<ProjectDtos> projects = new ArrayList<>();
        for(Project project : this.projectService.get(new PageRequest(pageId - 1, 15))) {
            projects.add(ProjectDtos.convert(project));
        }
        return projects;
    }

    @RequestMapping("/project")
    @ResponseBody
    public ProjectDtos readProject(@RequestParam("project-id") int projectId) {
        logger.info("retrieving project from id"+ projectId);
        return ProjectDtos.convert(projectService.get(projectId));
    }
}
