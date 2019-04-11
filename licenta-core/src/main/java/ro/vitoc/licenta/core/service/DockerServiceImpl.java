package ro.vitoc.licenta.core.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.vitoc.licenta.core.model.BaseProject;
import ro.vitoc.licenta.miscellaneous.service.ProcessService;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class DockerServiceImpl implements DockerService {
    private static final Logger log = LoggerFactory.getLogger(DockerServiceImpl.class);
    private DockerClient dockerClient;

    @Autowired
    private ProcessService processService;

    @Value("${docker.username}")
    private String dockerUser;

    @Value("${docker.password}")
    private String dockerPass;

    @PostConstruct
    private void setDockerClient() {
        log.trace("SET");
        DefaultDockerClientConfig.Builder config
                = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerHost("tcp://127.0.0.1:2375");
        dockerClient = DockerClientBuilder
                .getInstance(config)
                .build();
    }

    @Override
    public void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }

    @Override
    public String createImage(BaseProject project) {
        log.trace("createImage: name={},location={}", project.getName(), project.getLocation());
        /*setDockerClient();
        Set<String> set = new TreeSet<>();
        set.add(project.getName());
        return dockerClient.buildImageCmd()
                .withDockerfile(new File(project.getLocation() + "\\Dockerfile"))
                .withBaseDirectory(new File(project.getLocation()))
                .exec(new BuildImageResultCallback())
                .awaitImageId(60, TimeUnit.SECONDS);*/
        try {
            return processService.executeCommand(new String[]{"docker", "build", "--tag=" + project.getName(), project.getLocation()});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String pushImage(BaseProject project) {
        log.trace("pushImage: name={},location={}", project.getName(), project.getLocation());
        try {
            String newTag = dockerUser + "/" + project.getName();
            log.trace(processService.executeCommand(new String[]{"docker","tag",project.getName(),newTag}));
            log.trace(processService.executeCommand(new String[]{"docker", "push", newTag}));
            return newTag;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String runImage(String tag, List<String> args) {
        log.trace("runImage: tag={},args={}", tag, args);
        List<String> command = new ArrayList<>();
        command.addAll(Arrays.asList("docker", "run"));
        for (int i = 0; i < args.size(); i++) {
            command.add("-e");
            command.add("ARG" + i + "=" + args.get(i).replace(" ", "%20"));
        }
        command.add(tag);
        try {
            return processService.executeCommand(command.toArray(new String[command.size()]));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String createContainer(BaseProject project) {
        return dockerClient.createContainerCmd(project.getName())
                .withName(project.getName())
                .exec().getId();
    }
}
