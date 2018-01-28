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

import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.services.ProjectService;
import io.chapp.scriptinator.services.ScriptService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/project/{projectId}/script")
public class ScriptController {
    private final ScriptService scriptService;
    private final ProjectService projectService;
    private final ProjectController projectController;

    public ScriptController(ScriptService scriptService, ProjectService projectService, ProjectController projectController) {
        this.scriptService = scriptService;
        this.projectService = projectService;
        this.projectController = projectController;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("activeTab", "scripts");
    }

    @GetMapping
    public String showCreateScriptForm(@PathVariable int projectId, Model model) {
        model.addAttribute(Project.ATTRIBUTE, projectService.get(projectId));
        if (!model.containsAttribute(Script.ATTRIBUTE)) {
            model.addAttribute(Script.ATTRIBUTE, new Script());
        }
        return "pages/new_script";
    }

    @PostMapping
    public String createNewScript(@PathVariable int projectId, @ModelAttribute("script") Script script) {
        // Set the project, save the script.
        Project project = projectService.get(projectId);
        script.setProject(project);
        scriptService.create(script);

        return "redirect:/project/" + projectId;
    }

    @GetMapping("{scriptId}")
    public String showScript(@PathVariable int projectId, @PathVariable int scriptId, Model model) {
        Script script = getSafeScript(projectId, scriptId);
        model.addAttribute(Script.ATTRIBUTE, script);
        model.addAttribute(Project.ATTRIBUTE, script.getProject());
        return "pages/view_script";
    }

    @PatchMapping("{scriptId}")
    public String updateScript(@PathVariable int projectId, @PathVariable int scriptId, @RequestParam("code") String code, Model model) {
        Script script = getSafeScript(projectId, scriptId);
        script.setCode(code);
        scriptService.update(script);
        return showScript(projectId, scriptId, model);
    }

    @DeleteMapping("{scriptId}")
    public String deleteScript(@PathVariable int projectId, @PathVariable int scriptId, Model model) {
        scriptService.delete(getSafeScript(projectId, scriptId).getId());
        return projectController.viewScripts(projectId, model);
    }

    private Script getSafeScript(int projectId, int scriptId) {
        Script script = scriptService.get(scriptId);
        if (!script.getProject().getId().equals(projectId)) {
            throw new NoSuchElementException();
        }
        return script;
    }
}
