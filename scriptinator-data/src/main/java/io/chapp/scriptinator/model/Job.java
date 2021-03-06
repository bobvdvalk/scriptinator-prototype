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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class Job extends AbstractEntity {
    public static final String ATTRIBUTE = "job";
    public static final String LIST_ATTRIBUTE = "jobs";
    public static final String EXECUTE_JOB_QUEUE = "execute_job";

    private String displayName;

    @NotNull
    private Status status;

    @NotNull
    private Date queuedTime;

    private Date startedTime;

    private Date finishedTime;

    @ManyToOne(optional = false)
    @JsonIgnore
    private Script script;

    @NotNull
    @Lob
    private String output = "";

    @Lob
    private String argument;

    private Long triggeredByJobId;

    private Long triggeredByScheduleId;

    private Long triggeredByWebhookId;

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

    public Link getScriptUrl() {
        return getScript().getUrl();
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    /**
     * Append a message to the output.
     *
     * @param level   The log level.
     * @param message The message.
     */
    public void log(String level, String message) {
        output += String.format("%s - [%s]: %s%n", new Date(), level, message);
    }

    public String getArgument() {
        return argument;
    }

    @JsonSetter
    public void setArgument(Object argument) {
        try {
            this.argument = new ObjectMapper().writeValueAsString(argument);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public Long getTriggeredByJobId() {
        return triggeredByJobId;
    }

    public void setTriggeredByJobId(Long triggeredByJobId) {
        this.triggeredByJobId = triggeredByJobId;
    }

    public Long getTriggeredByScheduleId() {
        return triggeredByScheduleId;
    }

    public void setTriggeredByScheduleId(Long triggeredByScheduleId) {
        this.triggeredByScheduleId = triggeredByScheduleId;
    }

    public Long getTriggeredByWebhookId() {
        return triggeredByWebhookId;
    }

    public void setTriggeredByWebhookId(Long triggeredByWebhookId) {
        this.triggeredByWebhookId = triggeredByWebhookId;
    }

    @Override
    public Link getUrl() {
        return new Link("/jobs/" + this.getId());
    }

    public enum Status {
        QUEUED,
        RUNNING,
        FAILED,
        FINISHED
    }
}
