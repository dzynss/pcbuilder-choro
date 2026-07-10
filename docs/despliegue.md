# Guía de despliegue remoto

Este proyecto no tiene despliegue remoto activo todavía. Este documento deja listos los
archivos de configuración y los pasos exactos para desplegar en **Railway** (recomendado)
o **Render**, sin que tengas que investigar la plataforma desde cero.

## Por qué Railway es la opción más simple para este proyecto

Railway ofrece un plugin de MySQL administrado con un clic, y permite crear un servicio
por carpeta del monorepo señalando el "Root Directory" — muy parecido a como ya está
organizado `docker-compose.yml`. Render, en cambio, no tiene MySQL administrado (solo
Postgres), así que ahí hay que traer tu propio MySQL externo.

## Variables de entorno comunes a los 11 servicios

Estas son las mismas que ya usa `docker-compose.yml`, solo cambia el host de la base de datos:

| Variable | Valor |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` |
| `DB_HOST` | host de tu MySQL remoto (Railway te lo da al crear el plugin) |
| `DB_USERNAME` | usuario de tu MySQL remoto |
| `DB_PASSWORD` | password de tu MySQL remoto |
| `JWT_SECRET` | el mismo valor en **los 11 servicios** — si no coincide, el JWT emitido por `ms_login` no va a validar en los demás |

Además, cada servicio que depende de otro necesita su `MS_*_URL` apuntando a la URL pública
(o interna, si la plataforma lo soporta) del servicio del que depende — ver la tabla de
dependencias en el [`README.md`](../README.md) raíz.

---

## Opción A — Railway

1. Crea un proyecto nuevo en Railway y conéctalo a este repositorio de GitHub.
2. Agrega el plugin **MySQL** ("+ New" → "Database" → "MySQL"). Railway te da `MYSQLHOST`,
   `MYSQLUSER`, `MYSQLPASSWORD` automáticamente — puedes referenciarlos en los demás
   servicios con `${{MySQL.MYSQLHOST}}` etc. usando las "Variable References" de Railway.
3. Por cada uno de los 11 microservicios, crea un servicio nuevo ("+ New" → "GitHub Repo",
   mismo repo) y en Settings → "Root Directory" apunta a la carpeta correspondiente
   (ej. `ms-usuarios`, `ms_login`, `ms-gateway`, etc.). Railway detecta el `Dockerfile` de
   cada carpeta automáticamente.
4. En cada servicio, agrega las variables de entorno de la tabla de arriba. Para las
   `MS_*_URL`, usa la URL pública que Railway asigna a cada servicio (Settings → Networking
   → "Generate Domain"), o el hostname interno si activas "Private Networking" (Railway
   soporta comunicación privada entre servicios del mismo proyecto vía `http://<nombre-servicio>.railway.internal:<puerto>`).
5. Solo expón públicamente (`Generate Domain`) el servicio `ms-gateway` — los otros 10
   pueden quedar sin dominio público, igual que en `docker-compose.yml`.
6. Verifica Swagger de cada servicio en su URL pública/interna: `https://<servicio>.up.railway.app/swagger-ui.html`.

No se requiere ningún archivo adicional para Railway más allá de este repo — el `Dockerfile`
de cada carpeta es suficiente. Si prefieres fijar configuración de build/deploy por servicio
(healthcheck, política de reinicio), puedes agregar un `railway.json` dentro de cada carpeta,
pero no es obligatorio.

---

## Opción B — Render (usando `render.yaml`)

Ya se agregó [`render.yaml`](../render.yaml) en la raíz del repo con los 11 servicios
configurados como Blueprint.

1. En Render, "New +" → "Blueprint" → conecta este repositorio. Render detecta `render.yaml`
   automáticamente y muestra los 11 servicios a crear (10 "private services" + el gateway
   como "web service" público).
2. Render no tiene MySQL administrado. `render.yaml` incluye un servicio `mysql` corriendo
   la imagen `mysql:8.0` con un disco persistente de 1GB — **esto requiere un plan pago**
   (los discos no están disponibles en el free tier). Si quieres quedarte en el free tier,
   borra ese servicio de `render.yaml` y usa un MySQL externo gratuito (ej. Aiven, Railway,
   PlanetScale con el adaptador MySQL), reemplazando `DB_HOST`/`DB_USERNAME`/`DB_PASSWORD`
   en los 10 servicios de negocio por los datos de esa base externa.
3. Antes de desplegar, cambia el valor de `JWT_SECRET` en el archivo (o en el dashboard,
   una vez creados los servicios) por un secreto propio — hoy usa el mismo valor por
   defecto que el proyecto usa en local, lo cual está bien para pruebas pero no para producción.
4. Aplica el Blueprint. Render construirá las 11 imágenes Docker (usa `dockerfilePath`/`dockerContext`
   ya apuntados a cada carpeta) y las desplegará.
5. Verifica Swagger del gateway y, si necesitas probar un microservicio interno
   directamente, usa el "Shell" de Render o expón temporalmente ese servicio como `web`.

---

## Después de desplegar (cualquiera de las dos opciones)

- Prueba el flujo completo: `POST /api/auth/login` (a través del gateway) para obtener un
  JWT, y úsalo con el botón "Authorize" en el Swagger de cualquier otro microservicio para
  confirmar que la validación end-to-end funciona igual que en local.
- Actualiza el [`README.md`](../README.md) raíz con la URL pública real del gateway una vez
  que la tengas.
