# CrediYa — Microservicio de Solicitudes (`crediya-solicitudes-ms`)

Microservicio **imperativo (Spring MVC + JPA)** construido con Clean Architecture (scaffold de Bancolombia, `reactive=false`).
Es un repositorio independiente: tiene su propio build Gradle, su propia base de datos y su propio `docker-compose.yml`.
Sigue la misma estructura y convenciones que el microservicio de autenticación
([`crediya-autenticacion-ms`](https://github.com/juanmejia017/crediya-autenticacion-ms)).

## Estructura

| Módulo | Contenido |
|---|---|
| `domain/model` | Modelos `Solicitud`, `TipoPrestamo`, `Estado`, sus gateways y excepciones de negocio |
| `domain/usecase` | `SolicitudUseCase`: valida, verifica el tipo de préstamo, asigna el estado inicial y guarda la solicitud |
| `infrastructure/driven-adapters/jpa-repository` | Entidades JPA, repositorios, adaptadores (con `@Transactional`) y carga de datos iniciales |
| `infrastructure/entry-points/api-rest` | `ApiRest` (`@RestController`), filtro de traza `X-Trace-Id` y `GlobalExceptionHandler` |
| `applications/app-service` | Arranque de la aplicación y configuración |

## Modelo de datos

Tablas `estados`, `tipo_prestamo` y `solicitud` según el diagrama de diseño. Hibernate las crea al arrancar
(si no existen) y se cargan los estados (`Pendiente de revisión`, `Revisión manual`, `Aprobada`, `Rechazada`)
y los tipos de préstamo iniciales. La tabla `solicitud` guarda además `documento_identidad`, que pide la historia de usuario.

## Cómo ejecutarlo (PowerShell)

```powershell
cd C:\Users\juanm\crediya-solicitudes-ms
$env:DB_USERNAME="crediya"
$env:DB_PASSWORD="crediya"
$env:JWT_SECRET="cambia-este-secreto-por-uno-largo-de-32-o-mas"   # el MISMO de crediya-autenticacion-ms

docker compose up -d
.\gradlew.bat clean build
.\gradlew.bat :app-service:bootRun
```

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5433/crediya_solicitudes_db` |
| `DB_USERNAME` / `DB_PASSWORD` | Credenciales | *(obligatorias)* |
| `JWT_SECRET` | Secreto para validar los tokens (mínimo 32 caracteres). **Debe ser el mismo de `crediya-autenticacion-ms`** | *(obligatoria)* |

La base de datos de este microservicio corre en el contenedor `crediya-solicitudes-postgres`, puerto **5433**,
para no chocar con la de autenticación (5432).

El servicio queda en `http://localhost:8081`.

## Endpoint

### `POST /api/v1/solicitud` — HU 2: Registrar una solicitud de préstamo (protegido desde la HU 3)

Requiere el header `Authorization: Bearer <token>` obtenido en `POST /api/v1/login` de
`crediya-autenticacion-ms` (puerto 8080). Solo un usuario con rol **CLIENTE** puede crear solicitudes, y solo a su
nombre: el `email` (y el `documentoIdentidad`, si el cliente lo tiene registrado) deben ser los del usuario del token.

Este microservicio no genera tokens: solo verifica la firma con el mismo `JWT_SECRET`, el emisor, el vencimiento y el rol.

```
ApiRest.registrarSolicitudPrestamo
        → ValidadorPeticion.validarPeticionAutenticada (saca el token del header Bearer,
          AutenticacionUseCase.validarToken, cuerpo no vacío)
        → SolicitudUseCase.registrarSolicitudPrestamo (es cliente → validar → normalizar → es su propia solicitud
          → tipo → monto → estado → guardar)
        → SolicitudRepositoryAdapter (INSERT, SolicitudEntityMapper) → ApiRest (201)
```

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

Organización del código:
- **ApiRest:** solo recibe la petición HTTP, la delega y arma la respuesta (sin logs ni validaciones).
- **ValidadorPeticion** (`api/validacion`): lee el header `Authorization: Bearer`, hace validar el token, registra quién
  hizo la petición y revisa que el cuerpo no venga vacío.
- **SolicitudUseCase:** solo orquesta; las reglas viven en el modelo (`SolicitudValidator`, `Solicitud.perteneceA`,
  `TipoPrestamo.validarMonto`).
- **Adapters:** solo persisten o consultan; la conversión dominio ↔ entidad está en `jpa/mapper`
  (`SolicitudEntityMapper`, `TipoPrestamoEntityMapper`, `EstadoEntityMapper`).
- **Nombres de métodos en español.** Se mantienen en inglés solo los que exige el framework: los métodos derivados de
  Spring Data (`findBy…`, `existsBy…`, `save`, `findById`), los `@Override` de Spring (`run`, `doFilter`) y los beans
  `entityManagerFactory` y `transactionManager`.

| Código | Cuándo |
|---|---|
| 201 | Solicitud registrada (header `Location` con la URL del recurso) |
| 400 | Datos inválidos, cuerpo vacío o JSON mal formado |
| 401 | Sin token, token inválido o vencido (`NO_AUTENTICADO`) |
| 403 | El usuario no es cliente, o el email/documento no son los suyos (`ACCESO_DENEGADO`) |
| 415 | El `Content-Type` no es `application/json` |
| 422 | El tipo de préstamo no existe o el monto está fuera de su rango |
| 500 | Error inesperado (mensaje genérico, el detalle solo queda en el log) |

Todas las respuestas incluyen el header `X-Trace-Id` (si lo envías, se reutiliza) y los errores lo devuelven en el campo
`traceId`. Ese mismo id aparece al inicio de cada línea de log de la petición.
