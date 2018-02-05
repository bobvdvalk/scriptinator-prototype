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

import io.chapp.scriptinator.ScriptinatorWebConfiguration;
import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.services.JobService;
import io.chapp.scriptinator.services.ProjectService;
import io.chapp.scriptinator.services.ScriptService;
import org.hibernate.exception.ConstraintViolationException;
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

import static java.util.Collections.singletonMap;

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
        model.addAttribute("activeTab", "scripts");
    }

    @GetMapping
    public String showCreateScriptForm(@PathVariable long projectId, Model model) {
        model.addAttribute(Project.ATTRIBUTE, projectService.get(projectId));
        if (!model.containsAttribute(Script.ATTRIBUTE)) {
            model.addAttribute(Script.ATTRIBUTE, new Script());
        }
        return "pages/new_script";
    }

    @PostMapping
    public String createNewScript(@PathVariable long projectId, @ModelAttribute("script") Script script, Model model) {
        Project project = projectService.get(projectId);
        script.setProject(project);

        try {
            script = scriptService.create(script);
        } catch (DataIntegrityViolationException e) {
            Throwable cause = e.getCause();

            // Check for a duplicate script name constraint error.
            if (cause instanceof ConstraintViolationException && ((ConstraintViolationException) cause).getConstraintName().equals("script_name")) {
                model.addAttribute(Script.ATTRIBUTE, script);
                model.addAttribute(
                        "errors",
                        singletonMap(
                                "name",
                                "There already seems to be a script with that name, please choose a different one."
                        )
                );
            }
            return showCreateScriptForm(projectId, model);
        }

        return "redirect:/project/" + projectId + "/script/" + script.getId();
    }

    @GetMapping("{scriptId}")
    public String showScript(@PathVariable long projectId, @PathVariable long scriptId, Model model) {
        Script script = getSafeScript(projectId, scriptId);
        model.addAttribute(Script.ATTRIBUTE, script);
        model.addAttribute(Project.ATTRIBUTE, script.getProject());

        Page<Job> jobs = jobService.get(
                scriptId,
                new PageRequest(
                        0,
                        configuration.getDefaultPageSize(),
                        new Sort(
                                Sort.Direction.DESC,
                                "id"
                        )
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

    @PatchMapping("{scriptId}")
    public String updateScript(@PathVariable long projectId, @PathVariable long scriptId, @RequestParam("code") String code, Model model) {
        Script script = getSafeScript(projectId, scriptId);
        script.setCode(code);
        scriptService.update(script);
        return showScript(projectId, scriptId, model);
    }

    @DeleteMapping("{scriptId}")
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
}
