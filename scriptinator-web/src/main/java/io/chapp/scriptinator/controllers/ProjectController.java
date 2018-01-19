package io.chapp.scriptinator.controllers;

import io.chapp.scriptinator.dataservices.ProjectService;
import io.chapp.scriptinator.model.Project;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("project")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public String newProjectForm(Model model) {
        Project newProject = new Project();
        newProject.setCommitId("master");

        model.addAttribute("project", newProject);
        return "pages/new_project";
    }

    @PostMapping
    public String createNewProject(@Valid @ModelAttribute Project project) {
        projectService.create(project);
        return "redirect:/project/" + project.getId();
    }

    @GetMapping("{projectId}")
    public String projectOverview(@PathVariable int projectId, Model model) {
        model.addAttribute("project", projectService.get(projectId));
        return "pages/project";
    }

    @DeleteMapping("{projectId}")
    public String deleteProject(@PathVariable int projectId) {
        projectService.delete(projectId);
        return "redirect:/projects";
    }
}
