package ro.vitoc.licenta.core.model;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ServiceConfiguration extends BaseEntity<Long>{
    String image;
    @Value("${serviceconf.cpus}")
    Float cpus;
    @Value("${serviceconf.memory}")
    String memory;
}