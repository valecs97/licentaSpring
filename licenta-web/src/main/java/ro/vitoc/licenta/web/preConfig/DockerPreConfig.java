package ro.vitoc.licenta.web.preConfig;

import ro.vitoc.licenta.core.model.BaseProject;

public interface DockerPreConfig {
    void attachLogsToWebsocket();
    void attachLogToWebSocket(BaseProject project);
}
