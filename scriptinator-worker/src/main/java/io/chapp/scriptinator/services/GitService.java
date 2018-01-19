package io.chapp.scriptinator.services;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class GitService {

    public Git getRepository(Path folder, String gitUrl) throws IOException, GitAPIException {
        if (folder.toFile().isDirectory()) {
            return Git.open(folder.toFile());
        }
        CloneCommand cloneCommand = Git.cloneRepository();
        cloneCommand.setURI(gitUrl);
        cloneCommand.setDirectory(folder.toFile());
        return cloneCommand.call();
    }
}
