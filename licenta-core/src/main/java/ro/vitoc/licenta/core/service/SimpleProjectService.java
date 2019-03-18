package ro.vitoc.licenta.core.service;

import ro.vitoc.licenta.core.model.SimpleProject;

import java.util.List;

public interface SimpleProjectService {
    List<SimpleProject> findAll();
    //Student updateStudent(Long studentId, String serialNumber, String name, Integer groupNumber);
    SimpleProject createProjectScript(SimpleProject simpleProject);
    SimpleProject findSimpleProjectByName(String simpleProject);
    //void deleteStudent(Long studentId);

}
