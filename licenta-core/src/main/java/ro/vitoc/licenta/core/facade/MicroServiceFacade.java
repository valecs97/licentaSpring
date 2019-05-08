package ro.vitoc.licenta.core.facade;

import org.springframework.data.domain.Example;
import ro.vitoc.licenta.core.dto.MicroServiceDto;
import ro.vitoc.licenta.core.model.MicroService;

import java.util.List;

public interface MicroServiceFacade {
    List<MicroServiceDto> findAll();
    MicroServiceDto find(Example<MicroService> example);
    MicroServiceDto findByUrlAndBranch(String url,String branch);
    MicroServiceDto createMicroService(MicroServiceDto microServiceDto, String branch, String location);
    MicroServiceDto update(MicroServiceDto microServiceDto);
}
