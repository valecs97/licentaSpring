package ro.vitoc.licenta.web.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.core.model.SimpleScript;
import ro.vitoc.licenta.web.dto.SimpleScriptDto;

@Component
public class SimpleScriptConvertor extends BaseConverter<SimpleScript, SimpleScriptDto>  {
    private static final Logger log = LoggerFactory.getLogger(SimpleScriptConvertor.class);

    @Override
    public SimpleScript convertDtoToModel(SimpleScriptDto dto) {
        SimpleScript simpleScript = SimpleScript.builder()
                .gitUrl(dto.getGitUrl())
                .branch(dto.getBranch())
                .webhook(dto.getWebhook())
                .name(dto.getName())
                .lang(dto.getLang())
                .main(dto.getMain())
                .req(dto.getReq())
                .parameters(dto.getParameters())
                .build();
        return simpleScript;
    }

    @Override
    public SimpleScriptDto convertModelToDto(SimpleScript simpleScript) {
        SimpleScriptDto dto = SimpleScriptDto.builder()
                .gitUrl(simpleScript.getGitUrl())
                .branch(simpleScript.getBranch())
                .webhook(simpleScript.getWebhook())
                .name(simpleScript.getName())
                .main(simpleScript.getMain())
                .lang(simpleScript.getLang())
                .req(simpleScript.getReq())
                .parameters(simpleScript.getParameters())
                .build();
        dto.setId(simpleScript.getId());
        return dto;
    }
}
