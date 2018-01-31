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

import java.util.ArrayList;
import java.util.List;

public class ProjectDtos {
    private String projectName;
    private String description;
    private OwnerDtos ownerDtos;
    private List<ScriptDtos> scriptDtos;
    private List<JobDtos> jobDtos;

    public static ProjectDtos convert(Project project) {
        List<JobDtos> jobDtos = new ArrayList<>();
        for(Job job : project.getJobs()) {
            jobDtos.add(JobDtos.convert(job));
        }

        List<ScriptDtos> scriptDtos = new ArrayList<>();
        for(Script script : project.getScripts()) {
            scriptDtos.add(ScriptDtos.convert(script));
        }
        return new ProjectDtos(project.getDisplayName(), project.getDescription(), OwnerDtos.convert(project.getOwner()), scriptDtos, jobDtos);
    }

    public ProjectDtos(String projectName, String description, OwnerDtos ownerDtos, List<ScriptDtos> scriptDtos, List<JobDtos> jobDtos) {
        this.projectName = projectName;
        this.description = description;
        this.ownerDtos = ownerDtos;
        this.scriptDtos = scriptDtos;
        this.jobDtos = jobDtos;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getDescription() {
        return description;
    }

    public OwnerDtos getOwnerDtos() {
        return ownerDtos;
    }

    public List<ScriptDtos> getScriptDtos() {
        return scriptDtos;
    }

    public List<JobDtos> getJobDtos() {
        return jobDtos;
    }
}
