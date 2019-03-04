package ro.vitoc.licenta.web.dto;


import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BaseProject extends BaseDto implements Serializable {
    private String gitUrl;
    private String branch;
    private Boolean webhook;


}
