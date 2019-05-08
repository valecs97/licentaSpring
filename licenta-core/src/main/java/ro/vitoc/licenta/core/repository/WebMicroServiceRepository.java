package ro.vitoc.licenta.core.repository;

import ro.vitoc.licenta.core.model.WebMicroService;

import java.util.List;

public interface WebMicroServiceRepository extends Repository<WebMicroService, Long> {
    List<WebMicroService> findByGitUrlAndBranchAllIgnoreCase(String gitUrl, String branch);
}
