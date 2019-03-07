package ro.vitoc.licenta.web.dto;


import lombok.*;

import javax.persistence.Column;
import javax.ws.rs.DefaultValue;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BaseProject extends BaseDto implements Serializable {
    private String name;
    private String gitUrl;
    private String branch;
    private String webhook;
    private String lang;
    private String main;
    private List<String> req;
    private Integer parameters;
}
