package ro.vitoc.licenta.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.vitoc.licenta.core.cache.CacheManager;
import ro.vitoc.licenta.core.model.SimpleProject;
import ro.vitoc.licenta.core.repository.SimpleProjectRepository;

import java.util.List;

@Service
public class SimpleProjectServiceImpl implements SimpleProjectService {
    private static final Logger log = LoggerFactory.getLogger(SimpleProjectServiceImpl.class);
    private SimpleProjectRepository simpleProjectRepository;
    private CacheManager<SimpleProject> cacheManager;

    @Autowired
    public SimpleProjectServiceImpl(SimpleProjectRepository simpleProjectRepository, CacheManager<SimpleProject> cacheManager) {
        this.simpleProjectRepository = simpleProjectRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    public List<SimpleProject> findAll() {
        log.trace("findAll simple project--- method entered");

        List<SimpleProject> simpleProjects = simpleProjectRepository.findAll();

        log.trace("findAll: simpleProjects={}", simpleProjects);

        return simpleProjects;
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
    public SimpleProject createProjectScript(SimpleProject simpleProject) {
        log.trace("before createSimpleScript: simpleProject={}",
                simpleProject);
        simpleProject = simpleProjectRepository.save(simpleProject);

        log.trace("after createSimpleScript: simpleProject={}", simpleProject);

        return simpleProject;
    }

    @Override
    public SimpleProject findSimpleProjectByName(String simpleProject) {
        log.trace("before findSimpleProject: simpleProject={}",
                simpleProject);
        SimpleProject res = cacheManager.checkCache(SimpleProject.builder().name(simpleProject).build());
        if (res == null){
            res = simpleProjectRepository.findAll().stream().filter(elem -> elem.getName().equals(simpleProject)).findAny().orElse(null);
            if (res != null)
                cacheManager.cacheValue(res);
        }

        log.trace("after findSimpleProject: simpleProject={}", res);

        return res;
    }

/*    @Override
    public void deleteStudent(Long studentId) {
        log.trace("deleteStudent: studentId={}", studentId);

        studentRepository.deleteById(studentId);

        log.trace("deleteStudent - method end");
    }*/

}
