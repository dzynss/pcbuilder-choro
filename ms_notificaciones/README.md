# ms_notificaciones

## Contexto
Microservicio del sistema **PC Builder**. Envía notificaciones (EMAIL o SMS) a los usuarios.

## Integrantes
- Martín Narváez — implementación de ms-usuarios, ms-componentes, ms-resenas, ms_cotizaciones, ms_login
- Martin — implementación de ms-inventario, ms-ofertas, ms-soporte, ms_despachos, ms_notificaciones

## Responsabilidad
CRUD de notificaciones. Al crear una notificación valida, vía Feign Client, que el usuario destinatario exista en `ms-usuarios`.

## Rutas principales (vía Gateway en :9099)
| Método | Ruta | Descripción |
|---|---|---|
| GET | /api/notificaciones | Lista todas las notificaciones |
| GET | /api/notificaciones/{id} | Busca una notificación por ID |
| POST | /api/notificaciones | Crea una notificación (valida el usuario) |
| DELETE | /api/notificaciones/{id} | Elimina una notificación |

## Swagger
http://localhost:9094/swagger-ui.html

## Ejecución local
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
Puerto: `9094`. Requiere que `ms-usuarios` (8083) esté corriendo.

## Ejecución con Docker
```bash
docker compose up ms-notificaciones ms-usuarios mysql
```

## Variables de entorno
| Variable | Default | Descripción |
|---|---|---|
| DB_HOST | localhost | Host de MySQL |
| DB_USERNAME | root | Usuario de la BD |
| DB_PASSWORD | (vacío) | Password de la BD |
| MS_USUARIOS_URL | http://localhost:8083 | URL del Feign Client a ms-usuarios |
| SPRING_PROFILES_ACTIVE | dev | Perfil activo |
