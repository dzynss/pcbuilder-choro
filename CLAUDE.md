# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

**PC Builder** is a microservices system (school project) for building/quoting PCs: catalog, inventory, coupons, reviews, support tickets, quotes, shipments, notifications, login and a gateway. There is **no root Maven project** — each `ms-*` directory is a fully independent, self-contained Maven module (its own `pom.xml`, `mvnw`, `Dockerfile`). There is no shared library between them; common classes (`GlobalExceptionHandler`, `RecursoNoEncontradoException`, DTOs used across services) are copy-pasted per service rather than extracted.

Naming inconsistency to be aware of: some module folders use hyphens (`ms-usuarios`, `ms-componentes`, `ms-resenas`, `ms-inventario`, `ms-ofertas`, `ms-soporte`, `ms-gateway`) and others use underscores (`ms_cotizaciones`, `ms_despachos`, `ms_login`, `ms_notificaciones`), but the Java package name is **always** the underscore form (e.g. folder `ms-usuarios` → package `com.pcbuilder.ms_usuarios`).

### Services and ports

| Module dir | Service / package | Port | Depends on (Feign) |
|---|---|---|---|
| `ms-usuarios` | usuarios (CRUD + credential check) | 8083 | — |
| `ms-resenas` | resenas (reviews) | 8084 | ms-componentes |
| `ms-componentes` | componentes (parts catalog + categories) | 8085 | — |
| `ms_login` | login/auth, issues JWT | 8086 | ms-usuarios |
| `ms_cotizaciones` | cotizaciones (quotes, computes total) | 8087 | ms-usuarios, ms-componentes |
| `ms-inventario` | inventario (stock) | 9090 | — |
| `ms-ofertas` | ofertas (discount coupons) | 9091 | — |
| `ms-soporte` | soporte (support tickets) | 9092 | ms-usuarios, ms-componentes |
| `ms_despachos` | despachos (shipping/tracking) | 9093 | ms-usuarios |
| `ms_notificaciones` | notificaciones (email/SMS) | 9094 | ms-usuarios |
| `ms-gateway` | Spring Cloud Gateway, single entry point | 8080 (docker) / **9099 in `application-dev.yml`** | routes to all of the above |

All external traffic goes through the gateway, path-routed by prefix (`/api/usuarios/**`, `/api/resenas/**`, `/api/componentes/**`, `/api/auth/**`, `/api/cotizaciones/**`, `/api/inventario/**`, `/api/ofertas/**`, `/api/soporte/**`, `/api/despachos/**`, `/api/notificaciones/**`) — see `ms-gateway/src/main/resources/application-dev.yml`. Note the gateway's dev profile sets `server.port: 9099` while `docker-compose.yml` maps `8080:8080` for it — check which one is actually active before assuming a port.

Each downstream service exposes Swagger UI at `http://localhost:<port>/swagger-ui.html`.

## Commands

Each service is built/run independently from its own directory using its Maven wrapper.

```bash
# From inside a given ms-* directory:
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev   # run locally (dev profile activates ports/env-based DB URLs)
./mvnw test                                              # run all tests for that service
./mvnw test -Dtest=UsuarioServiceTest                    # run a single test class
./mvnw test -Dtest=UsuarioServiceTest#login_deberiaAutenticar_cuandoCorreoYPasswordSonCorrectos  # single test method
./mvnw clean package                                     # build the jar (also generates JaCoCo coverage report)
```

On Windows use `mvnw.cmd` instead of `./mvnw`.

Full stack (MySQL + all 11 services) via Docker Compose from the repo root:

```bash
docker-compose up --build
docker-compose up -d mysql ms-usuarios ms-componentes   # bring up a subset, e.g. to iterate on one service + its deps
```

`docker-compose.yml` starts a single shared `mysql:8.0` container; each service creates its own database on demand (`createDatabaseIfNotExist=true` in its datasource URL, e.g. `db_usuarios`, `db_resenas`) — there's one DB per service, not one shared schema.

## Architecture / conventions common to every service

Each service follows the identical Spring Boot layout under `src/main/java/com/pcbuilder/<pkg>/`:

