package ro.vitoc.licenta.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import ro.vitoc.licenta.core.model.MicroService;
import ro.vitoc.licenta.core.repository.MicroServiceRepository;

import java.util.List;

@Service
public class MicroServiceServiceImpl implements MicroServiceService{
    private static final Logger log = LoggerFactory.getLogger(MicroServiceService.class);

    @Autowired
    private MicroServiceRepository microServiceRepository;

    @Value("${docker.username}")
    private String dockerUser;

    @Override
    public List<MicroService> findAll() {
        log.trace("findAll microService--- method entered");

        List<MicroService> microServices = microServiceRepository.findAll();

        log.trace("findAll: microServices={}", microServices);

        return microServices;
    }

    @Override
    public MicroService find(Example<MicroService> example) {
        log.trace("find MicroService--- method entered");

        MicroService microService = microServiceRepository.findOne(example).orElse(null);

        log.trace("find: MicroService={}", microService);

        return microService;
    }

    @Override
    public MicroService findByUrlAndBranch(String url, String branch) {
        log.trace("find MicroService--- method entered");

        MicroService microService = microServiceRepository.findByGitUrlAndBranchAllIgnoreCase(url,branch).stream().findAny().orElse(null);

        log.trace("find: MicroService={}", microService);

        return microService;
    }

    @Override
    public MicroService createUpdateMicroService(MicroService microService) {
        log.trace("before createMicroService: microService={}",
                microService);

        microService.getConfiguration().setImage(dockerUser + "/" + microService.getName());

        microService = microServiceRepository.save(microService);

        log.trace("after createMicroService: microService={}", microService);

        return microService;
    }
}
