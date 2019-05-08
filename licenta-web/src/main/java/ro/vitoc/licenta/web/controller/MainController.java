package ro.vitoc.licenta.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.vitoc.licenta.core.converter.MicroServiceConvertor;
import ro.vitoc.licenta.core.converter.WebMicroServiceConvertor;
import ro.vitoc.licenta.core.dto.MicroServiceDto;
import ro.vitoc.licenta.core.dto.WebMicroServiceDto;
import ro.vitoc.licenta.core.facade.*;
import ro.vitoc.licenta.core.model.BaseProject;
import ro.vitoc.licenta.core.model.MicroService;
import ro.vitoc.licenta.core.model.SimpleProject;
import ro.vitoc.licenta.core.model.WebMicroService;
import ro.vitoc.licenta.core.service.CommonService;
import ro.vitoc.licenta.core.service.DockerService;
import ro.vitoc.licenta.core.service.GitService;
import ro.vitoc.licenta.core.service.SimpleProjectService;
import ro.vitoc.licenta.core.converter.SimpleScriptConvertor;
import ro.vitoc.licenta.core.dto.SimpleProjectDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class MainController {
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    private SimpleProjectFacade simpleProjectFacade;
    private MicroServiceFacade microServiceFacade;
    private WebMicroServiceFacade webMicroServiceFacade;
    private GitFacade gitFacade;
    private DockerFacade dockerFacade;
    private CommonFacade commonFacade;
    private SimpleScriptConvertor simpleScriptConvertor;
    private MicroServiceConvertor microServiceConvertor;
    private WebMicroServiceConvertor webMicroServiceConvertor;

    @Autowired
    public MainController(SimpleProjectFacade simpleProjectFacade, MicroServiceFacade microServiceFacade, WebMicroServiceFacade webMicroServiceFacade, GitFacade gitFacade, DockerFacade dockerFacade, CommonFacade commonFacade, SimpleScriptConvertor simpleScriptConvertor, MicroServiceConvertor microServiceConvertor, WebMicroServiceConvertor webMicroServiceConvertor) {
        this.simpleProjectFacade = simpleProjectFacade;
        this.microServiceFacade = microServiceFacade;
        this.webMicroServiceFacade = webMicroServiceFacade;
        this.gitFacade = gitFacade;
        this.dockerFacade = dockerFacade;
        this.commonFacade = commonFacade;
        this.simpleScriptConvertor = simpleScriptConvertor;
        this.microServiceConvertor = microServiceConvertor;
        this.webMicroServiceConvertor = webMicroServiceConvertor;
    }

    @RequestMapping(value = "/simpleProjects", method = RequestMethod.GET)
    public List<SimpleProjectDto> getSimpleScripts() {
        log.trace("getSimpleScripts");

        List<SimpleProjectDto> simpleProjects = simpleProjectFacade.findAll();

        log.trace("getSimpleScripts: simpleProjects={}", simpleProjects);

        return simpleProjects;
    }

    @RequestMapping(value = "/webhook", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity webhook(HttpEntity<String> payload){
        log.trace("webhook New payload received, payload={}",payload);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(payload.getBody());
        } catch (IOException e) {
            log.trace("webhook failed to parse body with message={}",e.getMessage());
            return new ResponseEntity("webhook failed to parse body with message={}",HttpStatus.BAD_REQUEST);
        }
        String url = root.path("repository").path("url").asText();
        String ref = root.path("ref").asText();
        log.trace("webhook New payload for url={}, ref={}",url,ref);

        SimpleProjectDto simpleProjectDto = simpleProjectFacade.findByUrlAndBranch(url,ref);
        if (simpleProjectDto != null){
            log.trace("webhook Update project with name={}",simpleProjectDto.getName());
            simpleProjectDto.setAvailable(false);
            simpleProjectDto = simpleProjectFacade.update(simpleProjectDto);
            commonFacade.deleteFolder(gitFacade.getLocation(simpleProjectDto.getName()));
            gitFacade.cloneGitRepository(simpleProjectDto.getName(),simpleProjectDto.getGitUrl(),simpleProjectDto.getBranch());
            SimpleProject simpleProject = simpleScriptConvertor.convertDtoToModel(simpleProjectDto);
            simpleProject.setLocation(gitFacade.getLocation(simpleProject.getName()));
            try {
                commonFacade.createRequirementsFile(simpleProject);
                switch (commonFacade.createDockerFile(simpleProject)){
                    case 1:
                        log.trace("webhook The programming language specified by you does not exists !");
                        return new ResponseEntity("webhook The programming language specified by you does not exists !",HttpStatus.CONFLICT);
                }
            } catch (IOException e) {
                log.trace("webhook Cannot create docker files ! message={}",e.getMessage());
                return new ResponseEntity("ebhook Cannot create docker files !",HttpStatus.CONFLICT);
            }

            pushNewImage(simpleProject);

            simpleProjectDto.setAvailable(true);
            simpleProjectDto = simpleProjectFacade.update(simpleProjectDto);

            log.trace("webhook IMAGE UPDATED name={}",simpleProjectDto.getName());
            return new ResponseEntity("ALL OK",HttpStatus.OK);
        }
        MicroServiceDto microServiceDto = microServiceFacade.findByUrlAndBranch(url,ref);
        if (microServiceDto != null){
            log.trace("webhook Update project with name={}",microServiceDto.getName());
            microServiceDto.setAvailable(false);
            microServiceDto = microServiceFacade.update(microServiceDto);

            commonFacade.deleteFolder(gitFacade.getLocation(microServiceDto.getName()));
            gitFacade.cloneGitRepository(microServiceDto.getName(),microServiceDto.getGitUrl(),microServiceDto.getBranch());
            MicroService microService = microServiceConvertor.convertDtoToModel(microServiceDto);
            microService.setLocation(gitFacade.getLocation(microService.getName()));
            try {
                commonFacade.createRequirementsFile(microService);
                switch (commonFacade.createDockerFile(microService,false)){
                    case 1:
                        log.trace("webhook The programming language specified by you does not exists !");
                        return new ResponseEntity("webhook The programming language specified by you does not exists !",HttpStatus.CONFLICT);
                }
            } catch (IOException e) {
                log.trace("webhook Cannot create docker files ! message={}",e.getMessage());
                return new ResponseEntity("ebhook Cannot create docker files !",HttpStatus.CONFLICT);
            }

            pushNewImage(microService);

            microServiceDto.setAvailable(true);
            microServiceDto = microServiceFacade.update(microServiceDto);
            dockerFacade.redeployAll();

            log.trace("webhook IMAGE UPDATED name={}",microServiceDto.getName());
            return new ResponseEntity("ALL OK",HttpStatus.OK);
        }
        WebMicroServiceDto webMicroServiceDto = webMicroServiceFacade.findByUrlAndBranch(url,ref);
        if (webMicroServiceDto != null){
            log.trace("webhook Update project with name={}",webMicroServiceDto.getName());
            webMicroServiceDto.setAvailable(false);
            webMicroServiceDto = webMicroServiceFacade.update(webMicroServiceDto);

            commonFacade.deleteFolder(gitFacade.getLocation(webMicroServiceDto.getName()));
            gitFacade.cloneGitRepository(webMicroServiceDto.getName(),webMicroServiceDto.getGitUrl(),webMicroServiceDto.getBranch());
            WebMicroService webMicroService = webMicroServiceConvertor.convertDtoToModel(webMicroServiceDto);
            webMicroService.setLocation(gitFacade.getLocation(webMicroService.getName()));
            try {
                commonFacade.createRequirementsFile(webMicroService);
                switch (commonFacade.createDockerFile(webMicroService,true)){
                    case 1:
                        log.trace("webhook The programming language specified by you does not exists !");
                        return new ResponseEntity("webhook The programming language specified by you does not exists !",HttpStatus.CONFLICT);
                }
            } catch (IOException e) {
                log.trace("webhook Cannot create docker files ! message={}",e.getMessage());
                return new ResponseEntity("ebhook Cannot create docker files !",HttpStatus.CONFLICT);
            }

            pushNewImage(webMicroService);

            webMicroServiceDto.setAvailable(true);
            webMicroServiceDto = webMicroServiceFacade.update(webMicroServiceDto);
            dockerFacade.redeployAll();

            log.trace("webhook IMAGE UPDATED name={}",webMicroServiceDto.getName());
            return new ResponseEntity("ALL OK",HttpStatus.OK);
        }
        return new ResponseEntity("No project matches this webhook !", HttpStatus.NOT_FOUND);
    }

    private void pushNewImage(BaseProject project){
        dockerFacade.removeImage(project);
        dockerFacade.createImage(project);
        dockerFacade.pushImage(project);
    }
}
