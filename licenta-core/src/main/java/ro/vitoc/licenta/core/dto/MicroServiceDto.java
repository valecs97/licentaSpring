package ro.vitoc.licenta.core.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class MicroServiceDto extends BaseProjectDto {
    @Builder
    public MicroServiceDto(String name, String gitUrl, String branch, String webhook, String lang, String main) {
        super(name, gitUrl, branch, webhook, lang, main);
    }
}
