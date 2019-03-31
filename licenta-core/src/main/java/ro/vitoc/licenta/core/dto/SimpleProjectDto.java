package ro.vitoc.licenta.core.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class SimpleProjectDto extends BaseProjectDto {
    @Builder
    public SimpleProjectDto(String name, String gitUrl, String branch, String webhook, String lang, String main, List<String> req, Integer parameters) {
        super(name, gitUrl, branch, webhook, lang, main, req, parameters);
    }
}