package ro.vitoc.licenta.core.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SimpleProjectDto extends BaseProjectDto {
    private Integer parameters;

    @Builder
    public SimpleProjectDto(String name, String gitUrl, String branch, String lang, String main, Integer parameters) {
        super(name, gitUrl, branch, lang, main);
        this.parameters = parameters;
    }
}
