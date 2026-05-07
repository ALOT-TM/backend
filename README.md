# FluxusBackend

Backend DDD con bounded contexts para gestionar merma, donaciones, beneficiarios, identidad y compañías.

## Proposito del proyecto

Este backend implementa un flujo realista de gestion de merma y donaciones con trazabilidad,
separando responsabilidades por bounded context y aplicando patrones DDD y CQRS ligero.

## Requisitos y entorno

- Java 25 (segun `pom.xml`)
- MySQL 8.x
- Maven o Maven Wrapper (`mvnw.cmd`)

## Estructura del proyecto

```
src/main/java/com/fluxusbackend/fluxusbackend/
  shared/
    domain/model/aggregates/AuditableAggregateRoot.java
    domain/model/valueobjects/CompanyId.java
  companymanagement/
    domain/model/aggregates/Company.java
  mermamanagement/
    domain/model/{aggregates,valueobjects,commands,queries,events,enums}
    domain/services
    application/internal/{commandservices,queryservices}
    application/acl
    infrastructure/persistence/jpa/repositories
    interfaces/{rest/transform,acl}
  donationsmanagement/
    domain/model/{aggregates,valueobjects,commands,queries,events,enums}
    domain/services
    application/internal/{commandservices,queryservices,outboundservices/acl}
    infrastructure/persistence/jpa/repositories
    interfaces/rest/transform
  beneficiariesmanagement/
    domain/model/{aggregates,valueobjects,commands,queries,events,enums}
    domain/services
    application/internal/{commandservices,queryservices}
    application/acl
    infrastructure/persistence/jpa/repositories
    interfaces/{rest/transform,acl}
  identityaccessmanagement/
    domain/model/{aggregates,valueobjects,commands,queries,enums}
    domain/services
    application/internal/{commandservices,queryservices}
    infrastructure/persistence/jpa/repositories
    interfaces/rest/transform
  config/SecurityConfig.java
  shared/application/security/{JwtTokenService,JwtAuthenticationFilter,CurrentUserProvider,AuthenticatedUserPrincipal,AclService}.java
```

## Resumen general

El sistema cubre dos perspectivas:
- Usuario Retail (encargado/admin): registra merma, clasifica donable/no donable, crea donaciones y gestiona beneficiarios.
- Beneficiario (colegios/albergues/ONG): ve donaciones asignadas y confirma recepcion.

Flujo principal:
1) Registrar merma.
2) Clasificar merma como donable.
3) Crear donacion y asignar beneficiario.
4) Marcar entrega.
5) Beneficiario confirma recepcion.

## Estado actual del código

Este repositorio está en un estado funcional y compilable. Cambios principales implementados y activos en el códigobase:

- **Autenticación JWT**: `JwtTokenService` y `JwtAuthenticationFilter` con HMAC-SHA256. Login emite JWT con `userId`, `email`, `companyId` y `role`.
- **Swagger UI con JWT**: `OpenApiConfig` configura autenticación Bearer en Swagger (candado 🔒 en endpoints protegidos).
- **Sin formulario de login**: `SecurityConfig` deshabilita form login y HTTP Basic. Solo API REST.
- **Seguridad stateless**: `SessionCreationPolicy.STATELESS` + JWT validation en cada request.
- **Multi-tenancy**: `CompanyId` embebido en todas las entidades. ACL filtra datos por `companyId`.
- **MySQL persistencia**: Base de datos `fluxus` con auto-creación de esquema. DDL mode `update`.
- `AuthenticatedUserPrincipal` y `CurrentUserProvider` exponen usuario desde `SecurityContext`.
- `Company` aggregate para representar empresas retail.
- Endpoint `GET /api/iam/profile` expone perfil del usuario autenticado.
- **Company endpoints públicos** (sin JWT):
  - `POST /api/companies` - Crear compañía
  - `GET /api/companies` - Listar compañías
  - `GET /api/companies/{companyId}` - Obtener compañía por id
- **Nuevos endpoints**:
  - `GET /api/donations/statistics` - Estadísticas de donaciones por beneficiario (manager only)
  - `GET /api/requests/company` - Listado de requests de la compañía (manager only)
  - `GET /api/requests/product/{productName}` - Filtrar requests por nombre de producto (manager only)
