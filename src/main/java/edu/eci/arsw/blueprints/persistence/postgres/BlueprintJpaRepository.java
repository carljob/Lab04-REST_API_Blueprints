package edu.eci.arsw.blueprints.persistence.postgres;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para blueprints.
 * Spring genera automáticamente la implementación de estos métodos.
 */
public interface BlueprintJpaRepository extends JpaRepository<BlueprintEntity, Long> {

    List<BlueprintEntity> findByAuthor(String author);

    Optional<BlueprintEntity> findByAuthorAndName(String author, String name);
}