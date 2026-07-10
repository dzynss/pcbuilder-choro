# PC Builder

Sistema de microservicios (proyecto escolar, curso Fullstack I) para armar y cotizar PCs: catálogo de componentes, inventario, cupones de descuento, reseñas, soporte técnico, cotizaciones, despachos, notificaciones, login y un gateway como punto único de entrada.

## Arquitectura

El sistema no tiene un proyecto Maven raíz: cada `ms-*` es un módulo Maven independiente y autocontenido (su propio `pom.xml`, `mvnw` y `Dockerfile`), sin librerías compartidas entre ellos. Todo el tráfico externo pasa por el **API Gateway** (`ms-gateway`, puerto `9099`), que enruta cada petición según el prefijo del path hacia el microservicio correspondiente.

Diagramas de arquitectura y de modelo de datos: ver [`docs/arquitectura.md`](docs/arquitectura.md).

## Microservicios

| Módulo | Servicio | Puerto | Responsabilidad | Depende de (Feign) |
|---|---|---|---|---|
| `ms-usuarios` | usuarios | 8083 | CRUD de usuarios y validación de credenciales (correo + password) | — |
| `ms-resenas` | resenas | 8084 | Reseñas y calificaciones (1 a 5) sobre componentes del catálogo | ms-componentes |
| `ms-componentes` | componentes | 8085 | Catálogo de piezas (componentes) y categorías | — |
| `ms_login` | login / auth | 8086 | Autenticación, emite JWT | ms-usuarios |
| `ms_cotizaciones` | cotizaciones | 8087 | Cotizaciones de compra, calcula el total con el precio real del catálogo | ms-usuarios, ms-componentes |
| `ms-inventario` | inventario | 9090 | Stock disponible por componente en bodega | — |
| `ms-ofertas` | ofertas | 9091 | Cupones de descuento | — |
| `ms-soporte` | soporte | 9092 | Tickets de soporte técnico | ms-usuarios, ms-componentes |
| `ms_despachos` | despachos | 9093 | Envío y seguimiento de encomiendas | ms-usuarios |
| `ms_notificaciones` | notificaciones | 9094 | Envío de notificaciones (EMAIL / SMS) | ms-usuarios |
| `ms-gateway` | Gateway | 9099 | Punto único de entrada, enruta hacia los 10 microservicios anteriores | routea a todos |

## Ejecución local

### Opción 1: Docker Compose (recomendada, levanta todo)

Desde la raíz del repositorio:

```bash
docker-compose up --build
```

Esto levanta un único contenedor MySQL 8.0 compartido (cada servicio crea su propia base de datos on-demand, una BD por servicio) y los 11 microservicios.

Para levantar solo un subconjunto (por ejemplo, para iterar en un servicio y sus dependencias):

```bash
docker-compose up -d mysql ms-usuarios ms-componentes
```

### Opción 2: cada servicio por separado con su Maven wrapper

Desde dentro de cada directorio `ms-*`:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

En Windows usa `mvnw.cmd` en lugar de `./mvnw`. El perfil `dev` es el que se usa siempre y define el puerto y la URL de la base de datos (por variables de entorno `DB_HOST`, `DB_USERNAME`, `DB_PASSWORD`).

## Swagger UI por servicio

Cada microservicio expone su documentación OpenAPI en `/swagger-ui.html`:

| Servicio | Swagger |
|---|---|
| ms-usuarios | http://localhost:8083/swagger-ui.html |
| ms-resenas | http://localhost:8084/swagger-ui.html |
| ms-componentes | http://localhost:8085/swagger-ui.html |
| ms_login | http://localhost:8086/swagger-ui.html |
| ms_cotizaciones | http://localhost:8087/swagger-ui.html |
| ms-inventario | http://localhost:9090/swagger-ui.html |
| ms-ofertas | http://localhost:9091/swagger-ui.html |
| ms-soporte | http://localhost:9092/swagger-ui.html |
| ms_despachos | http://localhost:9093/swagger-ui.html |
| ms_notificaciones | http://localhost:9094/swagger-ui.html |

El gateway (puerto `9099`) no expone Swagger propio; enruta las peticiones `/api/**` hacia cada servicio de arriba.

## Integrantes

- Martín Narváez — implementación de ms-usuarios, ms-componentes, ms-resenas, ms_cotizaciones, ms_login
- Martin — implementación de ms-inventario, ms-ofertas, ms-soporte, ms_despachos, ms_notificaciones
