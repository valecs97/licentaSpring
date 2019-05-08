package ro.vitoc.licenta.core.facade;

import org.springframework.data.domain.Example;
import ro.vitoc.licenta.core.dto.WebMicroServiceDto;
import ro.vitoc.licenta.core.model.WebMicroService;

import java.util.List;

public interface WebMicroServiceFacade {
    List<WebMicroServiceDto> findAll();
    WebMicroServiceDto find(Example<WebMicroService> example);
    WebMicroServiceDto findByUrlAndBranch(String url,String branch);
    WebMicroServiceDto update(WebMicroServiceDto webMicroServiceDto);
    WebMicroServiceDto createWebMicroService(WebMicroServiceDto microServiceDto, String branch, String location);
}
