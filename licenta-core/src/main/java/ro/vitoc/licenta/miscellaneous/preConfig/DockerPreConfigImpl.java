package ro.vitoc.licenta.miscellaneous.preConfig;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.miscellaneous.service.ProcessService;
import ro.vitoc.licenta.miscellaneous.service.ProcessServiceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;

@Component
public class DockerPreConfigImpl implements DockerPreConfig {
    private static final Logger log = LoggerFactory.getLogger(DockerPreConfigImpl.class);

    @Autowired
    private ProcessService processService;

    @Value("${docker.defaultVMName}")
    private String defaultVMName;

    @Value("${docker.defaultDockerComposerFile}")
    private String defaultDockerComposerFile;

    @Value("${docker.swarmName}")
    private String swarmName;

    private String swarmInitCommand = "docker swarm init --advertise-addr";

    private String deployStackCommand = "docker stack deploy -c";

    private String createDefaultDockerComposer() throws IOException, URISyntaxException {
        File file = Paths.get(getClass().getClassLoader().getResource(defaultDockerComposerFile).toURI()).toFile();
        FileInputStream fis = null;
        fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        return StringEscapeUtils.escapeJava(new String(data, "UTF-8")).replace("\\r\\n", "\\n");
    }

    @Override
    public void createDefaultVM() throws IOException {
        log.trace("Creating docker composer file");
        String fileContent = null;
        try {
            fileContent = createDefaultDockerComposer();
        } catch (IOException | URISyntaxException e) {
            log.trace("createDefaultDockerComposer failed with error : " + e.getMessage());
        }
        log.trace(processService.executeCommand(processService.executeInVM(defaultVMName, "printf \'" + fileContent + "\' > " + defaultDockerComposerFile)));
        String vm = processService.getVMInfo(defaultVMName);
        if (vm != null) {
            String ip = vm.split(" ")[2].split("//")[1].split(":")[0];
            log.trace("Creating swarm init");
            log.trace(processService.executeCommand(processService.executeInVM(defaultVMName, swarmInitCommand + " " + ip)));
            log.trace("Launching the docker composer");
            log.trace(processService.executeCommand(processService.executeInVM(defaultVMName, deployStackCommand + " " + defaultDockerComposerFile + " " + swarmName)));
        }
    }
}
