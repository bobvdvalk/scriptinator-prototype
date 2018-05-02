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

import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.services.JobService;
import io.chapp.scriptinator.services.ProjectService;
import io.chapp.scriptinator.services.ScriptService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardWebController {
    private static final int PROJECT_EXCERPT_COUNT = 5;
    private static final int JOB_EXCERPT_COUNT = 10;

    private final ProjectService projectService;
    private final JobService jobService;
    private final ScriptService scriptService;

    public DashboardWebController(ProjectService projectService, JobService jobService, ScriptService scriptService) {
        this.projectService = projectService;
        this.jobService = jobService;
        this.scriptService = scriptService;
    }

    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute(Project.LIST_ATTRIBUTE, projectService.findAllOwnedByPrincipal(new PageRequest(
                0,
                PROJECT_EXCERPT_COUNT
        )));

        model.addAttribute(Job.LIST_ATTRIBUTE, jobService.getAllOwnedByPrincipal(new PageRequest(
                0,
                JOB_EXCERPT_COUNT,
                new Sort(Sort.Direction.DESC, "id")
        )));

        model.addAttribute("jobCount", jobService.getAllOwnedByPrincipal(new PageRequest(0, 1)).getTotalElements());
        model.addAttribute("scriptCount", scriptService.getAllOwnedByPrincipal(new PageRequest(0, 1)).getTotalElements());

        return "pages/view_dashboard";
    }
}
