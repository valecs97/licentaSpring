package ro.vitoc.licenta.core.facade;

import ro.vitoc.licenta.core.dto.BaseProjectDto;
import ro.vitoc.licenta.core.dto.SimpleProjectDto;
import ro.vitoc.licenta.core.model.BaseProject;
import ro.vitoc.licenta.core.model.SimpleProject;

import java.io.IOException;

public interface CommonFacade {
    Integer createDockerFile(BaseProject project, Boolean web) throws IOException;
    Integer createDockerFile(SimpleProject project) throws IOException;
    void createRequirementsFile(BaseProject project) throws IOException;
    Boolean preCheckProject(BaseProject project);
    Boolean postCheckProject(BaseProject project);
    void deleteFolder(String folder);
}
