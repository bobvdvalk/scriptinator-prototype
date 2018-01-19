package io.chapp.scriptinator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(indexes = @Index(columnList = "status"))
public class Job extends AbstractEntity {
    public static final String NEW_JOB_QUEUE = "scriptinator-new-jobs";
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnore
    private Action action;
    @NotNull
    private Status status;
    private Date queuedOn;
    private Date startedOn;
    private Date finishedOn;
    private String worker;
    @NotNull
    @Lob
    private String output = "";

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Link getActionUrl() {
        return getAction().getUrl();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void changeStatus(Status status) {
        setStatus(status);
        switch (status) {
            case QUEUED:
                setQueuedOn(new Date());
                break;
            case RUNNING:
                setStartedOn(new Date());
                break;
            case DONE:
            case FAILED:
                setFinishedOn(new Date());
                break;
        }
    }

    public Date getQueuedOn() {
        return queuedOn;
    }

    public void setQueuedOn(Date queuedOn) {
        this.queuedOn = queuedOn;
    }

    public Date getStartedOn() {
        return startedOn;
    }

    public void setStartedOn(Date startedOn) {
        this.startedOn = startedOn;
    }

    public Date getFinishedOn() {
        return finishedOn;
    }

    public void setFinishedOn(Date finishedOn) {
        this.finishedOn = finishedOn;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    @Override
    public String name() {
        return getAction().getDisplayName() + "#" + getId();
    }

    @JsonIgnore
    public boolean isFinished() {
        return status == Status.DONE || status == Status.FAILED;
    }

    @JsonIgnore
    public boolean isSuccessful() {
        return status == Status.DONE;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public void log(LogEntry entry) {
        output += entry.getMessage() + "\n";
    }

    public enum Status {
        QUEUED,
        RUNNING,
        DONE,
        FAILED
    }
}
