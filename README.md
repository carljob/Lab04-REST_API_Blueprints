## Laboratorio #4 – REST API Blueprints (Java 21 / Spring Boot 3.3.x)
# Escuela Colombiana de Ingeniería – Arquitecturas de Software  

---

## 📋 Requisitos
- Java 21
- Maven 3.9+

## ▶️ Ejecución del proyecto
```bash
mvn clean install
mvn spring-boot:run
```
Probar con `curl`:
```bash
curl -s http://localhost:8080/blueprints | jq
curl -s http://localhost:8080/blueprints/john | jq
curl -s http://localhost:8080/blueprints/john/house | jq
curl -i -X POST http://localhost:8080/blueprints -H 'Content-Type: application/json' -d '{ "author":"john","name":"kitchen","points":[{"x":1,"y":1},{"x":2,"y":2}] }'
curl -i -X PUT  http://localhost:8080/blueprints/john/kitchen/points -H 'Content-Type: application/json' -d '{ "x":3,"y":3 }'
```

> Si deseas activar filtros de puntos (reducción de redundancia, *undersampling*, etc.), implementa nuevas clases que implementen `BlueprintsFilter` y cámbialas por `IdentityFilter` con `@Primary` o usando configuración de Spring.
---

Abrir en navegador:  
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)  

---

## 🗂️ Estructura de carpetas (arquitectura)

```
src/main/java/edu/eci/arsw/blueprints
  ├── model/         # Entidades de dominio: Blueprint, Point
  ├── persistence/   # Interfaz + repositorios (InMemory, Postgres)
  │    └── impl/     # Implementaciones concretas
  ├── services/      # Lógica de negocio y orquestación
  ├── filters/       # Filtros de procesamiento (Identity, Redundancy, Undersampling)
  ├── controllers/   # REST Controllers (BlueprintsAPIController)
  └── config/        # Configuración (Swagger/OpenAPI, etc.)
```

> Esta separación sigue el patrón **capas lógicas** (modelo, persistencia, servicios, controladores), facilitando la extensión hacia nuevas tecnologías o fuentes de datos.

---

## 📖 Actividades del laboratorio

### 1. Familiarización con el código base
- Revisa el paquete `model` con las clases `Blueprint` y `Point`.  
- Entiende la capa `persistence` con `InMemoryBlueprintPersistence`.  
- Analiza la capa `services` (`BlueprintsServices`) y el controlador `BlueprintsAPIController`.

### 2. Migración a persistencia en PostgreSQL
- Configura una base de datos PostgreSQL (puedes usar Docker).  
- Implementa un nuevo repositorio `PostgresBlueprintPersistence` que reemplace la versión en memoria.  
- Mantén el contrato de la interfaz `BlueprintPersistence`.  

### 3. Buenas prácticas de API REST
- Cambia el path base de los controladores a `/api/v1/blueprints`.  
- Usa **códigos HTTP** correctos:  
  - `200 OK` (consultas exitosas).  
  - `201 Created` (creación).  
  - `202 Accepted` (actualizaciones).  
  - `400 Bad Request` (datos inválidos).  
  - `404 Not Found` (recurso inexistente).  
- Implementa una clase genérica de respuesta uniforme:
  ```java
  public record ApiResponse<T>(int code, String message, T data) {}
  ```
  Ejemplo JSON:
  ```json
  {
    "code": 200,
    "message": "execute ok",
    "data": { "author": "john", "name": "house", "points": [...] }
  }
  ```

### 4. OpenAPI / Swagger
- Configura `springdoc-openapi` en el proyecto.  
- Expón documentación automática en `/swagger-ui.html`.  
- Anota endpoints con `@Operation` y `@ApiResponse`.

### 5. Filtros de *Blueprints*
- Implementa filtros:
  - **RedundancyFilter**: elimina puntos duplicados consecutivos.  
  - **UndersamplingFilter**: conserva 1 de cada 2 puntos.  
- Activa los filtros mediante perfiles de Spring (`redundancy`, `undersampling`).  

---

## ✅ Entregables

1. Repositorio en GitHub con:  
   - Código fuente actualizado.  
   - Configuración PostgreSQL (`application.yml` o script SQL).  
   - Swagger/OpenAPI habilitado.  
   - Clase `ApiResponse<T>` implementada.  

2. Documentación:  
   - Informe de laboratorio con instrucciones claras.  
   - Evidencia de consultas en Swagger UI y evidencia de mensajes en la base de datos.  
   - Breve explicación de buenas prácticas aplicadas.  

---

## 📊 Criterios de evaluación

