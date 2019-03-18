package ro.vitoc.licenta.core.service;

import ro.vitoc.licenta.core.model.MicroService;

import java.util.List;

public interface MicroServiceService {
    List<MicroService> findAll();
    MicroService createMicroService(MicroService microService);
}
