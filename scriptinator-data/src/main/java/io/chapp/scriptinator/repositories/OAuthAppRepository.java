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
package io.chapp.scriptinator.repositories;

import io.chapp.scriptinator.model.OAuthApp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.Optional;

public interface OAuthAppRepository extends AbstractEntityRepository<OAuthApp> {
    Optional<OAuthApp> findOneByClientId(String clientId);

    Optional<OAuthApp> findOneByOwnerUsernameAndId(String username, long id);

    Page<OAuthApp> findAllByOwnerUsername(String username, Pageable pageable);

    @Transactional
    void deleteByOwnerUsernameAndId(String username, long id);
}
