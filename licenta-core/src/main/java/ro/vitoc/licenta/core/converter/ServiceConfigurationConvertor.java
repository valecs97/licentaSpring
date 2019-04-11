package ro.vitoc.licenta.core.converter;

import org.springframework.stereotype.Component;
import ro.vitoc.licenta.core.dto.ServiceConfigurationDto;
import ro.vitoc.licenta.core.model.ServiceConfiguration;

@Component
public class ServiceConfigurationConvertor extends BaseConverter<ServiceConfiguration, ServiceConfigurationDto> {
    @Override
    public ServiceConfiguration convertDtoToModel(ServiceConfigurationDto dto) {
        return ServiceConfiguration.builder()
                .cpus(dto.getCpus())
                .image(dto.getImage())
                .memory(dto.getMemory())
                .build();
    }

    @Override
    public ServiceConfigurationDto convertModelToDto(ServiceConfiguration serviceConfiguration) {
        ServiceConfigurationDto dto = ServiceConfigurationDto.builder()
                .cpus(serviceConfiguration.getCpus())
                .memory(serviceConfiguration.getMemory())
                .image(serviceConfiguration.getImage())
                .build();
        dto.setId(serviceConfiguration.getId());
        return dto;
    }
}
