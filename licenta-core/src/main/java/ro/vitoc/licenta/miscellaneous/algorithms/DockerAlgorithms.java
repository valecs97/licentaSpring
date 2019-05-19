package ro.vitoc.licenta.miscellaneous.algorithms;

import ro.vitoc.licenta.core.model.BaseProject;
import ro.vitoc.licenta.core.model.MicroService;
import ro.vitoc.licenta.core.model.WebMicroService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;

public interface DockerAlgorithms {
    String createDefaultDockerComposer() throws IOException, URISyntaxException;
    String createDefaultDockerComposer(List<WebMicroService> configs, List<MicroService> configs2) throws IOException, URISyntaxException;


    void createSwarm() throws IOException;
    void deployComposerFile(String fileContent) throws IOException;
    void rebalanceStack(List<WebMicroService> configs,List<MicroService> configs2);
}
