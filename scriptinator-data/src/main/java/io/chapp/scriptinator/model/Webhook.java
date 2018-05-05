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
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "webhook_name",
                columnNames = {"project_id", "name"}
        ),
        indexes = @Index(
                name = "webhook_uuid_idx",
                columnList = "uuid",
                unique = true
        )
)
public class Webhook extends AbstractEntity {
    public static final String ATTRIBUTE = "webhook";
    public static final String LIST_ATTRIBUTE = "webhooks";

    @NotNull
    private String uuid;

    @NotNull
    @Pattern(regexp = "[a-z][\\w-]*", flags = {Pattern.Flag.CASE_INSENSITIVE})
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @NotNull
    private String description = "";

    @NotEmpty
    private String scriptName;

    private Date lastCall;

    private boolean sendResponse = true;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public Date getLastCall() {
        return lastCall;
    }

    public void setLastCall(Date lastCall) {
        this.lastCall = lastCall;
    }

    public boolean getSendResponse() {
        return sendResponse;
    }

    public void setSendResponse(boolean sendResponse) {
        this.sendResponse = sendResponse;
    }

    @Override
    public Link getUrl() {
        return new Link("/webhooks/" + getId());
    }
}
