# Superheroes API

API REST para gestionar héroes de cómic (DC y Marvel), desarrollada con Spring Boot 3 y Java 21.  
Incluye documentación OpenAPI/Swagger y base de datos H2 en memoria, inicializada mediante migraciones Flyway.

---

## Requisitos de ejecución

- **Java**: OpenJDK 21
- **Maven**: 3.9.x 
- **IDE recomendado**: IntelliJ IDEA
- **Puerto por defecto**: `5300`

Dependencias principales:

- Spring Boot 3 (Web, Data JPA, Validation)
- H2 Database (in-memory)
- Flyway (migraciones de base de datos)
- springdoc-openapi (Swagger UI)

---

## Cómo ejecutar la aplicación

### 1. Desde línea de comandos

> Bash
> ```bash
> # Ejecutar la aplicación
> ./mvnw spring-boot:run
> ```

> PowerShell
> ```PowerShell
> # Ejecutar la aplicación
> mvnw.cmd spring-boot:run
> ```


La aplicación quedará disponible en:

- `http://localhost:5300`

### 2. Desde IntelliJ IDEA

1. Importar el proyecto como **Maven Project**.
2. Asegurarse de que el **SDK del proyecto** es Java 21.
3. Ejecutar la clase:

   - `SuperheroesApplication`

4. Verificar en consola que la app se levanta sin errores.

---

## Cómo ejecutar tests

### Todos los tests

> Bash
> ```bash
> # Compilar y ejecutar tests
> ./mvnw clean test
> ```

> PowerShell
> ```PowerShell
> # Compilar y ejecutar tests
> mvnw.cmd test
> ```

### Tests desde IntelliJ

- Click derecho sobre el paquete `com.opitech.superheroes` → `Run 'Tests in ...'`
- O sobre clases concretas, por ejemplo:
  - `HeroServiceTest`
  
Los tests de servicio utilizan **Mockito** para mockear el repositorio y validar:

- Creación de héroes (incluyendo conflictos por nombre duplicado).
- Búsqueda por id (incluyendo caso 404).

---

## Base de datos y migraciones

### H2 In-Memory

La aplicación utiliza una base de datos **H2 en memoria**:

- URL: `jdbc:h2:mem:superheroesdb`
- Usuario: `sa`
- Password: *(vacío)*

Consola web de H2 habilitada en:

- `http://localhost:5300/h2-console`

(La URL de conexión debe ser `jdbc:h2:mem:superheroesdb`).

### Flyway

El esquema y los datos iniciales se gestionan con **Flyway**:

- Carpeta de migraciones: `src/main/resources/db/migration`

Ejemplos de migraciones:

- `V1__create_heroes_schema.sql`  
  Crea la tabla `heroes` con sus columnas y constraints.
- `V2__insert_initial_heroes.sql`  
  Inserta un conjunto de héroes de DC y Marvel para facilitar las pruebas (población inicial).

Hibernate está configurado con:

```yaml
spring.jpa.hibernate.ddl-auto: validate
```

Para validar que el esquema coincida con las entidades JPA.

---

## Ruta de Swagger / OpenAPI

La documentación OpenAPI se expone mediante **springdoc-openapi**:

- Especificación OpenAPI (JSON):  
  `http://localhost:5300/v3/api-docs`
- Swagger UI:  
  `http://localhost:5300/swagger-ui/index.html`

Desde Swagger UI se pueden probar todos los endpoints de la API.

---

## Endpoints principales

Base path:

- `/api/v1/heroes`

Ejemplos:

- `GET /api/v1/heroes`  
  Lista paginada de héroes.
- `GET /api/v1/heroes/{id}`  
  Detalle de un héroe por identificador.
- `POST /api/v1/heroes`  
  Crea un nuevo héroe.
- `PUT /api/v1/heroes/{id}`  
  Actualiza un héroe existente.
- `DELETE /api/v1/heroes/{id}`  
  Elimina un héroe.

---

## Consideraciones técnicas y decisiones de diseño

### Lenguaje y framework

- **Java 21** y **Spring Boot 3.3.4**

### Capa de persistencia

- **Flyway** se encarga del DDL y datos iniciales:
  - Evita depender de `ddl-auto=create/update` en entornos reales.
  - Migraciones versionadas permiten reproducir el esquema fácilmente.

- **JPA/Hibernate** Encargada de la validacion con anotaciones en la entidad `Hero`:
  - Tabla `heroes`.
  - Campos:
    - `name` (único, no nulo)
    - `alias`
    - `universe` (enum `Universe`, almacenado como `STRING`)
    - `powerLevel`, `active`
    - `createdAt`, `updatedAt` con `@PrePersist`/`@PreUpdate`.

### DTOs y mapeo

- Uso de **DTOs** (`HeroRequestDto`, `HeroResponseDto`) para desacoplar:
  - Modelo de dominio (`Hero`) de las estructuras expuestas en la API.
- **`HeroMapper`** centraliza la conversión entre entidad y DTO:
  - `toEntity`, `toResponseDto`, `updateEntityFromDto`, etc.

### Lógica de negocio y capas

- **`Controller`**
- **`Service`**
- **`Reposotory`**
- **`DTOs`**
- **`Mapper`**

### Manejo de errores

- Excepciones específicas:
  - `HeroNotFoundException` → 404 Not Found.
  - `HeroAlreadyExistsException` → 409 Conflict.
- `@ControllerAdvice` para mapear excepciones a respuestas HTTP consistentes.

### Tests

- **Tests unitarios de servicio** con JUnit 5 + Mockito:
  - Mock de `HeroRepository`.
  - Verificación de escenarios de éxito y error.

---
