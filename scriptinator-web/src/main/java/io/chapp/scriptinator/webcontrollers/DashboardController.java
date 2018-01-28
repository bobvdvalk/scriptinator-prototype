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
import io.chapp.scriptinator.services.ProjectService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    public static final int PROJECT_EXCERPT_COUNT = 5;
    private final ProjectService projectService;

    public DashboardController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute(Project.LIST_ATTRIBUTE, projectService.get(new PageRequest(
                0,
                PROJECT_EXCERPT_COUNT
        )));
        return "pages/view_dashboard";
    }
}
