# ms_despachos

## Contexto
Microservicio del sistema **PC Builder**. Gestiona el envío y seguimiento de las encomiendas hacia los usuarios.

## Integrantes
- Martín Narváez — implementación de ms-usuarios, ms-componentes, ms-resenas, ms_cotizaciones, ms_login
- Martin — implementación de ms-inventario, ms-ofertas, ms-soporte, ms_despachos, ms_notificaciones

## Responsabilidad
CRUD de despachos y cambio de estado de seguimiento. Al crear un despacho valida, vía Feign Client, que el usuario exista en `ms-usuarios`.

## Rutas principales (vía Gateway en :9099)
| Método | Ruta | Descripción |
|---|---|---|
| GET | /api/despachos | Lista todos los despachos |
| GET | /api/despachos/{id} | Busca un despacho por ID |
| POST | /api/despachos | Crea un despacho (valida el usuario) |
| PATCH | /api/despachos/{id}/estado | Actualiza el estado de seguimiento |
| DELETE | /api/despachos/{id} | Elimina un despacho |

## Swagger
http://localhost:9093/swagger-ui.html

## Ejecución local
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
Puerto: `9093`. Requiere que `ms-usuarios` (8083) esté corriendo.

## Ejecución con Docker
```bash
docker compose up ms-despachos ms-usuarios mysql
```

## Variables de entorno
| Variable | Default | Descripción |
|---|---|---|
| DB_HOST | localhost | Host de MySQL |
| DB_USERNAME | root | Usuario de la BD |
| DB_PASSWORD | (vacío) | Password de la BD |
| MS_USUARIOS_URL | http://localhost:8083 | URL del Feign Client a ms-usuarios |
| SPRING_PROFILES_ACTIVE | dev | Perfil activo |
