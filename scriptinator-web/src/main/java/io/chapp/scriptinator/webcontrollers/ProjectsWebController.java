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
package io.chapp.scriptinator.webcontrollers;

import io.chapp.scriptinator.ScriptinatorConfiguration;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.services.ProjectService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.Min;

@Controller
public class ProjectsWebController {
    private final ProjectService projectService;
    private final ScriptinatorConfiguration configuration;

    public ProjectsWebController(ProjectService projectService, ScriptinatorConfiguration configuration) {
        this.projectService = projectService;
        this.configuration = configuration;
    }

    @GetMapping("/projects")
    public String projectList(Model model) {
        return projectList(1, model);
    }

    @GetMapping("/projects/{pageId}")
    public String projectList(@PathVariable @Min(1) int pageId, Model model) {
        model.addAttribute(
                Project.LIST_ATTRIBUTE, projectService.findAllOwnedByPrincipal(new PageRequest(
                        pageId - 1,
                        configuration.getDefaultPageSize()
                ))
        );
        return "pages/view_projects";
    }
}
