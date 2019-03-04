package ro.vitoc.licenta.web.controller;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.vitoc.licenta.core.model.SimpleScript;
import ro.vitoc.licenta.core.service.ProjectService;
import ro.vitoc.licenta.core.service.SimpleScriptService;
import ro.vitoc.licenta.web.converter.SimpleScriptConvertor;
import ro.vitoc.licenta.web.dto.EmptyJsonResponse;
import ro.vitoc.licenta.web.dto.SimpleScriptDto;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private SimpleScriptService simpleScriptService;

    @Autowired
    private ProjectService projectService;

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

    @RequestMapping(value = "/simpleScript", method = RequestMethod.POST)
    public ResponseEntity createSimpleScript(
            @RequestBody final SimpleScriptDto simpleScriptDto) {
        log.trace("createSimpleScript: simpleScriptDto={}", simpleScriptDto);

        SimpleScript simpleScript = simpleScriptConvertor.convertDtoToModel(simpleScriptDto);

        if (!projectService.cloneGitRepository(simpleScript.getGitUrl(), simpleScript.getBranch()))
            return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.NOT_FOUND);

        String branch = projectService.getBranchRef(simpleScriptDto.getGitUrl(),simpleScriptDto.getBranch()).getName();

        simpleScript = simpleScriptService.createSimpleScript(
                simpleScriptDto.getGitUrl(),
                branch,
                simpleScriptDto.getWebhook(),
                simpleScriptDto.getTimeout());

        SimpleScriptDto result = simpleScriptConvertor.convertModelToDto(simpleScript);

        log.trace("createdSimpleScript: result={}", result);

        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
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
