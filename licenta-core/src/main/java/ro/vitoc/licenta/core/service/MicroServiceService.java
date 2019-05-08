package ro.vitoc.licenta.core.service;

import org.springframework.data.domain.Example;
import ro.vitoc.licenta.core.model.MicroService;

import java.util.List;

public interface MicroServiceService {
    List<MicroService> findAll();
    MicroService find(Example<MicroService> example);
    MicroService findByUrlAndBranch(String url,String branch);
    MicroService createUpdateMicroService(MicroService microService);

}
