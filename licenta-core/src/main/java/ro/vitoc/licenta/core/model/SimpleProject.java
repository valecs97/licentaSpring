package ro.vitoc.licenta.core.model;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Entity;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import java.io.Serializable;
import java.util.List;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SimpleProject extends BaseProject implements Serializable {
    private Integer parameters;
    @Builder
    public SimpleProject(String name, String location, String gitUrl, String branch, String lang, String main,Boolean available, Integer parameters) {
        super(name, location, gitUrl, branch, lang, main,available);
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "SimpleProject{" +
                "name=" + super.getName() +
                '}';
    }
}
