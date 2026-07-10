# ms-ofertas

## Contexto
Microservicio del sistema **PC Builder**. Administra cupones de descuento y permite aplicar su porcentaje sobre un monto.

## Integrantes
- Martín Narváez — implementación de ms-usuarios, ms-componentes, ms-resenas, ms_cotizaciones, ms_login
- Martin — implementación de ms-inventario, ms-ofertas, ms-soporte, ms_despachos, ms_notificaciones

## Responsabilidad
CRUD de cupones (código + % de descuento) y regla de negocio de aplicación de descuento sobre un monto base.

## Rutas principales (vía Gateway en :9099)
| Método | Ruta | Descripción |
|---|---|---|
| GET | /api/ofertas | Lista todos los cupones |
| GET | /api/ofertas/{id} | Busca un cupón por ID |
| GET | /api/ofertas/codigo/{codigo} | Valida un código promocional |
| POST | /api/ofertas | Crea un cupón |
| DELETE | /api/ofertas/{id} | Elimina un cupón |

## Swagger
http://localhost:9091/swagger-ui.html

## Ejecución local
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
Puerto: `9091`

## Ejecución con Docker
```bash
docker compose up ms-ofertas mysql
```

## Variables de entorno
| Variable | Default | Descripción |
|---|---|---|
| DB_HOST | localhost | Host de MySQL |
| DB_USERNAME | root | Usuario de la BD |
| DB_PASSWORD | (vacío) | Password de la BD |
| SPRING_PROFILES_ACTIVE | dev | Perfil activo |