| Criterio | Peso |
|----------|------|
| Diseño de API (versionamiento, DTOs, ApiResponse) | 25% |
| Migración a PostgreSQL (repositorio y persistencia correcta) | 25% |
| Uso correcto de códigos HTTP y control de errores | 20% |
| Documentación con OpenAPI/Swagger + README | 15% |
| Pruebas básicas (unitarias o de integración) | 15% |

**Bonus**:  

- Imagen de contenedor (`spring-boot:build-image`).  
- Métricas con Actuator.  

---

## 🚀 Solución implementada

Esta sección documenta cómo ejecutar el proyecto ya terminado y las funcionalidades desarrolladas.

### Formas de ejecutar

**Opción A — Todo con Docker Compose (recomendado):**
```bash
docker compose up --build
```
Levanta la aplicación y PostgreSQL juntas. Para detener: `docker compose down`.

**Opción B — App local + PostgreSQL en Docker:**
```bash
docker run --name postgres-blueprints -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=blueprintsdb -p 5432:5432 -d postgres
mvn spring-boot:run "-Dspring-boot.run.profiles=postgres"
```

**Opción C — Sin base de datos (persistencia en memoria):**
```bash
mvn spring-boot:run
```

### Activación de filtros por perfil

| Perfil | Filtro | Comportamiento |
|--------|--------|----------------|
| *(ninguno)* | `IdentityFilter` | Sin cambios |
| `redundancy` | `RedundancyFilter` | Elimina puntos duplicados consecutivos |
| `undersampling` | `UndersamplingFilter` | Conserva 1 de cada 2 puntos |

```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=undersampling"
```

### Funcionalidades completadas

- [x] API REST versionada (`/api/v1/blueprints`)
- [x] Respuesta uniforme con `ApiResponse<T>`
- [x] Códigos HTTP correctos (200, 201, 202, 403, 404)
- [x] Documentación Swagger con `@Operation` y `@Tag`
- [x] Persistencia en PostgreSQL con JPA (`PostgresBlueprintPersistence`)
- [x] Filtros configurables por perfil de Spring
- [x] Dockerización completa (app + base de datos con `docker-compose.yml`)

### Verificar datos en la base de datos

```bash
docker exec -it postgres-blueprints psql -U postgres -d blueprintsdb -c "SELECT * FROM blueprints;"
```

---

## 🧾 Evidencia de pruebas

### 1. Creación de un blueprint (POST → 201 Created)

Petición a `POST /api/v1/blueprints` con un blueprint nuevo. Respuesta:

```json
{
  "code": 201,
  "message": "resource created",
  "data": {
    "author": "uribe",
    "name": "apartamento",
    "points": [
      { "x": 0, "y": 0 },
      { "x": 10, "y": 0 },
      { "x": 10, "y": 8 },
      { "x": 0, "y": 8 }
    ]
  }
}
```

### 2. Validación de duplicados (POST → 403 Forbidden)

Al intentar crear un blueprint que ya existe:

```json
{
  "code": 403,
  "message": "Blueprint already exists: carlos:casa",
  "data": null
}
```

### 3. Datos persistidos en PostgreSQL

Consulta directa a la base de datos confirmando la persistencia:

```text
docker exec -it postgres-blueprints psql -U postgres -d blueprintsdb -c "SELECT * FROM blueprints;"

 id | author |    name
----+--------+-------------
  1 | uribe  | apartamento
(1 row)
```

### 4. Relación uno-a-muchos (blueprint → puntos)

Tabla `points` con la columna `blueprint_id` que vincula cada punto a su blueprint:

```text
docker exec -it postgres-blueprints psql -U postgres -d blueprintsdb -c "SELECT * FROM points;"

 id | x | y | blueprint_id
----+---+---+--------------
  1 | 1 | 1 |            1
  2 | 2 | 2 |            1
  3 | 5 | 5 |            2
  4 | 8 | 8 |            2
(4 rows)
```

### 5. Creación automática de tablas (Hibernate)

Al arrancar con el perfil `postgres`, Hibernate crea las tablas automáticamente:

```sql
create table blueprints (
                            id bigint generated by default as identity,
                            author varchar(255),
                            name varchar(255),
                            primary key (id)
);

create table points (
                        id bigint generated by default as identity,
                        x integer not null,
                        y integer not null,
                        blueprint_id bigint,
                        primary key (id)
);
```

### 6. Filtro Undersampling en acción

El blueprint `john/house` (4 puntos) consultado con el perfil `undersampling` devuelve solo 2 puntos (1 de cada 2):

```json
{
  "code": 200,
  "message": "execute ok",
  "data": {
    "author": "john",
    "name": "house",
    "points": [ { "x": 0, "y": 0 }, { "x": 10, "y": 10 } ]
  }
}
```