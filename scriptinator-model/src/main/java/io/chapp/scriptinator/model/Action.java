package io.chapp.scriptinator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Action extends AbstractEntity {
    @NotEmpty
    @NotNull
    private String displayName;
    @NotNull
    @Pattern(regexp = "[\\w]+")
    private String name;
    @NotEmpty
    @NotNull
    private String script;
    @ManyToOne(optional = false)
    @JsonIgnore
    @NotNull
    private Project project;
    @OneToMany(orphanRemoval = true, mappedBy = "action")
    @JsonIgnore
    private List<Job> jobs = new ArrayList<>();

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Link getProjectUrl() {
        return getProject().getUrl();
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public Link getJobsUrl() {
        return new Link(getUrl().getHref() + "/jobs");
    }

    @Override
    public String name() {
        return getDisplayName();
    }
}
