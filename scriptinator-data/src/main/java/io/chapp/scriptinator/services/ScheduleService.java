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
package io.chapp.scriptinator.services;

import io.chapp.scriptinator.model.Schedule;
import io.chapp.scriptinator.repositories.ScheduleRepository;
import net.redhogs.cronparser.CronExpressionDescriptor;
import net.redhogs.cronparser.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class ScheduleService extends AbstractEntityService<Schedule, ScheduleRepository> {
    private static final Logger LOGGER = Logger.getLogger(ScheduleService.class);
    private static final Options CRON_DESCRIPTION_OPTIONS;

    static {
        // Set the cron parsing options according to how the Quartz cron expression operates.
        Options options = Options.twentyFourHour();
        options.setZeroBasedDayOfWeek(false);
        CRON_DESCRIPTION_OPTIONS = options;
    }

    public List<Schedule> getRunnableSchedules(Date now) {
        return getRepository().findAllByEnabledIsTrueAndNextRunBefore(now);
    }

    public Page<Schedule> get(String projectName, PageRequest request) {
        return getRepository().findAllByProjectName(projectName, request);
    }

    @Override
    public Schedule create(Schedule entity) {
        setNextRun(entity);
        entity.setCronString(sanitizeCronString(entity.getCronString()));
        return super.create(entity);
    }

    @Override
    public Schedule update(Schedule entity) {
        setNextRun(entity);
        entity.setCronString(sanitizeCronString(entity.getCronString()));
        return super.update(entity);
    }

    private void setNextRun(Schedule schedule) {
        // Try to parse the cron expression.
        // Format documentation: http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html
        try {
            CronExpression cron = new CronExpression(schedule.getCronString());
            schedule.setNextRun(cron.getNextValidTimeAfter(now()));
        } catch (Exception e) {
            // This can actually throw any exception.
            LOGGER.error("Could not parse cron expression: '" + schedule.getCronString() + "' for script '" + schedule.toString() + "'.", e);
            schedule.setStatus(Schedule.Status.INVALID_CRON);
        }
    }

    public Date now() {
        return Date.from(Instant.now(Clock.systemUTC()));
    }

    /**
     * Sanitize a cron string.
     * This trims all whitespaces, and replaces multiple whitespaces by a single one.
     *
     * @param cronString The cron string to sanitize.
     * @return The sanitized cron string.
     */
    private String sanitizeCronString(String cronString) {
        return cronString.trim().replaceAll("\\s+", " ");
    }

    /**
     * Get the human-readable description for a cron string.
     *
     * @param cronString The cron string to describe.
     * @return A description of the cron string.
     */
    public String getCronDescription(String cronString) {
        if (cronString == null || cronString.isEmpty()) {
            return "";
        }

        cronString = sanitizeCronString(cronString);

        // Validate the cron expression.
        if (!CronExpression.isValidExpression(cronString)) {
            return "Invalid cron expression";
        }

        // Try to get the description.
        try {
            String description = CronExpressionDescriptor.getDescription(cronString, CRON_DESCRIPTION_OPTIONS);

            // Lowercase the first letter of the description sentence.
            // This is used instead of the lowercase parse option, because that also lowercases month names.
            return "Runs " + StringUtils.uncapitalize(description);
        } catch (Exception e) {
            // This can actually throw any exception.
            LOGGER.trace("Could not get cron expression description for: '" + cronString + "'.", e);
            return "Invalid cron expression";
        }
    }
}
