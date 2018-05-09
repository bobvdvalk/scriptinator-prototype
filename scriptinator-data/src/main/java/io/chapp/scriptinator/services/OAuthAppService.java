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

import io.chapp.scriptinator.model.OAuthApp;
import io.chapp.scriptinator.repositories.OAuthAppRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OAuthAppService extends AbstractEntityService<OAuthApp, OAuthAppRepository> {
    private static final int SECRET_LENGTH = 30;

    public Page<OAuthApp> findAllOwnedByPrincipal(PageRequest pageRequest) {
        return findAllOwnedBy(
                DataServiceUtils.getPrincipalName(),
                pageRequest
        );
    }

    public Page<OAuthApp> findAllOwnedBy(String username, PageRequest pageRequest) {
        return getRepository().findAllByOwnerUsername(username, pageRequest);
    }

    public void deleteIfOwnedByPrincipal(long id) {
        deleteIfOwnedBy(
                DataServiceUtils.getPrincipalName(),
                id
        );
    }

    public void deleteIfOwnedBy(String username, long id) {
        getRepository().deleteByOwnerUsernameAndId(username, id);
    }

    @Override
    public OAuthApp create(OAuthApp entity) {
        entity.setClientId(UUID.randomUUID().toString().toLowerCase());
        entity.setClientSecret(RandomStringUtils.randomAlphanumeric(SECRET_LENGTH));
        return super.create(entity);
    }

    public OAuthApp getOwnedByPrincipal(long appId) {
        return getOwnedBy(
                DataServiceUtils.getPrincipalName(),
                appId
        );
    }

    public OAuthApp getOwnedBy(String username, long appId) {
        return getRepository()
                .findOneByOwnerUsernameAndId(username, appId)
                .orElseThrow(() -> noSuchElement(appId));
    }
}
