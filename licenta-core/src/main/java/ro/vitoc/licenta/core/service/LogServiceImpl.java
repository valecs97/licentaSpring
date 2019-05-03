package ro.vitoc.licenta.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ro.vitoc.licenta.core.cache.CacheManager;
import ro.vitoc.licenta.core.model.Log;
import ro.vitoc.licenta.core.repository.LogRepository;

import java.util.List;

@Service
public class LogServiceImpl implements LogService {
    private static final Logger log = LoggerFactory.getLogger(LogServiceImpl.class);
    private LogRepository logRepository;
    private CacheManager<Log> cacheManager;

    @Autowired
    public LogServiceImpl(LogRepository logRepository, CacheManager<Log> cacheManager) {
        this.logRepository = logRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    public List<Log> findAll(Integer limit, Integer start) {
        log.trace("findAll log--- method entered");

        List<Log> logs = logRepository.findAll(PageRequest.of(start, limit, new Sort(Sort.Direction.DESC, "id"))).getContent();

        log.trace("findAll: logs={}", logs);

        return logs;
    }

    @Override
    public Log createLog(Log log) {
        LogServiceImpl.log.trace("before createLog: log={}",
                log);
        log = logRepository.save(log);

        LogServiceImpl.log.trace("after createLog: log={}", log);

        return log;
    }

    @Override
    public List<Log> filterLogs(String filter, Integer limit, Integer start) {
        log.trace("before findSimpleProject: filter={}",
                filter);

        Example<Log> example = Example.of(Log.builder().name(filter).build());
        List<Log> res = logRepository.findAll(example,PageRequest.of(start, limit, new Sort(Sort.Direction.DESC, "id"))).getContent();
//                .stream().filter(elem -> elem.getName().contains(from)).collect(Collectors.toList());

        log.trace("after findSimpleProject: simpleProject={}", res);

        return res;
    }
}
