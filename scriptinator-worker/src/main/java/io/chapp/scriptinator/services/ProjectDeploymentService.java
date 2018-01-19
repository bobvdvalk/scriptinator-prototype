package io.chapp.scriptinator.services;

import io.chapp.scriptinator.ScriptinatorWorkerConfiguration;
import io.chapp.scriptinator.model.Project;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProjectDeploymentService {
    private final Path workspace;
    private final GitService gitService;
    private Map<Integer, String> checkedOutCommits = new ConcurrentHashMap<>();

    public ProjectDeploymentService(ScriptinatorWorkerConfiguration configuration, GitService gitService) {
        this.workspace = Paths.get(configuration.getWorkspace());
        this.gitService = gitService;
    }

    public Path deploy(Project project) throws IOException, GitAPIException {
        Path projectFolder = workspace.resolve(Integer.toString(project.getId()));
        if (checkedOutCommits.getOrDefault(project.getId(), "").equals(project.getCommitId())) {
            // We have already checked out this project
            return projectFolder;
        }

        try (Git git = gitService.getRepository(projectFolder, project.getGitUrl())) {
            CheckoutCommand checkoutCommand = git.checkout();
            checkoutCommand.setName(project.getCommitId());
            checkoutCommand.call();
            checkedOutCommits.put(project.getId(), project.getCommitId());
        }

        return projectFolder;
    }
}
