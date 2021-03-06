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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User extends AbstractEntity {
    public static final String ATTRIBUTE = "user";
    public static final String LIST_ATTRIBUTE = "users";
    @Pattern(regexp = "[\\w.-]{4,}")
    @Column(unique = true)
    private String username;

    @NotNull
    @NotEmpty
    private String password;

    private String displayName;
    @Email
    private String email;
    private String avatarUrl;

    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    @NotNull
    @JsonIgnore
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    @NotNull
    @JsonIgnore
    private List<OAuthApp> apps = new ArrayList<>();

    @JsonIgnore
    private String emailActivationToken;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        if (StringUtils.isEmpty(displayName)) {
            return username;
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        if (StringUtils.isEmpty(avatarUrl) && !StringUtils.isEmpty(email)) {
            return "https://www.gravatar.com/avatar/" + DigestUtils.md5Hex(
                    email.toLowerCase().trim()
            );
        }
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public Link getProjectsUrl() {
        return new Link(getUrl().getHref() + "/projects");
    }

    public List<OAuthApp> getApps() {
        return apps;
    }

    public void setApps(List<OAuthApp> apps) {
        this.apps = apps;
    }

    public String getEmailActivationToken() {
        return emailActivationToken;
    }

    public void setEmailActivationToken(String emailActivationToken) {
        this.emailActivationToken = emailActivationToken;
    }

    @Override
    public Link getUrl() {
        return new Link("/users/" + username);
    }
}
