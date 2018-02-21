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

import io.chapp.scriptinator.ScriptinatorWebConfiguration;
import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.Schedule;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.services.JobService;
import io.chapp.scriptinator.services.ProjectService;
import io.chapp.scriptinator.services.ScriptService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/project/{projectId}/script")
public class ScriptController {
    private final ScriptService scriptService;
    private final ProjectService projectService;
    private final ProjectController projectController;
    private final JobService jobService;
    private final ScriptinatorWebConfiguration configuration;

    public ScriptController(ScriptService scriptService, ProjectService projectService, ProjectController projectController, JobService jobService, ScriptinatorWebConfiguration configuration) {
        this.scriptService = scriptService;
        this.projectService = projectService;
        this.projectController = projectController;
        this.jobService = jobService;
        this.configuration = configuration;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("activeTab", Tab.SCRIPTS);
    }

    /*===== New script =====*/

    @GetMapping
    public String showNewScriptForm(@PathVariable long projectId, Model model) {
        model.addAttribute(Project.ATTRIBUTE, projectService.get(projectId));
        model.addAttribute(Script.ATTRIBUTE, new Script());
        return "pages/new_script";
    }

    @PostMapping
    public String createScript(@PathVariable long projectId, @ModelAttribute("script") Script script, Model model) {
        return saveScript(projectId, script, model, "pages/new_script");
    }

    /*===== View script =====*/

    @PatchMapping("{scriptId}")
    public String updateCode(@PathVariable long projectId, @PathVariable long scriptId, @RequestParam("code") String code, Model model) {
        Script script = getSafeScript(projectId, scriptId);
        script.setCode(code);
        scriptService.update(script);
        return showScript(projectId, scriptId, model);
    }

    @GetMapping("{scriptId}")
    public String showScript(@PathVariable long projectId, @PathVariable long scriptId, Model model) {
        Script script = getSafeScript(projectId, scriptId);
        model.addAttribute(Script.ATTRIBUTE, script);
        model.addAttribute(Project.ATTRIBUTE, script.getProject());

        // Get all job pages.
        Page<Job> jobs = jobService.get(
                scriptId,
                new PageRequest(
                        0,
                        configuration.getDefaultPageSize(),
                        new Sort(Sort.Direction.DESC, "id")
                )
        );

        // Add all job triggers to the model.
        Map<Long, Job> jobTriggers = new HashMap<>(jobs.getSize());
        for (Job job : jobs) {
            if (job.getTriggeredByJobId() != null) {
                jobService.findOne(job.getTriggeredByJobId())
                        .ifPresent(trigger -> jobTriggers.put(job.getId(), trigger));
            }
        }
        model.addAttribute("jobTriggers", jobTriggers);

        // Add a map of all schedules by their id to the model.
        model.addAttribute(
                "schedulesById",
                script.getProject().getSchedules().stream()
                        .collect(Collectors.toMap(Schedule::getId, Function.identity()))
        );

        // Check if a job is running.
        model.addAttribute(Job.LIST_ATTRIBUTE, jobs);
        model.addAttribute(
                "isRunning",
                jobs.getContent().stream().anyMatch(job -> job.getFinishedTime() == null)
        );

        return "pages/view_script";
    }

    @PostMapping("{scriptId}")
    public String runScript(@PathVariable long projectId, @PathVariable long scriptId, Model model) {
        scriptService.run(scriptService.get(scriptId));
        return showScript(projectId, scriptId, model);
    }

    /*===== Script settings =====*/

    @GetMapping("{scriptId}/settings")
    public String showEditScriptForm(@PathVariable long projectId, @PathVariable long scriptId, Model model) {
        model.addAttribute(Script.ATTRIBUTE, getSafeScript(projectId, scriptId));
        model.addAttribute(Project.ATTRIBUTE, projectService.get(projectId));
        return "pages/edit_script";
    }

    @PostMapping("{scriptId}/settings")
    public String updateScript(@PathVariable long projectId, @PathVariable long scriptId, @ModelAttribute("script") Script script, Model model) {
        Script oldScript = getSafeScript(projectId, scriptId);
        script.setId(scriptId);
        script.setCode(oldScript.getCode());
        return saveScript(projectId, script, model, "pages/edit_script");
    }

    @DeleteMapping("{scriptId}/settings")
    public String deleteScript(@PathVariable long projectId, @PathVariable long scriptId, Model model) {
        scriptService.delete(getSafeScript(projectId, scriptId).getId());
        return projectController.viewScripts(projectId, model);
    }


    private Script getSafeScript(long projectId, long scriptId) {
        Script script = scriptService.get(scriptId);
        if (!script.getProject().getId().equals(projectId)) {
            throw new NoSuchElementException();
        }
        return script;
    }

    private String saveScript(Long projectId, Script script, Model model, String errorPage) {
        Project project = projectService.get(projectId);
        script.setProject(project);
        model.addAttribute(Project.ATTRIBUTE, project);

        try {
            // Create or update the script.
            script = script.getId() == null ? scriptService.create(script) : scriptService.update(script);
        } catch (DataIntegrityViolationException e) {
            ProjectController.addDbErrorsToModel(e, model, project, "script_name", Script.ATTRIBUTE);
            model.addAttribute(Script.ATTRIBUTE, script);
            return errorPage;
        }

        model.addAttribute(Script.ATTRIBUTE, script);
        return "redirect:/project/" + projectId + "/script/" + script.getId();
    }
}
