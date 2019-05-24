package ro.vitoc.licenta.core.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.ProcessExecutor;
import ro.vitoc.licenta.core.dto.LogDto;
import ro.vitoc.licenta.core.model.BaseProject;
import ro.vitoc.licenta.core.model.Log;
import ro.vitoc.licenta.core.model.MicroService;
import ro.vitoc.licenta.core.model.WebMicroService;
import ro.vitoc.licenta.core.repository.MicroServiceRepository;
import ro.vitoc.licenta.core.repository.WebMicroServiceRepository;
import ro.vitoc.licenta.miscellaneous.algorithms.DockerAlgorithms;
import ro.vitoc.licenta.miscellaneous.service.ProcessService;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeoutException;

@Service
public class DockerServiceImpl implements DockerService {
    private static final Logger log = LoggerFactory.getLogger(DockerServiceImpl.class);
    private DockerClient dockerClient;

    private ProcessService processService;
    private WebMicroServiceRepository webMicroServiceRepository;
    private MicroServiceRepository microServiceRepository;
    private DockerAlgorithms dockerAlgorithms;

    @Autowired
    public DockerServiceImpl(ProcessService processService, WebMicroServiceRepository webMicroServiceRepository, MicroServiceRepository microServiceRepository, DockerAlgorithms dockerAlgorithms) {
        this.processService = processService;
        this.webMicroServiceRepository = webMicroServiceRepository;
        this.microServiceRepository = microServiceRepository;
        this.dockerAlgorithms = dockerAlgorithms;
    }

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
            //return processService.executeCommand(new String[]{"docker", "build", "--tag=" + project.getName(), project.getLocation()});
            try {
                processService.executeCommandFIX(new String[]{"docker", "build", "--tag=" + project.getName(), project.getLocation()});
            } catch (IOException | InterruptedException | TimeoutException e) {
                log.trace("attachLogToWebSocket failed with message={}", e.getMessage());
                throw new IOException();
            }
            return "DONE";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String removeImage(BaseProject project){
        log.trace("removeImage: name={},location={}", project.getName(), project.getLocation());
        try {
            processService.executeCommandFIX(new String[]{"docker","image","rm","--force",project.getName()});
            return "Process finished";
        } catch (IOException | InterruptedException | TimeoutException e) {
            log.trace("removeImage failed with message={}",e.getMessage());
            return null;
        }
    }

    @Override
    public String pushImage(BaseProject project) {
        log.trace("pushImage: name={},location={}", project.getName(), project.getLocation());
        try {
            String newTag = dockerUser + "/" + project.getName();
            processService.executeCommandFIX(new String[]{"docker","tag",project.getName(),newTag});
            processService.executeCommandFIX(new String[]{"docker", "push", newTag});
            return newTag;
        } catch (IOException | InterruptedException | TimeoutException e) {
            log.trace("pushImage failed with message={}",e.getMessage());
            return null;
        }
    }

    @Override
    public String runImage(String tag, List<String> args, Boolean argType) {
        log.trace("runImage: tag={},args={}", tag, args);
        List<String> command = new ArrayList<>();
        command.addAll(Arrays.asList("docker", "run"));
        if (argType) {
            for (int i = 0; i < args.size(); i++) {
                command.add("-e");
                command.add("ARG" + i + "=" + args.get(i).replace(" ", "%20"));
            }
        }
        command.add(tag);
        if (!argType)
            command.addAll(args);
        try {
            processService.executeCommand(command.toArray(new String[command.size()]));
            return "Process finished";
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


    @Override
    public void redeployAll() {
        log.trace("redeployAll dockerService--- method entered");

        List<WebMicroService> webMicroServices = webMicroServiceRepository.findAll();
        List<MicroService> microServices = microServiceRepository.findAll();

        String fileContent = null;
        try {
            fileContent = dockerAlgorithms.createDefaultDockerComposer(webMicroServices,microServices);
            dockerAlgorithms.deployComposerFile(fileContent);
        } catch (IOException | URISyntaxException | InterruptedException | TimeoutException  e) {
            log.trace("createDefaultDockerComposer failed with error : " + e.getMessage());
        }

        dockerAlgorithms.rebalanceStack(webMicroServices,microServices);
    }
}
