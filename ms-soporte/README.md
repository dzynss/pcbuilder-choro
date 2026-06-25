# ms-soporte

## Contexto
Microservicio del sistema **PC Builder**. Gestiona tickets de soporte técnico que un usuario abre por problemas con un componente comprado.

## Integrantes
- Martín Narváez — implementación de ms-usuarios, ms-componentes, ms-resenas, ms_cotizaciones, ms_login
- [Nombre de tu compañero] — implementación de ms-inventario, ms-ofertas, ms-soporte, ms_despachos, ms_notificaciones

## Responsabilidad
CRUD de tickets de soporte. Al crear un ticket valida, vía Feign Client, que el usuario exista en `ms-usuarios` y que el componente exista en `ms-componentes`.

## Rutas principales (vía Gateway en :8080)
| Método | Ruta | Descripción |
|---|---|---|
| GET | /api/soporte | Lista todos los tickets |
| GET | /api/soporte/{id} | Busca un ticket por ID |
| POST | /api/soporte | Abre un ticket (valida usuario y componente) |
| PUT | /api/soporte/{id}/cerrar | Cierra un ticket |
| DELETE | /api/soporte/{id} | Elimina un ticket |

## Swagger
http://localhost:9092/swagger-ui.html

## Ejecución local
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
Puerto: `9092`. Requiere que `ms-usuarios` (8083) y `ms-componentes` (8085) estén corriendo.

## Ejecución con Docker
```bash
docker compose up ms-soporte ms-usuarios ms-componentes mysql
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
