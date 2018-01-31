package io.chapp.scriptinator.api.model;

import java.util.List;

public class Project {
    private String projectName;
    private String description;
    private Owner owner;
    private List<Script> scripts;
    private List<Job> jobs;

    public Project(String projectName, String description, Owner owner, List<Script> scripts, List<Job> jobs) {
        this.projectName = projectName;
        this.description = description;
        this.owner = owner;
        this.scripts = scripts;
        this.jobs = jobs;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getDescription() {
        return description;
    }

    public Owner getOwner() {
        return owner;
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public List<Job> getJobs() {
        return jobs;
    }
}
