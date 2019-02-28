package ro.vitoc.licenta.web.dto;

import lombok.*;

@Getter
@Setter
@ToString
public class SimpleScriptDto extends BaseProject {
    private Integer timeout;

    @Builder
    public SimpleScriptDto(String gitUrl, Boolean webhook, Integer timeout){
        super(gitUrl,webhook);
        this.timeout = timeout;
    }
}
