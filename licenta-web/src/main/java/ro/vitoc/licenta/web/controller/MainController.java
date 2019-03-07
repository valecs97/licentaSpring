package ro.vitoc.licenta.web.controller;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.vitoc.licenta.core.model.BaseProject;
import ro.vitoc.licenta.core.model.SimpleScript;
import ro.vitoc.licenta.core.service.CommonService;
import ro.vitoc.licenta.core.service.DockerService;
import ro.vitoc.licenta.core.service.GitService;
import ro.vitoc.licenta.core.service.SimpleScriptService;
import ro.vitoc.licenta.web.converter.SimpleScriptConvertor;
import ro.vitoc.licenta.web.dto.EmptyJsonResponse;
import ro.vitoc.licenta.web.dto.SimpleScriptDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private SimpleScriptService simpleScriptService;
    @Autowired
    private GitService gitService;
    @Autowired
    private DockerService dockerService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private SimpleScriptConvertor simpleScriptConvertor;


    @RequestMapping(value = "/simpleScripts", method = RequestMethod.GET)
    public List<SimpleScriptDto> getSimpleScripts() {
        log.trace("getSimpleScripts");

        List<SimpleScript> simpleScripts = simpleScriptService.findAll();

        log.trace("getSimpleScripts: simpleScripts={}", simpleScripts);

        return new ArrayList<>(simpleScriptConvertor.convertModelsToDtos(simpleScripts));
    }
/*
    @RequestMapping(value = "/students/{studentId}", method = RequestMethod.PUT)
    public StudentDto updateStudent(
            @PathVariable final Long studentId,
            @RequestBody final StudentDto studentDto) {
        log.trace("updateStudent: studentId={}, studentDtoMap={}", studentId, studentDto);

        Student student = simpleScriptService.updateStudent(studentId,
                studentDto.getSerialNumber(),
                studentDto.getName(), studentDto.getGroupNumber());

        StudentDto result = studentConverter.convertModelToDto(student);

        log.trace("updateStudent: result={}", result);

        return result;
    }*/

    @RequestMapping(value = "/simpleScript", method = RequestMethod.GET)
    public ResponseEntity executeSimpleScript(
            @RequestParam(value = "name") String tag,
            @RequestParam(value = "args") List<String> args
    ) {
        log.trace("executeSimpleScript");

        String result = dockerService.runImage(tag,args);

        log.trace("executeSimpleScript: result={}", result);

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/simpleScript", method = RequestMethod.POST)
    public ResponseEntity createSimpleScript(
            @RequestBody final SimpleScriptDto simpleScriptDto) {
        log.trace("createSimpleScript: simpleScriptDto={}", simpleScriptDto);

        SimpleScript simpleScript = simpleScriptConvertor.convertDtoToModel(simpleScriptDto);
        String branch = gitService.getBranchRef(simpleScriptDto.getGitUrl(),simpleScriptDto.getBranch()).getName();
        simpleScript.setBranch(branch);
        simpleScript.setLocation(gitService.getLocation(simpleScript.getName()));

        if (!gitService.cloneGitRepository(simpleScript.getName(),simpleScript.getGitUrl(), simpleScript.getBranch()))
            return new ResponseEntity("Repository does not exists ! Or another error.",HttpStatus.NOT_FOUND);

        try {
            commonService.createRequirementsFile(simpleScript);
            switch (commonService.createDockerFile(simpleScript)){
                case 1:
                    return new ResponseEntity("The programming language specified by you does not exists !",HttpStatus.CONFLICT);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity("Cannot create docker files !",HttpStatus.CONFLICT);
        }

        String result = dockerService.createImage(simpleScript);

        log.trace(result);

        simpleScript = simpleScriptService.createSimpleScript(simpleScript);

        SimpleScriptDto dto = simpleScriptConvertor.convertModelToDto(simpleScript);

        log.trace("createdSimpleScript: dto={}", dto);

        return new ResponseEntity("All ok !", HttpStatus.OK);
    }
/*
    @RequestMapping(value = "students/{studentId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteStudent(@PathVariable final Long studentId) {
        log.trace("deleteStudent: studentId={}", studentId);

        simpleScriptService.deleteStudent(studentId);

        log.trace("deleteStudent - method end");

        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }*/


}
