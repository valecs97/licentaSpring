package ro.vitoc.licenta.core.converter;

import org.springframework.stereotype.Component;
import ro.vitoc.licenta.core.dto.MicroServiceDto;
import ro.vitoc.licenta.core.model.MicroService;
import ro.vitoc.licenta.core.model.SimpleProject;

@Component
public class MicroServiceConvertor extends BaseConverter<MicroService, MicroServiceDto>  {
    @Override
    public MicroService convertDtoToModel(MicroServiceDto dto) {
        MicroService microService = MicroService.builder()
                .gitUrl(dto.getGitUrl())
                .branch(dto.getBranch())
                .name(dto.getName())
                .lang(dto.getLang())
                .main(dto.getMain())
                .build();
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
                .build();
        dto.setId(microService.getId());
        return dto;
    }
}
