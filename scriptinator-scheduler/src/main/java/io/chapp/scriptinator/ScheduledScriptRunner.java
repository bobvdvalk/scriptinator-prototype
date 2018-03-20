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
package io.chapp.scriptinator;

import io.chapp.scriptinator.model.Schedule;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.repositories.ScheduleRepository;
import io.chapp.scriptinator.repositories.ScriptRepository;
import io.chapp.scriptinator.services.ScheduleService;
import io.chapp.scriptinator.services.ScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class ScheduledScriptRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledScriptRunner.class);

    private final ScriptRepository scriptRepository;
    private final ScriptService scriptService;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleService scheduleService;
    private final ScriptinatorSchedulerConfiguration configuration;

    private long lastProcessTime;

    public ScheduledScriptRunner(ScriptRepository scriptRepository, ScriptService scriptService, ScheduleRepository scheduleRepository, ScheduleService scheduleService, ScriptinatorSchedulerConfiguration configuration) {
        this.scriptRepository = scriptRepository;
        this.scriptService = scriptService;
        this.scheduleRepository = scheduleRepository;
        this.scheduleService = scheduleService;
        this.configuration = configuration;
    }


    @Scheduled(fixedRate = 1000)
    public void tick() {
        long epochSeconds = System.currentTimeMillis() / 1000;

        // Check if the last process time was longer ago than the configured interval.
        if (epochSeconds - lastProcessTime > configuration.getSchedulerInterval()) {
            processSchedules();
            lastProcessTime = epochSeconds;
        }
    }

    private void processSchedules() {
        Date now = scheduleService.now();
        List<Schedule> schedules = scheduleRepository.findAllByEnabledIsTrueAndNextRunBefore(now);

        LOGGER.debug("Running {} schedules...", schedules.size());
        schedules.forEach(schedule -> runSchedule(now, schedule));
    }

    private void runSchedule(Date now, Schedule schedule) {
        schedule.setLastRun(now);

        // Try to get the script.
        Optional<Script> script = scriptRepository.findByProjectOwnerUsernameAndProjectNameAndName(
                schedule.getProject().getOwner().getUsername(),
                schedule.getProject().getName(),
                schedule.getScriptName()
        );

        if (!script.isPresent()) {
            schedule.setStatus(Schedule.Status.INVALID_SCRIPT);
            scheduleService.update(schedule);
            return;
        }

        // Update the schedule, run the script.
        schedule = scheduleService.update(schedule);
        scriptService.runScheduled(script.get(), schedule);
    }
}
