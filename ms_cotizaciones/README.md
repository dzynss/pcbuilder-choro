# ms_cotizaciones

## Contexto
Microservicio del sistema **PC Builder**. Permite a un usuario armar una cotización de compra de un componente, calculando el total según el precio real del catálogo.

## Integrantes
- Martín Narváez — implementación de ms-usuarios, ms-componentes, ms-resenas, ms_cotizaciones, ms_login
- [Nombre de tu compañero] — implementación de ms-inventario, ms-ofertas, ms-soporte, ms_despachos, ms_notificaciones

## Responsabilidad
CRUD de cotizaciones. Al crear una cotización valida, vía Feign Client, que el usuario exista en `ms-usuarios` y que el componente exista en `ms-componentes`, y calcula el total (`precio x cantidad`) con el precio real obtenido de ese servicio.

## Rutas principales (vía Gateway en :8080)
| Método | Ruta | Descripción |
|---|---|---|
| GET | /api/cotizaciones | Lista todas las cotizaciones |
| GET | /api/cotizaciones/{id} | Busca una cotización por ID |
| GET | /api/cotizaciones/usuario/{idUsuario} | Cotizaciones de un usuario |
| GET | /api/cotizaciones/usuario/{idUsuario}/total | Total gastado por un usuario |
| POST | /api/cotizaciones | Crea una cotización (valida usuario y componente, calcula el total) |
| PUT | /api/cotizaciones/{id} | Actualiza una cotización |
| DELETE | /api/cotizaciones/{id} | Elimina una cotización |

## Swagger
http://localhost:8087/swagger-ui.html

## Ejecución local
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
Puerto: `8087`. Requiere que `ms-usuarios` (8083) y `ms-componentes` (8085) estén corriendo.

## Ejecución con Docker
```bash
docker compose up ms-cotizaciones ms-usuarios ms-componentes mysql
```

## Variables de entorno
| Variable | Default | Descripción |
|---|---|---|
| DB_HOST | localhost | Host de MySQL |
| DB_USERNAME | root | Usuario de la BD |
| DB_PASSWORD | (vacío) | Password de la BD |
| MS_USUARIOS_URL | http://localhost:8083 | URL del Feign Client a ms-usuarios |
| MS_COMPONENTES_URL | http://localhost:8085 | URL del Feign Client a ms-componentes |
| SPRING_PROFILES_ACTIVE | dev | Perfil activo |
