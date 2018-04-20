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

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "schedule_name",
        columnNames = {"project_id", "name"}
))
public class Schedule extends AbstractEntity {
    public static final String ATTRIBUTE = "schedule";
    public static final String LIST_ATTRIBUTE = "schedules";

    @NotNull
    @Pattern(regexp = "[a-z][\\w-]*", flags = {Pattern.Flag.CASE_INSENSITIVE})
    private String name;

    @NotNull
    private String description = "";

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @NotEmpty
    private String scriptName;

    @Lob
    private String argument;

    @NotNull
    private String cronString = "";

    private boolean enabled;

    private Date nextRun;

    private Status status = Status.SUCCESS;

    private Date lastRun;

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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getArgument() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public String getCronString() {
        return cronString;
    }

    public void setCronString(String cronString) {
        this.cronString = cronString;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getNextRun() {
        return nextRun;
    }

    public void setNextRun(Date nextRun) {
        this.nextRun = nextRun;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;

        // Disable invalid schedules.
        if (status != Status.SUCCESS) {
            enabled = false;
        }
    }

    public Date getLastRun() {
        return lastRun;
    }

    public void setLastRun(Date lastRun) {
        this.lastRun = lastRun;
    }

    @Override
    public Link getUrl() {
        return new Link("/schedules/" + getId());
    }

    public enum Status {
        SUCCESS,
        INVALID_CRON,
        INVALID_SCRIPT
    }
}
