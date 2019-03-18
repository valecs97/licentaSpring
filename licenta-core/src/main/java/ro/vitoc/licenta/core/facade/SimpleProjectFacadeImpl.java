package ro.vitoc.licenta.core.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.core.converter.SimpleScriptConvertor;
import ro.vitoc.licenta.core.dto.SimpleProjectDto;
import ro.vitoc.licenta.core.model.SimpleProject;
import ro.vitoc.licenta.core.service.GitService;
import ro.vitoc.licenta.core.service.SimpleProjectService;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SimpleProjectFacadeImpl implements SimpleProjectFacade{
    private static final Logger log = LoggerFactory.getLogger(SimpleProjectFacadeImpl.class);

    private SimpleProjectService simpleProjectService;
    private SimpleScriptConvertor simpleScriptConvertor;

    @Autowired
    public SimpleProjectFacadeImpl(SimpleProjectService simpleProjectService, SimpleScriptConvertor simpleScriptConvertor) {
        this.simpleProjectService = simpleProjectService;
        this.simpleScriptConvertor = simpleScriptConvertor;
    }

    @Override
    public List<SimpleProjectDto> findAll() {
        log.trace("findAll simpleProjects dao");
        return simpleProjectService.findAll().stream().map(elem -> simpleScriptConvertor.convertModelToDto(elem)).collect(Collectors.toList());
    }

    @Override
    public SimpleProjectDto createProjectScript(SimpleProjectDto simpleProjectdto,String branch, String location) {
        log.trace("createProjectScript dao, simpleProjectDTO={}",simpleProjectdto);
        SimpleProject simpleProject = simpleScriptConvertor.convertDtoToModel(simpleProjectdto);
        simpleProject.setBranch(branch);
        simpleProject.setLocation(location);
        return simpleScriptConvertor.convertModelToDto(simpleProjectService.createProjectScript(simpleProject));
    }

    @Override
    public SimpleProject findSimpleProjectByName(String simpleProject) {
        log.trace("findSimpleProjectByName dao");
        return simpleProjectService.findSimpleProjectByName(simpleProject);
    }
}
