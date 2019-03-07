package ro.vitoc.licenta.core.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import java.util.List;
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class SimpleScript extends BaseProject{
    @Builder
    public SimpleScript(String name, String location, String gitUrl, String branch, String webhook, String lang, String main, List<String> req, Integer parameters) {
        super(name, location, gitUrl, branch, webhook, lang, main, req, parameters);
    }
}
