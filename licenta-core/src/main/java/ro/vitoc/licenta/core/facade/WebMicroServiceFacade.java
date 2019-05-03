package ro.vitoc.licenta.core.facade;

import ro.vitoc.licenta.core.dto.WebMicroServiceDto;

import java.util.List;

public interface WebMicroServiceFacade {
    List<WebMicroServiceDto> findAll();
    WebMicroServiceDto createWebMicroService(WebMicroServiceDto microServiceDto, String branch, String location);
}
