package ro.vitoc.licenta.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.vitoc.licenta.core.converter.SimpleScriptConvertor;
import ro.vitoc.licenta.core.dto.SimpleProjectDto;
import ro.vitoc.licenta.core.facade.CommonFacade;
import ro.vitoc.licenta.core.facade.DockerFacade;
import ro.vitoc.licenta.core.facade.GitFacade;
import ro.vitoc.licenta.core.facade.SimpleProjectFacade;
import ro.vitoc.licenta.core.model.SimpleProject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class SimpleProjectController {
    private static final Logger log = LoggerFactory.getLogger(SimpleProjectController.class);

    private SimpleProjectFacade simpleProjectFacade;
    private GitFacade gitFacade;
    private DockerFacade dockerFacade;
    private CommonFacade commonFacade;
    private SimpleScriptConvertor simpleScriptConvertor;

    @Autowired
    public SimpleProjectController(SimpleProjectFacade simpleProjectFacade, GitFacade gitFacade, DockerFacade dockerFacade, CommonFacade commonFacade, SimpleScriptConvertor simpleScriptConvertor) {
        this.simpleProjectFacade = simpleProjectFacade;
        this.gitFacade = gitFacade;
        this.dockerFacade = dockerFacade;
        this.commonFacade = commonFacade;
        this.simpleScriptConvertor = simpleScriptConvertor;
    }


    @RequestMapping(value = "/simpleProject", method = RequestMethod.GET)
    public ResponseEntity executeSimpleProject(
            @RequestParam(value = "name") String tag,
            @RequestParam(value = "args") List<String> args
    ) {
        log.trace("executeSimpleScript");

        long start = System.nanoTime();

        SimpleProject res = simpleProjectFacade.findSimpleProjectByName(tag);

        log.trace("Project found, res={}",res);
        if (res == null || !res.getAvailable())
            return new ResponseEntity("The project does not exist or is not available at the moment (an upgrade is on it's way) !", HttpStatus.NOT_FOUND);

        String result;
        if (res.getLang().toLowerCase().contains("python"))
            result = dockerFacade.runImage(tag, args, true);
        else
            result = dockerFacade.runImage(tag, args, false);

        log.trace("executeSimpleScript: result=\n{}", result);

        log.trace("Performance executing ms={}", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)/100);

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/simpleProject", method = RequestMethod.POST)
    public ResponseEntity createSimpleProject(
            @RequestBody final SimpleProjectDto simpleProjectDto) {
        log.trace("createSimpleScript: simpleProjectDto={}", simpleProjectDto);

        long start = System.nanoTime();

        if (simpleProjectFacade.findSimpleProjectByName(simpleProjectDto.getName()) != null) {
            return new ResponseEntity("A project with the same name exists already !", HttpStatus.CONFLICT);
        }

        simpleProjectDto.setLang(gitFacade.detectLanguage(simpleProjectDto.getGitUrl().split("/")[3], simpleProjectDto.getGitUrl().split("/")[4]));
        //return new ResponseEntity("All ok till now with " + simpleProjectDto.getLang(),HttpStatus.OK);
        SimpleProject simpleProject = simpleScriptConvertor.convertDtoToModel(simpleProjectDto);
        String branch = gitFacade.getBranchRef(simpleProjectDto.getGitUrl(), simpleProjectDto.getBranch()).getName();
        simpleProject.setBranch(branch);
        simpleProject.setLocation(gitFacade.getLocation(simpleProject.getName()));

        if (!commonFacade.preCheckProject(simpleProject))
            return new ResponseEntity("Programming language not supported or container name already exists !", HttpStatus.CONFLICT);

        if (!gitFacade.cloneGitRepository(simpleProjectDto.getName(), simpleProjectDto.getGitUrl(), branch))
            return new ResponseEntity("Repository does not exists ! Or another error.", HttpStatus.NOT_FOUND);

        if (!commonFacade.postCheckProject(simpleProject))
            return new ResponseEntity("Main file cannot be found ! Process is reversing !", HttpStatus.NOT_FOUND);

        try {
            commonFacade.createRequirementsFile(simpleProject);
            switch (commonFacade.createDockerFile(simpleProject)) {
                case 1:
                    return new ResponseEntity("The programming language specified by you does not exists !", HttpStatus.CONFLICT);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity("Cannot create docker files !", HttpStatus.CONFLICT);
        }

        String result = dockerFacade.createImage(simpleProject);

        log.trace(result);

        SimpleProjectDto dto = simpleProjectFacade.createProjectScript(simpleProjectDto, branch, gitFacade.getLocation(simpleProject.getName()));

        log.trace("createdSimpleScript: dto={}", dto);

        log.trace("Performance adding simple project ms={}",TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)/100);

        return new ResponseEntity("All ok !", HttpStatus.OK);
    }
}
