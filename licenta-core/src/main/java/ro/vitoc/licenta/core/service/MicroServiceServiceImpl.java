package ro.vitoc.licenta.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.vitoc.licenta.core.model.MicroService;
import ro.vitoc.licenta.core.repository.MicroServiceRepository;

import java.util.List;

@Service
public class MicroServiceServiceImpl implements MicroServiceService{
    private static final Logger log = LoggerFactory.getLogger(MicroServiceService.class);

    @Autowired
    private MicroServiceRepository microServiceRepository;

    @Override
    public List<MicroService> findAll() {
        log.trace("findAll microService--- method entered");

        List<MicroService> microServices = microServiceRepository.findAll();

        log.trace("findAll: microServices={}", microServices);

        return microServices;
    }

    @Override
    public MicroService createMicroService(MicroService microService) {
        log.trace("before createMicroService: microService={}",
                microService);
        microService = microServiceRepository.save(microService);

        log.trace("after createMicroService: microService={}", microService);

        return microService;
    }
}
