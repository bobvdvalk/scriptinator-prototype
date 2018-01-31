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
package io.chapp.scriptinator.model;

import java.util.List;

public class OwnerDtos {
    private String username;
    private String displayName;
    private String avatarUrl;
    private List<ProjectDtos> projectDtos;

    public static OwnerDtos convert(User user) {
        return new OwnerDtos(user.getUsername(), user.getDisplayName(), user.getAvatarUrl(), ProjectDtos.convert(user.getProjects()));
    }

    public OwnerDtos(String username, String displayName, String avatarUrl, List<ProjectDtos> projectDtos) {
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.projectDtos = projectDtos;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<ProjectDtos> getProjectDtos() {
        return projectDtos;
    }

    public void setProjectDtos(List<ProjectDtos> projectDtos) {
        this.projectDtos = projectDtos;
    }

}
