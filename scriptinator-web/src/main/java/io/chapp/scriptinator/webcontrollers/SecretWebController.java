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
import io.chapp.scriptinator.model.Secret;
import io.chapp.scriptinator.services.ProjectService;
import io.chapp.scriptinator.services.SecretService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/project/{projectId}/secret")
public class SecretWebController {
    private static final String EDIT_SECRET_PAGE = "pages/edit_secret";

    private final ProjectService projectService;
    private final SecretService secretService;
    private final ProjectWebController projectWebController;

    public SecretWebController(ProjectService projectService, SecretService secretService, ProjectWebController projectWebController) {
        this.projectService = projectService;
        this.secretService = secretService;
        this.projectWebController = projectWebController;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("activeTab", Tab.SECRETS);
    }

    /*===== New secret =====*/

    @GetMapping
    public String showNewSecretForm(@PathVariable long projectId, Model model) {
        return showEditSecret(projectId, new Secret(), model);
    }

    @PostMapping
    public String createNewSecret(@PathVariable long projectId, @ModelAttribute("secret") Secret secret, Model model) {
        return saveSecret(projectId, secret, model);
    }

    /*===== Edit secret =====*/

    @GetMapping("{secretId}")
    public String showEditSecretForm(@PathVariable long projectId, @PathVariable long secretId, Model model) {
        return showEditSecret(projectId, secretService.getOwnedByPrincipal(projectId, secretId), model);
    }

    @PostMapping("{secretId}")
    public String updateSecret(@PathVariable long projectId, @PathVariable long secretId, @ModelAttribute("secret") Secret secret, Model model) {
        secret.setId(secretId);

        // If the new value is empty, keep the old value.
        if ("".equals(secret.getValue())) {
            secret.setValue(secretService.getOwnedByPrincipal(projectId, secretId).getValue());
        }

        return saveSecret(projectId, secret, model);
    }

    @DeleteMapping("{secretId}")
    public String deleteSecret(@PathVariable long projectId, @PathVariable long secretId, Model model) {
        secretService.deleteIfOwnedByPrincipal(secretService.getOwnedByPrincipal(projectId, secretId).getId());
        return projectWebController.viewSecrets(projectId, model);
    }


    private String showEditSecret(long projectId, Secret secret, Model model) {
        Project project = projectService.getOwnedByPrincipal(projectId);
        model.addAttribute(Project.ATTRIBUTE, project);
        model.addAttribute(Secret.ATTRIBUTE, secret);
        return EDIT_SECRET_PAGE;
    }

    private String saveSecret(long projectId, Secret secret, Model model) {
        Project project = projectService.getOwnedByPrincipal(projectId);
        model.addAttribute(Project.ATTRIBUTE, project);
        secret.setProject(project);

        try {
            // Create or update the secret.
            secret = secret.getId() == null ? secretService.create(secret) : secretService.update(secret);
        } catch (DataIntegrityViolationException e) {
            ProjectWebController.addDbErrorsToModel(e, model, project, "secret_name", Secret.ATTRIBUTE);
            model.addAttribute(Secret.ATTRIBUTE, secret);
            return EDIT_SECRET_PAGE;
        }

        model.addAttribute(Secret.ATTRIBUTE, secret);
        return "redirect:/project/" + projectId + "/secret/" + secret.getId();
    }
}
