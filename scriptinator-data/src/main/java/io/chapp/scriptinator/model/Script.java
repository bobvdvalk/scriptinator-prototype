/*
 * Copyright © 2018 Thomas Biesaart (thomas.biesaart@gmail.com)
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "script_name",
        columnNames = {"project_id", "fully_qualified_name"}
))
public class Script extends AbstractEntity {
    public static final String ATTRIBUTE = "script";
    public static final String LIST_ATTRIBUTE = "scripts";

    @NotNull
    @Pattern(regexp = "[\\w]+")
    @Column(name = "fully_qualified_name")
    private String fullyQualifiedName;

    @NotNull
    private String description;

    @NotNull
    @Lob
    private String code;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @NotNull
    @OneToMany(mappedBy = "project", orphanRemoval = true)
    private List<Job> jobs = new ArrayList<>();

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public void setFullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
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
