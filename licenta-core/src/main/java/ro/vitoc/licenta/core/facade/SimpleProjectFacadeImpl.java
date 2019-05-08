package ro.vitoc.licenta.core.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
    public SimpleProjectDto find(Example<SimpleProject> example) {
        log.trace("find simple project dao, example={}",example.getProbe());
        SimpleProject result = simpleProjectService.find(example);
        return result != null ? simpleScriptConvertor.convertModelToDto(result) : null;
    }

    @Override
    public SimpleProjectDto findByUrlAndBranch(String url, String branch) {
        log.trace("findByUrlAndBranch simple project dao, url={},branch={}",url,branch);
        SimpleProject result = simpleProjectService.findByUrlAndBranch(url,branch);
        return result != null ? simpleScriptConvertor.convertModelToDto(result) : null;
    }

    @Override
    public SimpleProjectDto createProjectScript(SimpleProjectDto simpleProjectdto,String branch, String location) {
        log.trace("createUpdateProjectScript dao, simpleProjectDTO={}",simpleProjectdto);
        SimpleProject simpleProject = simpleScriptConvertor.convertDtoToModel(simpleProjectdto);
        simpleProject.setBranch(branch);
        simpleProject.setLocation(location);
        return simpleScriptConvertor.convertModelToDto(simpleProjectService.createUpdateProjectScript(simpleProject));
    }

    @Override
    public SimpleProjectDto update(SimpleProjectDto simpleProjectDto) {
        log.trace("updateProject dao, simpleProjectDTO={}",simpleProjectDto);
        SimpleProject oldProject = simpleProjectService.find(Example.of(SimpleProject.builder().name(simpleProjectDto.getName()).build()));
        SimpleProject newProject = simpleScriptConvertor.convertDtoToModel(simpleProjectDto);
        log.trace("updateProject dao oldProject={}, newProject={}",oldProject,newProject);
        newProject.setBranch(oldProject.getBranch());
        newProject.setLocation(oldProject.getLocation());
        newProject.setId(oldProject.getId());
        return simpleScriptConvertor.convertModelToDto(simpleProjectService.createUpdateProjectScript(newProject));
    }

    @Override
    public SimpleProject findSimpleProjectByName(String simpleProject) {
        log.trace("findSimpleProjectByName dao");
        SimpleProject result =  simpleProjectService.find(Example.of(SimpleProject.builder().name(simpleProject).build()));
        return result;
    }
}
