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

import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.repositories.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProjectService extends AbstractEntityService<Project, ProjectRepository> {
    public Project getOwnedBy(String username, long id) {
        return getRepository().findOneByOwnerUsernameAndId(
                username,
                id
        )
                .orElseThrow(() -> noSuchElement(id));
    }

    public Project getOwnedByPrincipal(long id) {
        return getOwnedBy(
                DataServiceUtils.getPrincipalName(),
                id
        );
    }

    public Project getOwnedBy(String username, String name) {
        return getRepository().findOneByOwnerUsernameAndName(
                username,
                name
        )
                .orElseThrow(() -> noSuchElement(name));
    }

    public Project getOwnedByPrincipal(String name) {
        return getOwnedBy(
                DataServiceUtils.getPrincipalName(),
                name
        );
    }


    public void deleteIfOwnedBy(String username, long id) {
        getRepository().deleteAllByOwnerUsernameAndId(username, id);
    }

    public Page<Project> findAllOwnedBy(String username, Pageable pageable) {
        return getRepository().findAllByOwnerUsername(username, pageable);
    }

    public Page<Project> findAllOwnedByPrincipal(Pageable pageable) {
        return findAllOwnedBy(DataServiceUtils.getPrincipalName(), pageable);
    }

    public void deleteIfOwnedByPrincipal(long id) {
        deleteIfOwnedBy(DataServiceUtils.getPrincipalName(), id);
    }


}
