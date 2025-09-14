# CrediYa - Microservicio de Solicitudes

## Descripción

Microservicio encargado de la gestión completa del ciclo de vida de solicitudes de crédito en el sistema CrediYa. Implementado con Spring Boot 3.5.4 y Spring WebFlux para un enfoque reactivo y no bloqueante.

## Arquitectura

- **Patrón**: Arquitectura Hexagonal (Puertos y Adaptadores)
- **Framework**: Spring Boot 3.5.4 con WebFlux
- **Base de Datos**: PostgreSQL con R2DBC (Reactivo)
- **Documentación**: OpenAPI/Swagger 3.0
- **Autenticación**: JWT con OAuth2 Resource Server
- **Comunicación**: WebClient para llamadas inter-servicio

## Características Principales

- ✅ Gestión completa de solicitudes de crédito
- ✅ Arquitectura hexagonal con separación clara de responsabilidades
- ✅ Programación reactiva con Spring WebFlux
- ✅ Documentación automática con OpenAPI/Swagger
- ✅ Autenticación y autorización basada en roles
- ✅ Comunicación inter-servicio con el microservicio de autenticación
- ✅ Logging detallado para monitoreo y debugging
- ✅ Manejo robusto de errores con códigos específicos
- ✅ Tests unitarios completos

## Endpoints de la API

### 1. Registrar Nueva Solicitud de Crédito
```http
POST /api/v1/solicitud
```

**Descripción**: Crea una nueva solicitud de crédito en el sistema.

**Roles permitidos**: `CLIENTE`, `ADMIN`

**Headers requeridos**:
```
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

**Body**:
```json
{
  "monto": 5000000.00,
  "plazo": 24,
  "email": "cliente@email.com",
  "tipoPrestamo": "PERSONAL",
  "documentoIdentidad": "1234567890"
}
```

**Respuestas**:
- `201 Created`: Solicitud creada exitosamente
- `400 Bad Request`: Datos inválidos
- `401 Unauthorized`: Token inválido o expirado
- `403 Forbidden`: Usuario sin permisos
- `422 Unprocessable Entity`: Error de validación de negocio

### 2. Listar Solicitudes Pendientes
```http
GET /api/v1/solicitud/pendientes?page=0&size=10
```

**Descripción**: Obtiene una lista paginada de solicitudes pendientes de revisión.

**Roles permitidos**: `CLIENTE`

**Parámetros de consulta**:
- `page` (opcional): Número de página (0-based, default: 0)
- `size` (opcional): Tamaño de página (default: 10)

**Respuestas**:
- `200 OK`: Lista obtenida exitosamente
- `400 Bad Request`: Parámetros inválidos
- `401 Unauthorized`: Token inválido

### 3. Listar Solicitudes para Revisión (Asesor)
```http
GET /api/v1/solicitud?page=0&size=10
```

**Descripción**: Obtiene una lista paginada de solicitudes que requieren revisión manual por parte del asesor. Solo muestra solicitudes en estados: `EN_REVISION`, `RECHAZADA`, `REVISION_MANUAL`.

**Roles permitidos**: `ASESOR`

**Parámetros de consulta**:
- `page` (opcional): Número de página (0-based, default: 0)
- `size` (opcional): Tamaño de página (default: 10)

**Respuesta exitosa (200 OK)**:
```json
{
  "items": [
    {
      "monto": 5000000.00,
      "plazo": 24,
      "email": "cliente@email.com",
      "nombre": "Juan Pérez",
      "tipoPrestamo": "Crédito Personal",
      "tasaInteres": 0.025,
      "estadoSolicitud": "EN_REVISION",
      "salarioBase": 3000000,
      "montoMensualSolicitud": 250000.00
    }
  ],
  "page": 0,
  "size": 10,
  "total": 1
}
```

**Respuestas**:
- `200 OK`: Lista obtenida exitosamente
- `400 Bad Request`: Parámetros inválidos
- `401 Unauthorized`: Token inválido
- `403 Forbidden`: Usuario sin rol ASESOR

## Seguridad y Autenticación

### Configuración JWT
- **Issuer**: `crediya-auth`
- **Algoritmo**: HS256
- **Claims requeridos**: `role`, `sub` (email)

### Roles del Sistema
- `CLIENTE`: Puede crear solicitudes y ver sus propias solicitudes pendientes
- `ASESOR`: Puede revisar todas las solicitudes que requieren atención manual
- `ADMIN`: Tiene acceso completo a todas las funcionalidades

### Validaciones de Seguridad
- Todas las rutas requieren autenticación JWT válida
- Control de acceso basado en roles usando `@PreAuthorize`
- Validación de que los usuarios solo puedan acceder a sus propios recursos (cuando aplique)

## Comunicación Inter-Servicio

### Servicio de Autenticación
El microservicio se comunica con `CrediYaMsAutenticacion` para:

1. **Enriquecimiento de datos**: Obtener nombre y salario base del usuario
2. **Validación de usuarios**: Verificar existencia de usuarios
3. **Endpoint**: `GET /api/v1/usuarios/{email}`

### Configuración WebClient
```yaml
autenticaciones:
  service:
    url: http://localhost:8081
