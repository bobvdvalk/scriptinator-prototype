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

import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService extends AbstractEntityService<User, UserRepository> {
    public User getByUsername(String username) {
        return getRepository()
                .findByUsername(username)
                .orElseThrow(() -> noSuchElement(username));
    }

    public Page<User> getAllOwnedBy(String username) {
        return new PageImpl<>(Collections.singletonList(getByUsername(username)));
    }

    public Page<User> getAllOwnedByPrincipal() {
        return getAllOwnedBy(DataServiceUtils.getPrincipalName());
    }

    public boolean exists(String username) {
        return getRepository().findByUsername(username).isPresent();
    }

    public Optional<User> getByToken(String activationToken) {
        return getRepository().findByEmailActivationToken(activationToken);
    }
}
