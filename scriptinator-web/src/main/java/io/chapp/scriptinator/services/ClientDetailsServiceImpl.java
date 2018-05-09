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

import io.chapp.scriptinator.ScriptinatorConfiguration;
import io.chapp.scriptinator.model.OAuthApp;
import io.chapp.scriptinator.repositories.OAuthAppRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {
    private static final int ONE_DAY_IN_SECONDS = 86400;
    private final OAuthAppRepository appRepository;
    private final ScriptinatorConfiguration configuration;

    public ClientDetailsServiceImpl(OAuthAppRepository appRepository, ScriptinatorConfiguration configuration) {
        this.appRepository = appRepository;
        this.configuration = configuration;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        return new CustomClientDetails(
                appRepository.findOneByClientId(clientId).orElseThrow(
                        () -> new ClientRegistrationException(clientId)
                )
        );
    }

    private class CustomClientDetails implements ClientDetails {
        private final OAuthApp oAuthApp;

        public CustomClientDetails(OAuthApp oAuthApp) {
            this.oAuthApp = oAuthApp;
        }

        @Override
        public String getClientId() {
            return oAuthApp.getClientId();
        }

        @Override
        public Set<String> getResourceIds() {
            return Collections.singleton(configuration.getResourceId());
        }

        @Override
        public boolean isSecretRequired() {
            return true;
        }

        @Override
        public String getClientSecret() {
            return oAuthApp.getClientSecret();
        }

        @Override
        public boolean isScoped() {
            return true;
        }

        @Override
        public Set<String> getScope() {
            return new HashSet<>(Arrays.asList(oAuthApp.getScopes()));
        }

        @Override
        public Set<String> getAuthorizedGrantTypes() {
            return Collections.singleton("implicit");
        }

        @Override
        public Set<String> getRegisteredRedirectUri() {
            return Collections.emptySet();
        }

        @Override
        public Collection<GrantedAuthority> getAuthorities() {
            return Collections.emptyList();
        }

        @Override
        public Integer getAccessTokenValiditySeconds() {
            return ONE_DAY_IN_SECONDS;
        }

        @Override
        public Integer getRefreshTokenValiditySeconds() {
            return 0;
        }

        @Override
        public boolean isAutoApprove(String s) {
            return false;
        }

        @Override
        public Map<String, Object> getAdditionalInformation() {
            return Collections.emptyMap();
        }
    }
}
