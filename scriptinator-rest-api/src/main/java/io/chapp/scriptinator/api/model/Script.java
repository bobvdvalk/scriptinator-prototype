package io.chapp.scriptinator.api.model;

import java.util.List;

public class Script {
    private String filename;
    private String description;
    private String code;
    private Project project;
    private List<Job> jobs;

    public Script(String filename, String description, String code, Project project, List<Job> jobs) {
        this.filename = filename;
        this.description = description;
        this.code = code;
        this.project = project;
        this.jobs = jobs;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
}
