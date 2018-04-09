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
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.model.User;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Optional;

@Service
public class UserRegistrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegistrationService.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final ProjectService projectService;
    private final ScriptService scriptService;

    public UserRegistrationService(UserService userService, PasswordEncoder passwordEncoder, MailService mailService, ProjectService projectService, ScriptService scriptService) {
        this.mailService = mailService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.projectService = projectService;
        this.scriptService = scriptService;
    }


    public boolean register(User user, URI requestURI) {
        if (userService.exists(user.getUsername())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailActivationToken(RandomStringUtils.randomAlphanumeric(50));
        User createdUser = userService.create(user);

        HashMap<String, Object> variables = new HashMap<>();
        variables.put("user", createdUser);
        variables.put("activateUrl", buildActivationUrl(requestURI, user));
        variables.put("docUrl", buildDocsUrl(requestURI));

        mailService.sendEmail(
                user.getEmail(),
                "Welcome to Scriptinator!",
                "email/welcome",
                variables
        );

        return true;
    }

    private String buildDocsUrl(URI requestURI) {
        try {
            return buildUrl(requestURI, "/docs")
                    .build()
                    .toString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Could not build docs url: " + e.getMessage(), e);
        }
    }

    private String buildActivationUrl(URI requestURI, User user) {
        try {
            return buildUrl(requestURI, "/register/activate")
                    .addParameter("token", user.getEmailActivationToken())
                    .build()
                    .toString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Could not build activation url: " + e.getMessage(), e);
        }
    }

    private URIBuilder buildUrl(URI requestURI, String path) {
        return new URIBuilder(requestURI)
                .removeQuery()
                .setPath(path);
    }

    @Transactional
    public void activate(String activationToken) {
        LOGGER.debug("Activating using with {}", activationToken);

        Optional<User> user = userService.getByToken(activationToken);

        if (user.isPresent()) {
            user.get().setEmailActivationToken(null);
            userService.update(user.get());

            Project project = new Project();
            String projectName = user.get().getUsername() + "-first-project";
            project.setName(projectName);

            for (int i = 1; projectService.exists(project.getName()); i++) {
                project.setName(projectName + "-" + i);
            }

            project.setOwner(user.get());
            project.setDescription("A simple project to help you get started");
            project = projectService.create(project);

            Script script = new Script();
            script.setName("hello-world");
            try (InputStream data = getClass().getResourceAsStream("/scripts/hello-world.js")) {
                script.setCode(IOUtils.toString(data, "UTF-8"));
            } catch (IOException e) {
                LOGGER.error("Failed to load code for default script", e);
            }
            script.setDescription("Welcome and hello!");
            script.setProject(project);
            scriptService.create(script);
        } else {
            LOGGER.error("No user was found for activation token {}", activationToken);
        }
    }
}
