package ro.vitoc.licenta.miscellaneous.preCheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.miscellaneous.algorithms.DockerAlgorithms;
import ro.vitoc.licenta.miscellaneous.service.ProcessService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

@Component
public class DockerPreCheckImpl implements DockerPreCheck {
    private static final Logger log = LoggerFactory.getLogger(DockerPreCheckImpl.class);
    public static CountDownLatch VMStatus = new CountDownLatch(1);

    private DockerAlgorithms dockerAlgorithms;
    private ProcessService processService;

    @Value("${docker.defaultVMName}")
    private String defaultVMName;

    @Value("${docker.switch}")
    private String switchName;

    private static String getVMsCommand = "docker-machine ls -f \"{{.Name}} {{.State}} {{.URL}}\"";
    private static String createDefaultVMWithHyperV = "docker-machine create -d hyperv --hyperv-virtual-switch";
    private static String startDefaultVM = "docker-machine start";

    @Autowired
    public DockerPreCheckImpl(DockerAlgorithms dockerAlgorithms, ProcessService processService) {
        this.dockerAlgorithms = dockerAlgorithms;
        this.processService = processService;
    }

    @PostConstruct
    public void checkDefaultVM() throws IOException {
        String vms = processService.executeCommand(getVMsCommand);
        if (Arrays.stream(vms.split("\n")).noneMatch(elem -> elem.split(" ")[0].equals(defaultVMName))) {
            log.trace("Default virtual machine doesn't exists ! Creating one !");
            String res = processService.executeCommand(createDefaultVMWithHyperV + " " + switchName + " " + defaultVMName);
            log.trace(res);
            String fileContent = null;
            try {
                fileContent = dockerAlgorithms.createDefaultDockerComposer();
                dockerAlgorithms.deployComposerFile(fileContent);
            } catch (IOException | URISyntaxException e) {
                log.trace("createDefaultDockerComposer failed with error : " + e.getMessage());
            }
        }else if (Arrays.stream(vms.split("\n")).noneMatch(elem -> elem.split(" ")[0].equals(defaultVMName) && elem.split(" ")[1].equals("Running"))) {
            log.trace("Default virtual machine is not running ! Starting the machine !");
            String res = processService.executeCommand(startDefaultVM + " " + defaultVMName);
            log.trace(res);
        }
        log.trace("Default virtual machine up and running !");
        VMStatus.countDown();
    }


}
