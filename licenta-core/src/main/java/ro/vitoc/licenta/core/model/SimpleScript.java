package ro.vitoc.licenta.core.model;

import lombok.*;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SimpleScript extends BaseProject{
    private Integer timeout;

    @Builder
    public SimpleScript(String gitUrl, Boolean webhook, Integer timeout){
        super(gitUrl,webhook);
        this.timeout = timeout;
    }
}
