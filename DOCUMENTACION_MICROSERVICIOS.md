# Documentación técnica — PC Builder (microservicios)

Este documento explica, servicio por servicio y archivo por archivo, qué hace cada uno de los 11 microservicios que componen el sistema **PC Builder**. Generado a partir de una lectura completa del código fuente (controllers, services, repositories, entities, DTOs, excepciones, clientes Feign, seguridad y configuración de cada módulo).

> Nota: cada `ms-*`/`ms_*` es un módulo Maven **totalmente independiente** (su propio `pom.xml`, `mvnw`, `Dockerfile`). No hay librería compartida: clases como `GlobalExceptionHandler` o `RecursoNoEncontradoException` están copiadas y adaptadas en cada servicio.

## Índice

1. [Visión general de la arquitectura](#visión-general-de-la-arquitectura)
2. [ms-gateway — Punto de entrada](#ms-gateway--punto-de-entrada)
3. [ms-usuarios — Gestión de usuarios](#ms-usuarios--gestión-de-usuarios)
4. [ms_login — Autenticación / emisión de JWT](#ms_login--autenticación--emisión-de-jwt)
5. [ms-componentes — Catálogo de piezas](#ms-componentes--catálogo-de-piezas)
6. [ms-resenas — Reseñas de componentes](#ms-resenas--reseñas-de-componentes)
7. [ms_cotizaciones — Cotizaciones](#ms_cotizaciones--cotizaciones)
8. [ms-inventario — Stock](#ms-inventario--stock)
9. [ms-ofertas — Cupones de descuento](#ms-ofertas--cupones-de-descuento)
10. [ms-soporte — Tickets de soporte](#ms-soporte--tickets-de-soporte)
11. [ms_despachos — Envíos / tracking](#ms_despachos--envíos--tracking)
12. [ms_notificaciones — Notificaciones](#ms_notificaciones--notificaciones)
13. [Seguridad JWT: cómo encaja todo](#seguridad-jwt-cómo-encaja-todo)

---

## Visión general de la arquitectura

Todo el tráfico externo entra por **`ms-gateway`** (puerto 9099), que rutea por prefijo de path hacia el microservicio correspondiente. Cada servicio tiene su propia base de datos MySQL (`db_usuarios`, `db_resenas`, etc., todas en el mismo contenedor `mysql:8.0` de `docker-compose.yml`, pero con `createDatabaseIfNotExist=true` — una BD por servicio, no un esquema compartido). El esquema de cada BD lo gestiona **Liquibase** (`ddl-auto: validate`, Hibernate nunca crea tablas).

| Servicio | Paquete Java | Puerto | Depende de (Feign) | Rol |
|---|---|---|---|---|
| `ms-gateway` | `com.pcbuilder.ms_gateway` | 9099 | rutea a los 10 restantes | Único punto de entrada externo |
| `ms-usuarios` | `com.pcbuilder.ms_usuarios` | 8083 | — | CRUD de usuarios + verificación de credenciales |
| `ms_login` | `com.pcbuilder.ms_login` | 8086 | ms-usuarios | Emite JWT tras validar credenciales |
| `ms-componentes` | `com.pcbuilder.ms_componentes` | 8085 | — | Catálogo de categorías y piezas de PC |
| `ms-resenas` | `com.pcbuilder.ms_resenas` | 8084 | ms-componentes | Reseñas/calificaciones de componentes |
| `ms_cotizaciones` | `com.pcbuilder.ms_cotizaciones` | 8087 | ms-usuarios, ms-componentes | Cotiza componente × cantidad al precio real |
| `ms-inventario` | `com.pcbuilder.ms_inventario` | 9090 | — | Stock disponible por componente/bodega |
| `ms-ofertas` | `com.pcbuilder.ms_ofertas` | 9091 | — | Cupones de descuento |
| `ms-soporte` | `com.pcbuilder.ms_soporte` | 9092 | ms-usuarios, ms-componentes | Tickets de soporte técnico |
| `ms_despachos` | `com.pcbuilder.ms_despachos` | 9093 | ms-usuarios | Envíos y tracking |
| `ms_notificaciones` | `com.pcbuilder.ms_notificaciones` | 9094 | ms-usuarios | Bitácora de notificaciones EMAIL/SMS |

**Patrón repetido en todos los servicios de negocio:** `controller/` (delgado) → `service/` (lógica + mapeo entidad↔DTO) → `repository/` (`JpaRepository`) → `entity/` (JPA) ; `dto/` con **records** de entrada/salida; `exception/` con `RecursoNoEncontradoException` (404) y un `GlobalExceptionHandler` (`@RestControllerAdvice`) que traduce excepciones a `{timestamp, status, mensaje}`; `client/` con interfaces `@FeignClient` cuando el servicio depende de otro; `security/` con `JwtAuthFilter` + `SecurityConfig` en los 9 servicios de negocio (no en `ms_login` ni conceptualmente necesario en `ms-gateway`, que usa su propio filtro reactivo).

**Integridad referencial entre servicios:** como no hay FKs de base de datos entre microservicios, la integridad se hace en tiempo de ejecución vía Feign: antes de crear/actualizar un recurso que referencia un ID de otro servicio (usuario, componente), el service llama al otro microservicio; un 404 remoto se traduce en `RecursoNoEncontradoException` (→ 404 local) y cualquier otro fallo de comunicación (timeout, 5xx, servicio caído) en `ErrorComunicacionException` (→ 502 local).

---

## ms-gateway — Punto de entrada

**Spring Cloud Gateway sobre WebFlux** (reactivo, no servlet). No tiene lógica de negocio: solo enruta y valida JWT como primera línea de defensa.

### `MsGatewayApplication.java`
Clase de arranque estándar. No define `RouteLocatorBuilder` en código — el ruteo es 100% declarativo vía YAML.

### `filter/JwtValidationGlobalFilter.java`
`GlobalFilter` con `getOrder() = HIGHEST_PRECEDENCE + 10` (se ejecuta muy temprano). Construye la clave HMAC desde `jwt.secret` en el constructor.
- **Rutas exentas de validación:** cualquier path que empiece con `/api/auth` (todo `ms_login`), y `POST /api/usuarios` exacto (registro de usuario nuevo — comparación exacta de path, no `startsWith`).
- **Resto de rutas:** exige header `Authorization: Bearer <token>`; si falta o el JWT es inválido/expirado, responde `401` con JSON `{timestamp, status:401, mensaje:"Token JWT ausente o inválido"}` construido manualmente con `DataBufferFactory` (estilo WebFlux).
- Si el token es válido, deja pasar la petición **sin** inyectar headers de identidad (no propaga `X-User-Id` ni similar) — solo valida, no enriquece.

### Configuración (`application-dev.yml`)
- `server.port: 9099`.
- **Tabla de rutas** (`spring.cloud.gateway.server.webflux.routes`), cada una con predicate `/api/x/**` y `/api/x`:

| Prefijo | Servicio destino | URI (env var / default) |
|---|---|---|
| `/api/usuarios/**` | ms-usuarios | `${MS_USUARIOS_URL:http://localhost:8083}` |
| `/api/resenas/**` | ms-resenas | `${MS_RESENAS_URL:http://localhost:8084}` |
| `/api/componentes/**` | ms-componentes | `${MS_COMPONENTES_URL:http://localhost:8085}` |
| `/api/auth/**` | ms-login | `${MS_LOGIN_URL:http://localhost:8086}` |
| `/api/cotizaciones/**` | ms-cotizaciones | `${MS_COTIZACIONES_URL:http://localhost:8087}` |
| `/api/inventario/**` | ms-inventario | `${MS_INVENTARIO_URL:http://localhost:9090}` |
| `/api/ofertas/**` | ms-ofertas | `${MS_OFERTAS_URL:http://localhost:9091}` |
| `/api/soporte/**` | ms-soporte | `${MS_SOPORTE_URL:http://localhost:9092}` |
| `/api/despachos/**` | ms-despachos | `${MS_DESPACHOS_URL:http://localhost:9093}` |
| `/api/notificaciones/**` | ms-notificaciones | `${MS_NOTIFICACIONES_URL:http://localhost:9094}` |

- **No** hay CORS, timeouts, circuit breaker (resilience4j) ni rate limiting configurados — solo rutas + logging + secreto JWT. No hay `StripPrefix`: el path completo se reenvía tal cual, por eso cada microservicio expone sus endpoints bajo el mismo prefijo `/api/xxx`.

---

## ms-usuarios — Gestión de usuarios

**Puerto 8083 · sin dependencias Feign salientes.** CRUD de usuarios + verificación de credenciales (login "de bajo nivel", consumido por `ms_login`).

- **`MsUsuariosApplication.java`** — arranque Spring Boot.
- **`config/OpenApiConfig.java`** — esquema de seguridad `bearerAuth` en Swagger.
- **`controller/UsuarioController.java`** (`/api/usuarios`):

| Método | Path | Descripción | Respuesta |
|---|---|---|---|
| POST | `/login` | Valida correo/password, devuelve el usuario (sin password) | 200 |
| GET | `` | Lista todos los usuarios | 200 |
| GET | `/{id}` | Busca por ID | 200 / 404 |
| POST | `` | Registra un usuario | 201 |
| PUT | `/{id}` | Actualiza (password solo si viene no vacío) | 200 |
| DELETE | `/{id}` | Elimina | 204 |

- **`entity/Usuario.java`** — `id, nombre, correo (unique), password (texto plano, sin hashing), rol (ADMIN|USER)`. Sin relaciones.
- **`dto/`** — `LoginRequestDTO` (correo, password), `UsuarioRequestDTO` (nombre, correo, password, rol — password sin `@NotBlank` para permitir dejarla vacía al actualizar), `UsuarioResponseDTO` (nunca incluye password).
- **`repository/UsuarioRepository.java`** — `findByCorreoAndPassword(correo, password)` para el login.
- **`service/UsuarioService.java`** — valida manualmente que `password` no sea nulo/blank al crear (`SolicitudInvalidaException` si falla); en `actualizar`, el password solo se sobrescribe si viene no vacío; `login()` compara correo+password exactos en texto plano y lanza `CredencialesInvalidasException` si no hay match (no genera JWT — eso lo hace `ms_login`).
- **`exception/`** — `CredencialesInvalidasException` (401), `RecursoNoEncontradoException` (404), `SolicitudInvalidaException` (400), más el `GlobalExceptionHandler` estándar (409 en `DataIntegrityViolationException` por correo duplicado, 400/405/500 para el resto).
- **`security/`** — `JwtAuthFilter` + `SecurityConfig`: público `POST /api/usuarios` y `POST /api/usuarios/login` (y Swagger); todo lo demás requiere JWT.
- **Seed data (Liquibase):** 10 usuarios de prueba, todos con `password="1234"` (2 ADMIN, 8 USER) — ver `db/changelog/db.changelog-master.xml`.

---

## ms_login — Autenticación / emisión de JWT

**Puerto 8086 · depende de ms-usuarios.** Servicio de autenticación puro: no tiene tabla de usuarios propia, solo un historial de intentos de login.

### Flujo completo (`POST /api/auth/login`)
1. `AuthController` recibe `LoginRequestDTO {correo, password}` (validado con `@Valid`).
2. `AuthService` llama a `UsuarioClient.login(...)` → `ms-usuarios` (`POST /api/usuarios/login`).
3. Si `ms-usuarios` responde 401/404 → se guarda el intento como `"FALLIDO"` en `HistorialLogin` y se lanza `CredencialesInvalidasException` (→ 401).
4. Si falla la comunicación (timeout, 5xx) → intento `"FALLIDO"`, `ErrorComunicacionException` (→ 502).
5. Si las credenciales son válidas → `JwtUtil.generarToken(correo)` genera el JWT, se guarda el intento como `"EXITOSO"`, se devuelve `TokenResponseDTO{token}` (200).
6. El token se firma **solo con el correo** como `subject` — no incluye el `rol` devuelto por `ms-usuarios` ni ningún otro claim.

### `util/JwtUtil.java`
HMAC (`Keys.hmacShaKeyFor`) con clave `jwt.secret`; expiración fija de **1 hora** (3600000 ms); claims: `subject=correo`, `issuedAt`, `expiration`. Sin claim de rol ni de id de usuario.

### Endpoints (`controller/AuthController.java`, `/api/auth`)
| Método | Path | Descripción |
|---|---|---|
| POST | `/login` | Login → JWT |
| GET | `/historial` | Lista todos los intentos de login (éxito/fallo) |

### ⚠️ Particularidad de seguridad
`ms_login` **no tiene `spring-boot-starter-security`** ni `SecurityConfig`/`JwtAuthFilter` propios. Sus dos endpoints están completamente abiertos a nivel de aplicación — solo `/api/auth/**` está exento en el gateway porque debe serlo (login), pero `/historial` queda sin protección alguna si se llama directo al puerto 8086 sin pasar por el gateway.

- **`entity/HistorialLogin.java`** — `id, correoUsuario, fechaHora, estado (EXITOSO|FALLIDO)`.
- **`repository/HistorialRepository.java`** — CRUD estándar.
- **`exception/`** — `CredencialesInvalidasException` (401), `ErrorComunicacionException` (502), `GlobalExceptionHandler` estándar.
- **Seed data:** 10 registros de historial de ejemplo con estados mixtos.

---

## ms-componentes — Catálogo de piezas

**Puerto 8085 · sin dependencias Feign salientes** (es consumido por otros 3 servicios: `ms_cotizaciones`, `ms-resenas`, `ms-soporte`).

- **`config/DataLoader.java`** — `CommandLineRunner` que agrega 3 CPUs adicionales al arrancar (solo si `count() < 10`), complementando el seed de Liquibase.
- **Controllers:**
  - `CategoriaController` (`/api/categorias`): CRUD completo; `DELETE` falla con 409 si la categoría tiene componentes asociados.
  - `ComponenteController` (`/api/componentes`): CRUD completo; el `id` de categoría se resuelve y valida al crear/actualizar.
- **`entity/Categoria.java`** (id, nombre) 1—N **`entity/Componente.java`** (id, nombre, marca, precio, stock, `categoria_id` FK).
- **`dto/ComponenteResponseDTO`** — `id, nombre, marca, precio, stock, categoria` (nombre, no id) — este es el contrato que consumen los otros 3 microservicios vía Feign.
- **`service/CategoriaService.java`** — `eliminar()` valida `existsByCategoriaId` antes de borrar (regla de integridad a nivel de aplicación, sin FK `ON DELETE`).
- **`service/ComponenteService.java`** — resuelve `idCategoria` contra `CategoriaRepository` al guardar/actualizar (404 si no existe).
- **`security/`** — mismo patrón `JwtAuthFilter` + `SecurityConfig` (todo protegido salvo Swagger).
- **Seed data:** categorías `GPU` e `CPU`, con 5 GPUs y 2 CPUs vía Liquibase + 3 CPUs más vía `DataLoader`.

---

## ms-resenas — Reseñas de componentes

**Puerto 8084 · depende de ms-componentes.** CRUD de reseñas (autor, comentario, 1-5 estrellas) sobre un `idComponente`.

- **`controller/ResenaController.java`** (`/api/resenas`): listar, por ID, por `calificacion` (estrellas), crear, actualizar, eliminar.
- **`entity/Resena.java`** — `id, autor, comentario, calificacion (1-5), idComponente` (referencia lógica, sin FK real).
- **`client/ComponenteClient.java`** — Feign hacia `ms-componentes`, `GET /api/componentes/{id}`.
- **`service/ResenaService.java`** — antes de `guardar`/`actualizar`, llama a `validarComponenteExiste(idComponente)`: 404 remoto → `RecursoNoEncontradoException`; otro fallo → `ErrorComunicacionException`. El componente obtenido solo se usa para validar, no se persiste ningún dato suyo en la reseña.
- **`security/`** — mismo patrón estándar.
- **Seed data:** 10 reseñas de ejemplo sobre componentes 1-8.

---

## ms_cotizaciones — Cotizaciones

**Puerto 8087 · depende de ms-usuarios y ms-componentes.** Calcula el total de una cotización (componente × cantidad) usando el **precio real** obtenido en caliente desde `ms-componentes` — nunca confía en un precio enviado por el cliente.

### Flujo de creación (`POST /api/cotizaciones`)
1. Valida que el `idUsuario` exista (`UsuarioClient.buscarPorId`, Feign → `ms-usuarios`).
2. Obtiene el componente real (`ComponenteClient.buscarPorId`, Feign → `ms-componentes`) — necesita el precio.
3. Calcula `total = componente.precio() * cantidad`; si `precio` viene `null`, lanza `ErrorComunicacionException` (dato inválido del servicio remoto).
4. Persiste `Cotizacion{idUsuario, idComponente, cantidad, total}`.

En ambos pasos, 404 remoto → `RecursoNoEncontradoException`; otro fallo → `ErrorComunicacionException`. `PUT` repite exactamente la misma validación y recalcula el total.

- **`controller/CotizacionController.java`** (`/api/cotizaciones`): además del CRUD, expone `GET /usuario/{idUsuario}` (cotizaciones de un usuario) y `GET /usuario/{idUsuario}/total` (suma total gastado).
- **`entity/Cotizacion.java`** — modelo plano (una cotización = un componente + cantidad, no un carrito multi-ítem): `id, idUsuario, idComponente, cantidad, total`.
- **`security/`** — mismo patrón estándar.
- **Seed data:** 10 cotizaciones de prueba.

---

## ms-inventario — Stock

**Puerto 9090 · sin dependencias Feign** (confirmado: no hay `spring-cloud-starter-openfeign` en el `pom.xml`).

- **`controller/InventarioController.java`** (`/api/inventario`): CRUD completo.
- **`entity/Inventario.java`** — `id, idComponente (referencia lógica), cantidadDisponible (≥0), ubicacionBodega, ultimaActualizacion` (autogenerada por `@PrePersist`/`@PreUpdate`).
- **`service/InventarioService.java`** — `actualizar()` **sobrescribe completamente** el registro (no hay lógica de incremento/decremento de stock; el llamador debe enviar la cantidad final deseada). No hay transacciones explícitas ni bloqueo optimista/pesimista.
- **`repository/InventarioRepository.java`** — tiene `findByIdComponente` declarado pero no usado actualmente.
- **`security/`** — mismo patrón estándar.
- **Seed data:** 2 registros (componente 1: 50 uds; componente 2: 15 uds).

---

## ms-ofertas — Cupones de descuento

**Puerto 9091 · sin dependencias Feign** (confirmado, no hay `@FeignClient` en el código).

- **`controller/OfertaController.java`** (`/api/ofertas`): CRUD + `GET /codigo/{codigo}` (valida un cupón por código).
- **`entity/Oferta.java`** — `id, codigo (unique), porcentajeDescuento (1-100), activa (boolean, default true)`.
- **`service/OfertaService.java`** — `guardar`/`buscarPorCodigo` normalizan el código a mayúsculas; `actualizar` **solo** permite modificar `porcentajeDescuento` (el código es inmutable, documentado explícitamente); `aplicarDescuento(oferta, montoBase)` calcula `montoBase - montoBase*porcentaje/100`, valida `montoBase>0` y `activa==true` — **pero este método no está expuesto por ningún endpoint**, es lógica de negocio sin consumidor actual. No hay fechas de vigencia (`fechaInicio`/`fechaFin`), solo el flag `activa`.
- **`security/`** — mismo patrón estándar.
- **Seed data:** cupones `PCGAMER2026` (15%) y `PROFECARLOS` (50%), ambos activos.
- Nota curiosa en el changelog: un changeset (`fix-tipo-columna-activa`) corrige el tipo de la columna `activa` de `TINYINT(1)` a `BIT(1)` porque Hibernate fallaba al validar el esquema (`SchemaManagementException`) contra el tipo `boolean`.

---

## ms-soporte — Tickets de soporte

**Puerto 9092 · depende de ms-usuarios y ms-componentes.** Gestiona tickets de soporte técnico con un ciclo de vida simple de dos estados.

- **`controller/SoporteController.java`** (`/api/soporte`): listar, por ID, crear, `PUT /{id}/cerrar` (cierre), editar, eliminar.
- **`entity/TicketSoporte.java`** — `id, idUsuario, idComponente, descripcion, estado (ABIERTO|CERRADO, string simple), fechaCreacion`.
- **`client/`** — `UsuarioClient` y `ComponenteClient` (Feign hacia ambos servicios).
- **`service/SoporteService.java`** — al crear, fuerza `estado="ABIERTO"` y `fechaCreacion=now()` sin importar el input; valida usuario y componente al crear y al editar (no al cerrar/listar/eliminar); `cerrarTicket()` es la única transición posible — si ya está `CERRADO`, lanza `EstadoInvalidoException` (409, sin reapertura posible).
- **`exception/EstadoInvalidoException.java`** — específica de este servicio, 409.
- **`security/`** — mismo patrón estándar.
- **Seed data:** 2 tickets (uno ABIERTO, uno CERRADO).

---

## ms_despachos — Envíos / tracking

**Puerto 9093 · depende de ms-usuarios.** Gestiona el ciclo de vida logístico de un envío.

- **`controller/DespachoController.java`** (`/api/despachos`): listar, rastrear por ID, crear, `PATCH /{id}/estado` (cambia estado vía **query param**, no body), editar (dirección/transportista), eliminar (borrado físico).
- **`entity/Despacho.java`** — `id, idUsuario, direccionEnvio, empresaTransporte, estadoSeguimiento (BODEGA|EN_RUTA|ENTREGADO), fechaDespacho`.
- **`client/UsuarioClient.java`** — Feign hacia `ms-usuarios`.
- **`service/DespachoService.java`** — al crear, fuerza `estadoSeguimiento="BODEGA"` y `fechaDespacho=now()`; `actualizarEstado()` valida contra el set `{BODEGA, EN_RUTA, ENTREGADO}` (normaliza a mayúsculas, `EstadoInvalidoException` si no calza) — **no valida transiciones**, se puede saltar de `ENTREGADO` a `BODEGA` sin restricción; `actualizar()` (PUT) no toca el estado, solo datos logísticos.
- **`repository/DespachoRepository.java`** — tiene `findByIdUsuario` declarado pero sin consumidor actual (posible endpoint pendiente).
- **`security/`** — mismo patrón estándar, sin autorización por rol.
- **Seed data:** 2 despachos (uno EN_RUTA, uno ENTREGADO).

---

## ms_notificaciones — Notificaciones

**Puerto 9094 · depende de ms-usuarios.** Bitácora de notificaciones EMAIL/SMS — **no envía correos/SMS reales** (no hay `spring-boot-starter-mail` ni SDK de SMS en el `pom.xml`); "enviar" equivale a persistir el registro con `estado="ENVIADO"`.

- **`controller/NotificacionController.java`** (`/api/notificaciones`): listar, por ID, crear (valida usuario), actualizar (solo `tipoMensaje`/`contenido`, no reasigna usuario ni estado), eliminar.
- **`entity/Notificacion.java`** — `id, idUsuario, tipoMensaje (EMAIL|SMS, validado por regex), contenido, estado, fechaEnvio`.
- **`client/UsuarioClient.java`** — Feign hacia `ms-usuarios`.
- **`service/NotificacionService.java`** — `guardar()` valida el usuario destinatario antes de persistir con `estado="ENVIADO"` y `fechaEnvio=now()`. Sin motor de plantillas (contenido es texto libre).
- **`security/`** — **este es el servicio que originó la revisión de JWT**: tiene `JwtAuthFilter` + `SecurityConfig` con `anyRequest().authenticated()` (todo protegido salvo Swagger), igual que los demás.
- **Seed data:** 2 notificaciones de ejemplo (una EMAIL, una SMS).

---

## Seguridad JWT: cómo encaja todo

1. **Emisión:** solo `ms_login` emite tokens (`JwtUtil`, HS256 vía `Keys.hmacShaKeyFor`, HMAC con la clave `jwt.secret`, expiración 1h, claim `subject=correo` únicamente).
2. **Clave compartida:** los 11 servicios leen la misma variable `JWT_SECRET` (mismo valor por defecto `pcbuilder-choro-ms-login-clave-secreta-cambiar-en-produccion-2026` en cada `application-dev.yml` y en `docker-compose.yml`), lo que permite que cualquiera valide localmente sin llamar de vuelta a `ms_login`.
3. **Primera validación — Gateway:** `ms-gateway` rechaza con 401 cualquier request sin JWT válido, excepto `/api/auth/**` y `POST /api/usuarios`.
4. **Segunda validación — cada microservicio de negocio:** los 9 servicios de negocio (todos menos `ms_login` y `ms-gateway`) repiten la validación con su propio `JwtAuthFilter` + `SecurityConfig` (`anyRequest().authenticated()`, salvo Swagger y, en `ms-usuarios`, el registro/login). Es defensa en profundidad: si alguien accede directo al puerto de un microservicio sin pasar por el gateway, sigue protegido.
5. **Excepción notable:** `ms_login` no valida nada de sí mismo — no tiene Spring Security. Su endpoint `/api/auth/historial` queda abierto si se llama directo al puerto 8086 (fuera del gateway).
6. **Sin control de roles:** en ningún servicio se usa `hasRole`/`hasAuthority` — el filtro solo exige *estar autenticado* con un token válido; el `rol` del usuario (ADMIN/USER) no se propaga como claim del JWT ni se usa para autorizar endpoints.
