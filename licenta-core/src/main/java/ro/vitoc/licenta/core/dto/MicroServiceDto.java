package ro.vitoc.licenta.core.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MicroServiceDto extends BaseProjectDto {
    ServiceConfigurationDto configuration;

    @Builder
    public MicroServiceDto(String name, String gitUrl, String branch, String lang, String main, ServiceConfigurationDto configuration) {
        super(name, gitUrl, branch, lang, main);
        this.configuration = configuration;
    }
}
