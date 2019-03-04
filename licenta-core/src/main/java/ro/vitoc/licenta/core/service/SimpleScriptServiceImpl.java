package ro.vitoc.licenta.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.vitoc.licenta.core.model.SimpleScript;
import ro.vitoc.licenta.core.repository.SimpleScriptRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SimpleScriptServiceImpl implements SimpleScriptService {
    private static final Logger log = LoggerFactory.getLogger(SimpleScriptServiceImpl.class);

    @Autowired
    private SimpleScriptRepository simpleScriptRepository;


    @Override
    public List<SimpleScript> findAll() {
        log.trace("findAll simple script--- method entered");

        List<SimpleScript> simpleScripts = simpleScriptRepository.findAll();

        log.trace("findAll: simpleScripts={}", simpleScripts);

        return simpleScripts;
    }

/*    @Override
    @Transactional
    public Student updateStudent(Long studentId, String serialNumber, String name, Integer groupNumber) {
        log.trace("updateStudent: serialNumber={}, name={}, groupNumber={}", serialNumber, name, groupNumber);

        Optional<Student> student = studentRepository.findById(studentId);

        student.ifPresent(s->{
            s.setSerialNumber(serialNumber);
            s.setName(name);
            s.setGroupNumber(groupNumber);
        });

        log.trace("updateStudent: student={}", student.get());

        return student.orElse(null);
    }*/

    @Override
    public SimpleScript createSimpleScript(String gitUrl, String branch, Boolean webhook, Integer timeout) {
        log.trace("createSimpleScript: gitUrl={}, webhook={}, timeout={}",
                gitUrl, webhook, timeout);

        SimpleScript simpleScript = SimpleScript.builder().gitUrl(gitUrl).branch(branch).webhook(webhook).timeout(timeout).build();
        simpleScript = simpleScriptRepository.save(simpleScript);

        log.trace("createSimpleScript: simpleScript={}", simpleScript);

        return simpleScript;
    }

/*    @Override
    public void deleteStudent(Long studentId) {
        log.trace("deleteStudent: studentId={}", studentId);

        studentRepository.deleteById(studentId);

        log.trace("deleteStudent - method end");
    }*/

}
