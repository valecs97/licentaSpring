package ro.vitoc.licenta.core.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.core.converter.MicroServiceConvertor;
import ro.vitoc.licenta.core.converter.SimpleScriptConvertor;
import ro.vitoc.licenta.core.dto.MicroServiceDto;
import ro.vitoc.licenta.core.model.MicroService;
import ro.vitoc.licenta.core.service.MicroServiceService;
import ro.vitoc.licenta.core.service.SimpleProjectService;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MicroServiceFacadeImpl implements MicroServiceFacade{
    private static final Logger log = LoggerFactory.getLogger(MicroServiceFacadeImpl.class);

    private MicroServiceService microServiceService;
    private MicroServiceConvertor microServiceConvertor;

    @Autowired
    public MicroServiceFacadeImpl(MicroServiceService microServiceService, MicroServiceConvertor microServiceConvertor) {
        this.microServiceService = microServiceService;
        this.microServiceConvertor = microServiceConvertor;
    }

    @Override
    public List<MicroServiceDto> findAll() {
        log.trace("findAll microService dao");
        return microServiceService.findAll().stream().map(elem -> microServiceConvertor.convertModelToDto(elem)).collect(Collectors.toList());
    }

    @Override
    public MicroServiceDto createMicroService(MicroServiceDto microServiceDto, String branch, String location,String containerId) {
        log.trace("createMicroService dao, microServiceDto={}",microServiceDto);
        MicroService microService = microServiceConvertor.convertDtoToModel(microServiceDto);
        microService.setBranch(branch);
        microService.setLocation(location);
        microService.setContainerId(containerId);
        return microServiceConvertor.convertModelToDto(microServiceService.createMicroService(microService));
    }
}
