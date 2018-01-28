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
package io.chapp.scriptinator.webcontrollers;

import io.chapp.scriptinator.model.Action;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.services.ProjectService;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;
    private final ObjectFactory<User> currentUserFactory;
    private final ProjectsController projectsController;

    public ProjectController(ProjectService projectService, ObjectFactory<User> currentUserFactory, ProjectsController projectsController) {
        this.projectService = projectService;
        this.currentUserFactory = currentUserFactory;
        this.projectsController = projectsController;
    }

    @GetMapping("{projectId}")
    public String viewOverview(@PathVariable int projectId) {
        return "redirect:/project/" + projectId + "/scripts";
    }

    @GetMapping("{projectId}/scripts")
    public String viewScripts(@PathVariable int projectId, Model model) {
        Project project = projectService.get(projectId);
        model.addAttribute(Project.ATTRIBUTE, project);
        model.addAttribute(Script.LIST_ATTRIBUTE, project.getScripts());
        model.addAttribute(Tab.ATTRIBUTE, Tab.SCRIPTS);

        model.addAttribute("scriptCount", project.getScripts().size());
        return "pages/view_scripts";
    }

    @GetMapping("{projectId}/actions")
    public String viewActions(@PathVariable int projectId, Model model) {
        Project project = projectService.get(projectId);
        model.addAttribute(Project.ATTRIBUTE, project);
        model.addAttribute(Action.LIST_ATTRIBUTE, project.getActions());
        model.addAttribute(Tab.ATTRIBUTE, Tab.ACTIONS);
        model.addAttribute("actionCount", project.getActions().size());
        return "pages/view_actions";
    }

    @GetMapping("{projectId}/settings")
    public String editProject(@PathVariable int projectId, Model model) {
        model.addAttribute(Project.ATTRIBUTE, projectService.get(projectId));
        model.addAttribute(Tab.ATTRIBUTE, Tab.SETTINGS);
        return "pages/edit_project";
    }

    @GetMapping("")
    public String showCreateProjectForm(Model model) {
        if (!model.containsAttribute(Project.ATTRIBUTE)) {
            model.addAttribute(Project.ATTRIBUTE, new Project());
        }
        return "pages/new_project";
    }

    @PostMapping("")
    public String createNewProject(@ModelAttribute(Project.ATTRIBUTE) Project project) {
        // Set the owner, save the project.
        project.setOwner(currentUserFactory.getObject());
        project = projectService.create(project);

        return "redirect:/project/" + project.getId();
    }

    @PostMapping("{projectId}/settings")
    public String updateProject(@PathVariable int projectId, @ModelAttribute(Project.ATTRIBUTE) Project projectChanges, Model model) {
        // Update the current project with the project changes.
        Project currentProject = projectService.get(projectId);
        currentProject.setDisplayName(projectChanges.getDisplayName());
        currentProject.setDescription(projectChanges.getDescription());
        projectService.update(currentProject);

        return viewScripts(projectId, model);
    }

    @DeleteMapping("{projectId}")
    public String deleteProject(@PathVariable int projectId, Model model) {
        projectService.delete(projectId);
        return projectsController.projectList(model);
    }
}
