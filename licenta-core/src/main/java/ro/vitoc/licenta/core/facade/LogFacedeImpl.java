package ro.vitoc.licenta.core.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.core.converter.LogConvertor;
import ro.vitoc.licenta.core.dto.LogDto;
import ro.vitoc.licenta.core.model.Log;
import ro.vitoc.licenta.core.service.LogService;
import ro.vitoc.licenta.core.service.LogServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LogFacedeImpl implements LogFacade {
    private static final Logger log = LoggerFactory.getLogger(LogFacedeImpl.class);

    private LogService logService;
    private LogConvertor logConvertor;

    @Autowired
    public LogFacedeImpl(LogService logService, LogConvertor logConvertor) {
        this.logService = logService;
        this.logConvertor = logConvertor;
    }

    @Override
    public List<LogDto> findAll(Integer limit, Integer start) {
        log.trace("findAll logs dao");
        return logService.findAll(limit, start).stream().map(elem -> logConvertor.convertModelToDto(elem)).collect(Collectors.toList());
    }

    @Override
    public LogDto createLog(LogDto logDto) {
        log.trace("createLog dao, logDto={}",logDto);
        Log log = logConvertor.convertDtoToModel(logDto);
        return logConvertor.convertModelToDto(logService.createLog(log));
    }

    @Override
    public List<LogDto> filterLogs(String from,Integer limit, Integer start) {
        log.trace("filterLogs logs dao");
        return logService.filterLogs(from,limit,start).stream().map(elem -> logConvertor.convertModelToDto(elem)).collect(Collectors.toList());
    }
}
