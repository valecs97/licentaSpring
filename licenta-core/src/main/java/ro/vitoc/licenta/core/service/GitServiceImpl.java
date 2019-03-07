package ro.vitoc.licenta.core.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.lib.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Collections;

@Service
public class GitServiceImpl implements GitService {
    private static final Logger log = LoggerFactory.getLogger(GitServiceImpl.class);
    private static final String gitLocation = "\\Git projects\\";

    @Override
    public Boolean cloneGitRepository(String name,String url, String branch) {
        log.trace("cloneGitRepository: url={},branch={}",url,branch);
        if (!checkGitRepository(url))
            return false;
        try {
            Ref branchRef = getBranchRef(url,branch);
            if (branchRef == null)
                throw new InvalidRefNameException("Branch does not exist");
            log.trace("cloneGitRepository clone folder: " + getLocation(name));
            Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(Paths.get(getLocation(name)).toFile())
                    .setBranchesToClone(Collections.singleton(branchRef.getName()))
                    .setBranch(branchRef.getName())
                    .call();
        } catch (GitAPIException e) {
            log.trace("Git error : " + e.getMessage());
            return false;
        }
        return true;
    }

    private Boolean checkGitRepository(String url){
        log.trace("checkGitRepository: url={}",url);
        try {
            Git.lsRemoteRepository()
                    .setHeads(true)
                    .setTags(true)
                    .setRemote(url)
                    .call();
        } catch (GitAPIException e){
            log.trace("Git error : " + e.getMessage());
            return false;
        }
        return true;
    }


    @Override
    public Ref getBranchRef(String url, String branch) {
        log.trace("getBranchRef: url={},branch={}",url,branch);
        try {
            return Git.lsRemoteRepository()
                    .setHeads(true)
                    .setTags(true)
                    .setRemote(url)
                    .call()
                    .stream()
                    .filter(ref -> ref.getName().contains(branch))
                    .findAny()
                    .orElse(null);
        } catch (GitAPIException e){
            log.trace("Git error : " + e.getMessage());
            return null;
        }
    }

    @Override
    public String getLocation(String repositoryName) {
        return System.getenv("APPDATA") + gitLocation + repositoryName;
    }
/*
    private String getRepositoryName(String url){
        String[] var = url.split(String.valueOf('/'));
        return var[var.length-1];
    }*/
}
