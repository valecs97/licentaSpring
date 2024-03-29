package ro.vitoc.licenta.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import ro.vitoc.licenta.core.model.WebMicroService;
import ro.vitoc.licenta.core.repository.WebMicroServiceRepository;
import ro.vitoc.licenta.miscellaneous.algorithms.DockerAlgorithms;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Service
public class WebMicroServiceServiceImpl implements WebMicroServiceService {
    private static final Logger log = LoggerFactory.getLogger(WebMicroService.class);

    @Value("${docker.username}")
    private String dockerUser;

    @Autowired
    private WebMicroServiceRepository webMicroServiceRepository;


    @Override
    public List<WebMicroService> findAll() {
        log.trace("findAll webMicroServices--- method entered");

        List<WebMicroService> webMicroServices = webMicroServiceRepository.findAll();

        log.trace("findAll: webMicroServices={}", webMicroServices);

        return webMicroServices;
    }

    @Override
    public WebMicroService find(Example<WebMicroService> example) {
        log.trace("find webMicroService--- method entered");

        WebMicroService webMicroService = webMicroServiceRepository.findOne(example).orElse(null);

        log.trace("find: webMicroService={}", webMicroService);

        return webMicroService;
    }

    @Override
    public WebMicroService findByUrlAndBranch(String url, String branch) {
        log.trace("find webMicroService--- method entered");

        WebMicroService webMicroService = webMicroServiceRepository.findByGitUrlAndBranchAllIgnoreCase(url,branch).stream().findAny().orElse(null);

        log.trace("find: webMicroService={}", webMicroService);

        return webMicroService;
    }

    @Override
    public WebMicroService createUpdateWebMicroService(WebMicroService webMicroService) {
        log.trace("before createWebMicroService: webMicroService={}",
                webMicroService);

        webMicroService.getConfiguration().setImage(dockerUser + "/" + webMicroService.getName());

        webMicroService = webMicroServiceRepository.save(webMicroService);

        log.trace("after createWebMicroService: webMicroService={}", webMicroService);

        return webMicroService;
    }
}