- El proyecto compila correctamente con `mvn -DskipTests package`.

Estos cambios soportan multi-tenancy segura, autenticación JWT, y documentación interactiva sin exposición de un login HTML.

## Arquitectura y capas

Capas por contexto:
- Domain: modelo, reglas, VOs, comandos, queries, eventos, servicios (interfaces).
- Application: implementacion de servicios, coordinacion de casos de uso.
- Infrastructure: persistencia JPA (repositorios).
- Interfaces: REST controllers y ACL (facades/external services).

Patrones aplicados:
- DDD (Aggregate Root, Value Objects, Domain Services)
- CQRS ligero (CommandService y QueryService separados)
- ACL (Anti-Corruption Layer) entre contextos
- JPA/Hibernate para persistencia
- Swagger/OpenAPI para documentacion

## Bounded Contexts (responsabilidades y limites)

### Merma Management
Responsabilidad: registrar y clasificar productos en merma (estado, motivo, vencimiento).

- Aggregate Root: `Merma`
- Value Objects: `MermaId`, `ProductName`, `CategoryName`, `Quantity`, `ExpirationDate`
- Enums: `MermaStatus`, `MermaReason`
- Commands: `RegisterMermaCommand`, `MarkMermaDonableCommand`, `MarkMermaNotDonableCommand`, `MarkMermaDonatedCommand`
- Queries: `GetMermaByIdQuery`, `ListMermasByStatusQuery`
- Eventos: `MermaRegisteredEvent`
- Repositorio: `MermaRepository`
- Servicios: `MermaCommandService`, `MermaQueryService`
- ACL (provider): `MermaContextFacade` (expuesto a Donations)

Reglas clave:
- Una merma se registra como `REGISTERED`.
- Solo una merma `REGISTERED` puede pasar a `DONABLE` o `NOT_DONABLE`.
- Solo una merma `DONABLE` puede pasar a `DONATED`.

### Donations Management
Responsabilidad: coordinar donaciones a partir de mermas donables y beneficiarios activos.

- Aggregate Root: `Donation`
- Value Objects: `DonationId`, `MermaReferenceId`, `BeneficiaryReferenceId`, `DonationQuantity`,
  `ScheduledDeliveryDate`, `DeliveryDate`, `ReceptionDate`
- Enum: `DonationStatus`
- Commands: `CreateDonationCommand`, `MarkDonationDeliveredCommand`, `ConfirmDonationReceptionCommand`
- Queries: `GetDonationByIdQuery`, `ListDonationsByStatusQuery`, `ListDonationsByBeneficiaryQuery`
- Eventos: `DonationAssignedEvent`, `DonationConfirmedEvent`
- Repositorio: `DonationRepository`
- Servicios: `DonationCommandService`, `DonationQueryService`
- ACL (consumer): `ExternalMermaService`, `ExternalBeneficiaryService`

Reglas clave:
- La donacion inicia en `ASSIGNED`.
- Solo una donacion `ASSIGNED` puede pasar a `DELIVERED`.
- Solo una donacion `DELIVERED` puede pasar a `CONFIRMED`.
- Al confirmar recepcion se marca la merma como `DONATED` via ACL.

### Donation Requests (within Donations Management)
Responsabilidad: permitir que los beneficiarios reclamen mermas donables y notificar al manager de retail.

- Aggregate Root: `DonationRequest`
- Value Objects: `DonationRequestId`, `MermaReferenceId`, `BeneficiaryReferenceId`
- Enum: `DonationRequestStatus`
- Commands: `CreateDonationRequestCommand`, `AcceptDonationRequestCommand`, `RejectDonationRequestCommand`, `CancelDonationRequestCommand`
- Queries: `GetDonationRequestByIdQuery`, `ListDonationRequestsByBeneficiaryQuery`, `ListDonationRequestsByMermaQuery`
- Repository: `DonationRequestRepository`
- Services: `DonationRequestCommandService`, `DonationRequestQueryService`
- Endpoint: `/api/requests`

Reglas:
- Una solicitud comienza en `PENDING`.
- Un manager puede `ACCEPT` o `REJECT` una solicitud.
- Un beneficiario puede `CANCEL` una solicitud si está en `PENDING` o `ACCEPTED`.
- Una solicitud se marca como `COMPLETED` una vez alcanza ese estado luego de ser aceptada.
- Una merma donable puede tener múltiples solicitudes de diferentes beneficiarios.

