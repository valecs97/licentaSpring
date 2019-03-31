package ro.vitoc.licenta.core.model;

import lombok.*;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ServiceConfiguration{
    String name;
    String image;
    Integer replicas;
    Float cpus;
    String memory;
    Integer port;
}