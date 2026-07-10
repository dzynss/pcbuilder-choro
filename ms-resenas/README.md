# ms-resenas

## Contexto
Microservicio del sistema **PC Builder**. Permite a los usuarios dejar comentarios y calificaciones (1 a 5 estrellas) sobre los componentes del catálogo.

## Integrantes
- Martín Narváez — implementación de ms-usuarios, ms-componentes, ms-resenas, ms_cotizaciones, ms_login
- Martin — implementación de ms-inventario, ms-ofertas, ms-soporte, ms_despachos, ms_notificaciones

## Responsabilidad
CRUD de reseñas. Antes de guardar una reseña valida, vía Feign Client, que el componente referenciado exista en `ms-componentes`.

## Rutas principales (vía Gateway en :9099)
| Método | Ruta | Descripción |
|---|---|---|
| GET | /api/resenas | Lista todas las reseñas |
| GET | /api/resenas/{id} | Busca una reseña por ID |
| GET | /api/resenas/estrellas/{calificacion} | Filtra por calificación |
| POST | /api/resenas | Crea una reseña (valida el componente en ms-componentes) |
| PUT | /api/resenas/{id} | Actualiza una reseña |
| DELETE | /api/resenas/{id} | Elimina una reseña |

## Swagger
http://localhost:8084/swagger-ui.html

## Ejecución local
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
Puerto: `8084`. Requiere que `ms-componentes` (puerto 8085) esté corriendo.

## Ejecución con Docker
```bash
docker compose up ms-resenas ms-componentes mysql
```

## Variables de entorno
| Variable | Default | Descripción |
|---|---|---|
| DB_HOST | localhost | Host de MySQL |
| DB_USERNAME | root | Usuario de la BD |
| DB_PASSWORD | (vacío) | Password de la BD |
| MS_COMPONENTES_URL | http://localhost:8085 | URL del Feign Client a ms-componentes |
| SPRING_PROFILES_ACTIVE | dev | Perfil activo |
