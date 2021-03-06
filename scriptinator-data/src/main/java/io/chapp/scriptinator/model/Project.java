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
package io.chapp.scriptinator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "project_name",
        columnNames = {"name"}
))
public class Project extends AbstractEntity {
    public static final String ATTRIBUTE = "project";
    public static final String LIST_ATTRIBUTE = "projects";

    @NotNull
    @Pattern(regexp = "[a-z][\\w-]*", flags = {Pattern.Flag.CASE_INSENSITIVE})
    private String name;

    private String description;

    @ManyToOne(optional = false)
    private User owner;

    @NotNull
    @OneToMany(mappedBy = "project", orphanRemoval = true)
    @JsonIgnore
    private List<Script> scripts = new ArrayList<>();

    @NotNull
    @OneToMany(mappedBy = "project", orphanRemoval = true)
    @JsonIgnore
    private List<Schedule> schedules = new ArrayList<>();

    @NotNull
    @OneToMany(mappedBy = "project", orphanRemoval = true)
    @JsonIgnore
    private List<Secret> secrets = new ArrayList<>();

    @NotNull
    @OneToMany(mappedBy = "project", orphanRemoval = true)
    @JsonIgnore
    private List<Webhook> webhooks = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public void setScripts(List<Script> scripts) {
        this.scripts = scripts;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public List<Secret> getSecrets() {
        return secrets;
    }

    public void setSecrets(List<Secret> secrets) {
        this.secrets = secrets;
    }

    public List<Webhook> getWebhooks() {
        return webhooks;
    }

    public void setWebhooks(List<Webhook> webhooks) {
        this.webhooks = webhooks;
    }

    public Link getScriptsUrl() {
        return new Link('/' + LIST_ATTRIBUTE + '/' + name + "/scripts");
    }

    @Override
    public Link getUrl() {
        return new Link('/' + LIST_ATTRIBUTE + '/' + name);
    }

    public Link getSchedulesUrl() {
        return new Link('/' + LIST_ATTRIBUTE + '/' + name + "/schedules");
    }
}
