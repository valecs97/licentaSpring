package ro.vitoc.licenta.core.service;

import org.springframework.data.domain.Example;
import ro.vitoc.licenta.core.model.SimpleProject;

import java.util.List;

public interface SimpleProjectService {
    List<SimpleProject> findAll();
    SimpleProject find(Example<SimpleProject> example);
    SimpleProject findByUrlAndBranch(String url,String branch);
    //Student updateStudent(Long studentId, String serialNumber, String name, Integer groupNumber);
    SimpleProject createUpdateProjectScript(SimpleProject simpleProject);
    SimpleProject findSimpleProjectByName(String simpleProject);
    //void deleteStudent(Long studentId);

}
