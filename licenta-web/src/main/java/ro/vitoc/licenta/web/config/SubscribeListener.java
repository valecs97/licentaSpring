package ro.vitoc.licenta.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class SubscribeListener implements ApplicationListener<SessionSubscribeEvent> {
    private static final Logger log = LoggerFactory.getLogger(SubscribeListener.class);

    private final SimpMessagingTemplate messagingTemplate;

    private static String lastSessionId = null;

    @Autowired
    public SubscribeListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        log.trace("NEW CLIENT CONNECTED !");
//        messagingTemplate.convertAndSend("/logs/get","New client connected !");
//        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
//        String sessionId = sha.getSessionId();
//        log.trace("New client has connected with sessionId={}",sessionId);
//
//        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
//        accessor.setHeader(SimpMessageHeaderAccessor.SESSION_ID_HEADER, sessionId);
//        messagingTemplate.convertAndSendToUser(sessionId,"/logs/get","#TODO",accessor.getMessageHeaders());
//        if (lastSessionId!=null){
//            accessor = SimpMessageHeaderAccessor.create();
//            accessor.setHeader(SimpMessageHeaderAccessor.SESSION_ID_HEADER, lastSessionId);
//            messagingTemplate.convertAndSendToUser(lastSessionId,"/logs/get","#TODO",accessor.getMessageHeaders());
//        }
//        lastSessionId = sessionId;
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}