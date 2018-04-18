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

import io.chapp.scriptinator.model.*;
import io.chapp.scriptinator.services.ProjectService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static java.util.Collections.singletonMap;

@Controller
@RequestMapping("/project")
public class ProjectWebController {
    private final ProjectService projectService;
    private final ObjectFactory<User> currentUserFactory;
    private final ProjectsWebController projectsWebController;

    public ProjectWebController(ProjectService projectService, ObjectFactory<User> currentUserFactory, ProjectsWebController projectsWebController) {
        this.projectService = projectService;
        this.currentUserFactory = currentUserFactory;
        this.projectsWebController = projectsWebController;
    }

    /*===== New project =====*/

    static void addDbErrorsToModel(DataIntegrityViolationException e, Model model, Project project, String constraintName, String entityType) {
        if (!model.containsAttribute(Project.ATTRIBUTE)) {
            model.addAttribute(Project.ATTRIBUTE, project);
        }

        // Check for a duplicate project name constraint error.
        Throwable cause = e.getCause();
        if (cause instanceof ConstraintViolationException && ((ConstraintViolationException) cause).getConstraintName().equals(constraintName)) {
            model.addAttribute(
                    "errors",
                    singletonMap(
                            "name",
                            "There already seems to be a " + entityType + " with that name, please choose a different one."
                    )
            );
        }
    }

    @GetMapping
    public String showCreateProjectForm(Model model) {
        if (!model.containsAttribute(Project.ATTRIBUTE)) {
            model.addAttribute(Project.ATTRIBUTE, new Project());
        }
        return "pages/new_project";
    }

    /*===== View project =====*/

    @PostMapping
    public String createNewProject(@ModelAttribute(Project.ATTRIBUTE) Project project, Model model) {
        project.setOwner(currentUserFactory.getObject());

        try {
            project = projectService.create(project);
        } catch (DataIntegrityViolationException e) {
            addDbErrorsToModel(e, model, project, "project_name", Project.ATTRIBUTE);
            return showCreateProjectForm(model);
        }

        return "redirect:/project/" + project.getId();
    }

    @GetMapping("{projectId}")
    public String viewOverview(@PathVariable long projectId) {
        return "redirect:/project/" + projectId + "/scripts";
    }

    @GetMapping("{projectId}/scripts")
    public String viewScripts(@PathVariable long projectId, Model model) {
        Project project = projectService.getOwnedByPrincipal(projectId);
        model.addAttribute(Project.ATTRIBUTE, project);
        model.addAttribute(Script.LIST_ATTRIBUTE, project.getScripts());
        model.addAttribute(Tab.ATTRIBUTE, Tab.SCRIPTS);

        model.addAttribute("scriptCount", project.getScripts().size());
        return "pages/view_scripts";
    }

    @GetMapping("{projectId}/schedules")
    public String viewSchedules(@PathVariable long projectId, Model model) {
        Project project = projectService.getOwnedByPrincipal(projectId);
        model.addAttribute(Project.ATTRIBUTE, project);
        model.addAttribute(Schedule.LIST_ATTRIBUTE, project.getSchedules());

        model.addAttribute("scheduleCount", project.getSchedules().size());
        model.addAttribute(Tab.ATTRIBUTE, Tab.SCHEDULES);
        return "pages/view_schedules";
    }

    @GetMapping("{projectId}/webhooks")
    public String viewWebhooks(@PathVariable long projectId, Model model) {
        Project project = projectService.getOwnedByPrincipal(projectId);
        model.addAttribute(Project.ATTRIBUTE, project);
        model.addAttribute(Webhook.LIST_ATTRIBUTE, project.getWebhooks());

        model.addAttribute("webhookCount", project.getWebhooks().size());
        model.addAttribute(Tab.ATTRIBUTE, Tab.WEBHOOKS);
        return "pages/view_webhooks";
    }

    /*===== Project settings =====*/

    @GetMapping("{projectId}/secrets")
    public String viewSecrets(@PathVariable long projectId, Model model) {
        Project project = projectService.getOwnedByPrincipal(projectId);
        model.addAttribute(Project.ATTRIBUTE, project);
        model.addAttribute(Secret.LIST_ATTRIBUTE, project.getSecrets());

        model.addAttribute("secretCount", project.getSecrets().size());
        model.addAttribute(Tab.ATTRIBUTE, Tab.SECRETS);
        return "pages/view_secrets";
    }

    @GetMapping("{projectId}/settings")
    public String editProject(@PathVariable long projectId, Model model) {
        if (!model.containsAttribute(Project.ATTRIBUTE)) {
            model.addAttribute(Project.ATTRIBUTE, projectService.getOwnedByPrincipal(projectId));
        }
        model.addAttribute(Tab.ATTRIBUTE, Tab.SETTINGS);
        return "pages/edit_project";
    }

    @PostMapping("{projectId}/settings")
    public String updateProject(@PathVariable long projectId, @ModelAttribute(Project.ATTRIBUTE) Project projectChanges, Model model) {
        // Update the current project with the project changes.
        Project currentProject = projectService.getOwnedByPrincipal(projectId);
        currentProject.setName(projectChanges.getName());
        currentProject.setDescription(projectChanges.getDescription());

        try {
            projectService.update(currentProject);
        } catch (DataIntegrityViolationException e) {
            addDbErrorsToModel(e, model, currentProject, "project_name", Project.ATTRIBUTE);
            return editProject(currentProject.getId(), model);
        }

        return viewScripts(projectId, model);
    }

    @DeleteMapping("{projectId}")
    public String deleteProject(@PathVariable long projectId, Model model) {
        projectService.deleteIfOwnedByPrincipal(projectId);
        return projectsWebController.projectList(model);
    }
}
