package ro.vitoc.licenta.core.service;

import com.github.dockerjava.api.DockerClient;
import ro.vitoc.licenta.core.model.BaseProject;

import java.io.IOException;
import java.util.List;

public interface DockerService {
    String createImage(BaseProject project);
    String pushImage(BaseProject project);
    String runImage(String tag, List<String> args);
    String createContainer(BaseProject project);
    void startContainer(String containerId);
}
