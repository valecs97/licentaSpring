package ro.vitoc.licenta.core.facade;

import ro.vitoc.licenta.core.dto.LogDto;

import java.util.List;

public interface LogFacade {
    List<LogDto> findAll(Integer limit, Integer start);
    LogDto createLog(LogDto log);
    List<LogDto> filterLogs(String from,Integer limit, Integer start);
}
