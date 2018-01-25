package io.chapp.scriptinator.controllers;

import io.chapp.scriptinator.dataservices.AuditService;
import io.chapp.scriptinator.dataservices.ProjectService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final ProjectService projectService;
    private final AuditService auditService;

    public DashboardController(ProjectService projectService, AuditService auditService) {
        this.projectService = projectService;
        this.auditService = auditService;
    }

    @GetMapping("")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String loadDashboard(Model model) {
        model.addAttribute("projects", projectService.get(new PageRequest(0, 10)));
        model.addAttribute("auditLog", auditService.getLast(10));
        return "pages/dashboard";
    }
}
