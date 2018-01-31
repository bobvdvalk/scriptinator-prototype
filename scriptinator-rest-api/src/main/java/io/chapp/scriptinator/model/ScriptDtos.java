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

import java.util.List;

public class ScriptDtos {
    private String filename;
    private String description;
    private String code;
    private ProjectDtos projectDtos;
    private List<JobDtos> jobDtos;

    public static ScriptDtos convert(Script script) {
        return null;
    }

    public ScriptDtos(String filename, String description, String code, ProjectDtos projectDtos, List<JobDtos> jobDtos) {
        this.filename = filename;
        this.description = description;
        this.code = code;
        this.projectDtos = projectDtos;
        this.jobDtos = jobDtos;
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

    public ProjectDtos getProjectDtos() {
        return projectDtos;
    }

    public void setProjectDtos(ProjectDtos projectDtos) {
        this.projectDtos = projectDtos;
    }

    public List<JobDtos> getJobDtos() {
        return jobDtos;
    }

    public void setJobDtos(List<JobDtos> jobDtos) {
        this.jobDtos = jobDtos;
    }
}
