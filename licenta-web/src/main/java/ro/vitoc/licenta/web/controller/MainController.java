package ro.vitoc.licenta.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.vitoc.licenta.core.facade.CommonFacade;
import ro.vitoc.licenta.core.facade.DockerFacade;
import ro.vitoc.licenta.core.facade.GitFacade;
import ro.vitoc.licenta.core.facade.SimpleProjectFacade;
import ro.vitoc.licenta.core.model.SimpleProject;
import ro.vitoc.licenta.core.service.CommonService;
import ro.vitoc.licenta.core.service.DockerService;
import ro.vitoc.licenta.core.service.GitService;
import ro.vitoc.licenta.core.service.SimpleProjectService;
import ro.vitoc.licenta.core.converter.SimpleScriptConvertor;
import ro.vitoc.licenta.core.dto.SimpleProjectDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MainController {
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    private SimpleProjectFacade simpleProjectFacade;
    private GitFacade gitFacade;
    private DockerFacade dockerFacade;
    private CommonFacade commonFacade;
    private SimpleScriptConvertor simpleScriptConvertor;

    @Autowired
    public MainController(SimpleProjectFacade simpleProjectFacade, GitFacade gitFacade, DockerFacade dockerFacade, CommonFacade commonFacade, SimpleScriptConvertor simpleScriptConvertor) {
        this.simpleProjectFacade = simpleProjectFacade;
        this.gitFacade = gitFacade;
        this.dockerFacade = dockerFacade;
        this.commonFacade = commonFacade;
        this.simpleScriptConvertor = simpleScriptConvertor;
    }

    @RequestMapping(value = "/simpleProjects", method = RequestMethod.GET)
    public List<SimpleProjectDto> getSimpleScripts() {
        log.trace("getSimpleScripts");

        List<SimpleProjectDto> simpleProjects = simpleProjectFacade.findAll();

        log.trace("getSimpleScripts: simpleProjects={}", simpleProjects);

        return simpleProjects;
    }
/*
    @RequestMapping(value = "/students/{studentId}", method = RequestMethod.PUT)
    public StudentDto updateStudent(
            @PathVariable final Long studentId,
            @RequestBody final StudentDto studentDto) {
        log.trace("updateStudent: studentId={}, studentDtoMap={}", studentId, studentDto);

        Student student = simpleProjectService.updateStudent(studentId,
                studentDto.getSerialNumber(),
                studentDto.getName(), studentDto.getGroupNumber());

        StudentDto result = studentConverter.convertModelToDto(student);

        log.trace("updateStudent: result={}", result);

        return result;
    }*/
/*
    @RequestMapping(value = "students/{studentId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteStudent(@PathVariable final Long studentId) {
        log.trace("deleteStudent: studentId={}", studentId);

        simpleProjectService.deleteStudent(studentId);

        log.trace("deleteStudent - method end");

        return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
    }*/


}
