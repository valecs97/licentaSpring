package ro.vitoc.licenta.core.model;

import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MicroService extends BaseProject{
    @ManyToOne(targetEntity=ServiceConfiguration.class,fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    ServiceConfiguration configuration;

    @Builder
    public MicroService(String name, String location, String gitUrl, String branch, String lang, String main, ServiceConfiguration configuration) {
        super(name, location, gitUrl, branch, lang, main);
        this.configuration = configuration;
    }
}
