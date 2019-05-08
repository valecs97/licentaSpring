package ro.vitoc.licenta.core.facade;

import org.springframework.data.domain.Example;
import ro.vitoc.licenta.core.dto.SimpleProjectDto;
import ro.vitoc.licenta.core.model.SimpleProject;

import java.util.List;

public interface SimpleProjectFacade {
    List<SimpleProjectDto> findAll();
    SimpleProjectDto find(Example<SimpleProject> example);
    SimpleProjectDto findByUrlAndBranch(String url, String branch);
    SimpleProjectDto createProjectScript(SimpleProjectDto simpleProjectdto, String branch, String location);
    SimpleProjectDto update(SimpleProjectDto simpleProjectDto);
    SimpleProject findSimpleProjectByName(String simpleProject);
}
