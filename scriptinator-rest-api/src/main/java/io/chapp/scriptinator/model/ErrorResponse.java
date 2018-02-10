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

import org.springframework.http.HttpStatus;

import java.util.Date;

public class ErrorResponse {
    private final Date timestamp;
    private final int status;
    private final String error;
    private final String exception;
    private final String message;
    private final String path;

    public ErrorResponse(HttpStatus status, Exception exception, String path) {
        this(new Date(), status.value(), status.getReasonPhrase(), exception.getClass().getName(), exception.getMessage(), path);
    }

    public ErrorResponse(Date timestamp, int status, String error, String exception, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.exception = exception;
        this.message = message;
        this.path = path;
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
}
