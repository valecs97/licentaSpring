package ro.vitoc.licenta.core.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import ro.vitoc.licenta.core.model.SimpleScript;

import java.util.List;


public interface SimpleScriptRepository extends Repository<SimpleScript, Long> {
}
