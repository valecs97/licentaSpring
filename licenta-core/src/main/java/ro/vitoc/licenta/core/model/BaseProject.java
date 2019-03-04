package ro.vitoc.licenta.core.model;

import lombok.*;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class BaseProject extends BaseEntity<Long> implements Serializable {
    private String gitUrl;
    private String branch;
    private Boolean webhook;

}
