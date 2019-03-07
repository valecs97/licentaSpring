package ro.vitoc.licenta.core.service;

import ro.vitoc.licenta.core.model.SimpleScript;

import java.util.List;

public interface SimpleScriptService {
    List<SimpleScript> findAll();

    //Student updateStudent(Long studentId, String serialNumber, String name, Integer groupNumber);

    SimpleScript createSimpleScript(SimpleScript simpleScript);

    //void deleteStudent(Long studentId);

}
