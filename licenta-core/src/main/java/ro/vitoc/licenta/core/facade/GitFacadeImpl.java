package ro.vitoc.licenta.core.facade;

import org.eclipse.jgit.lib.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.core.service.DockerService;
import ro.vitoc.licenta.core.service.DockerServiceImpl;
import ro.vitoc.licenta.core.service.GitService;

@Component
public class GitFacadeImpl implements GitFacade {
    private static final Logger log = LoggerFactory.getLogger(GitFacadeImpl.class);
    @Autowired
    private GitService gitService;

    @Override
    public Boolean cloneGitRepository(String name, String url, String branch) {
        log.trace("cloneGitRepository dao, name={},url={},branch={}",name,url,branch);
        return gitService.cloneGitRepository(name,url,branch);
    }

    @Override
    public Ref getBranchRef(String url, String branch) {
        log.trace("getBranchRef dao, url={},branch={}",url,branch);
        return gitService.getBranchRef(url,branch);
    }

    @Override
    public String getLocation(String repositoryName) {
        log.trace("getLocation dao, repositoryName={}",repositoryName);
        return gitService.getLocation(repositoryName);
    }

    @Override
    public String detectLanguage(String user, String repo) {
        log.trace("detectLanguage dao, user={}, repo={}",user,repo);
        return gitService.detectLanguage(user,repo);
    }
}
