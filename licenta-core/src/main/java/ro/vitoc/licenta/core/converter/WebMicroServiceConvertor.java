package ro.vitoc.licenta.core.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.core.dto.WebMicroServiceDto;
import ro.vitoc.licenta.core.model.ServiceConfiguration;
import ro.vitoc.licenta.core.model.WebMicroService;

@Component
public class WebMicroServiceConvertor extends BaseConverter<WebMicroService, WebMicroServiceDto> {
    @Autowired
    private ServiceConfigurationConvertor serviceConfigurationConvertor;

    @Value("${serviceconf.replicas}")
    private Integer replicas;

    @Value("${serviceconf.cpus}")
    Float cpus;

    @Value("${serviceconf.memory}")
    String memory;

    @Override
    public WebMicroService convertDtoToModel(WebMicroServiceDto dto) {
        WebMicroService res = WebMicroService.builder()
                .gitUrl(dto.getGitUrl())
                .branch(dto.getBranch())
                .name(dto.getName())
                .lang(dto.getLang())
                .main(dto.getMain())
                .configuration(dto.getConfiguration() != null ? serviceConfigurationConvertor.convertDtoToModel(dto.getConfiguration()) : new ServiceConfiguration())
                .portIn(dto.getPortIn())
                .portOut(dto.getPortOut())
                .replicas(dto.getReplicas() == null ? replicas : dto.getReplicas())
                .build();
        if (res.getConfiguration().getCpus() == null)
            res.getConfiguration().setCpus(cpus);
        if (res.getConfiguration().getMemory() == null)
            res.getConfiguration().setMemory(memory);
        res.setId(dto.getId());
        return res;
    }

    @Override
    public WebMicroServiceDto convertModelToDto(WebMicroService webMicroService) {
        WebMicroServiceDto dto = WebMicroServiceDto.builder()
                .gitUrl(webMicroService.getGitUrl())
                .branch(webMicroService.getBranch())
                .name(webMicroService.getName())
                .lang(webMicroService.getLang())
                .main(webMicroService.getMain())
                .configuration(serviceConfigurationConvertor.convertModelToDto(webMicroService.getConfiguration()))
                .portIn(webMicroService.getPortIn())
                .portOut(webMicroService.getPortOut())
                .replicas(webMicroService.getReplicas())
                .build();
        dto.setId(webMicroService.getId());
        return dto;

    }
}
