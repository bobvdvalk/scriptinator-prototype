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

import io.chapp.scriptinator.model.*;
import io.chapp.scriptinator.services.ProjectService;
import io.chapp.scriptinator.services.ScheduleService;
import io.chapp.scriptinator.services.ScriptService;
import io.chapp.scriptinator.services.WebhookService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private static final String PROJECTS_URL = "/projects";

    private final ProjectService projectService;
    private final ScriptService scriptService;
    private final ScheduleService scheduleService;
    private final WebhookService webhookService;

    public ProjectController(ProjectService projectService, ScriptService scriptService, ScheduleService scheduleService, WebhookService webhookService) {
        this.projectService = projectService;
        this.scriptService = scriptService;
        this.scheduleService = scheduleService;
        this.webhookService = webhookService;
    }

    @GetMapping
    @PreAuthorize("#oauth2.hasScope('project:read')")
    public PageResult<Project> projectList(HttpServletRequest request) {
        Page<Project> result = projectService.findAllOwnedByPrincipal(
                PageResult.request(request)
        );

        return PageResult.of(
                new Link(PROJECTS_URL),
                result
        );
    }

    @GetMapping("{projectName}")
    @PreAuthorize("#oauth2.hasScope('project:read')")
    public Project getProjectByName(@PathVariable String projectName) {
        return projectService.getOwnedByPrincipal(projectName);
    }

    @GetMapping("{projectName}/scripts")
    @PreAuthorize("#oauth2.hasScope('script') or #oauth2.hasScope('script:read')")
    public PageResult<Script> getProjectScripts(@PathVariable String projectName, HttpServletRequest request) {
        return PageResult.of(
                new Link(PROJECTS_URL + "/" + projectName + "/scripts"),
                scriptService.findAllForProjectOwnedByPrincipal(
                        projectName,
                        PageResult.request(request)
                )
        );
    }

    @GetMapping("{projectName}/schedules")
    @PreAuthorize("#oauth2.hasScope('schedule:read')")
    public PageResult<Schedule> getProjectSchedules(@PathVariable String projectName, HttpServletRequest request) {
        return PageResult.of(
                new Link(PROJECTS_URL + "/" + projectName + "/schedules"),
                scheduleService.findAllForProjectOwnedByPrincipal(
                        projectName,
                        PageResult.request(request)
                )
        );
    }

    @GetMapping("{projectName}/webhooks")
    @PreAuthorize("#oauth2.hasScope('webhook:read')")
    public PageResult<Webhook> getProjectWebhooks(@PathVariable String projectName, HttpServletRequest request) {
        return PageResult.of(
                new Link(PROJECTS_URL + "/" + projectName + "/webhooks"),
                webhookService.findAllForProjectOwnedByPrincipal(
                        projectName,
                        PageResult.request(request)
                )
        );
    }
}
