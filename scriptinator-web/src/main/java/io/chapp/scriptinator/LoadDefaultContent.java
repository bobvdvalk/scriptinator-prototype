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
package io.chapp.scriptinator;

import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.repositories.ProjectRepository;
import io.chapp.scriptinator.repositories.ScriptRepository;
import io.chapp.scriptinator.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class LoadDefaultContent implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ScriptRepository scriptRepository;
    private final PasswordEncoder passwordEncoder;

    public LoadDefaultContent(UserRepository userRepository, ProjectRepository projectRepository, ScriptRepository scriptRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.scriptRepository = scriptRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... strings) {
        if (userRepository.count() == 0) {
            User defaultUser = new User();
            defaultUser.setUsername("user");
            defaultUser.setPassword(passwordEncoder.encode("default"));
            defaultUser.setDisplayName("Default User");
            defaultUser.setEmail("default@user.com");
            defaultUser = userRepository.save(defaultUser);

            Project project = new Project();
            project.setDisplayName("My First Project");
            project.setDescription("A simple project to help you get started");
            project.setOwner(defaultUser);
            project = projectRepository.save(project);

            Script script = new Script();
            script.setProject(project);
            script.setCode("Script.info('Hello World');");
            script.setDescription("Hello World");
            script.setFullyQualifiedName("greet");
            scriptRepository.save(script);
        }
    }
}
