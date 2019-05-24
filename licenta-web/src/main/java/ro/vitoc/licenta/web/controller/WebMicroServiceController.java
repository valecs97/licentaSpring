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
import ro.vitoc.licenta.core.converter.WebMicroServiceConvertor;
import ro.vitoc.licenta.core.dto.WebMicroServiceDto;
import ro.vitoc.licenta.core.facade.CommonFacade;
import ro.vitoc.licenta.core.facade.DockerFacade;
import ro.vitoc.licenta.core.facade.GitFacade;
import ro.vitoc.licenta.core.facade.WebMicroServiceFacade;
import ro.vitoc.licenta.core.model.WebMicroService;
import ro.vitoc.licenta.web.preConfig.DockerPreConfig;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class WebMicroServiceController {
    private static final Logger log = LoggerFactory.getLogger(WebMicroServiceController.class);
    private GitFacade gitFacade;
    private DockerFacade dockerFacade;
    private CommonFacade commonFacade;
    private WebMicroServiceFacade webMicroServiceFacade;
    private WebMicroServiceConvertor webMicroServiceConvertor;
    private DockerPreConfig dockerPreConfig;

    @Autowired
    public WebMicroServiceController(GitFacade gitFacade, DockerFacade dockerFacade, CommonFacade commonFacade, WebMicroServiceFacade webMicroServiceFacade, WebMicroServiceConvertor webMicroServiceConvertor, DockerPreConfig dockerPreConfig) {
        this.gitFacade = gitFacade;
        this.dockerFacade = dockerFacade;
        this.commonFacade = commonFacade;
        this.webMicroServiceFacade = webMicroServiceFacade;
        this.webMicroServiceConvertor = webMicroServiceConvertor;
        this.dockerPreConfig = dockerPreConfig;
    }

    @RequestMapping(value = "/webMicroServices", method = RequestMethod.GET)
    public List<WebMicroServiceDto> getWebMicroServices() {
        log.trace("getWebMicroServices");

        List<WebMicroServiceDto> webMicroService = webMicroServiceFacade.findAll();

        log.trace("webMicroServices: webMicroService={}", webMicroService);

        return webMicroService;
    }

    @RequestMapping(value = "/webMicroService", method = RequestMethod.POST)
    public ResponseEntity createWebMicroService(
            @RequestBody final WebMicroServiceDto webMicroServiceDto) {
        log.trace("createWebMicroService: webMicroServiceDto={}", webMicroServiceDto);
        long start = System.nanoTime();
        if (webMicroServiceFacade.find(Example.of(WebMicroService.builder().name(webMicroServiceDto.getName()).build())) != null){
            return new ResponseEntity("A project with the same name exists already !", HttpStatus.CONFLICT);
        }

        webMicroServiceDto.setLang(gitFacade.detectLanguage(webMicroServiceDto.getGitUrl().split("/")[3],webMicroServiceDto.getGitUrl().split("/")[4]));
        WebMicroService webMicroService = webMicroServiceConvertor.convertDtoToModel(webMicroServiceDto);
        String branch = gitFacade.getBranchRef(webMicroServiceDto.getGitUrl(), webMicroServiceDto.getBranch()).getName();
        webMicroService.setBranch(branch);
        webMicroService.setLocation(gitFacade.getLocation(webMicroService.getName()));

        if (!commonFacade.preCheckProject(webMicroService))
            return new ResponseEntity("Programming language not supported or cointainer name already exists !", HttpStatus.CONFLICT);

        if (!gitFacade.cloneGitRepository(webMicroService.getName(), webMicroService.getGitUrl(), branch))
            return new ResponseEntity("Repository does not exists ! Or another error.",HttpStatus.NOT_FOUND);

        if (!commonFacade.postCheckProject(webMicroService))
            return new ResponseEntity("Main file cannot be found ! Process is reversing !",HttpStatus.NOT_FOUND);

        try {
            commonFacade.createRequirementsFile(webMicroService);
            switch (commonFacade.createDockerFile(webMicroService,true)){
                case 1:
                    return new ResponseEntity("The programming language specified by you does not exists !",HttpStatus.CONFLICT);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity("Cannot create docker files !",HttpStatus.CONFLICT);
        }

        String result = dockerFacade.createImage(webMicroService);
        log.trace(result);
        result = dockerFacade.pushImage(webMicroService);
        log.trace(result);

        WebMicroServiceDto dto = webMicroServiceFacade.createWebMicroService(webMicroServiceDto,branch,gitFacade.getLocation(webMicroServiceDto.getName()));

        log.trace("createdSimpleScript: dto={}", dto);

        dockerFacade.redeployAll();

        dockerPreConfig.attachLogToWebSocket(webMicroServiceConvertor.convertDtoToModel(dto));

        log.trace("Performance adding webmicro ms={}", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)/100);

        return new ResponseEntity("All ok !", HttpStatus.OK);
    }
}
