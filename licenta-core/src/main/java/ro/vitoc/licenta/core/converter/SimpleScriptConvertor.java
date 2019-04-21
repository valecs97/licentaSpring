package ro.vitoc.licenta.core.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.core.dto.SimpleProjectDto;
import ro.vitoc.licenta.core.model.SimpleProject;

@Component
public class SimpleScriptConvertor extends BaseConverter<SimpleProject, SimpleProjectDto>  {

    @Override
    public SimpleProject convertDtoToModel(SimpleProjectDto dto) {
        SimpleProject simpleProject = SimpleProject.builder()
                .gitUrl(dto.getGitUrl())
                .branch(dto.getBranch())
                .name(dto.getName())
                .lang(dto.getLang())
                .main(dto.getMain())
                .parameters(dto.getParameters())
                .build();
        return simpleProject;
    }

    @Override
    public SimpleProjectDto convertModelToDto(SimpleProject simpleProject) {
        SimpleProjectDto dto = SimpleProjectDto.builder()
                .gitUrl(simpleProject.getGitUrl())
                .branch(simpleProject.getBranch())
                .name(simpleProject.getName())
                .main(simpleProject.getMain())
                .lang(simpleProject.getLang())
                .parameters(simpleProject.getParameters())
                .build();
        dto.setId(simpleProject.getId());
        return dto;
    }
}
