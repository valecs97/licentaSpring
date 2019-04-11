package ro.vitoc.licenta.miscellaneous.algorithms;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.core.model.WebMicroService;
import ro.vitoc.licenta.miscellaneous.service.ProcessService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

@Component
public class DockerAlgorithmsImpl implements DockerAlgorithms {
    private static final Logger log = LoggerFactory.getLogger(DockerAlgorithmsImpl.class);

    @Autowired
    private ProcessService processService;

    @Value("${docker.defaultDockerComposerFile}")
    private String defaultDockerComposerFile;

    @Value("${docker.defaultVMName}")
    private String defaultVMName;

    @Value("${docker.swarmName}")
    private String swarmName;

    private String swarmInitCommand = "docker swarm init --advertise-addr";

    private String deployStackCommand = "docker stack deploy -c";

    private String rebalanceService = "docker service update --force";

    @Override
    public String createDefaultDockerComposer() throws IOException, URISyntaxException {
        log.trace("Creating docker DEFAULT composer file");
        return createDefaultDockerComposer(null);
    }

    public String createDefaultDockerComposer(List<WebMicroService> configs) throws IOException, URISyntaxException {
        log.trace("Creating docker composer file");
        File file = Paths.get(getClass().getClassLoader().getResource(defaultDockerComposerFile).toURI()).toFile();
        FileInputStream fis = null;
        fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String res = StringEscapeUtils.escapeJava(new String(data, "UTF-8"));
        if (configs != null)
            for (WebMicroService config : configs) {
                res += "\\t" + config.getName() + ":\\n";
                res += "\\t\\timage: " + config.getConfiguration().getImage() + "\\n";
                res += "\\t\\tdeploy:\\n";
                res += "\\t\\t\\treplicas: " + config.getReplicas() + "\\n";
                res += "\\t\\t\\tresources: \\n";
                res += "\\t\\t\\t\\tlimits:\\n";
                res += "\\t\\t\\t\\t\\tcpus: \\\"" + config.getConfiguration().getCpus() + "\\\"\\n";
                res += "\\t\\t\\t\\t\\tmemory: " + config.getConfiguration().getMemory() + "\\n";
                res += "\\t\\t\\trestart_policy:\\n";
                res += "\\t\\t\\t\\tcondition: on-failure\\n";
                res += "\\t\\tports:\\n";
                res += "\\t\\t\\t- \\\"" + config.getPortOut() + ":" + config.getPortIn() + "\\\"\\n";
            }
        res += "networks:\\n";
        res += "\\twebnet:\\n";
        return res.replace("\\r\\n", "\\n").replace("\\t", "  ");
    }

    @Override
    public void deployComposerFile(String fileContent) throws IOException {
        log.trace("Deploying docker composer file");

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

    @Override
    public void rebalanceStack(List<WebMicroService> configs) {
        log.trace("rebalanceStack , configs = {}", configs);
        for (WebMicroService config : configs) {
            log.trace("Rebalance microservice " + config.getName());
            try {
                processService.executeCommandWithoutWait(rebalanceService + " " + swarmName + "_" + config.getName());
            } catch (IOException e) {
                log.trace("rebalanceStack failed with error " + e.getMessage());
            }
        }
    }
}
