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
package io.chapp.scriptinator.utils;

import io.chapp.scriptinator.ScriptinatorConfiguration;
import io.chapp.scriptinator.security.ApiScope;
import okhttp3.Headers;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.*;
import java.util.stream.Collectors;

import static io.chapp.scriptinator.utils.ScriptinatorTestCase.DEFAULT_CLIENT_ID;
import static io.chapp.scriptinator.utils.ScriptinatorTestCase.DEFAULT_USERNAME;

@Configuration
public class OkHTTPClientConfig {
    private final ApplicationContext context;

    public OkHTTPClientConfig(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    Headers accessToken() {
        String token = generateFakeToken(Arrays.asList(ApiScope.values()));
        return new Headers.Builder()
                .add("Authorization", "Bearer " + token)
                .build();
    }


    public String generateFakeToken(Collection<ApiScope> scopes) {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(
                context.getBean(ScriptinatorConfiguration.class).getSigningKey()
        );

        DefaultOAuth2AccessToken unencodedToken = new DefaultOAuth2AccessToken("unit test jonguh");
        unencodedToken.setExpiration(new Date(System.currentTimeMillis() + 10000000));
        Map<String, Object> map = new HashMap<>();
        map.put("user_name", DEFAULT_USERNAME);
        map.put("scope", scopes);
        unencodedToken.setAdditionalInformation(map);

        return converter.enhance(
                unencodedToken,
                new OAuth2Authentication(
                        new OAuth2Request(
                                Collections.emptyMap(),
                                DEFAULT_CLIENT_ID,
                                Collections.emptySet(),
                                true,
                                scopes.stream().map(Object::toString).collect(Collectors.toSet()),
                                Collections.singleton(context.getBean(ScriptinatorConfiguration.class).getResourceId()),
                                "/",
                                null,
                                null
                        ),
                        new RunAsUserToken(DEFAULT_USERNAME, null, null, null, OAuth2Authentication.class)
                )
        ).getValue();
    }
}
