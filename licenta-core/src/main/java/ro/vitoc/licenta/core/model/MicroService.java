package ro.vitoc.licenta.core.model;

import lombok.*;

import javax.persistence.Entity;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MicroService extends BaseProject{
    String containerId;

    @Builder
    public MicroService(String name, String location, String gitUrl, String branch, String lang, String main, String containerId) {
        super(name, location, gitUrl, branch, lang, main);
        this.containerId = containerId;
    }
}
