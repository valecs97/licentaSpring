package ro.vitoc.licenta.core.service;

import ro.vitoc.licenta.core.model.BaseProject;
import ro.vitoc.licenta.core.model.SimpleProject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

public interface CommonService {
    Integer createDockerFile(BaseProject project, Boolean web) throws IOException;
    Integer createDockerFile(SimpleProject project) throws IOException;
    String createDockerFileJava(int parameters) throws IOException, URISyntaxException;
    void createRequirementsFile(BaseProject project) throws IOException;
    Boolean preCheckProject(BaseProject project);
    Boolean postCheckProject(BaseProject project);
    void deleteFolder(String folder);
}
