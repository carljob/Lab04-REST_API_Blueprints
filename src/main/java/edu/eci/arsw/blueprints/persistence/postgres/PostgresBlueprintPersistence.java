package edu.eci.arsw.blueprints.persistence.postgres;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistence;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación de BlueprintPersistence que guarda los datos en PostgreSQL.
 * Se activa solo con el perfil "postgres".
 */
@Repository
@Profile("postgres")
public class PostgresBlueprintPersistence implements BlueprintPersistence {

    private final BlueprintJpaRepository repo;

    public PostgresBlueprintPersistence(BlueprintJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        if (repo.findByAuthorAndName(bp.getAuthor(), bp.getName()).isPresent()) {
            throw new BlueprintPersistenceException(
                    "Blueprint already exists: " + bp.getAuthor() + ":" + bp.getName());
        }
        repo.save(toEntity(bp));
    }

    @Override
    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        BlueprintEntity entity = repo.findByAuthorAndName(author, name)
                .orElseThrow(() -> new BlueprintNotFoundException(
                        "Blueprint not found: %s/%s".formatted(author, name)));
        return toDomain(entity);
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        List<BlueprintEntity> found = repo.findByAuthor(author);
        if (found.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints for author: " + author);
        }
        return found.stream().map(this::toDomain).collect(Collectors.toSet());
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        return repo.findAll().stream().map(this::toDomain).collect(Collectors.toSet());
    }

    @Override
    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        BlueprintEntity entity = repo.findByAuthorAndName(author, name)
                .orElseThrow(() -> new BlueprintNotFoundException(
                        "Blueprint not found: %s/%s".formatted(author, name)));
        entity.getPoints().add(new PointEntity(x, y));
        repo.save(entity);
    }

    private BlueprintEntity toEntity(Blueprint bp) {
        List<PointEntity> pts = bp.getPoints().stream()
                .map(p -> new PointEntity(p.x(), p.y()))
                .collect(Collectors.toList());
        return new BlueprintEntity(bp.getAuthor(), bp.getName(), pts);
    }

    private Blueprint toDomain(BlueprintEntity entity) {
        List<Point> pts = entity.getPoints().stream()
                .map(pe -> new Point(pe.getX(), pe.getY()))
                .collect(Collectors.toList());
        return new Blueprint(entity.getAuthor(), entity.getName(), pts);
    }
}