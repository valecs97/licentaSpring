package ro.vitoc.licenta.web.preConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.zeroturnaround.exec.ProcessExecutor;
import ro.vitoc.licenta.core.dto.LogDto;
import ro.vitoc.licenta.core.model.BaseProject;
import ro.vitoc.licenta.core.model.Log;
import ro.vitoc.licenta.core.service.MicroServiceService;
import ro.vitoc.licenta.core.service.WebMicroServiceService;
import ro.vitoc.licenta.miscellaneous.service.ProcessService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Component
public class DockerPreConfigImpl implements DockerPreConfig {
    private static final Logger log = LoggerFactory.getLogger(DockerPreConfigImpl.class);
    private static final String serviceLogs = "docker service logs -f";
    private static final String URL = "ws://127.0.0.1:8080/api/log/";

    private MicroServiceService microServiceService;
    private WebMicroServiceService webMicroServiceService;
    private ProcessService processService;

    @Value("${docker.defaultVMName}")
    private String defaultVMName;

    @Value("${docker.swarmName}")
    private String swarmName;

    @Autowired
    public DockerPreConfigImpl(MicroServiceService microServiceService, WebMicroServiceService webMicroServiceService, ProcessService processService) {
        this.microServiceService = microServiceService;
        this.webMicroServiceService = webMicroServiceService;
        this.processService = processService;
    }

    @PostConstruct
    public void attachLogsToWebsocket() {
        log.trace("attachLogsToWebsocket entered");
        webMicroServiceService.findAll().stream().forEach(this::attachLogToWebSocket);
        microServiceService.findAll().stream().forEach(this::attachLogToWebSocket);
    }

    @Override
    public void attachLogToWebSocket(BaseProject project) {
        LogOutputRedirect runnable = new LogOutputRedirect(project);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private class LogOutputRedirect implements Runnable {
        BaseProject project;

        public LogOutputRedirect(BaseProject project) {
            this.project = project;
        }

        @Override
        public void run() {
            log.trace("LogOutputRedirect trace");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.trace("Thread interrupted with exception={}",e.getMessage());
            }
            List<Transport> transports = new ArrayList<>(1);
            transports.add(new WebSocketTransport( new StandardWebSocketClient()) );
            WebSocketClient client = new SockJsClient(transports);
            WebSocketStompClient stompClient = new WebSocketStompClient(client);
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());

            StompSessionHandler sessionHandler = new StompSessionHandler() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    log.trace("project={} connected to websocket",project.getName());
                    try {
                        new ProcessExecutor().command(processService.executeInVM(defaultVMName, serviceLogs + " " + swarmName + "_" + project.getName()))
                                .redirectOutput(new org.zeroturnaround.exec.stream.LogOutputStream() {
                                    final String processName = project.getName();

                                    @Override
                                    protected void processLine(String line) {
                                        LogDto log = LogDto.builder()
                                                .name(processName)
                                                .log(line)
                                                .type(line.toLowerCase().contains("[error]") ? Log.LogType.ERROR : line.toLowerCase().contains("[warning]") ? Log.LogType.WARNING : Log.LogType.GENERAL)
                                                .build();
                                        session.send("/app/log",log);
                                        //logController.add(log);
                                    }
                                }).execute();
                    } catch (IOException | InterruptedException | TimeoutException e) {
                        log.trace("attachLogToWebSocket failed with message={}", e.getMessage());
                    }
                }

                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    log.trace("handleException when trying to connect project={} to websocket with exception={}",project.getName(),exception.getMessage());
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    log.trace("handleTransportError when trying to connect project={} to websocket with exception={}",project.getName(),exception.getMessage());
                }

                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return null;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {

                }
            };
            stompClient.connect(URL, sessionHandler);


        }
    }
}
