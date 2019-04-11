package ro.vitoc.licenta.core.dto;


import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BaseProjectDto extends BaseDto implements Serializable {
    private String name;
    private String gitUrl;
    private String branch;
    private String webhook;
    private String lang;
    private String main;
}
