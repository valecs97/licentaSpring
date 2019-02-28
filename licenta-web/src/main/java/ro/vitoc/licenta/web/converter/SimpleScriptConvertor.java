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
                .webhook(dto.getWebhook())
                .timeout(dto.getTimeout())
                .build();
        return simpleScript;
    }

    @Override
    public SimpleScriptDto convertModelToDto(SimpleScript simpleScript) {
        SimpleScriptDto dto = SimpleScriptDto.builder()
                .gitUrl(simpleScript.getGitUrl())
                .webhook(simpleScript.getWebhook())
                .timeout(simpleScript.getTimeout())
                .build();
        dto.setId(simpleScript.getId());
        return dto;
    }
}
