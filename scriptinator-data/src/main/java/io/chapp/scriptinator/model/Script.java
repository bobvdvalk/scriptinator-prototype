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

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "script_name",
        columnNames = {"project_id", "name"}
))
public class Script extends AbstractEntity {
    public static final String ATTRIBUTE = "script";
    public static final String LIST_ATTRIBUTE = "scripts";

    @NotNull
    @Pattern(regexp = "[a-z][\\w-]*", flags = {Pattern.Flag.CASE_INSENSITIVE})
    private String name;

    @NotNull
    private String description;

    @NotNull
    @Lob
    private String code;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @NotNull
    @OneToMany(mappedBy = "script", orphanRemoval = true)
    @JsonIgnore
    private List<Job> jobs = new ArrayList<>();

    /**
     * Get the full script name constructed from the project and script name, i.e.: "project/script".
     *
     * @return The full script name.
     */
    @Override
    public String toString() {
        return project.getName() + '/' + name;
    }

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

    public Link getJobsUrl() {
        return new Link("/scripts/" + getId() + "/jobs");
    }

    @Override
    public Link getUrl() {
        return new Link("/scripts/" + getId());
    }
}
