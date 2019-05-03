package ro.vitoc.licenta.core.service;

import ro.vitoc.licenta.core.model.Log;

import java.util.List;

public interface LogService {
    List<Log> findAll(Integer limit, Integer start);
    Log createLog(Log log);
    List<Log> filterLogs(String from,Integer limit, Integer start);
}