### Beneficiaries Management
Responsabilidad: administrar instituciones beneficiarias y su disponibilidad.

- Aggregate Root: `Beneficiary`
- Value Objects: `BeneficiaryId`, `BeneficiaryName`, `Address`, `AcceptedProduct`
- Enums: `BeneficiaryType`, `BeneficiaryStatus`
- Commands: `RegisterBeneficiaryCommand`, `UpdateBeneficiaryInfoCommand`,
  `ActivateBeneficiaryCommand`, `DeactivateBeneficiaryCommand`
- Queries: `GetBeneficiaryByIdQuery`, `ListBeneficiariesByStatusQuery`
- Evento: `BeneficiaryRegisteredEvent`
- Repositorio: `BeneficiaryRepository`
- Servicios: `BeneficiaryCommandService`, `BeneficiaryQueryService`
- ACL (provider): `BeneficiariesContextFacade` (expuesto a Donations)

Reglas clave:
- Un beneficiario se registra como `ACTIVE`.
- Puede ser activado o desactivado segun gestion administrativa.

### Identity & Access Management (IAM)
Responsabilidad: registro y autenticacion de usuarios internos y beneficiarios.

- Aggregate Root: `UserAccount`
- Value Objects: `UserId`, `EmailAddress`, `PasswordHash`, `CompanyId`
- Enums: `UserRole`, `UserStatus`
- Commands: `RegisterUserCommand`
- Queries: `GetUserByIdQuery`, `GetUserByEmailQuery`, `LoginUserQuery`
- Repositorio: `UserAccountRepository`
- Servicios: `UserCommandService`, `UserQueryService`, `UserAuthenticationQueryService`

Reglas clave:
- Registro requiere email valido y password >= 6.
- Si `role=MANAGER`, `companyId` es obligatorio.
- Si `role=BENEFICIARY`, `companyId` debe ser `null` (o campo omitido).
- Login valida credenciales (hash con BCrypt) y emite JWT con `userId`, `email`, `companyId` y `role`.

### Company Management
Responsabilidad: agrupar la informacion operativa por empresa/cliente retail.

- Aggregate Root: `Company`
- Value Objects: `CompanyId`
- Atributos principales: `name`, `headquarters`

Reglas clave:
- Cada usuario interno queda asociado a una `companyId`.
- La informacion operativa de merma y donacion queda aislada por `companyId` para usuarios retail.

## Integraciones entre contextos (ACL)

- Donations consume Merma y Beneficiaries via ACL (facades) para evitar acoplamiento.
- Merma y Beneficiaries exponen facades con operaciones minimas (buscar id, marcar donada).
- Las operaciones de merma y donacion quedan restringidas por `companyId` para usuarios retail.
- Los usuarios beneficiarios siguen accediendo a la logica de donaciones segun su flujo propio y no usan el mismo ACL de pertenencia por empresa para merma publicada.

## Modelo de datos (tablas principales)

- `mermas`
  - id, product_name, category_name, quantity, expiration_date, reason, status, company_id, created_at, updated_at
- `donations`
  - id, merma_id, beneficiary_id, donation_quantity, scheduled_delivery_date,
    delivery_date, reception_date, reception_comment, status, company_id, created_at, updated_at
- `donation_requests`
  - id, merma_id, beneficiary_id, status, notes, company_id, created_at, updated_at
- `beneficiaries`
  - id, beneficiary_name, type, address, status, company_id, created_at, updated_at
- `beneficiary_accepted_products`
  - beneficiary_id, accepted_product
- `companies`
  - id, name, headquarters, created_at, updated_at
- `user_accounts`
  - id, email, password_hash, company_id, role, status, created_at, updated_at

Relaciones principales:
- Donation referencia Merma (merma_id) y Beneficiary (beneficiary_id) como VOs embebidos.
- DonationRequest references Merma (merma_id) and Beneficiary (beneficiary_id) as embedded value objects.
- Beneficiary tiene coleccion de productos aceptados.
- Todas las entidades tienen company_id para multi-tenancy.

## Base URL y documentacion

