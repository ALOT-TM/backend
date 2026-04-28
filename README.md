# FluxusBackend

Backend DDD con cuatro bounded contexts para gestionar merma, donaciones, beneficiarios e identidad.

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
- Value Objects: `UserId`, `EmailAddress`, `PasswordHash`
- Enums: `UserRole`, `UserStatus`
- Commands: `RegisterUserCommand`
- Queries: `GetUserByIdQuery`, `GetUserByEmailQuery`, `LoginUserQuery`
- Repositorio: `UserAccountRepository`
- Servicios: `UserCommandService`, `UserQueryService`, `UserAuthenticationQueryService`

Reglas clave:
- Registro requiere email valido y password >= 6.
- Login valida credenciales (hash con BCrypt).

## Integraciones entre contextos (ACL)

- Donations consume Merma y Beneficiaries via ACL (facades) para evitar acoplamiento.
- Merma y Beneficiaries exponen facades con operaciones minimas (buscar id, marcar donada).

## Modelo de datos (tablas principales)

- `mermas`
  - id, product_name, category_name, quantity, expiration_date, reason, status, created_at, updated_at
- `donations`
  - id, merma_id, beneficiary_id, donation_quantity, scheduled_delivery_date,
    delivery_date, reception_date, reception_comment, status, created_at, updated_at
- `beneficiaries`
  - id, beneficiary_name, type, address, status, created_at, updated_at
- `beneficiary_accepted_products`
  - beneficiary_id, accepted_product
- `user_accounts`
  - id, email, password_hash, role, status, created_at, updated_at

Relaciones principales:
- Donation referencia Merma (merma_id) y Beneficiary (beneficiary_id) como VOs embebidos.
- Beneficiary tiene coleccion de productos aceptados.

## Base URL y documentacion

Base (por defecto): `http://localhost:8080`
- `GET /api-docs` OpenAPI JSON
- `GET /swagger-ui.html` Swagger UI

## Estados y enums

MermaStatus: `REGISTERED`, `DONABLE`, `DONATED`, `NOT_DONABLE`
MermaReason: `EXPIRATION`, `DAMAGED_PACKAGING`, `OVERSTOCK`
BeneficiaryStatus: `ACTIVE`, `INACTIVE`
BeneficiaryType: `SCHOOL`, `SHELTER`, `NGO`
DonationStatus: `ASSIGNED`, `DELIVERED`, `CONFIRMED`
UserRole: `RETAIL_MANAGER`, `BENEFICIARY`
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

IAM:
- `POST /api/iam/register`
- `POST /api/iam/login`

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

IAM (registro y login):
```http
POST /api/iam/register
Content-Type: application/json

{
  "email": "manager@retail.com",
  "rawPassword": "admin123",
  "role": "RETAIL_MANAGER"
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

- No se usan DTOs ni mappers; los endpoints reciben comandos/queries y retornan agregados.
- Swagger documenta respuestas y errores basicos.
- Se usa `AuditableAggregateRoot` para `id`, `createdAt`, `updatedAt`.
- IAM devuelve usuario sin exponer `passwordHash`.

## Limitaciones conocidas

- No hay autenticacion con tokens; solo login/registro.
- No hay validaciones con anotaciones Bean Validation en records.
- Manejo de errores usa respuestas por defecto de Spring.

## Roadmap sugerido

- Agregar JWT y filtros de seguridad.
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

## Matriz de permisos (conceptual)

- Retail (RETAIL_MANAGER):
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
```json
{
  "email": "admin@retail.com",
  "rawPassword": "admin123",
  "role": "RETAIL_MANAGER"
}
```
- Login (`POST /api/iam/login`):
```json
{
  "email": "admin@retail.com",
  "rawPassword": "admin123"
}
```

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
