package edu.eci.arsw.blueprints.persistence.postgres;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa un blueprint almacenado en PostgreSQL.
 * Es la versión "para base de datos" del Blueprint de dominio.
 */
@Entity
@Table(name = "blueprints")
public class BlueprintEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "blueprint_id")
    private List<PointEntity> points = new ArrayList<>();

    // JPA exige un constructor vacío
    public BlueprintEntity() { }

    public BlueprintEntity(String author, String name, List<PointEntity> points) {
        this.author = author;
        this.name = name;
        if (points != null) this.points = points;
    }

    public Long getId() { return id; }
    public String getAuthor() { return author; }
    public String getName() { return name; }
    public List<PointEntity> getPoints() { return points; }

    public void setAuthor(String author) { this.author = author; }
    public void setName(String name) { this.name = name; }
    public void setPoints(List<PointEntity> points) { this.points = points; }
}