Base (por defecto): `http://localhost:8080`
- `GET /api-docs` OpenAPI JSON
- `GET /swagger-ui.html` Swagger UI (sin formulario de login, autenticación por JWT token)

## Configuración y Setup

### Base de datos - MySQL

La aplicación usa **MySQL 8.x** para persistencia. Configuración en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fluxus?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=@Mysequelroot1
```

- **URL**: `localhost:3306` (cambiar si MySQL está en otro host/puerto)
- **BD**: `fluxus` (se crea automáticamente si no existe)
- **Usuario/Contraseña**: cambiar según tu configuración de MySQL
- **DDL**: `ddl-auto=update` (crea/actualiza tablas automáticamente)

**Requisitos previos:**
1. MySQL debe estar corriendo en la máquina
2. El usuario y contraseña deben ser válidos
3. La BD `fluxus` se creará automáticamente al iniciar la app

### Ejecución local

```powershell
# 1. Compilar
mvn clean package -DskipTests

# 2. Ejecutar desde VS Code
# Abrir FluxusBackendApplication.java y usar "Run" (F5)

# O ejecutar manualmente
java -jar target/FluxusBackend-0.0.1-SNAPSHOT.jar
```

La app escuchará en `http://localhost:8080`

### Autenticación y Swagger UI

La app **no tiene formulario de login** en la web. La autenticación es **JWT basada en API**:

1. **Registrar usuario** (sin auth requerida):
   ```bash
   curl -X POST http://localhost:8080/api/iam/register \
     -H "Content-Type: application/json" \
     -d '{
       "email": "user@test.com",
       "rawPassword": "pass123",
       "role": "MANAGER",
       "companyId": 1
     }'
   ```

   Beneficiario (sin compañia):
   ```bash
   curl -X POST http://localhost:8080/api/iam/register \
     -H "Content-Type: application/json" \
     -d '{
       "email": "beneficiary@test.com",
       "rawPassword": "pass123",
       "role": "BENEFICIARY",
       "companyId": null
     }'
   ```

2. **Hacer login** (obtener JWT token):
   ```bash
   curl -X POST http://localhost:8080/api/iam/login \
     -H "Content-Type: application/json" \
     -d '{"email": "user@test.com", "rawPassword": "pass123"}'
   ```
   
   Respuesta:
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   }
   ```

3. **Usar Swagger UI** (`http://localhost:8080/swagger-ui.html`):
   - Click en botón **Authorize** (candado)
   - Pega el token JWT completo (sin "Bearer")
   - Ya puedes usar los endpoints protegidos

4. **Usar en requests manuales**:
   ```bash
   curl -H "Authorization: Bearer <token>" \
        http://localhost:8080/api/iam/profile
   ```

### Seguridad

- **Login form deshabilitado**: `@Configuration(proxyBeanMethods=false)` + `.formLogin(form -> form.disable())`
- **HTTP Basic deshabilitado**: `.httpBasic(basic -> basic.disable())`
- **JWT required**: todos los endpoints excepto `/api/iam/register`, `/api/iam/login`, `/api/companies/**` y Swagger docs
- **Session stateless**: `SessionCreationPolicy.STATELESS`
- **CORS enabled**: permite `http://localhost:5173` (configurado en `WebConfig`)

## Estados y enums

MermaStatus: `REGISTERED`, `DONABLE`, `DONATED`, `NOT_DONABLE`
MermaReason: `EXPIRATION`, `DAMAGED_PACKAGING`, `OVERSTOCK`
BeneficiaryStatus: `ACTIVE`, `INACTIVE`
BeneficiaryType: `SCHOOL`, `SHELTER`, `NGO`
DonationStatus: `ASSIGNED`, `DELIVERED`, `CONFIRMED`
DonationRequestStatus: `PENDING`, `ACCEPTED`, `REJECTED`, `CANCELLED`, `COMPLETED`
UserRole: `MANAGER` (retail), `BENEFICIARY`
UserStatus: `ACTIVE`, `INACTIVE`

## Endpoints (mapa rapido)

Merma:
- `POST /api/mermas/register`
- `PATCH /api/mermas/{mermaId}/donable`
- `PATCH /api/mermas/{mermaId}/not-donable`
- `PATCH /api/mermas/{mermaId}/donated`
- `GET /api/mermas/{mermaId}`
- `GET /api/mermas?status=REGISTERED|DONABLE|DONATED|NOT_DONABLE`

