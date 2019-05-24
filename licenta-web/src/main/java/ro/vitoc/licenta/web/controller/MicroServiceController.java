package ro.vitoc.licenta.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
import ro.vitoc.licenta.core.model.WebMicroService;
import ro.vitoc.licenta.core.service.CommonService;
import ro.vitoc.licenta.core.service.DockerService;
import ro.vitoc.licenta.core.service.GitService;
import ro.vitoc.licenta.web.preConfig.DockerPreConfig;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class MicroServiceController {
    private static final Logger log = LoggerFactory.getLogger(MicroServiceController.class);
    private GitFacade gitFacade;
    private DockerFacade dockerFacade;
    private CommonFacade commonFacade;
    private MicroServiceFacade microServiceFacade;
    private MicroServiceConvertor microServiceConvertor;
    private DockerPreConfig dockerPreConfig;

    @Autowired
    public MicroServiceController(GitFacade gitFacade, DockerFacade dockerFacade, CommonFacade commonFacade, MicroServiceFacade microServiceFacade, MicroServiceConvertor microServiceConvertor, DockerPreConfig dockerPreConfig) {
        this.gitFacade = gitFacade;
        this.dockerFacade = dockerFacade;
        this.commonFacade = commonFacade;
        this.microServiceFacade = microServiceFacade;
        this.microServiceConvertor = microServiceConvertor;
        this.dockerPreConfig = dockerPreConfig;
    }

    @RequestMapping(value = "/microServices", method = RequestMethod.GET)
    public List<MicroServiceDto> getWebMicroServices() {
        log.trace("getWebMicroServices");

        List<MicroServiceDto> microServiceDtos = microServiceFacade.findAll();

        log.trace("getWebMicroServices: microServiceDtos={}", microServiceDtos);

        return microServiceDtos;
    }

    @RequestMapping(value = "/microService", method = RequestMethod.POST)
    public ResponseEntity createMicroService(
            @RequestBody final MicroServiceDto microServiceDto) {
        log.trace("createMicroService: microServiceDto={}", microServiceDto);
        long start = System.nanoTime();
        if (microServiceFacade.find(Example.of(MicroService.builder().name(microServiceDto.getName()).build())) != null){
            return new ResponseEntity("A project with the same name exists already !", HttpStatus.CONFLICT);
        }

        microServiceDto.setLang(gitFacade.detectLanguage(microServiceDto.getGitUrl().split("/")[3],microServiceDto.getGitUrl().split("/")[4]));
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
            switch (commonFacade.createDockerFile(microService,false)){
                case 1:
                    return new ResponseEntity("The programming language specified by you does not exists !",HttpStatus.CONFLICT);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity("Cannot create docker files !",HttpStatus.CONFLICT);
        }

        String result = dockerFacade.createImage(microService);
        log.trace(result);
        result = dockerFacade.pushImage(microService);
        log.trace(result);

        MicroServiceDto dto = microServiceFacade.createMicroService(microServiceDto,branch,gitFacade.getLocation(microServiceDto.getName()));

        log.trace("createdSimpleScript: dto={}", dto);

        dockerFacade.redeployAll();

        dockerPreConfig.attachLogToWebSocket(microServiceConvertor.convertDtoToModel(dto));

        log.trace("Performance adding micro ms={}", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)/100);

        return new ResponseEntity("All ok !", HttpStatus.OK);
    }
}
