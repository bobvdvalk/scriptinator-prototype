package io.chapp.scriptinator.api.model;

import java.util.List;

public class Owner {
    private String username;
    private String displayName;
    private String avatarUrl;
    private List<Project> projects;

    public Owner(String username, String displayName, String avatarUrl, List<Project> projects) {
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.projects = projects;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
