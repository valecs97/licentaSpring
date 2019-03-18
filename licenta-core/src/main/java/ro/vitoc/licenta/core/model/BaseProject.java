package ro.vitoc.licenta.core.model;

import lombok.*;

import javax.persistence.*;
import javax.ws.rs.DefaultValue;
import java.io.Serializable;
import java.util.List;

@NamedEntityGraphs({
        @NamedEntityGraph(name = "baseProjectWithReq")
})

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class BaseProject extends BaseEntity<Long> implements Serializable {
    @Column(unique = true)
    private String name;
    private String location;
    private String gitUrl;
    private String branch;
    @DefaultValue("null")
    private String webhook;
    private String lang;
    private String main;
    @Column
    @ElementCollection(targetClass=String.class,fetch = FetchType.EAGER)
    private List<String> req;
}
