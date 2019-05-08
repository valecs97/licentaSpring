package ro.vitoc.licenta.core.service;

import org.springframework.data.domain.Example;
import ro.vitoc.licenta.core.model.WebMicroService;

import java.util.List;

public interface WebMicroServiceService {
    List<WebMicroService> findAll();
    WebMicroService find(Example<WebMicroService> example);
    WebMicroService findByUrlAndBranch(String url,String branch);
    WebMicroService createUpdateWebMicroService(WebMicroService webMicroService);

}
