package ro.vitoc.licenta.core.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.core.converter.WebMicroServiceConvertor;
import ro.vitoc.licenta.core.dto.WebMicroServiceDto;
import ro.vitoc.licenta.core.model.WebMicroService;
import ro.vitoc.licenta.core.service.WebMicroServiceService;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WebMicroServiceFacadeImpl implements WebMicroServiceFacade{
    private static final Logger log = LoggerFactory.getLogger(MicroServiceFacadeImpl.class);

    private WebMicroServiceService webMicroServiceService;
    private WebMicroServiceConvertor webMicroServiceConvertor;

    @Autowired
    public WebMicroServiceFacadeImpl(WebMicroServiceService webMicroServiceService, WebMicroServiceConvertor webMicroServiceConvertor) {
        this.webMicroServiceService = webMicroServiceService;
        this.webMicroServiceConvertor = webMicroServiceConvertor;
    }

    @Override
    public List<WebMicroServiceDto> findAll() {
        log.trace("findAll webMicroService dao");
        return webMicroServiceService.findAll().stream().map(elem -> webMicroServiceConvertor.convertModelToDto(elem)).collect(Collectors.toList());
    }

    @Override
    public WebMicroServiceDto createWebMicroService(WebMicroServiceDto microServiceDto, String branch, String location) {
        log.trace("createWebMicroService dao, webMicroServiceDto={}",microServiceDto);
        WebMicroService webMicroService = webMicroServiceConvertor.convertDtoToModel(microServiceDto);
        webMicroService.setBranch(branch);
        webMicroService.setLocation(location);
        return webMicroServiceConvertor.convertModelToDto(webMicroServiceService.createWebMicroService(webMicroService));
    }

    @Override
    public void redeployAll() {
        log.trace("redeployAll webMicroService dao");
        webMicroServiceService.redeployAll();
    }
}
