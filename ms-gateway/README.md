# ms-gateway

## Contexto
API Gateway centralizado del sistema **PC Builder**, construido con Spring Cloud Gateway. Es el único punto de entrada hacia los 10 microservicios.

## Integrantes
- Martín Narváez — implementación de ms-usuarios, ms-componentes, ms-resenas, ms_cotizaciones, ms_login
- Martin — implementación de ms-inventario, ms-ofertas, ms-soporte, ms_despachos, ms_notificaciones

## Responsabilidad
Enrutar cada petición entrante hacia el microservicio correspondiente según el path, sin lógica de negocio propia.

## Rutas configuradas
| Path | Microservicio destino |
|---|---|
| /api/usuarios/** | ms-usuarios (:8083) |
| /api/resenas/** | ms-resenas (:8084) |
| /api/componentes/** | ms-componentes (:8085) |
| /api/auth/** | ms_login (:8086) |
| /api/cotizaciones/** | ms_cotizaciones (:8087) |
| /api/inventario/** | ms-inventario (:9090) |
| /api/ofertas/** | ms-ofertas (:9091) |
| /api/soporte/** | ms-soporte (:9092) |
| /api/despachos/** | ms_despachos (:9093) |
| /api/notificaciones/** | ms_notificaciones (:9094) |

## Ejecución local
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
Puerto: `9099`. Requiere que los 10 microservicios estén corriendo en sus puertos por defecto.

## Ejecución con Docker
```bash
docker compose up
```
Levanta MySQL, los 10 microservicios y el Gateway juntos.

## Variables de entorno
Cada `MS_<NOMBRE>_URL` permite sobreescribir la URL de destino de una ruta (útil en Docker, donde los hosts no son `localhost`). Ver `application-dev.yml` para el listado completo y sus valores por defecto.
