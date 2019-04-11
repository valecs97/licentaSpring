package ro.vitoc.licenta.core.dto;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ro.vitoc.licenta.core.model.BaseProject;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class WebMicroServiceDto extends BaseProjectDto {
    Integer portIn;
    Integer portOut;
    ServiceConfigurationDto configuration;
    Integer replicas;

    @Builder
    public WebMicroServiceDto(String name, String gitUrl, String branch, String webhook, String lang, String main, Integer portIn, Integer portOut, ServiceConfigurationDto configuration, Integer replicas) {
        super(name, gitUrl, branch, webhook, lang, main);
        this.portIn = portIn;
        this.portOut = portOut;
        if (configuration != null)
            this.configuration = configuration;
        else
            configuration = new ServiceConfigurationDto();
        this.replicas = replicas;
    }

    public WebMicroServiceDto(String name, String gitUrl, String branch, String webhook, String lang, String main, Integer port) {
        super(name, gitUrl, branch, webhook, lang, main);
        this.portIn = portIn;
        this.portOut = portOut;
        configuration = new ServiceConfigurationDto();
    }
}
