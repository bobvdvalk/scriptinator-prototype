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

import io.chapp.scriptinator.repositories.ScheduleRepository;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ScheduleServiceTest {
    private final ScheduleService scheduleService = new ScheduleService();

    @BeforeClass
    public void mockScheduleRepository() {
        scheduleService.setRepository(Mockito.mock(ScheduleRepository.class));
    }

    @Test
    public void testGetCronDescriptionNullOrEmpty() {
        assertEquals(scheduleService.getCronDescription(null), "");
        assertEquals(scheduleService.getCronDescription(""), "");
    }

    @Test
    public void testGetCronDescriptionInvalid() {
        assertEquals(scheduleService.getCronDescription("nope"), "Invalid cron expression");
    }

    @Test
    public void testGetCronDescription() {
        assertEquals(scheduleService.getCronDescription("0 30 0/2 * * ?"), "Runs at 30 minutes past the hour, every 2 hours");
    }

    @Test
    public void testGetCronDescriptionIsSanitized() {
        // The cron-parser library doesn't like excessive whitespaces.
        assertEquals(scheduleService.getCronDescription("  0 0   * *    DEC ?  "), "Runs every hour, only in December");
    }
}
