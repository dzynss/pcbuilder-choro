# ms-usuarios

## Contexto
Microservicio del sistema **PC Builder**. Gestiona el registro, consulta y autenticación básica de los usuarios de la plataforma.

## Integrantes
- Martín Narváez — implementación de ms-usuarios, ms-componentes, ms-resenas, ms_cotizaciones, ms_login
- Martin — implementación de ms-inventario, ms-ofertas, ms-soporte, ms_despachos, ms_notificaciones

## Responsabilidad
CRUD de usuarios y validación de credenciales (correo + password). Es consumido por `ms_login`, `ms_cotizaciones`, `ms_despachos`, `ms_notificaciones` y `ms-soporte` para validar que un usuario exista.

## Rutas principales (vía Gateway en :9099)
| Método | Ruta | Descripción |
|---|---|---|
| GET | /api/usuarios | Lista todos los usuarios |
| GET | /api/usuarios/{id} | Busca un usuario por ID |
| POST | /api/usuarios | Crea un usuario |
| PUT | /api/usuarios/{id} | Actualiza un usuario |
| DELETE | /api/usuarios/{id} | Elimina un usuario |
| POST | /api/usuarios/login | Valida credenciales |

## Swagger
http://localhost:8083/swagger-ui.html

## Ejecución local
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
Puerto: `8083`

## Ejecución con Docker
```bash
docker compose up ms-usuarios mysql
```

## Variables de entorno
| Variable | Default | Descripción |
|---|---|---|
| DB_HOST | localhost | Host de MySQL |
| DB_USERNAME | root | Usuario de la BD |
| DB_PASSWORD | (vacío) | Password de la BD |
| SPRING_PROFILES_ACTIVE | dev | Perfil activo |
