package ro.vitoc.licenta.core.service;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

public interface ProjectService {
    Boolean cloneGitRepository(String url, String branch);
    Ref getBranchRef(String url, String branch);
}
