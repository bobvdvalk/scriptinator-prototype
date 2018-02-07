/*
 * Copyright © 2018 Thomas Biesaart (thomas.biesaart@gmail.com)
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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProjectService extends AbstractEntityService<Project, ProjectRepository> {
    public Optional<Project> find(String name) {
        return getRepository().findOneByName(name);
    }

    public Project get(String projectName) {
        return this.find(projectName).orElseThrow(NoSuchElementException::new);
    }

    public Page<Project> get(String username, PageRequest pageRequest) {
        return getRepository().findByOwnerUsername(username, pageRequest);
    }
}
