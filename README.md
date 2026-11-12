# CrediYa — Microservicio de Solicitudes (`crediya-solicitudes-ms`)

Microservicio **reactivo (Spring WebFlux + R2DBC)** construido con Clean Architecture (scaffold de Bancolombia, `reactive=true`).
Sigue la misma estructura del microservicio de autenticación ubicado en la raíz del repositorio.

## Estructura

| Módulo | Contenido |
|---|---|
| `domain/model` | Modelos `Solicitud`, `TipoPrestamo`, `Estado`, sus gateways, `TransactionalGateway` reactivo y excepciones de negocio |
| `domain/usecase` | `SolicitudUseCase`: valida, verifica el tipo de préstamo, asigna el estado inicial y guarda en una transacción reactiva |
| `infrastructure/driven-adapters/r2dbc-postgresql` | Entidades, repositorios reactivos, adaptadores, `TransactionalOperator` y scripts SQL (`db/schema.sql`, `db/data.sql`) |
| `infrastructure/entry-points/reactive-web` | `RouterRest` + `Handler` (functional endpoints), filtro de traza `X-Trace-Id` y manejador global de errores |
| `applications/app-service` | Arranque de la aplicación y configuración |

## Modelo de datos

Tablas `estados`, `tipo_prestamo` y `solicitud` según el diagrama de diseño. Se crean automáticamente al arrancar
(si no existen) y se cargan los estados (`Pendiente de revisión`, `Revisión manual`, `Aprobada`, `Rechazada`)
y los tipos de préstamo iniciales. La tabla `solicitud` guarda además `documento_identidad`, que pide la historia de usuario.

## Cómo ejecutarlo (PowerShell)

Usa el mismo contenedor de PostgreSQL del `docker-compose.yml` de la raíz.

```powershell
$env:DB_USERNAME="crediya"
$env:DB_PASSWORD="crediya"

docker compose up -d            # desde la raíz del repositorio
cd crediya-solicitudes-ms
.\gradlew.bat clean build
.\gradlew.bat :app-service:bootRun
```

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_HOST` | Host de PostgreSQL | `localhost` |
| `DB_PORT` | Puerto | `5432` |
| `DB_NAME` | Base de datos | `crediya_db` |
| `DB_SCHEMA` | Esquema | `public` |
| `DB_USERNAME` / `DB_PASSWORD` | Credenciales | *(obligatorias)* |

El servicio queda en `http://localhost:8081`.

## Endpoint

### `POST /api/v1/solicitud` — HU 2: Registrar una solicitud de préstamo

```json
{
  "monto": 5000000,
  "plazo": 24,
  "email": "juan@correo.com",
  "documentoIdentidad": "1020304050",
  "idTipoPrestamo": 1
}
```

Reglas:
- Todos los campos son obligatorios. `monto` > 0, `plazo` entre 1 y 360 meses, `email` con formato válido,
  `documentoIdentidad` de 5 a 20 caracteres alfanuméricos.
- `idTipoPrestamo` debe existir y el `monto` debe estar entre el mínimo y el máximo de ese tipo.
- La solicitud se registra siempre con el estado **"Pendiente de revisión"** (se ignora cualquier estado enviado).

| Código | Cuándo |
|---|---|
| 201 | Solicitud registrada (header `Location` con la URL del recurso) |
| 400 | Datos inválidos, cuerpo vacío o JSON mal formado |
| 415 | El `Content-Type` no es `application/json` |
| 422 | El tipo de préstamo no existe o el monto está fuera de su rango |
| 500 | Error inesperado (mensaje genérico, el detalle solo queda en el log) |

Todas las respuestas incluyen el header `X-Trace-Id` (si lo envías, se reutiliza) y los errores lo devuelven en el campo
`traceId`, para buscar la petición en los logs.
