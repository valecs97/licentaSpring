package ro.vitoc.licenta.core.repository;

import ro.vitoc.licenta.core.model.MicroService;
import ro.vitoc.licenta.core.model.SimpleProject;

import java.util.List;

public interface MicroServiceRepository extends Repository<MicroService, Long>  {
    List<MicroService> findByGitUrlAndBranchAllIgnoreCase(String gitUrl, String branch);
}
