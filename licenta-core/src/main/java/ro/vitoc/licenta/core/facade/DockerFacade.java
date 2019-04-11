package ro.vitoc.licenta.core.facade;

import ro.vitoc.licenta.core.model.BaseProject;

import java.io.IOException;
import java.util.List;

public interface DockerFacade {
    String createContainer(BaseProject project);
    void startContainer(String containerId);
    String createImage(BaseProject project);
    String runImage(String tag, List<String> args);
    String pushImage(BaseProject project);
}
