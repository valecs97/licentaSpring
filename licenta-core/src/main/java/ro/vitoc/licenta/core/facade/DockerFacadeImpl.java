package ro.vitoc.licenta.core.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.core.model.BaseProject;
import ro.vitoc.licenta.core.service.CommonService;
import ro.vitoc.licenta.core.service.CommonServiceImpl;
import ro.vitoc.licenta.core.service.DockerService;
import ro.vitoc.licenta.core.service.DockerServiceImpl;

import java.io.IOException;
import java.util.List;

@Component
public class DockerFacadeImpl implements DockerFacade{
    private static final Logger log = LoggerFactory.getLogger(DockerServiceImpl.class);
    @Autowired
    private DockerService dockerService;

    @Override
    public String createContainer(BaseProject project) {
        log.trace("createContainer dao, projectName={}",project.getName());
        String containerId = dockerService.createContainer(project);
        return containerId;
    }

    @Override
    public void startContainer(String containerId) {
        log.trace("startContainer dao, containerId={}",containerId);
        dockerService.startContainer(containerId);
    }

    @Override
    public String createImage(BaseProject project) {
        log.trace("createImage dao, projectName={}",project.getName());
        return dockerService.createImage(project);
    }

    @Override
    public String runImage(String tag, List<String> args) {
        log.trace("runImage dao, tag={}, args={}",tag,args);
        return dockerService.runImage(tag,args);
    }

    @Override
    public String executeCommand(String[] command) throws IOException, InterruptedException {
        log.trace("executeCommand dao, command={}",command);
        return dockerService.executeCommand(command);
    }
}