Beneficiarios:
- `POST /api/beneficiaries/register`
- `PUT /api/beneficiaries/{beneficiaryId}`
- `PATCH /api/beneficiaries/{beneficiaryId}/activate`
- `PATCH /api/beneficiaries/{beneficiaryId}/deactivate`
- `GET /api/beneficiaries/{beneficiaryId}`
- `GET /api/beneficiaries?status=ACTIVE|INACTIVE`

Donaciones:
- `POST /api/donations/create`
- `PATCH /api/donations/{donationId}/delivered`
- `PATCH /api/donations/{donationId}/confirm`
- `GET /api/donations/{donationId}`
- `GET /api/donations?status=ASSIGNED|DELIVERED|CONFIRMED`
- `GET /api/donations/by-beneficiary/{beneficiaryId}`
- `GET /api/donations/statistics` (devuelve cantidad de donaciones por beneficiario - manager only)

Donation Requests:
- `POST /api/requests` (beneficiary creates a request for a donable merma)
- `GET /api/requests/{requestId}` (gets a request by id)
- `GET /api/requests?beneficiaryId=X` (lists a beneficiary's requests)
- `GET /api/requests/merma/{mermaId}` (lists requests for a merma - manager only)
- `PATCH /api/requests/{requestId}/accept` (manager accepts a request)
- `PATCH /api/requests/{requestId}/reject` (manager rejects a request)
- `PATCH /api/requests/{requestId}/cancel` (beneficiary cancels a request)
- `GET /api/requests/company` (lists requests for the manager's company - manager only)
- `GET /api/requests/product/{productName}` (lists requests by merma product name - manager only)

Companies (public, no JWT):
- `POST /api/companies`
- `GET /api/companies`
- `GET /api/companies/{companyId}`

IAM:
- `POST /api/iam/register`
- `POST /api/iam/login`
- `GET /api/iam/profile` (devuelve `companyId`, `email`, `role` del usuario autenticado)

Seguridad:
- Las rutas protegidas requieren `Authorization: Bearer <jwt>`.
- El JWT contiene `userId`, `email`, `companyId` y `role`.
- Las rutas `/api/companies/**` son publicas y no requieren token.

## Ejemplos rapidos por contexto

Merma (registrar y marcar donable):
```http
POST /api/mermas/register
Content-Type: application/json

{
  "productName": "Yogurt Natural",
  "categoryName": "Lacteos",
  "quantity": 12,
  "expirationDate": "2026-05-10",
  "reason": "EXPIRATION"
}
```
```http
PATCH /api/mermas/1/donable
```

Beneficiarios (registrar y desactivar):
```http
POST /api/beneficiaries/register
Content-Type: application/json

{
  "name": "Colegio San Juan",
  "type": "SCHOOL",
  "address": "Av. Principal 123",
  "acceptedProducts": ["Lacteos", "Conservas"]
}
```
```http
PATCH /api/beneficiaries/1/deactivate
```

Donaciones (crear y confirmar):
```http
POST /api/donations/create
Content-Type: application/json

{
  "mermaReferenceId": 1,
  "beneficiaryReferenceId": 1,
  "quantity": 5,
  "scheduledDeliveryDate": "2026-05-05"
}
```
```http
PATCH /api/donations/1/confirm
Content-Type: application/json

{
  "receptionDate": "2026-05-06",
  "comment": "Recepcion completa"
}
```

Donation Requests (beneficiary claims a donable merma, manager accepts):
```http
POST /api/requests
Content-Type: application/json
Authorization: Bearer <token>

{
  "mermaId": 1,
  "beneficiaryId": 1,
  "notes": "Necesitamos urgente para los niños"
}
```
Response:
```json
{
  "id": 1,
  "mermaReferenceId": { "value": 1 },
  "beneficiaryReferenceId": { "value": 1 },
  "status": "PENDING",
  "notes": "Necesitamos urgente para los niños",
  "companyId": { "value": 1 }
}
```

Manager accepts the request:
```http
PATCH /api/requests/1/accept
Authorization: Bearer <token>
```

Manager rejects the request:
```http
PATCH /api/requests/1/reject
Authorization: Bearer <token>
```

Beneficiary cancels the request:
```http
PATCH /api/requests/1/cancel
Authorization: Bearer <token>
```

Company (crear sin login):
```http
POST /api/companies
Content-Type: application/json

{
  "name": "Retail Norte SAC",
  "headquarters": "Lima"
}
```

Donation statistics (manager views donations by beneficiary):
```http
GET /api/donations/statistics
Authorization: Bearer <token>
```
Response:
```json
[
  {
    "beneficiaryId": 1,
    "beneficiaryName": "Colegio San Juan",
    "totalDonations": 5,
    "totalQuantityDonated": 50
  },
  {
    "beneficiaryId": 2,
    "beneficiaryName": "Albergue Maria",
    "totalDonations": 3,
    "totalQuantityDonated": 25
  }
]
```

Requests by company (manager sees all company requests):
```http
GET /api/requests/company
Authorization: Bearer <token>
```

Requests by product (manager sees requests for a merma product):
```http
GET /api/requests/product/Yogurt%20Natural
Authorization: Bearer <token>
```
IAM (registro y login):
```http
POST /api/iam/register
Content-Type: application/json

{
  "email": "manager@retail.com",
  "rawPassword": "admin123",
  "role": "MANAGER",
  "companyId": 1
}
```

```http
POST /api/iam/register
Content-Type: application/json

{
  "email": "beneficiary@ngo.org",
  "rawPassword": "benef123",
  "role": "BENEFICIARY",
  "companyId": null
}
```

```http
POST /api/iam/login
Content-Type: application/json

{
  "email": "manager@retail.com",
  "rawPassword": "admin123"
}
```
Respuesta de login:
```json
{
  "user": {
    "id": 1,
    "email": { "value": "manager@retail.com" },
    "companyId": { "value": 1 },
    "role": "MANAGER",
    "status": "ACTIVE"
  },
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Endpoint de perfil (usuario autenticado)
------------------------------------

Obtén la información del usuario autenticado (`companyId`, `email`, `role`).

Request:

```http
GET /api/iam/profile
Authorization: Bearer <token>
```

Response (200):

```json
{
  "companyId": 1,
  "email": "manager@retail.com",
  "role": "MANAGER"
}
```

Ejemplo cURL:

```bash
curl -X GET http://localhost:8080/api/iam/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```
## Endpoints de usuario (IAM)

Se agregaron endpoints para consultar cuentas de usuario y listas filtradas por rol. Estos endpoints requieren un token JWT válido en el header `Authorization: Bearer <token>`, pero no validan *quién* realiza la consulta — cualquier usuario autenticado puede leerlos.

- `GET /api/iam/users/{userId}` — Devuelve la cuenta solicitada. Respuesta segura (`UserAccountDto`) con campos: `id`, `email`, `companyId` (nullable), `role`, `status`. Bajo ningún concepto se devuelve `passwordHash`.
- `GET /api/iam/users?role=MANAGER|BENEFICIARY` — Lista usuarios filtrados por rol. Si no se especifica `role` devuelve todos los usuarios (respuesta: array de `UserAccountDto`).

Ejemplos:

```bash
curl -X GET http://localhost:8080/api/iam/users/1 \
  -H "Authorization: Bearer <token>"
```

```bash
curl -X GET "http://localhost:8080/api/iam/users?role=BENEFICIARY" \
  -H "Authorization: Bearer <token>"
```

Nota: la implementación usa DTOs para evitar exponer `passwordHash` accidentalmente desde la entidad JPA.

Notas:
- El endpoint requiere autenticación; usa el token devuelto por `/api/iam/login`.
- Si el usuario no tiene `companyId` (por ejemplo, un beneficiario), el valor puede ser `null`.

## Ejemplos cURL (flujo completo)

1) Registrar merma:
```bash
curl -X POST http://localhost:8080/api/mermas/register \
  -H "Content-Type: application/json" \
  -d '{"productName":"Yogurt Natural","categoryName":"Lacteos","quantity":12,"expirationDate":"2026-05-10","reason":"EXPIRATION"}'
```

2) Marcar merma como donable:
```bash
curl -X PATCH http://localhost:8080/api/mermas/1/donable
```

