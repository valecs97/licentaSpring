package ro.vitoc.licenta.web.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SimpleScriptDto extends BaseProject {
    private Integer timeout;

    @Builder
    public SimpleScriptDto(String gitUrl, String branch, Boolean webhook, Integer timeout){
        super(gitUrl, branch, webhook);
        this.timeout = timeout;
    }
}
