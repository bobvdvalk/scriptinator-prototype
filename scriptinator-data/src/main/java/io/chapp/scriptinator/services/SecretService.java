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

import io.chapp.scriptinator.model.Secret;
import io.chapp.scriptinator.repositories.SecretRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SecretService extends AbstractEntityService<Secret, SecretRepository> {

    public Optional<Secret> findOwnedBy(String username, long projectId, long id) {
        return getRepository().findByProjectOwnerUsernameAndProjectIdAndId(username, projectId, id);
    }

    public Optional<Secret> findOwnedByPrincipal(long projectId, long id) {
        return findOwnedBy(DataServiceUtils.getPrincipalName(), projectId, id);
    }

    public Secret getOwnedByPrincipal(long projectId, long id) {
        return findOwnedByPrincipal(projectId, id).orElseThrow(() -> noSuchElement(id));
    }

    public void deleteIfOwnedBy(String username, long id) {
        getRepository().deleteAllByProjectOwnerUsernameAndId(username, id);
    }

    public void deleteIfOwnedByPrincipal(long id) {
        deleteIfOwnedBy(DataServiceUtils.getPrincipalName(), id);
    }

    /**
     * Mask secrets in a string.
     *
     * @param projectId The id of the project to read the secret values from.
     * @param value     The string value to mask
     * @return The masked string value.
     */
    public String filterSecrets(long projectId, String value) {
        List<Secret> secrets = getRepository().findAllByProjectId(projectId);
        for (Secret secret : secrets) {
            value = StringUtils.replaceIgnoreCase(value, secret.getValue(), "******");
        }
        return value;
    }

    /**
     * Get the value of a secret.
     *
     * @param projectId The id of the project containing the secret.
     * @param name      The name of the secret.
     * @return The value of the secret, or null if none was found.
     */
    public String getValue(long projectId, String name) {
        return getRepository().findOneByProjectIdAndName(projectId, name)
                .map(Secret::getValue)
                .orElse(null);
    }
}
