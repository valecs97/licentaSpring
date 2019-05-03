package ro.vitoc.licenta.core.model;

import lombok.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Log extends BaseEntity<Long> {
    String name;
    String log;
    String time;
    LogType type;
    public enum LogType{
        ANY,
        GENERAL,
        ERROR,
        WARNING,
        UNDEFINED;
    }
}

