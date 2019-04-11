package ro.vitoc.licenta.core.service;

import ro.vitoc.licenta.core.model.WebMicroService;

import java.util.List;

public interface WebMicroServiceService {
    List<WebMicroService> findAll();
    WebMicroService createWebMicroService(WebMicroService webMicroService);
    void redeployAll();
}
