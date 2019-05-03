package ro.vitoc.licenta.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ro.vitoc.licenta.core.dto.LogDto;
import ro.vitoc.licenta.core.facade.LogFacade;
import ro.vitoc.licenta.core.model.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class LogController {
    private static final Logger log = LoggerFactory.getLogger(LogController.class);

    @Autowired
    private LogFacade logFacade;

    @MessageMapping("/log")
    @SendTo("/logs/get")
    public LogDto add(LogDto logDto) {
        log.trace("add logDto={}", logDto);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        logDto.setTime(time);
        log.trace("add final logDto={}", logDto);
        logDto = logFacade.createLog(logDto);
        return logDto;
    }

    @RequestMapping(value = "/getLogs", method = RequestMethod.GET)
    public ResponseEntity getLogsWithLimit(@RequestParam(value = "limit") Integer limit,
                                     @RequestParam(value = "from") Integer start){
        log.trace("getLogsWithLimit");

        List<LogDto> logs = logFacade.findAll(limit,start);

        log.trace("getLogsWithLimit: logs={}", logs);

        return new ResponseEntity(logs, HttpStatus.OK);
    }

    @RequestMapping(value = "/filterLogs", method = RequestMethod.GET)
    public ResponseEntity getLogsWithFilter(@RequestParam(value = "limit") Integer limit,
                                           @RequestParam(value = "from") Integer start,
                                            @RequestParam(value = "filter") String filter) {
        log.trace("getLogsWithFilter");

        List<LogDto> logs = logFacade.filterLogs(filter,limit,start);

        log.trace("getLogsWithFilter: logs={}", logs);

        return new ResponseEntity(logs, HttpStatus.OK);
    }
}