3) Registrar beneficiario:
```bash
curl -X POST http://localhost:8080/api/beneficiaries/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Colegio San Juan","type":"SCHOOL","address":"Av. Principal 123","acceptedProducts":["Lacteos","Conservas"]}'
```

4) Crear donacion:
```bash
curl -X POST http://localhost:8080/api/donations/create \
  -H "Content-Type: application/json" \
  -d '{"mermaReferenceId":1,"beneficiaryReferenceId":1,"quantity":5,"scheduledDeliveryDate":"2026-05-05"}'
```

5) Marcar entrega:
```bash
curl -X PATCH http://localhost:8080/api/donations/1/delivered \
  -H "Content-Type: application/json" \
  -d '{"deliveryDate":"2026-05-06"}'
```

6) Confirmar recepcion:
```bash
curl -X PATCH http://localhost:8080/api/donations/1/confirm \
  -H "Content-Type: application/json" \
  -d '{"receptionDate":"2026-05-06","comment":"Recepcion completa"}'
```

## Validaciones y reglas de negocio

- IDs deben ser positivos.
- Strings no vacios (producto, categoria, nombre beneficiario, direccion).
- Quantity y DonationQuantity > 0.
- Fechas requeridas en comandos de entrega y confirmacion.
- Email debe tener formato valido.
- Password minimo 6 caracteres.
- Transiciones de estado controladas en agregados.

