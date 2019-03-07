package ro.vitoc.licenta.core.service;

import ro.vitoc.licenta.core.model.BaseProject;

import java.io.IOException;

public interface CommonService {
    Integer createDockerFile(BaseProject project) throws IOException;
    void createRequirementsFile(BaseProject project) throws IOException;

}
