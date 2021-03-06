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
package io.chapp.scriptinator.libraries;

import io.chapp.scriptinator.ScriptinatorWorker;
import io.chapp.scriptinator.model.*;
import io.chapp.scriptinator.repositories.JobRepository;
import io.chapp.scriptinator.repositories.ProjectRepository;
import io.chapp.scriptinator.repositories.SecretRepository;
import io.chapp.scriptinator.repositories.UserRepository;
import io.chapp.scriptinator.services.ScriptService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;

@ContextConfiguration(classes = ScriptinatorWorker.class)
public class ScriptLibraryIT extends AbstractTestNGSpringContextTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptLibraryIT.class);
    @Autowired
    private ScriptService scriptService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private SecretRepository secretRepository;

    private Project project;

    @DataProvider(parallel = true)
    public static Object[] getAllScripts() throws IOException {
        List<Path> paths = new ArrayList<>();
        paths.addAll(scanFolder(Paths.get("src/test/resources")));
        paths.addAll(scanFolder(Paths.get("../scriptinator-docs/src/main/asciidoc")));
        return paths.toArray();
    }

    private static List<Path> scanFolder(Path path) throws IOException {
        List<Path> result = new ArrayList<>();
        Files.walkFileTree(path.toAbsolutePath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".test.js") || file.toString().endsWith(".example.js")) {
                    result.add(file);
                }
                return super.visitFile(file, attrs);
            }
        });
        return result;
    }

    @BeforeClass
    public void createTestProject() {
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user = userRepository.save(user);

        project = new Project();
        project.setName("IntegrationTests");
        project.setOwner(user);
        project = projectRepository.save(project);

        Secret secret = new Secret();
        secret.setName("my-token");
        secret.setValue("9555701a-315b-419f-bffb-7c8ea8168b42");
        secret.setProject(project);
        secretRepository.save(secret);


        // This project is called using run
        Project myFirstProject = new Project();
        myFirstProject.setName("my-first-project");
        myFirstProject.setOwner(user);
        myFirstProject = projectRepository.save(myFirstProject);

        Script script = new Script();
        script.setName("a-script");
        script.setProject(myFirstProject);
        scriptService.create(script);
    }


    @Test(dataProvider = "getAllScripts")
    public void testEnqueueJob(Path file) throws IOException {
        String code = Files.lines(file).collect(Collectors.joining("\n"));
        String name = FilenameUtils.getBaseName(file.toString()).replace('.', '_');

        Script script = new Script();
        script.setName(name);
        script.setCode(code);
        script.setProject(project);
        script = scriptService.create(script);
        long jobId = scriptService.run(script).getId();

        await("Wait for test to complete: " + file)
                .atMost(1, TimeUnit.MINUTES)
                .until(() -> jobRepository.findOne(jobId).get().getFinishedTime() != null);

        Job finishedJob = jobRepository.findOne(jobId).get();

        LOGGER.info(
                "Job {} [{} ms]: {}",
                finishedJob.getStatus(),
                StringUtils.leftPad(
                        Long.toString(finishedJob.getFinishedTime().getTime() - finishedJob.getStartedTime().getTime()),
                        6
                ),
                finishedJob.getDisplayName()
        );

        if (finishedJob.getStatus() == Job.Status.FAILED) {
            Assert.fail(
                    "Job [" + finishedJob.getDisplayName() + "] failed:\n" + finishedJob.getOutput()
            );
        }
    }
}
