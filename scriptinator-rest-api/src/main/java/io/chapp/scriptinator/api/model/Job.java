package io.chapp.scriptinator.api.model;

import java.util.Date;

public class Job {
    private String displayName;
    private Status status;

    private Date queuedTime;
    private Date startedTime;
    private Date finishedTime;

    private Script script;
    private Project project;

    public Job(String displayName, Status status, Date queuedTime, Date startedTime, Date finishedTime, Script script,
               Project project) {
        this.displayName = displayName;
        this.status = status;
        this.queuedTime = queuedTime;
        this.startedTime = startedTime;
        this.finishedTime = finishedTime;
        this.script = script;
        this.project = project;
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

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public enum Status {
        QUEUED,
        RUNNING,
        FAILED,
        FINISHED
    }
}
