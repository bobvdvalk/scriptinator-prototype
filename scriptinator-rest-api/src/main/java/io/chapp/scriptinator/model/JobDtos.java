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

import java.util.Date;

public class JobDtos {
    private String displayName;
    private Status status;
    private Date queuedTime;
    private Date startedTime;
    private Date finishedTime;
    private ScriptDtos scriptDtos;
    private ProjectDtos projectDtos;

    public static JobDtos convert(Job job) {
        Status status = JobDtos.Status.valueOf(job.getStatus().toString());
        return new JobDtos(job.getDisplayName(), status, job.getQueuedTime(), job.getStartedTime(),
                job.getFinishedTime(), ScriptDtos.convert(job.getScript()) ,ProjectDtos.convert(job.getProject()));
    }

    public JobDtos(String displayName, Status status, Date queuedTime, Date startedTime, Date finishedTime, ScriptDtos scriptDtos,
                   ProjectDtos projectDtos) {
        this.displayName = displayName;
        this.status = status;
        this.queuedTime = queuedTime;
        this.startedTime = startedTime;
        this.finishedTime = finishedTime;
        this.scriptDtos = scriptDtos;
        this.projectDtos = projectDtos;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getQueuedTime() {
        return queuedTime;
    }

    public void setQueuedTime(Date queuedTime) {
        this.queuedTime = queuedTime;
    }

    public Date getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(Date startedTime) {
        this.startedTime = startedTime;
    }

    public Date getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(Date finishedTime) {
        this.finishedTime = finishedTime;
    }

    public ScriptDtos getScriptDtos() {
        return scriptDtos;
    }

    public void setScriptDtos(ScriptDtos scriptDtos) {
        this.scriptDtos = scriptDtos;
    }

    public ProjectDtos getProjectDtos() {
        return projectDtos;
    }

    public void setProjectDtos(ProjectDtos projectDtos) {
        this.projectDtos = projectDtos;
    }

    public enum Status {
        QUEUED,
        RUNNING,
        FAILED,
        FINISHED
    }
}
