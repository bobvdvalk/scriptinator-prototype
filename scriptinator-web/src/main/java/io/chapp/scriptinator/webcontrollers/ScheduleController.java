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

import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.Schedule;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.services.ProjectService;
import io.chapp.scriptinator.services.ScheduleService;
import io.chapp.scriptinator.services.ScriptService;
import org.quartz.CronExpression;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/project/{projectId}/schedule")
public class ScheduleController {
    private static final String EDIT_SCHEDULE_PAGE = "pages/edit_schedule";

    private final ProjectService projectService;
    private final ScheduleService scheduleService;
    private final ProjectController projectController;
    private final ScriptService scriptService;

    public ScheduleController(ProjectService projectService, ScheduleService scheduleService, ProjectController projectController, ScriptService scriptService) {
        this.projectService = projectService;
        this.scheduleService = scheduleService;
        this.projectController = projectController;
        this.scriptService = scriptService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("activeTab", Tab.SCHEDULES);
    }

    /*===== New schedule =====*/

    @GetMapping
    public String showNewScheduleForm(@PathVariable long projectId, Model model) {
        return showEditSchedule(projectId, new Schedule(), model);
    }

    @PostMapping
    public String createNewSchedule(@PathVariable long projectId, @ModelAttribute("schedule") Schedule schedule, Model model) {
        return saveSchedule(projectId, schedule, model);
    }

    /*===== Edit schedule =====*/

    @GetMapping("{scheduleId}")
    public String showEditScheduleForm(@PathVariable long projectId, @PathVariable long scheduleId, Model model) {
        return showEditSchedule(projectId, getSafeSchedule(projectId, scheduleId), model);
    }

    @PostMapping("{scheduleId}")
    public String updateSchedule(@PathVariable long projectId, @PathVariable long scheduleId, @ModelAttribute("schedule") Schedule schedule, Model model) {
        Schedule oldSchedule = getSafeSchedule(projectId, scheduleId);
        schedule.setId(scheduleId);
        schedule.setLastRun(oldSchedule.getLastRun()); // Remember the last run.
        return saveSchedule(projectId, schedule, model);
    }

    @DeleteMapping("{scheduleId}")
    public String deleteSchedule(@PathVariable long projectId, @PathVariable long scheduleId, Model model) {
        scheduleService.delete(getSafeSchedule(projectId, scheduleId).getId());
        return projectController.viewSchedules(projectId, model);
    }


    private String showEditSchedule(long projectId, Schedule schedule, Model model) {
        Project project = projectService.get(projectId);
        model.addAttribute(Project.ATTRIBUTE, project);
        model.addAttribute(Schedule.ATTRIBUTE, schedule);
        model.addAttribute(Script.LIST_ATTRIBUTE, project.getScripts());
        model.addAttribute("cronDescription", scheduleService.getCronDescription(schedule.getCronString()));
        return EDIT_SCHEDULE_PAGE;
    }

    private String saveSchedule(long projectId, Schedule schedule, Model model) {
        Project project = projectService.get(projectId);
        model.addAttribute(Project.ATTRIBUTE, project);
        model.addAttribute(Script.LIST_ATTRIBUTE, project.getScripts());
        schedule.setProject(project);

        // Check if the script exists.
        if (scriptService.getByFullName(schedule.getScriptName(), schedule.getProject().getName()) == null) {
            return saveError(schedule, model, "scriptName", "The referenced script could not be found.");
        }

        // Validate the cron expression.
        if (!CronExpression.isValidExpression(schedule.getCronString())) {
            return saveError(schedule, model, "cronString", "The cron expression is invalid.");
        }

        try {
            // Create or update the schedule.
            schedule = schedule.getId() == null ? scheduleService.create(schedule) : scheduleService.update(schedule);
        } catch (DataIntegrityViolationException e) {
            ProjectController.addDbErrorsToModel(e, model, project, "schedule_name", Schedule.ATTRIBUTE);
            model.addAttribute(Schedule.ATTRIBUTE, schedule);
            return EDIT_SCHEDULE_PAGE;
        }

        model.addAttribute(Schedule.ATTRIBUTE, schedule);
        return "redirect:/project/" + projectId + "/schedule/" + schedule.getId();
    }

    private String saveError(Schedule schedule, Model model, String property, String message) {
        model.addAttribute(
                "errors",
                Collections.singletonMap(property, message)
        );

        model.addAttribute(Schedule.ATTRIBUTE, schedule);
        return EDIT_SCHEDULE_PAGE;
    }

    private Schedule getSafeSchedule(long projectId, long scheduleId) {
        Schedule schedule = scheduleService.get(scheduleId);
        if (!schedule.getProject().getId().equals(projectId)) {
            throw new NoSuchElementException();
        }
        return schedule;
    }
}
