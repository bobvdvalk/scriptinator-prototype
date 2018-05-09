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
package io.chapp.scriptinator.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import static io.chapp.scriptinator.security.ApiScope.*;

@Entity
public class OAuthApp extends AbstractEntity {
    public static final String ATTRIBUTE = "oauthApp";
    public static final String LIST_ATTRIBUTE = "oauthApps";

    @NotNull
    @Length(max = 80)
    private String name;
    @NotNull
    @Length(min = 30, max = 50)
    private String clientId;
    @NotNull
    @Length(min = 30, max = 50)
    private String clientSecret;
    @NotNull
    @ManyToOne(optional = false)
    private User owner;
    @NotNull
    private String description = "";

    // Default to read-only scopes.
    @NotNull
    private String[] scopes = new String[]{
            JOB_READ.toString(),
            PROJECT_READ.toString(),
            SCRIPT_READ.toString(),
            SCHEDULE_READ.toString()
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getScopes() {
        return scopes;
    }

    public void setScopes(String[] scopes) {
        this.scopes = scopes;
    }

    @Override
    public Link getUrl() {
        return new Link("/apps/" + getId());
    }
}
