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
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.Date;

public class ErrorResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorResponse.class);
    private final Date timestamp;
    private final int status;
    private final String error;
    private final String exception;
    @JsonIgnore
    private final Exception realException;
    private final String message;
    private final String path;

    public ErrorResponse(HttpStatus status, Exception exception, String path) {
        this(new Date(), status.value(), status.getReasonPhrase(), exception.getClass().getName(), exception, exception.getMessage(), path);
    }

    public ErrorResponse(Date timestamp, int status, String error, String exception, Exception realException, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.exception = exception;
        this.message = message;
        this.path = path;
        this.realException = realException;


        LOGGER.debug("Api Error: {}", this);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getException() {
        return exception;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("timestamp", timestamp)
                .append("status", status)
                .append("error", error)
                .append("exception", exception)
                .append("message", message)
                .append("path", path)
                .append("trace", ExceptionUtils.getStackTrace(realException))
                .toString();
    }
}
