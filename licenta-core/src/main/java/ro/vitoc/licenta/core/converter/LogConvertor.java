package ro.vitoc.licenta.core.converter;

import org.springframework.stereotype.Component;
import ro.vitoc.licenta.core.dto.LogDto;
import ro.vitoc.licenta.core.model.Log;

@Component
public class LogConvertor extends BaseConverter<Log, LogDto> {
    @Override
    public Log convertDtoToModel(LogDto dto) {
        return new Log(dto.getName(),dto.getLog(),dto.getTime(),dto.getType());
    }

    @Override
    public LogDto convertModelToDto(Log log) {
        LogDto logDto =  LogDto.builder()
                .name(log.getName())
                .log(log.getLog())
                .time(log.getTime())
                .type(log.getType())
                .build();
        logDto.setId(log.getId());
        return logDto;
    }
}
