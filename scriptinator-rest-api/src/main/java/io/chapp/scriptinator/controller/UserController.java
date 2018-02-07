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
package io.chapp.scriptinator.controller;

import io.chapp.scriptinator.model.Link;
import io.chapp.scriptinator.model.PageResult;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.services.ProjectService;
import io.chapp.scriptinator.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;
    private final ProjectService projectService;

    public UserController(UserService userService, ProjectService projectService) {
        this.userService = userService;
        this.projectService = projectService;
    }

    @GetMapping("{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getByUsername(username);
    }

    @GetMapping("{username}/projects")
    public PageResult<Project> getUserProjects(@PathVariable String username, HttpServletRequest request) {
        Page<Project> result = projectService.get(username, PageResult.request(request));

        return PageResult.of(
                new Link("/users/" + username + "/projects"),
                result
        );
    }
}
