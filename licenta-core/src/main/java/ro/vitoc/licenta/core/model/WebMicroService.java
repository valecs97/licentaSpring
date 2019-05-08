package ro.vitoc.licenta.core.model;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WebMicroService extends BaseProject{
    Integer portIn;
    Integer portOut;
    @ManyToOne(targetEntity=ServiceConfiguration.class,fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    ServiceConfiguration configuration;
    @Value("${serviceconf.replicas}")
    Integer replicas;

    @Builder
    public WebMicroService(String name, String location, String gitUrl, String branch, String lang, String main,Boolean available, Integer portIn,Integer portOut, ServiceConfiguration configuration, Integer replicas) {
        super(name, location, gitUrl, branch, lang, main,available);
        this.portIn = portIn;
        this.portOut = portOut;
        this.configuration = configuration;
        this.replicas = replicas;
    }
}
