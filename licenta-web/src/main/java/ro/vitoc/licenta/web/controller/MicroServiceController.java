package ro.vitoc.licenta.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ro.vitoc.licenta.core.converter.MicroServiceConvertor;
import ro.vitoc.licenta.core.dto.MicroServiceDto;
import ro.vitoc.licenta.core.dto.SimpleProjectDto;
import ro.vitoc.licenta.core.facade.CommonFacade;
import ro.vitoc.licenta.core.facade.DockerFacade;
import ro.vitoc.licenta.core.facade.GitFacade;
import ro.vitoc.licenta.core.facade.MicroServiceFacade;
import ro.vitoc.licenta.core.model.MicroService;
import ro.vitoc.licenta.core.model.SimpleProject;
import ro.vitoc.licenta.core.service.CommonService;
import ro.vitoc.licenta.core.service.DockerService;
import ro.vitoc.licenta.core.service.GitService;

import java.io.IOException;

@RestController
public class MicroServiceController {
    private static final Logger log = LoggerFactory.getLogger(MicroServiceController.class);
    private GitFacade gitFacade;
    private DockerFacade dockerFacade;
    private CommonFacade commonFacade;
    private MicroServiceFacade microServiceFacade;
    private MicroServiceConvertor microServiceConvertor;

    @Autowired
    public MicroServiceController(GitFacade gitFacade, DockerFacade dockerFacade, CommonFacade commonFacade, MicroServiceFacade microServiceFacade, MicroServiceConvertor microServiceConvertor) {
        this.gitFacade = gitFacade;
        this.dockerFacade = dockerFacade;
        this.commonFacade = commonFacade;
        this.microServiceFacade = microServiceFacade;
        this.microServiceConvertor = microServiceConvertor;
    }

    @RequestMapping(value = "/microService", method = RequestMethod.POST)
    public ResponseEntity createMicroService(
            @RequestBody final MicroServiceDto microServiceDto) {
        log.trace("createMicroService: microServiceDto={}", microServiceDto);

        MicroService microService = microServiceConvertor.convertDtoToModel(microServiceDto);
        String branch = gitFacade.getBranchRef(microServiceDto.getGitUrl(), microServiceDto.getBranch()).getName();
        microService.setBranch(branch);
        microService.setLocation(gitFacade.getLocation(microService.getName()));

        if (!commonFacade.preCheckProject(microService))
            return new ResponseEntity("Programming language not supported or cointainer name already exists !", HttpStatus.CONFLICT);

        if (!gitFacade.cloneGitRepository(microServiceDto.getName(), microServiceDto.getGitUrl(), branch))
            return new ResponseEntity("Repository does not exists ! Or another error.",HttpStatus.NOT_FOUND);

        if (!commonFacade.postCheckProject(microService))
            return new ResponseEntity("Main file cannot be found ! Process is reversing !",HttpStatus.NOT_FOUND);

        try {
            commonFacade.createRequirementsFile(microService);
            switch (commonFacade.createDockerFile(microService)){
                case 1:
                    return new ResponseEntity("The programming language specified by you does not exists !",HttpStatus.CONFLICT);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity("Cannot create docker files !",HttpStatus.CONFLICT);
        }

        String result = dockerFacade.createImage(microService);
        log.trace(result);
        String containerId = dockerFacade.createContainer(microService);
        dockerFacade.startContainer(containerId);



        MicroServiceDto dto = microServiceFacade.createMicroService(microServiceDto,branch,gitFacade.getLocation(microServiceDto.getName()),containerId);

        log.trace("createdSimpleScript: dto={}", dto);

        return new ResponseEntity("All ok !", HttpStatus.OK);
    }
}
