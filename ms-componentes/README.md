# ms-componentes

## Contexto
Microservicio del sistema **PC Builder**. Mantiene el catálogo de piezas (componentes) disponibles para armar un computador, organizadas por categoría.

## Integrantes
- Martín Narváez — implementación de ms-usuarios, ms-componentes, ms-resenas, ms_cotizaciones, ms_login
- [Nombre de tu compañero] — implementación de ms-inventario, ms-ofertas, ms-soporte, ms_despachos, ms_notificaciones

## Responsabilidad
CRUD del catálogo de componentes y categorías. Es consumido por `ms-resenas`, `ms_cotizaciones` y `ms-soporte` para validar que una pieza exista y obtener su precio.

## Rutas principales (vía Gateway en :8080)
| Método | Ruta | Descripción |
|---|---|---|
| GET | /api/componentes | Lista todos los componentes |
| GET | /api/componentes/{id} | Busca un componente por ID |
| POST | /api/componentes | Crea un componente |
| PUT | /api/componentes/{id} | Actualiza un componente |
| DELETE | /api/componentes/{id} | Elimina un componente |

## Swagger
http://localhost:8085/swagger-ui.html

## Ejecución local
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
Puerto: `8085`

## Ejecución con Docker
```bash
docker compose up ms-componentes mysql
```

## Variables de entorno
| Variable | Default | Descripción |
|---|---|---|
| DB_HOST | localhost | Host de MySQL |
| DB_USERNAME | root | Usuario de la BD |
| DB_PASSWORD | (vacío) | Password de la BD |
| SPRING_PROFILES_ACTIVE | dev | Perfil activo |
