package ro.vitoc.licenta.core.facade;

import org.eclipse.jgit.lib.Ref;

public interface GitFacade {
    Boolean cloneGitRepository(String name,String url, String branch);
    Ref getBranchRef(String url, String branch);
    String getLocation(String repositoryName);
    String detectLanguage(String user, String repo);
}
