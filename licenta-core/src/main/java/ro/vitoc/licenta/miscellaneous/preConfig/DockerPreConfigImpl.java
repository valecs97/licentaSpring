package ro.vitoc.licenta.miscellaneous.preConfig;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.miscellaneous.algorithms.DockerAlgorithms;
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

    private ProcessService processService;
    private DockerAlgorithms dockerAlgorithms;

    @Autowired
    public DockerPreConfigImpl(ProcessService processService, DockerAlgorithms dockerAlgorithms) {
        this.processService = processService;
        this.dockerAlgorithms = dockerAlgorithms;
    }

}