```

## Manejo de Errores

### Códigos de Error Específicos
- `TIPO_PRESTAMO_NO_ENCONTRADO`: Tipo de préstamo inválido
- `DOCUMENTO_INVALIDO`: Documento de identidad inválido
- `EMAIL_INVALIDO`: Formato de email inválido
- `PLAZO_INVALIDO`: Plazo fuera de rango permitido
- `PARAMETROS_INVALIDOS`: Parámetros de consulta inválidos
- `ERROR_INTERNO`: Error interno del servidor

### Estructura de Respuesta de Error
```json
{
  "code": "ERROR_CODE",
  "message": "Descripción detallada del error"
}
```

## Logging y Monitoreo

### Niveles de Log
- `INFO`: Operaciones exitosas y flujos normales
- `WARN`: Situaciones que requieren atención
- `ERROR`: Errores que impiden el funcionamiento normal

### Eventos Importantes
- Creación de nuevas solicitudes
- Consultas de listados
- Errores de comunicación inter-servicio
- Validaciones fallidas
- Acceso no autorizado

## Tests

### Cobertura de Tests
- ✅ Tests unitarios para `SolicitudHandler`
- ✅ Tests de integración para endpoints
- ✅ Tests de seguridad y autenticación
- ✅ Tests de comunicación inter-servicio
- ✅ Tests de manejo de errores

### Ejecución de Tests
```bash
./gradlew test
```

## Configuración

### Variables de Entorno Requeridas
```bash
# JWT Configuration
APP_JWT_SECRET=your-jwt-secret-key
APP_JWT_ISSUER=crediya-auth

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=crediya_solicitudes
DB_USERNAME=crediya_user
DB_PASSWORD=crediya_password

# Authentication Service
AUTENTICACIONES_SERVICE_URL=http://localhost:8081
```

### Perfiles de Spring
- `dev`: Desarrollo local
- `test`: Tests automatizados
- `prod`: Producción

## Documentación de la API

### Swagger UI
Una vez ejecutado el servicio, la documentación completa está disponible en:
```
http://localhost:{port}/swagger-ui.html
```

### OpenAPI Specification
La especificación OpenAPI se genera automáticamente y está disponible en:
```
http://localhost:{port}/v3/api-docs
```

## Dependencias Principales

- `spring-boot-starter-webflux`: Framework web reactivo
- `spring-boot-starter-security`: Seguridad y autenticación
- `spring-boot-starter-oauth2-resource-server`: Servidor de recursos OAuth2
- `springdoc-openapi-starter-webflux-ui`: Documentación OpenAPI
- `spring-boot-starter-data-r2dbc`: Base de datos reactiva
- `r2dbc-postgresql`: Driver PostgreSQL reactivo

## Desarrollo

### Prerrequisitos
- Java 17+
- Gradle 7.6+
- PostgreSQL 13+
- Docker (opcional)

### Ejecución Local
```bash
./gradlew bootRun
```

### Construcción
```bash
./gradlew build
```

## Contribución

1. Crear rama feature desde `develop`
2. Implementar cambios siguiendo los principios de arquitectura hexagonal
3. Agregar tests unitarios y de integración
4. Actualizar documentación si es necesario
5. Crear Pull Request con descripción detallada

## Soporte

Para soporte técnico o preguntas:
- **Email**: desarrollo@crediya.com
- **Slack**: #crediya-solicitudes
- **Wiki**: [Documentación Interna](https://wiki.crediya.com/solicitudes)

---

**CrediYa Development Team** © 2024
