package ro.vitoc.licenta.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Session;
import org.apache.http.HttpStatus;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.vitoc.licenta.miscellaneous.service.ProcessService;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

@Service
public class GitServiceImpl implements GitService {
    private static final Logger log = LoggerFactory.getLogger(GitServiceImpl.class);
    private static final String gitLocation = "\\Git projects\\";
    private final SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
        @Override
        protected void configure(OpenSshConfig.Host host, Session session) {
            // do nothing
        }
    };

    @Autowired
    ProcessService processService;

    @Override
    public Boolean cloneGitRepository(String name, String url, String branch) {
        log.trace("cloneGitRepository: url={},branch={}", url, branch);
        if (!checkGitRepository(url))
            return false;
        try {
            Ref branchRef = getBranchRef(url, branch);
            if (branchRef == null)
                throw new InvalidRefNameException("Branch does not exist");
            log.trace("cloneGitRepository clone folder: " + getLocation(name));
            Git res = Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(Paths.get(getLocation(name)).toFile())
                    .setBranchesToClone(Collections.singleton(branchRef.getName()))
                    .setBranch(branchRef.getName())
/*                    .setTransportConfigCallback(transport -> {
                        SshTransport sshTransport = ( SshTransport )transport;
                        sshTransport.setSshSessionFactory( sshSessionFactory );
                    })*/
                    //.setCredentialsProvider(new UsernamePasswordCredentialsProvider("valecs97", ""))
                    .call();
            res.getRepository().close();
        } catch (GitAPIException e) {
            log.trace("Git error : " + e.getMessage());
            return false;
        }
        return true;
    }

    private Boolean checkGitRepository(String url) {
        log.trace("checkGitRepository: url={}", url);
        try {
            Git.lsRemoteRepository()
                    .setHeads(true)
                    .setTags(true)
                    .setRemote(url)
                    //.setCredentialsProvider(new UsernamePasswordCredentialsProvider("valecs97", ""))
/*                    .setTransportConfigCallback(transport -> {
                        SshTransport sshTransport = ( SshTransport )transport;
                        sshTransport.setSshSessionFactory( sshSessionFactory );
                    })*/
                    .call();
        } catch (GitAPIException e) {
            log.trace("Git error : " + e.getMessage());
            return false;
        }
        return true;
    }


    @Override
    public Ref getBranchRef(String url, String branch) {
        log.trace("getBranchRef: url={},branch={}", url, branch);
        try {
            return Git.lsRemoteRepository()
                    .setHeads(true)
                    .setTags(true)
                    .setRemote(url)
                    //.setCredentialsProvider(new UsernamePasswordCredentialsProvider("valecs97", ""))
/*                    .setTransportConfigCallback(transport -> {
                        SshTransport sshTransport = ( SshTransport )transport;
                        sshTransport.setSshSessionFactory( sshSessionFactory );
                    })*/
                    .call()
                    .stream()
                    .filter(ref -> ref.getName().contains(branch))
                    .findAny()
                    .orElse(null);
        } catch (GitAPIException e) {
            log.trace("Git error : " + e.getMessage());
            return null;
        }
    }

    @Override
    public String getLocation(String repositoryName) {
        return System.getenv("APPDATA") + gitLocation + repositoryName;
    }

    @Override
    public String detectLanguage(String user, String repo) {
        log.trace("getBranchRef: user={},repo={}", user, repo);
        try {
            //String payload = processService.executeCommand(new String[]{"curl","-s","https://api.github.com/repos/" + user + "/" + repo + "/languages"});
            String payload = processService.getRequest("https://api.github.com/repos/" + user + "/" + repo + "/languages");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = null;
            try {
                root = mapper.readTree(payload);
            } catch (IOException e) {
                log.trace("detectLanguage failed to parse body with message={}",e.getMessage());
                return null;
            }
            return root.fieldNames().next();
        } catch (Exception e) {
            log.trace("Exception : " + e.getMessage());
            return null;
        }
    }
/*
    private String getRepositoryName(String url){
        String[] var = url.split(String.valueOf('/'));
        return var[var.length-1];
    }*/
}
