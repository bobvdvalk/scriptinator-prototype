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

import io.chapp.scriptinator.model.Webhook;
import io.chapp.scriptinator.repositories.WebhookRepository;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

public class WebhookServiceTest {
    private final WebhookService webhookService = new WebhookService();

    @BeforeClass
    public void mockWebhookRepository() {
        WebhookRepository webhookRepository = Mockito.mock(WebhookRepository.class);
        when(webhookRepository.save(any())).thenAnswer(invocation -> invocation.getArgumentAt(0, Webhook.class));
        webhookService.setRepository(webhookRepository);
    }

    @Test
    public void testCreateWebhookSetsRandomUuid() {
        Webhook webhook = new Webhook();
        webhook.setScriptName("hooked");
        webhook = webhookService.create(webhook);

        assertNotNull(webhook.getUuid());
        assertNotEquals(webhook.getUuid(), "");
    }
}
