# ms-inventario

## Contexto
Microservicio del sistema **PC Builder**. Controla el stock disponible de cada componente en bodega.

## Integrantes
- Martín Narváez — implementación de ms-usuarios, ms-componentes, ms-resenas, ms_cotizaciones, ms_login
- [Nombre de tu compañero] — implementación de ms-inventario, ms-ofertas, ms-soporte, ms_despachos, ms_notificaciones

## Responsabilidad
CRUD de registros de inventario (cantidad disponible y ubicación en bodega) por componente.

## Rutas principales (vía Gateway en :8080)
| Método | Ruta | Descripción |
|---|---|---|
| GET | /api/inventario | Lista todos los registros |
| GET | /api/inventario/{id} | Busca un registro por ID |
| POST | /api/inventario | Crea un registro de stock |
| DELETE | /api/inventario/{id} | Elimina un registro |

## Swagger
http://localhost:9090/swagger-ui.html

## Ejecución local
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
Puerto: `9090`

## Ejecución con Docker
```bash
docker compose up ms-inventario mysql
```

## Variables de entorno
| Variable | Default | Descripción |
|---|---|---|
| DB_HOST | localhost | Host de MySQL |
| DB_USERNAME | root | Usuario de la BD |
| DB_PASSWORD | (vacío) | Password de la BD |
| SPRING_PROFILES_ACTIVE | dev | Perfil activo |