## Manejo de errores (comportamiento actual)

- `IllegalArgumentException` cuando la entrada es invalida.
- `NoSuchElementException` cuando no se encuentra un recurso.
- Estas excepciones se reflejan como error HTTP segun el manejo por defecto de Spring.

## Configuracion

- Base de datos MySQL configurada en `src/main/resources/application.properties`:
  - Usuario: `root`
  - Password: `admin`
  - DB: `fluxus` (autocrea si no existe)

## Notas de implementacion

- No se usan DTOs ni mappers, salvo la respuesta de autenticacion con token; los endpoints reciben comandos/queries y retornan agregados.
- Swagger documenta respuestas y errores basicos.
- Se usa `AuditableAggregateRoot` para `id`, `createdAt`, `updatedAt`.
- IAM devuelve usuario sin exponer `passwordHash`.
- El login devuelve `user + token` y el filtro JWT coloca la autenticacion en `SecurityContext`.

## Limitaciones conocidas

- La autenticacion usa JWT basico con clave simetrica de desarrollo.
- No hay validaciones con anotaciones Bean Validation en records.
- Manejo de errores usa respuestas por defecto de Spring.

## Roadmap sugerido

- Implementar `@ControllerAdvice` para errores consistentes.
- Agregar paginacion en listados.
- Test unitarios para reglas de negocio.

## Glosario del dominio

- Merma: producto fuera de inventario regular por vencimiento, daño o sobrestock.
- Donable: merma apta para donacion por criterio del encargado.
- Donacion: asignacion de merma a un beneficiario con fechas de entrega y recepcion.
- Beneficiario: institucion que recibe donaciones (colegio, albergue u ONG).
- Usuario Retail: encargado/administrador que opera la merma y las donaciones.
- IAM: identidad y acceso; registro y autenticacion basica.
- Company: empresa retail a la que se asocian usuarios y datos operativos.

## Matriz de permisos (conceptual)

- Retail (MANAGER):
  - Puede registrar merma, clasificar donable/no donable y crear donaciones.
  - Puede registrar/editar beneficiarios y activar/desactivar.
  - Puede ver reportes operativos via endpoints.
- Beneficiario (BENEFICIARY):
  - Puede ver sus donaciones asignadas y confirmar recepcion.
  - No puede registrar merma ni crear donaciones.

Nota: la seguridad por roles no esta implementada aun (ver Limitaciones).

## Codigos HTTP esperados (guia)

- 200 OK: consulta o actualizacion exitosa.
- 201 Created: registro/creacion exitosa.
- 400 Bad Request: validacion o entrada invalida.
- 404 Not Found: recurso no encontrado.
- 409 Conflict: conflicto de negocio (ej. duplicados).
- 500 Internal Server Error: error no controlado.

## Parametros de configuracion

