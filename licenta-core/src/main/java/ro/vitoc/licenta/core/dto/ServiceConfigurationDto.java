package ro.vitoc.licenta.core.dto;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ServiceConfigurationDto extends BaseDto{
    String image;
    Float cpus;
    String memory;
}
