package ro.vitoc.licenta.miscellaneous.preCheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ro.vitoc.licenta.miscellaneous.preConfig.DockerPreConfig;
import ro.vitoc.licenta.miscellaneous.preConfig.DockerPreConfigImpl;
import ro.vitoc.licenta.miscellaneous.service.ProcessService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class DockerPreCheckImpl implements DockerPreCheck {
    private static final Logger log = LoggerFactory.getLogger(DockerPreCheckImpl.class);
    public static CountDownLatch VMStatus = new CountDownLatch(1);

    private DockerPreConfig dockerPreConfig;
    private ProcessService processService;

    @Value("${docker.defaultVMName}")
    private String defaultVMName;

    @Value("${docker.switch}")
    private String switchName;

    private static String getVMsCommand = "docker-machine ls -f \"{{.Name}} {{.State}} {{.URL}}\"";
    private static String createDefaultVMWithHyperV = "docker-machine create -d hyperv --hyperv-virtual-switch";
    private static String startDefaultVM = "docker-machine start";

    @Autowired
    public DockerPreCheckImpl(DockerPreConfig dockerPreConfig, ProcessService processService) {
        this.dockerPreConfig = dockerPreConfig;
        this.processService = processService;
    }

    @PostConstruct
    public void checkDefaultVM() throws IOException {
        String vms = processService.executeCommand(getVMsCommand);
        if (Arrays.stream(vms.split("\n")).noneMatch(elem -> elem.split(" ")[0].equals(defaultVMName))) {
            log.trace("Default virtual machine doesn't exists ! Creating one !");
            String res = processService.executeCommand(createDefaultVMWithHyperV + " " + switchName + " " + defaultVMName);
            log.trace(res);
            dockerPreConfig.createDefaultVM();
        }else if (Arrays.stream(vms.split("\n")).noneMatch(elem -> elem.split(" ")[0].equals(defaultVMName) && elem.split(" ")[1].equals("Running"))) {
            log.trace("Default virtual machine is not running ! Starting the machine !");
            String res = processService.executeCommand(startDefaultVM + " " + defaultVMName);
            log.trace(res);
        }
        log.trace("Default virtual machine up and running !");
        VMStatus.countDown();
    }
}
