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
package io.chapp.scriptinator.webcontrollers;

import io.chapp.scriptinator.services.ScheduleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("explain")
public class ExplainController {
    private final ScheduleService scheduleService;

    public ExplainController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("cron")
    @ResponseBody
    public Map<String, String> explainCron(ServletRequest request) {
        String cronString = request.getParameter("cron");
        if (cronString == null) {
            throw new IllegalArgumentException("Request body must contain a 'cron' property.");
        }

        return Collections.singletonMap(
                "description", scheduleService.getCronDescription(cronString)
        );
    }
}
