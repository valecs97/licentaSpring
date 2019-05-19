package ro.vitoc.licenta.core.service;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

public interface GitService {
    Boolean cloneGitRepository(String name,String url, String branch);
    Ref getBranchRef(String url, String branch);
    String getLocation(String repositoryName);
    String detectLanguage(String user, String repo);
}
