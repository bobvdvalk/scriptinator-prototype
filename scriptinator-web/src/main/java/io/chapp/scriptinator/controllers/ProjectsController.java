package io.chapp.scriptinator.controllers;

import io.chapp.scriptinator.dataservices.ProjectService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("projects")
public class ProjectsController {
    private final ProjectService projectService;

    public ProjectsController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @RequestMapping
    public String listProjects(Model model) {
        return listProjects(1, model);
    }

    @RequestMapping("{pageNumber}")
    public String listProjects(@PathVariable int pageNumber, Model model) {
        model.addAttribute("projects", projectService.get(new PageRequest(pageNumber - 1, 10)));
        return "pages/projects";
    }
}
