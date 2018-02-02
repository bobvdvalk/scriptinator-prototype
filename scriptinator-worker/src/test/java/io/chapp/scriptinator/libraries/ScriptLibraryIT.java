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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ScriptinatorWorker.class)
public class ScriptLibraryIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptLibraryIT.class);
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

    @SuppressWarnings("squid:S2925") // We need to wait until the scripts are done
    @Test
    public void testScriptRunsWithoutErrors() throws IOException {
        queue(Paths.get("src/test/resources"));
        queue(Paths.get("../docs/libraries"));
        waitForComplete();
        checkForErrors();
    }

    private void queue(Path path) throws IOException {
        Files.walkFileTree(path.toAbsolutePath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".test.js") || file.toString().endsWith(".example.js")) {
                    String code = Files.lines(file).collect(Collectors.joining("\n"));
                    queue(code, FilenameUtils.getBaseName(file.toString()).replace('.', '_'));
                }
                return super.visitFile(file, attrs);
            }
        });
    }


    private void queue(String code, String name) {
        Script script = new Script();
        script.setFullyQualifiedName(name);
        script.setCode(code);
        script.setProject(project);
        script = scriptService.create(script);
        scriptService.run(script);
    }

    private void waitForComplete() {
        while (
                jobRepository.findByStatus(Job.Status.QUEUED, new PageRequest(0, 1)).hasContent() ||
                        jobRepository.findByStatus(Job.Status.RUNNING, new PageRequest(0, 1)).hasContent()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void checkForErrors() {
        Job failedJob = null;
        for (Job job : jobRepository.findAll()) {

            LOGGER.info(
                    "Job {} [{} ms]: {}",
                    job.getStatus(),
                    StringUtils.leftPad(
                            Long.toString(job.getFinishedTime().getTime() - job.getStartedTime().getTime()),
                            6
                    ),
                    job.getDisplayName()
            );

            if (job.getStatus() != Job.Status.FINISHED) {
                failedJob = job;
            }
        }
        if (failedJob != null) {
            Assert.fail(
                    "Job [" + failedJob.getDisplayName() + "] failed:\n" + failedJob.getOutput()
            );
        }
    }


}
