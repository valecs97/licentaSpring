package ro.vitoc.licenta.core.model;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

public class LogSpecification implements Specification<Log> {

    private final Map<String, String> criteria;
    private List<Predicate> filters;

    public LogSpecification(Map<String, String> criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Log> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        for (Map.Entry<String,String> entry : criteria.entrySet()){
            String key = entry.getKey();
            String filterValue = entry.getValue();
            filters.add(criteriaBuilder.like(criteriaBuilder.upper(root.<String>get(key)), "%" + filterValue.toUpperCase() + "%"));
        }
        return criteriaBuilder.and(filters.toArray(new Predicate[filters.size()]));
    }
}
