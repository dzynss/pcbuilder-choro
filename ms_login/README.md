# ms_login (Auth)

## Contexto
Microservicio del sistema **PC Builder**. Centraliza el login: valida credenciales contra `ms-usuarios` y emite un token JWT. Registra cada intento (exitoso o fallido) en su propio historial.

## Integrantes
- Martín Narváez — implementación de ms-usuarios, ms-componentes, ms-resenas, ms_cotizaciones, ms_login
- Martin — implementación de ms-inventario, ms-ofertas, ms-soporte, ms_despachos, ms_notificaciones

## Responsabilidad
Login y emisión de JWT. No tiene su propia tabla de usuarios: delega la validación de correo/password a `ms-usuarios` vía Feign Client y solo guarda el historial de intentos.

> Nota: actualmente el JWT se genera pero ningún otro microservicio lo valida (no hay filtro de seguridad en los demás servicios). Es un punto pendiente si se quiere optar al ítem opcional "JWT en todos los microservicios" de la pauta.

## Rutas principales (vía Gateway en :9099)
| Método | Ruta | Descripción |
|---|---|---|
| POST | /api/auth/login | Valida credenciales en ms-usuarios y devuelve un token JWT |
| GET | /api/auth/historial | Lista el historial de intentos de login |

## Swagger
http://localhost:8086/swagger-ui.html

## Ejecución local
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
Puerto: `8086`. Requiere que `ms-usuarios` (puerto 8083) esté corriendo.

## Ejecución con Docker
```bash
docker compose up ms-login ms-usuarios mysql
```

## Variables de entorno
| Variable | Default | Descripción |
|---|---|---|
| DB_HOST | localhost | Host de MySQL |
| DB_USERNAME | root | Usuario de la BD |
| DB_PASSWORD | (vacío) | Password de la BD |
| MS_USUARIOS_URL | http://localhost:8083 | URL del Feign Client a ms-usuarios |
| SPRING_PROFILES_ACTIVE | dev | Perfil activo |
