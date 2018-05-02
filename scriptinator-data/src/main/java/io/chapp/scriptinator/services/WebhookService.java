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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WebhookService extends AbstractEntityService<Webhook, WebhookRepository> {
    @Override
    public Webhook create(Webhook entity) {
        entity.setUuid(UUID.randomUUID().toString());
        return super.create(entity);
    }

    public Webhook getOwnedBy(String username, long webhookId) {
        return getRepository().findByProjectOwnerUsernameAndId(username, webhookId)
                .orElseThrow(() -> noSuchElement(webhookId));
    }

    public Webhook getOwnedByPrincipal(long webhookId) {
        return getOwnedBy(DataServiceUtils.getPrincipalName(), webhookId);
    }

    public Page<Webhook> findAllForProjectOwnedBy(String username, String projectName, Pageable pageable) {
        return getRepository().findAllByProjectOwnerUsernameAndProjectName(username, projectName, pageable);
    }

    public Page<Webhook> findAllForProjectOwnedByPrincipal(String projectName, Pageable pageable) {
        return findAllForProjectOwnedBy(DataServiceUtils.getPrincipalName(), projectName, pageable);
    }

    public Webhook getByUuid(String uuid) {
        return getRepository().findByUuid(uuid).orElseThrow(() -> noSuchElement(uuid));
    }

    public void deleteIfOwnedBy(String username, long webhookId) {
        getRepository().deleteAllByProjectOwnerUsernameAndId(username, webhookId);
    }

    public void deleteIfOwnedByPrincipal(long webhookId) {
        deleteIfOwnedBy(DataServiceUtils.getPrincipalName(), webhookId);
    }
}
