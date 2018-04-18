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

import io.chapp.scriptinator.model.Link;
import io.chapp.scriptinator.model.PageResult;
import io.chapp.scriptinator.model.Schedule;
import io.chapp.scriptinator.services.ScheduleService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    @PreAuthorize("#oauth2.hasScope('schedule:read')")
    public PageResult<Schedule> getSchedules(HttpServletRequest request) {
        Page<Schedule> result = scheduleService.findAllOwnedByPrincipal(
                PageResult.request(request)
        );

        return PageResult.of(
                new Link("/schedules"),
                result
        );
    }

    @GetMapping("{scheduleId}")
    @PreAuthorize("#oauth2.hasScope('schedule:read')")
    public Schedule getScheduleById(@PathVariable long scheduleId) {
        return scheduleService.getOwnedByPrincipal(scheduleId);
    }
}
