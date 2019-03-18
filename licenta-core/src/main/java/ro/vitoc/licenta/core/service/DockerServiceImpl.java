package ro.vitoc.licenta.core.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.vitoc.licenta.core.model.BaseProject;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class DockerServiceImpl implements DockerService {
    private static final Logger log = LoggerFactory.getLogger(DockerServiceImpl.class);
    private DockerClient dockerClient;

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
    public void startContainer(String containerId){
        dockerClient.startContainerCmd(containerId).exec();
    }

    @Override
    public String createImage(BaseProject project) {
        log.trace("createImage: name={},location={}",project.getName(),project.getLocation());
        /*setDockerClient();
        Set<String> set = new TreeSet<>();
        set.add(project.getName());
        return dockerClient.buildImageCmd()
                .withDockerfile(new File(project.getLocation() + "\\Dockerfile"))
                .withBaseDirectory(new File(project.getLocation()))
                .exec(new BuildImageResultCallback())
                .awaitImageId(60, TimeUnit.SECONDS);*/
        try {
            return executeCommand(new String[]{"docker","build","--tag="+project.getName(),project.getLocation()});
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String runImage(String tag,List<String> args) {
        log.trace("runImage: tag={},args={}",tag,args);
        List<String> command = new ArrayList<>();
        command.addAll(Arrays.asList("docker","run"));
        for (int i=0;i<args.size();i++){
            command.add("-e");
            command.add("ARG" + i + "=" + args.get(i).replace(" ","%20"));
        }
        command.add(tag);
        try {
            return executeCommand(command.toArray(new String[command.size()]));
        } catch (IOException | InterruptedException e) {
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
    public String executeCommand(String[] command) throws IOException, InterruptedException {
        log.trace("executeCommand: command={}", Arrays.stream(command).reduce((a,b) -> a + " " + b));
        Process process;
        process = Runtime.getRuntime().exec(command);
        process.waitFor();
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
        String line = "";
        String res = "";
        while ((line = reader.readLine()) != null) {
            res+=line + "\n";
        }
        while ((line = errorReader.readLine()) != null) {
            res+=line + "\n";
        }
        return res;
    }
}
