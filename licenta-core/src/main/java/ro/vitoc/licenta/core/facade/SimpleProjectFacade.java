package ro.vitoc.licenta.core.facade;

import ro.vitoc.licenta.core.dto.SimpleProjectDto;
import ro.vitoc.licenta.core.model.SimpleProject;

import java.util.List;

public interface SimpleProjectFacade {
    List<SimpleProjectDto> findAll();
    SimpleProjectDto createProjectScript(SimpleProjectDto simpleProjectdto, String branch, String location);
    SimpleProject findSimpleProjectByName(String simpleProject);
}
