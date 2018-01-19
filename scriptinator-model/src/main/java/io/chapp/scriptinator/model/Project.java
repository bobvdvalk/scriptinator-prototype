package io.chapp.scriptinator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project extends AbstractEntity {
    private String displayName;
    @NotEmpty
    @NotNull
    private String gitUrl;
    @NotEmpty
    private String commitId;
    @OneToMany(orphanRemoval = true, mappedBy = "project")
    @JsonIgnore
    private List<Action> actions = new ArrayList<>();

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Link getActionsUrl() {
        return new Link(getUrl().getHref() + "/actions");
    }

    @Override
    public String name() {
        return displayName;
    }
}
