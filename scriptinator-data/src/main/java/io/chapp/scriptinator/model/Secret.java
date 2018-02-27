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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "secret_name",
        columnNames = {"project_id", "name"}
))
public class Secret extends AbstractEntity {
    public static final String ATTRIBUTE = "secret";
    public static final String LIST_ATTRIBUTE = "secrets";

    @NotNull
    @Pattern(regexp = "[a-z][\\w-]*", flags = {Pattern.Flag.CASE_INSENSITIVE})
    private String name;

    @NotNull
    private String description = "";

    @NotNull
    @Lob
    private String value = "";

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public Link getUrl() {
        return null;
    }
}
