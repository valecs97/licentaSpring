package ro.vitoc.licenta.core.facade;

import ro.vitoc.licenta.core.dto.MicroServiceDto;
import ro.vitoc.licenta.core.model.MicroService;

import java.util.List;

public interface MicroServiceFacade {
    List<MicroServiceDto> findAll();
    MicroServiceDto createMicroService(MicroServiceDto microServiceDto, String branch, String location);
}