- `controller/` — REST controllers (`@RestController`), thin, delegate to service layer.
- `service/` — business logic, `@Service` + constructor injection via Lombok `@RequiredArgsConstructor`.
- `repository/` — `JpaRepository` interfaces.
- `entity/` — JPA entities.
- `dto/` — Java **records** for request/response DTOs (never expose entities directly over HTTP).
- `exception/` — `RecursoNoEncontradoException` (404), `GlobalExceptionHandler` (`@RestControllerAdvice` mapping exceptions → JSON body with `timestamp`/`status`/`mensaje`), plus service-specific ones (`CredencialesInvalidasException` in usuarios/login, `ErrorComunicacionException` for Feign failures).
- `client/` — present only in services that call another microservice; one `@FeignClient` interface per upstream dependency (e.g. `ms-soporte` has `UsuarioClient` and `ComponenteClient`). The client's base URL comes from a config property (`ms.usuarios.url`, `ms.componentes.url`) bound to an env var (`MS_USUARIOS_URL`, `MS_COMPONENTES_URL`), defaulting to `localhost:<port>` — see docker-compose for the container-network values.

Inter-service call pattern: service layer calls the Feign client directly (no circuit breaker), catches `FeignException` and rethrows as `ErrorComunicacionException`; a missing referenced resource (e.g. component/user not found upstream) surfaces as `RecursoNoEncontradoException`. This is how referential integrity across services is enforced (e.g. `ms_cotizaciones` won't create a quote for a nonexistent user or component, and computes the line total from the *real* price fetched from `ms-componentes` rather than trusting the request body).

### Config files per service

- `application.yml` — sets `spring.application.name` and `spring.profiles.active: dev` (dev is always the active profile).
- `application-dev.yml` — the profile actually used: `server.port`, datasource URL (`${DB_HOST:localhost}`, `${DB_USERNAME:root}`, `${DB_PASSWORD:}`), `spring.jpa.hibernate.ddl-auto: validate` (schema is never auto-generated by Hibernate — it comes from Liquibase), and any `ms.<service>.url` properties for Feign clients.
- `application.properties` — leftover default config (hardcoded port/DB URL, no env vars); effectively unused since `dev` is always active. Don't rely on it when tracing runtime config — check `application-dev.yml`.
- `src/main/resources/db/changelog/db.changelog-master.xml` — Liquibase changelog, the source of truth for schema (and, in some services like usuarios, seed data via `<insert>` changesets). `ddl-auto: validate` means Hibernate will fail fast if the entity mapping and this changelog diverge — schema changes must go through Liquibase changesets, not entity annotations.

### Testing

Tests live under `src/test/java/com/pcbuilder/<pkg>/`, using JUnit 5 + Mockito + AssertJ. Service-layer tests mock the repository (and Feign clients, where present) with `@ExtendWith(MockitoExtension.class)` / `@Mock` / `@InjectMocks`, following Given/When/Then comment structure and Spanish method names describing the scenario (e.g. `buscarPorId_deberiaLanzarExcepcion_cuandoNoExiste`). Not every service has service-level tests yet (e.g. `ms-gateway`, several of the smaller CRUD-only services only have the generated `*ApplicationTests` smoke test) — check the specific module's `src/test` before assuming coverage exists.

### Auth

`ms_login` validates credentials against `ms-usuarios` (it has no user table of its own — only a `HistorialLogin` table logging attempts) and issues a JWT (`JwtUtil`, HS256, 1h expiry, key generated in-memory at startup so tokens don't survive a restart). **No other service currently validates this JWT** — there is no security filter on the other microservices' endpoints. Treat the system as unauthenticated end-to-end unless/until that's added; don't assume `/api/**` routes are actually protected just because a login/token flow exists.

## Stack

Spring Boot 4.0.7 / Java 17 / Maven across all services. Data services: Spring Data JPA + MySQL 8 + Liquibase. Gateway: Spring Cloud Gateway (WebFlux) on Spring Cloud 2025.1.1. Inter-service calls: OpenFeign. API docs: springdoc-openapi. Lombok for boilerplate (`@RequiredArgsConstructor`, getters/setters on entities). Coverage: JaCoCo (runs automatically on `mvnw test`/`package`).
