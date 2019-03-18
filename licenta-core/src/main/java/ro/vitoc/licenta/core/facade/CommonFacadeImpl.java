package ro.vitoc.licenta.core.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.core.dto.BaseProjectDto;
import ro.vitoc.licenta.core.dto.SimpleProjectDto;
import ro.vitoc.licenta.core.model.BaseProject;
import ro.vitoc.licenta.core.model.SimpleProject;
import ro.vitoc.licenta.core.service.CommonService;
import ro.vitoc.licenta.core.service.CommonServiceImpl;

import java.io.IOException;

@Component
public class CommonFacadeImpl implements CommonFacade{
    private static final Logger log = LoggerFactory.getLogger(CommonServiceImpl.class);
    @Autowired
    private CommonService commonService;

    @Override
    public Integer createDockerFile(BaseProject project) throws IOException {
        log.trace("createDockerFile dao, projectName={}",project.getName());
        return commonService.createDockerFile(project);
    }

    @Override
    public Integer createDockerFile(SimpleProject project) throws IOException {
        log.trace("createDockerFile dao, projectName={}",project.getName());
        return commonService.createDockerFile(project);
    }

    @Override
    public void createRequirementsFile(BaseProject project) throws IOException {
        log.trace("createRequirementsFile dao, projectName={}",project.getName());
        commonService.createRequirementsFile(project);
    }

    @Override
    public Boolean preCheckProject(BaseProject project) {
        log.trace("preCheckProject dao, projectName={}",project.getName());
        return commonService.preCheckProject(project);
    }

    @Override
    public Boolean postCheckProject(BaseProject project) {
        log.trace("postCheckProject dao, projectName={}",project.getName());
        return commonService.postCheckProject(project);
    }
}
