package ro.vitoc.licenta.core.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.core.dto.MicroServiceDto;
import ro.vitoc.licenta.core.model.MicroService;
import ro.vitoc.licenta.core.model.ServiceConfiguration;
import ro.vitoc.licenta.core.model.SimpleProject;

@Component
public class MicroServiceConvertor extends BaseConverter<MicroService, MicroServiceDto>  {
    @Autowired
    private ServiceConfigurationConvertor serviceConfigurationConvertor;

    @Value("${serviceconf.cpus}")
    Float cpus;

    @Value("${serviceconf.memory}")
    String memory;

    @Override
    public MicroService convertDtoToModel(MicroServiceDto dto) {
        MicroService microService = MicroService.builder()
                .gitUrl(dto.getGitUrl())
                .branch(dto.getBranch())
                .name(dto.getName())
                .lang(dto.getLang())
                .main(dto.getMain())
                .configuration(dto.getConfiguration() != null ? serviceConfigurationConvertor.convertDtoToModel(dto.getConfiguration()) : new ServiceConfiguration())
                .build();
        if (microService.getConfiguration().getCpus() == null)
            microService.getConfiguration().setCpus(cpus);
        if (microService.getConfiguration().getMemory() == null)
            microService.getConfiguration().setMemory(memory);
        microService.setId(dto.getId());
        return microService;
    }

    @Override
    public MicroServiceDto convertModelToDto(MicroService microService) {
        MicroServiceDto dto = MicroServiceDto.builder()
                .gitUrl(microService.getGitUrl())
                .branch(microService.getBranch())
                .name(microService.getName())
                .main(microService.getMain())
                .lang(microService.getLang())
                .configuration(serviceConfigurationConvertor.convertModelToDto(microService.getConfiguration()))
                .build();
        dto.setId(microService.getId());
        return dto;
    }
}
