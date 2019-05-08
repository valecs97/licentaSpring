package ro.vitoc.licenta.core.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import ro.vitoc.licenta.core.model.SimpleProject;

import java.util.List;


public interface SimpleProjectRepository extends Repository<SimpleProject, Long> {

    List<SimpleProject> findByGitUrlAndBranchAllIgnoreCase(String gitUrl,String branch);
}
