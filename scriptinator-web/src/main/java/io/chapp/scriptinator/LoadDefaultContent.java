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
package io.chapp.scriptinator;

import io.chapp.scriptinator.model.OAuthApp;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.repositories.ProjectRepository;
import io.chapp.scriptinator.repositories.ScriptRepository;
import io.chapp.scriptinator.repositories.UserRepository;
import io.chapp.scriptinator.services.OAuthAppService;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class LoadDefaultContent implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ScriptRepository scriptRepository;
    private final PasswordEncoder passwordEncoder;
    private final OAuthAppService appService;

    public LoadDefaultContent(UserRepository userRepository, ProjectRepository projectRepository, ScriptRepository scriptRepository, PasswordEncoder passwordEncoder, OAuthAppService appService) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.scriptRepository = scriptRepository;
        this.passwordEncoder = passwordEncoder;
        this.appService = appService;
    }

    @Override
    public void run(String... strings) throws IOException {
        if (userRepository.count() == 0) {
            User defaultUser = new User();
            defaultUser.setUsername("user");
            defaultUser.setPassword(passwordEncoder.encode("default"));
            defaultUser.setDisplayName("Default User");
            defaultUser.setEmail("default@user.com");
            defaultUser = userRepository.save(defaultUser);

            Project project = new Project();
            project.setName("my-first-project");
            project.setDescription("A simple project to help you get started");
            project.setOwner(defaultUser);
            project = projectRepository.save(project);

            Script script = new Script();
            script.setProject(project);
            try (InputStream data = getClass().getResourceAsStream("/scripts/hello-world.js")) {
                script.setCode(IOUtils.toString(data, "UTF-8"));
            }
            script.setDescription("Hello World");
            script.setName("greet");
            scriptRepository.save(script);

            OAuthApp app = new OAuthApp();
            app.setName("Example App");
            app.setOwner(defaultUser);
            app.setDescription("An example OAuth application");
            appService.create(app);
        }
    }
}
