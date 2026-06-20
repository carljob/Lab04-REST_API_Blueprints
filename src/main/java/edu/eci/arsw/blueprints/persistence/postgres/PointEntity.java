package edu.eci.arsw.blueprints.persistence.postgres;

import jakarta.persistence.*;

/**
 * Entidad JPA que representa un punto almacenado en PostgreSQL.
 * Es la versión "para base de datos" del Point de dominio.
 */
@Entity
@Table(name = "points")
public class PointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int x;
    private int y;

    // JPA exige un constructor vacío
    public PointEntity() { }

    public PointEntity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Long getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}