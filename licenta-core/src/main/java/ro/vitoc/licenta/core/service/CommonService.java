package ro.vitoc.licenta.core.service;

import ro.vitoc.licenta.core.model.BaseProject;
import ro.vitoc.licenta.core.model.SimpleProject;

import java.io.IOException;

public interface CommonService {
    Integer createDockerFile(BaseProject project) throws IOException;
    Integer createDockerFile(SimpleProject project) throws IOException;
    void createRequirementsFile(BaseProject project) throws IOException;
    Boolean preCheckProject(BaseProject project);
    Boolean postCheckProject(BaseProject project);
}