`src/main/resources/application.properties`:
- `spring.datasource.url` URL MySQL (crea DB si no existe).
- `spring.datasource.username` usuario MySQL.
- `spring.datasource.password` password MySQL.
- `spring.jpa.hibernate.ddl-auto` estrategia de schema (`update`).
- `springdoc.api-docs.path` ruta OpenAPI.
- `springdoc.swagger-ui.path` ruta Swagger UI.
- `authorization.jwt.secret` clave simetrica para firmar JWT.
- `authorization.jwt.expiration-milliseconds` duracion del token en milisegundos.

## Catalogo de requests (todas las operaciones)

Merma:
- Register (`POST /api/mermas/register`):
```json
{
  "productName": "Yogurt Natural",
  "categoryName": "Lacteos",
  "quantity": 12,
  "expirationDate": "2026-05-10",
  "reason": "EXPIRATION"
}
```
- Donable (`PATCH /api/mermas/{mermaId}/donable`): sin body.
- Not Donable (`PATCH /api/mermas/{mermaId}/not-donable`): sin body.
- Donated (`PATCH /api/mermas/{mermaId}/donated`): sin body.

Beneficiarios:
- Register (`POST /api/beneficiaries/register`):
```json
{
  "name": "Colegio San Juan",
  "type": "SCHOOL",
  "address": "Av. Principal 123",
  "acceptedProducts": ["Lacteos", "Conservas"]
}
```
- Update (`PUT /api/beneficiaries/{beneficiaryId}`):
```json
{
  "name": "Colegio San Juan",
  "type": "SCHOOL",
  "address": "Av. Principal 123",
  "acceptedProducts": ["Lacteos", "Conservas", "Granos"]
}
```
- Activate/Deactivate (`PATCH /api/beneficiaries/{beneficiaryId}/activate|deactivate`): sin body.

Donaciones:
- Create (`POST /api/donations/create`):
```json
{
  "mermaReferenceId": 1,
  "beneficiaryReferenceId": 2,
  "quantity": 5,
  "scheduledDeliveryDate": "2026-05-05"
}
```
- Delivered (`PATCH /api/donations/{donationId}/delivered`):
```json
{
  "deliveryDate": "2026-05-06"
}
```
- Confirm (`PATCH /api/donations/{donationId}/confirm`):
```json
{
  "receptionDate": "2026-05-06",
  "comment": "Recepcion completa"
}
```

IAM:
- Register (`POST /api/iam/register`):
  - `MANAGER`: `companyId` obligatorio.
  - `BENEFICIARY`: `companyId` debe ser `null` o omitido.
```json
{
  "email": "admin@retail.com",
  "rawPassword": "admin123",
  "role": "MANAGER",
  "companyId": 1
}
```

```json
{
  "email": "beneficiary@ngo.org",
  "rawPassword": "benef123",
  "role": "BENEFICIARY",
  "companyId": null
}
```
- Login (`POST /api/iam/login`):
```json
{
  "email": "admin@retail.com",
  "rawPassword": "admin123"
}
```

Companies (public, no JWT):
- Create (`POST /api/companies`):
```json
{
  "name": "Retail Norte SAC",
  "headquarters": "Lima"
}
```
- List (`GET /api/companies`): sin body.
- Get by id (`GET /api/companies/{companyId}`): sin body.

## Supuestos del proyecto

- La merma registrada ya no pertenece al inventario general.
- La decision donable/no donable es responsabilidad del usuario retail.
- Beneficiarios desactivados no deben recibir nuevas donaciones.
- La confirmacion de recepcion cierra el ciclo de la donacion.

## Como ejecutar

Si no tienes Maven instalado, usa el wrapper:

```powershell
& "D:\Santiago\UPC\Ciclo 7\Fundamentos\FluxusBackend\FluxusBackend\mvnw.cmd" -f "D:\Santiago\UPC\Ciclo 7\Fundamentos\FluxusBackend\FluxusBackend\pom.xml" test
```

```powershell
& "D:\Santiago\UPC\Ciclo 7\Fundamentos\FluxusBackend\FluxusBackend\mvnw.cmd" -f "D:\Santiago\UPC\Ciclo 7\Fundamentos\FluxusBackend\FluxusBackend\pom.xml" spring-boot:run
```
