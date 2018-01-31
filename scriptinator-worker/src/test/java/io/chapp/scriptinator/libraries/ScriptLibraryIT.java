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
package io.chapp.scriptinator.libraries;

import io.chapp.scriptinator.ScriptinatorWorker;
import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.repositories.JobRepository;
import io.chapp.scriptinator.repositories.ProjectRepository;
import io.chapp.scriptinator.repositories.UserRepository;
import io.chapp.scriptinator.services.ScriptService;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ScriptinatorWorker.class)
public class ScriptLibraryIT {
    @Autowired
    private ScriptService scriptService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobRepository jobRepository;

    private Project project;

    @Before
    public void createTestProject() {
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user = userRepository.save(user);

        project = new Project();
        project.setDisplayName("IntegrationTests");
        project.setOwner(user);
        project = projectRepository.save(project);
    }

    @Test
    public void testScriptRunsWithoutErrors() throws IOException {
        Set<String> resources = new Reflections(
                getClass().getPackage().getName(),
                new ResourcesScanner()
        )
                .getResources(name -> name.endsWith(".test.js"));
        Pattern regex = Pattern.compile(".*/(\\w+)/([\\w]+).*");

        // Queue all scripts
        for (String resource : resources) {
            Matcher matcher = regex.matcher(resource);
            if (matcher.matches()) {
                String scriptName = matcher.group(1) + "_" + matcher.group(2);

                // Start the script
                try (InputStream data = getClass().getResourceAsStream("/" + resource)) {
                    String code = IOUtils.toString(data, "UTF-8");

                    Script script = new Script();
                    script.setFullyQualifiedName(scriptName);
                    script.setCode(code);
                    script.setProject(project);
                    script = scriptService.create(script);
                    scriptService.run(script);
                }

            }

        }

        // Wait for all scripts to complete
        while (
                jobRepository.findByStatus(Job.Status.QUEUED, new PageRequest(0, 1)).hasContent() ||
                        jobRepository.findByStatus(Job.Status.RUNNING, new PageRequest(0, 1)).hasContent()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Job job : jobRepository.findAll()) {
            if (job.getStatus() != Job.Status.FINISHED) {
                Assert.fail(
                        "Job [" + job.getDisplayName() + "] failed:\n" + job.getOutput()
                );
            }
        }
    }

}
