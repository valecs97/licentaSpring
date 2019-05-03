package ro.vitoc.licenta.core.dto;

import lombok.*;
import ro.vitoc.licenta.core.model.Log;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class LogDto extends BaseDto {
    String name;
    String log;
    String time;
    Log.LogType type;
}
