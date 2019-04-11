package ro.vitoc.licenta.miscellaneous.algorithms;

import ro.vitoc.licenta.core.model.BaseProject;
import ro.vitoc.licenta.core.model.WebMicroService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface DockerAlgorithms {
    String createDefaultDockerComposer() throws IOException, URISyntaxException;
    String createDefaultDockerComposer(List<WebMicroService> configs) throws IOException, URISyntaxException;
    void deployComposerFile(String fileContent) throws IOException;
    void rebalanceStack(List<WebMicroService> configs);
}
