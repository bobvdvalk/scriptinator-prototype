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
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.model.Webhook;
import io.chapp.scriptinator.services.ProjectService;
import io.chapp.scriptinator.services.ScriptService;
import io.chapp.scriptinator.services.WebhookService;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.Collections;

@Controller
@RequestMapping("/project/{projectId}/webhook")
public class WebhookWebController {
    private static final String EDIT_WEBHOOK_PAGE = "pages/edit_webhook";

    private final WebhookService webhookService;
    private final ProjectWebController projectWebController;
    private final ProjectService projectService;
    private final ScriptService scriptService;

    public WebhookWebController(WebhookService webhookService, ProjectWebController projectWebController, ProjectService projectService, ScriptService scriptService) {
        this.webhookService = webhookService;
        this.projectWebController = projectWebController;
        this.projectService = projectService;
        this.scriptService = scriptService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("activeTab", Tab.WEBHOOKS);
    }

    /*===== New webhook =====*/

    @GetMapping
    public String showNewWebhookForm(@PathVariable long projectId, Model model, HttpServletRequest request) {
        return showEditWebhook(projectId, new Webhook(), model, request);
    }

    @PostMapping
    public String createNewWebhook(@PathVariable long projectId, @ModelAttribute("webhook") Webhook webhook, Model model) {
        return saveWebhook(projectId, webhook, model);
    }

    /*===== Edit webhook =====*/

    @GetMapping("{webhookId}")
    public String showEditWebhookForm(@PathVariable long projectId, @PathVariable long webhookId, Model model, HttpServletRequest request) {
        return showEditWebhook(projectId, webhookService.getOwnedByPrincipal(webhookId), model, request);
    }

    @PostMapping("{webhookId}")
    public String updateWebhook(@PathVariable long projectId, @PathVariable long webhookId, @ModelAttribute("webhook") Webhook webhook, Model model) {
        webhook.setId(webhookId);

        // Move over some old values.
        Webhook oldWebhook = webhookService.getOwnedByPrincipal(webhookId);
        webhook.setUuid(oldWebhook.getUuid());
        webhook.setLastCall(oldWebhook.getLastCall());

        return saveWebhook(projectId, webhook, model);
    }

    @DeleteMapping("{webhookId}")
    public String deleteWebhook(@PathVariable long projectId, @PathVariable long webhookId, Model model) {
        webhookService.deleteIfOwnedByPrincipal(webhookService.getOwnedByPrincipal(webhookId).getId());
        return projectWebController.viewWebhooks(projectId, model);
    }


    private String showEditWebhook(long projectId, Webhook webhook, Model model, HttpServletRequest request) {
        Project project = projectService.getOwnedByPrincipal(projectId);
        model.addAttribute(Project.ATTRIBUTE, project);
        model.addAttribute(Webhook.ATTRIBUTE, webhook);
        model.addAttribute(Script.LIST_ATTRIBUTE, project.getScripts());
        model.addAttribute("webhookUrl", getWebhookUrl(webhook, request));
        return EDIT_WEBHOOK_PAGE;
    }

    private String getWebhookUrl(Webhook webhook, HttpServletRequest request) {
        URIBuilder builder;
        try {
            builder = new URIBuilder(request.getRequestURL().toString());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid request url", e);
        }

        return builder
                .removeQuery()
                .clearParameters()
                .setPath("/webhook/" + webhook.getUuid())
                .toString();
    }

    private String saveWebhook(long projectId, Webhook webhook, Model model) {
        Project project = projectService.getOwnedByPrincipal(projectId);
        model.addAttribute(Project.ATTRIBUTE, project);
        webhook.setProject(project);

        // Check if the script exists.
        if (!scriptService.findOwnedByPrincipal(webhook.getProject().getName(), webhook.getScriptName()).isPresent()) {
            model.addAttribute(
                    "errors",
                    Collections.singletonMap("scriptName", "The referenced script could not be found.")
            );

            model.addAttribute(Webhook.ATTRIBUTE, webhook);
            return EDIT_WEBHOOK_PAGE;
        }

        try {
            // Create or update the webhook.
            webhook = webhook.getId() == null ? webhookService.create(webhook) : webhookService.update(webhook);
        } catch (DataIntegrityViolationException e) {
            ProjectWebController.addDbErrorsToModel(e, model, project, "webhook_name", Webhook.ATTRIBUTE);
            model.addAttribute(Webhook.ATTRIBUTE, webhook);
            return EDIT_WEBHOOK_PAGE;
        }

        model.addAttribute(Webhook.ATTRIBUTE, webhook);
        return "redirect:/project/" + projectId + "/webhook/" + webhook.getId();
    }
}
