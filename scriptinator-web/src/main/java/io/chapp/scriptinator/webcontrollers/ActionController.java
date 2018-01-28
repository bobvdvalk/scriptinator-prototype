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
import io.chapp.scriptinator.services.ActionService;
import io.chapp.scriptinator.services.ProjectService;
import io.chapp.scriptinator.services.ScriptService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/project/{projectId}/action")
public class ActionController {
    private final ProjectService projectService;
    private final ActionService actionService;
    private final ScriptService scriptService;
    private final ProjectController projectController;

    public ActionController(ProjectService projectService, ActionService actionService, ScriptService scriptService, ProjectController projectController) {
        this.projectService = projectService;
        this.actionService = actionService;
        this.scriptService = scriptService;
        this.projectController = projectController;
    }

    @GetMapping
    public String showCreateActionForm(@PathVariable int projectId, Model model) {
        model.addAttribute(Tab.ATTRIBUTE, Tab.ACTIONS);
        model.addAttribute(Project.ATTRIBUTE, projectService.get(projectId));
        if (!model.containsAttribute(Action.ATTRIBUTE)) {
            Action action = new Action();
            action.setScript(new Script());
            model.addAttribute(Action.ATTRIBUTE, action);
        }
        return "pages/new_action";
    }

    @PostMapping
    public String createAction(@PathVariable int projectId, @ModelAttribute("action") Action action, Model model) {
        action.setProject(projectService.get(projectId));
        action.setScript(scriptService.get(action.getScript().getId()));
        actionService.create(action);
        return projectController.viewActions(projectId, model);
    }

    @GetMapping("{actionId}")
    public String showAction(@PathVariable int projectId, @PathVariable int actionId, Model model) {
        Action action = actionService.get(actionId);
        model.addAttribute(Action.ATTRIBUTE, action);
        model.addAttribute(Project.ATTRIBUTE, action.getProject());
        return "pages/view_action";
    }

    @DeleteMapping("{actionId}")
    public String deleteAction(@PathVariable int projectId, @PathVariable int actionId, Model model) {
        actionService.delete(actionId);
        return projectController.viewActions(projectId, model);
    }
}
