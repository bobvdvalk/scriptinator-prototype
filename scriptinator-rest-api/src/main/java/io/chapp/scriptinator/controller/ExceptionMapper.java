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
package io.chapp.scriptinator.controller;

import io.chapp.scriptinator.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ExceptionMapper {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(NoSuchElementException e, HttpServletRequest request) {
        return handle(e, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(Exception e, HttpServletRequest request, HttpStatus status) {
        return new ResponseEntity<>(
                createResponse(e, request, status),
                status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status
        );
    }

    private ErrorResponse createResponse(Exception e, HttpServletRequest request, HttpStatus status) {
        return new ErrorResponse(
                status,
                e,
                request.getRequestURI()
        );
    }
}
