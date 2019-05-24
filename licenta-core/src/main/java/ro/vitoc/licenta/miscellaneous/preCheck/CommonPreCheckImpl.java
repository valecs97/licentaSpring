package ro.vitoc.licenta.miscellaneous.preCheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ro.vitoc.licenta.miscellaneous.service.ProcessService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;

@Component
public class CommonPreCheckImpl implements CommonPreCheck {
    private static final Logger log = LoggerFactory.getLogger(CommonPreCheckImpl.class);

    @Autowired
    ProcessService processService;

    @Override
    @PostConstruct
    public void checkPip() {
        if (processService.returnCode(new String[]{"pip"}) != 0) {
            try {
                processService.executeCommandFIX(new String[]{
                    "python", getClass().getClassLoader().getResource("get-pip.py").toURI().toString().replace("file:/","")
                });
            } catch (IOException | URISyntaxException | InterruptedException | TimeoutException e) {
                log.trace("checkPip could not install pip ! message={}", e.getMessage());
            }
        }
    }
}
