package ro.vitoc.licenta.core.model;

import lombok.*;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WebMicroService extends MicroService{
    Integer port;
    @ElementCollection(targetClass=ServiceConfiguration.class,fetch = FetchType.EAGER)
    ServiceConfiguration configuration;

    @Builder
    public WebMicroService(String name, String location, String gitUrl, String branch, String webhook, String lang, String main, List<String> req, String containerId, Integer port, ServiceConfiguration configuration) {
        super(name, location, gitUrl, branch, webhook, lang, main, req, containerId);
        this.port = port;
        this.configuration = configuration;
    }
}
