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

- **Autenticación JWT**: `JwtTokenService` y `JwtAuthenticationFilter` con HMAC-SHA256. Login emite JWT con `userId`, `email`, `companyId`, `beneficiaryInstitutionId`, `actor`, `roleId` y `roleName`.
- **Rol por defecto en retail**: al registrar un usuario RETAIL, el sistema crea/reutiliza un rol "RETAIL_FULL_ACCESS" y asigna todos los permisos.
- **Swagger UI con JWT**: `OpenApiConfig` configura autenticación Bearer en Swagger (candado 🔒 en endpoints protegidos).
- **Sin formulario de login**: `SecurityConfig` deshabilita form login y HTTP Basic. Solo API REST.
- **Seguridad stateless**: `SessionCreationPolicy.STATELESS` + JWT validation en cada request.
- **Multi-tenancy**: `CompanyId` se aplica a las entidades retail y de trazabilidad. Los beneficiarios no llevan `companyId`; ACL filtra datos por `companyId` donde corresponde.
- **MySQL persistencia**: Base de datos `fluxus` con auto-creación de esquema. DDL mode `update`.
- `AuthenticatedUserPrincipal` y `CurrentUserProvider` exponen usuario desde `SecurityContext`.
- `Company` aggregate para representar empresas retail.
- Endpoint `GET /api/auth/profile` expone perfil del usuario autenticado.
- **Auditoria de cambios de estado**: se registra en `status_change_logs` con usuario y timestamp.
  - `GET /api/audit/status-changes` (filtros: `entityType`, `entityId`, `userId`)
  - `GET /api/audit/status-changes/{entityType}/{entityId}`

Ejemplos cURL:

```bash
curl -X GET "http://localhost:8080/api/audit/status-changes?entityType=SHRINKAGE&entityId=1" \
  -H "Authorization: Bearer <token>"
```

```bash
curl -X GET "http://localhost:8080/api/audit/status-changes?userId=1" \
  -H "Authorization: Bearer <token>"
```
- **Company endpoints públicos** (sin JWT):
  - `POST /api/retail-companies` - Crear compañía
  - `GET /api/retail-companies` - Listar compañías
  - `GET /api/retail-companies/{companyId}` - Obtener compañía por id
- **Beneficiary endpoints públicos** (sin JWT):
  - `POST /api/beneficiary-institutions`
  - `GET /api/beneficiary-institutions`
  - `GET /api/beneficiary-institutions/{beneficiaryId}`
  - `PUT /api/beneficiary-institutions/{beneficiaryId}`
- **Institution type endpoint público** (sin JWT):
  - `POST /api/institution-types`
- **Location endpoints públicos** (sin JWT):
  - `POST /api/countries`
  - `GET /api/countries`
  - `GET /api/countries/{countryId}`
  - `POST /api/addresses`
  - `GET /api/addresses`
  - `GET /api/addresses/{addressId}`
- **Headquarter endpoints públicos** (sin JWT):
  - `POST /api/retail-company-headquarters`
  - `GET /api/retail-company-headquarters`
  - `GET /api/retail-company-headquarters/{headquarterId}`
  - `POST /api/beneficiary-institution-headquarters`
  - `GET /api/beneficiary-institution-headquarters`
  - `GET /api/beneficiary-institution-headquarters/{headquarterId}`
- **Catálogos de merma**:
  - `POST /api/shrinkages/categories`
  - `PATCH /api/shrinkages/categories/{categoryId}`
  - `DELETE /api/shrinkages/categories/{categoryId}`
  - `GET /api/shrinkages/categories`
  - `GET /api/shrinkages/categories/{categoryId}`
  - `POST /api/shrinkages/reasons`
  - `PATCH /api/shrinkages/reasons/{shrinkageReasonId}`
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

### 1. Shrinkage Management (Gestión de Merma)

Responsabilidad: registrar y clasificar productos en merma (estado, motivo, vencimiento).

- Aggregate Roots: `Shrinkage`, `ShrinkageReason`, `Category`
- Tablas: `shrinkage`, `shrinkage_reason`, `category`, `shrinkage_log`
- Enums: `ShrinkageStatus` (REGISTERED, DONABLE, IN_PROCESS, DONATED, NOT_DONABLE)
- Repositorios: JPA repositories para cada entidad
- Servicios: Command y Query services
- Integración: Expone ACL para que Donations Management pueda consultar mermas donables

Reglas clave:
- La merma inicia en `REGISTERED`
- Puede ser clasificada como `DONABLE` (apta para donación) o `NOT_DONABLE`
- Al asignar donación, pasa a `IN_PROCESS`
- Al confirmar recepción de donación, pasa a `DONATED`
- Las mermas están asociadas a `company_id` para aislamiento multi-tenant

### 2. Donation Logistics Management (Logística de Donaciones)

Responsabilidad: coordinar donaciones a partir de mermas donables y beneficiarios activos.

- Aggregate Roots: `Donation`, `DonationItem`, `DonationRequest`
- Tablas: `donations`, `donation_items`, `donation_requests`
- Enums: 
  - `DonationStatus`: ASSIGNED, DELIVERED, CONFIRMED
  - `DonationItemStatus`: ASSIGNED, DELIVERED, CONFIRMED, CANCELLED
  - `DonationRequestStatus`: PENDING, ACCEPTED, REJECTED, CANCELLED, COMPLETED
- Repositorios: `DonationRepository`, `DonationItemRepository`, `DonationRequestRepository`
- Servicios: Command y Query services para cada entidad
- Endpoints: `/api/donations/**`, `/api/requests/**`

Reglas clave:
- Una donación inicia en `ASSIGNED`
- Transiciones: ASSIGNED → DELIVERED → CONFIRMED
- Una `DonationRequest` (solicitud de beneficiario) inicia en `PENDING`
- Manager puede ACCEPT o REJECT requests
- Beneficiarios pueden CANCEL sus requests
- Cada donación contiene múltiples `DonationItem` (items de distintas mermas)
- Las donaciones están asociadas a `company_id`

### 3. Beneficiary Management (Gestión de Beneficiarios)

Responsabilidad: administrar instituciones beneficiarias, tipos y ubicaciones.

- Aggregate Roots: `BeneficiaryInstitution`, `BeneficiaryInstitutionHeadquarter`, `InstitutionType`
- Tablas: `beneficiary_institution`, `beneficiary_institution_headquarter`, `institution_type`
- Repositorios: para cada entidad
- Servicios: Command y Query services
- Endpoints: `/api/beneficiary-institutions/**`, `/api/beneficiary-institution-headquarters/**`

Reglas clave:
- Una institución beneficiaria es de un tipo específico (InstitutionType)
- Puede tener múltiples sedes (BeneficiaryInstitutionHeadquarter)
- Cada sede está vinculada a una `Address`
- Los beneficiarios NO están asociados a `company_id` (son catálogo global)
- Las compañías pueden marcear favoritos (`company_favorite_institution`)

### 4. Location Management (Gestión de Ubicaciones)

Responsabilidad: gestionar direcciones y ubicaciones geográficas.

- Aggregate Roots: `Address`, `Country`
- Tablas: `address`, `country`
- Repositorios: para cada entidad
- Endpoints: `/api/countries/**`, `/api/addresses/**`

Reglas clave:
- Cada dirección pertenece a un País
- Direcciones pueden ser reutilizadas por múltiples entidades (Retail/Beneficiary headquarters)

### 5. Company Retail Management (Gestión de Compañías Retail)

Responsabilidad: administrar empresas retail y sus sedes.

- Aggregate Roots: `RetailCompany`, `RetailCompanyHeadquarter`, `CompanyFavoriteInstitution`
- Tablas: `retail_company`, `retail_company_headquarter`, `company_favorite_institution`
- Repositorios: para cada entidad
- Endpoints: `/api/retail-companies/**`, `/api/retail-company-headquarters/**`

Reglas clave:
- Una compañía retail pertenece a un Retail User (Manager)
- Puede tener múltiples sedes
- Puede marcar instituciones beneficiarias como favoritas
- Esta información se usa para aislamiento multi-tenant en Shrinkage y Donations

### 6. Identity & Access Management (IAM)

Responsabilidad: registro, autenticación y autorización de usuarios (retail y beneficiarios).

- Aggregate Roots: `UserAccount`, `RetailUser`, `BeneficiaryUser`, `Role`, `Permission`, `RolePermission`
- Tablas: `user_account`, `retail_user`, `beneficiary_user`, `role`, `permission`, `role_permission`
- Servicios: Command y Query services para autenticación y búsqueda
- Endpoints: `/api/auth/register`, `/api/auth/login`, `/api/auth/profile`, `/api/auth/users/**`

Reglas clave:
- Un `UserAccount` puede ser:
  - `RetailUser`: vinculado a una `RetailCompany` y un `Role`
  - `BeneficiaryUser`: vinculado a una `BeneficiaryInstitution`
- Autenticación genera JWT con:
  - userId, email, companyId (si retail), beneficiaryInstitutionId (si beneficiary), roleId, roleName
- Roles controlan acceso granular a través de `Permission` y `RolePermission`
- Registro requiere email válido y password ≥ 6 caracteres
- Contraseñas se hashean con BCrypt

### 7. Subscription Management (Suscripciones)

Responsabilidad: gestionar planes y suscripciones de compañías.

- Aggregate Roots: `Subscription`, `Plan`
- Tablas: `subscription`, `plan`
- Repositorios: para cada entidad

Reglas clave:
- Un `Plan` define límites: usuarios máximos, storage máximo
- Una `Subscription` vincula una compañía favorita a un plan
- Se puede activar/desactivar según gestión administrativa

### 8. Shared Context (Compartido)

Responsabilidad: auditoría y trazabilidad transversal.

- Entity: `StatusChangeLog`
- Tabla: `status_change_logs`
- Registra: cambios de estado de cualquier entidad (entity_type, entity_id, from_status, to_status, changed_by_user_id)

## Integraciones entre contextos (ACL - Anti-Corruption Layer)

### Estrategia de integración

- **Donation Logistics** consume **Shrinkage Management** y **Beneficiary Management** vía ACL para evitar acoplamiento directo
- **Shrinkage Management** expone servicios ACL para consultar mermas donables y marcar como entregadas
- **Beneficiary Management** expone servicios ACL para validar instituciones beneficiarias activas
- Las operaciones se coordinan a través de repositories y servicios especializados sin exposición de detalles internos

### Flujos de integración

1. **Crear donación** (Donation Logistics):
   - Valida merma mediante Shrinkage Management ACL
   - Valida beneficiario mediante Beneficiary Management ACL
   - Crea donation con items

2. **Confirmar recepción** (Donation Logistics):
   - Actualiza estado de donation a CONFIRMED
   - Notifica a Shrinkage Management para cambiar estado a DONATED
   - Registra log de auditoría en `status_change_logs`

3. **Crear solicitud de donación** (Beneficiary request):
   - Beneficiary consulta mermas en estado DONABLE (via Shrinkage ACL)
   - Crea DonationRequest en estado PENDING
   - Notifica a manager retail

4. **Aceptar/Rechazar solicitud** (Manager retail):
   - Si ACCEPT: crea Donation automáticamente, marca merma como IN_PROCESS
   - Si REJECT: mantiene merma en DONABLE

### Aislamiento multi-tenant

- **Operaciones retail**: todas usan `company_id` para filtrar datos del usuario autenticado
- **Operaciones beneficiarios**: no usan `company_id` (catálogo global de instituciones)
- **Validación de acceso**: se valida que el usuario pertenezca a la compañía en cada operación
- **Datos JWT**: el token contiene `companyId` (retail) o `beneficiaryInstitutionId` (beneficiario)

## Modelo de datos (tablas generadas en MySQL)

El backend genera **24 tablas** al iniciar con `spring.jpa.hibernate.ddl-auto=update`. 

### Tablas del dominio (por contexto)

#### 1. Shrinkage Management (Gestión de Merma)

- **`shrinkage`** - Productos en merma
  - shrinkage_id, name, quantity, expiration_date, specific_reason, status, pickup_date, retail_company_headquarter_id, category_id, shrinkage_reason_id, company_id (embedded), created_at, updated_at
  - Status: REGISTERED, DONABLE, IN_PROCESS, DONATED, NOT_DONABLE
  
- **`shrinkage_reason`** - Razones de merma
  - shrinkage_reason_id, name, created_at, updated_at
  
- **`category`** - Categorías de productos
  - category_id, name, created_at, updated_at
  
- **`shrinkage_log`** - Log de cambios en mermas
  - shrinkage_log_id, shrinkage_id, status, created_at, updated_at

#### 2. Donation Logistics Management (Logística de Donaciones)

- **`donations`** - Donaciones principales
  - id, beneficiary_institution_id (VO), donation_quantity (VO), scheduled_delivery_date (VO), delivery_date (VO), reception_date (VO), reception_comment, status, company_id (VO), completed_at, created_at, updated_at
  - Status: ASSIGNED, DELIVERED, CONFIRMED

- **`donation_items`** - Items dentro de cada donación
  - donation_item_id, donation_id (FK), shrinkage_reference_id (VO), status, created_at, updated_at
  - Status: ASSIGNED, DELIVERED, CONFIRMED, CANCELLED

- **`donation_requests`** - Solicitudes de donación por parte de beneficiarios
  - id, shrinkage_reference_id (VO), beneficiary_reference_id (VO), company_id (VO), status, notes, created_at, updated_at
  - Status: PENDING, ACCEPTED, REJECTED, CANCELLED, COMPLETED

#### 3. Beneficiary Management (Gestión de Beneficiarios)

- **`beneficiary_institution`** - Instituciones beneficiarias (colegios, albergues, ONGs)
  - beneficiary_institution_id, institution_type_id (FK), name, created_at, updated_at

- **`beneficiary_institution_headquarter`** - Sedes de instituciones beneficiarias
  - beneficiary_institution_headquarter_id, beneficiary_institution_id (FK), description, address_id (FK), created_at, updated_at

- **`institution_type`** - Tipos de instituciones beneficiarias
  - institution_type_id, name, created_at, updated_at

#### 4. Location Management (Gestión de Ubicaciones)

- **`address`** - Direcciones
  - address_id, street1, street2, city, state_province, postal_code, country_id (FK), created_at, updated_at

- **`country`** - Países
  - country_id, name, created_at, updated_at

#### 5. Company Retail Management (Gestión de Compañías Retail)

- **`retail_company`** - Empresas retail
  - retail_company_id, name, created_at, updated_at

- **`retail_company_headquarter`** - Sedes de empresas retail
  - retail_company_headquarter_id, retail_company_id (FK), description, address_id (FK), created_at, updated_at

- **`company_favorite_institution`** - Instituciones favoritas por compañía
  - company_favorite_institution_id, retail_company_id (FK), beneficiary_institution_id (FK), created_at, updated_at

#### 6. Auth & Access Management (IAM)

- **`user_account`** - Cuentas de usuario
  - user_account_id, email (VO), password_hash (VO), username, created_at, updated_at

- **`retail_user`** - Usuarios de retail (vinculados a compañía)
  - retail_user_id, user_account_id (FK, unique), retail_company_id (FK), role_id (FK), status

- **`beneficiary_user`** - Usuarios de beneficiarios
  - beneficiary_user_id, user_account_id (FK, unique), beneficiary_institution_id (FK)

- **`role`** - Roles disponibles
  - role_id, name, created_at, updated_at

- **`role_permission`** - Asignación de permisos a roles
  - role_permission_id, role_id (FK), permission_id (FK), created_at, updated_at

- **`permission`** - Permisos del sistema
  - permission_id, name, description, created_at, updated_at

#### 7. Shared/Common (Compartidas)

- **`status_change_logs`** - Auditoría de cambios de estado
  - id, entity_type (enum), entity_id, from_status, to_status, changed_by_user_id, changed_at

#### 8. Subscription Management (Suscripciones)

- **`subscription`** - Suscripciones
  - subscription_id, company_favorite_institution_id (FK), plan_id (FK), start_date, completed_at, status, created_at, updated_at

- **`plan`** - Planes de suscripción
  - plan_id, is_active, max_storage_bytes, max_users, plan_name, plan_price (decimal), created_at, updated_at

### Relaciones principales

- `shrinkage` → `retail_company_headquarter` (ManyToOne)
- `shrinkage` → `category` (ManyToOne)
- `shrinkage` → `shrinkage_reason` (ManyToOne)
- `donations` → `donation_items` (OneToMany con cascade)
- `beneficiary_institution` → `institution_type` (ManyToOne)
- `beneficiary_institution_headquarter` → `address` (ManyToOne)
- `retail_company_headquarter` → `address` (ManyToOne)
- `address` → `country` (ManyToOne)
- `retail_user` → `user_account` (OneToOne)
- `retail_user` → `retail_company` (ManyToOne)
- `retail_user` → `role` (ManyToOne)
- `beneficiary_user` → `user_account` (OneToOne)
- `beneficiary_user` → `beneficiary_institution` (ManyToOne)
- `company_favorite_institution` → `retail_company` (ManyToOne)
- `company_favorite_institution` → `beneficiary_institution` (ManyToOne)
- `subscription` → `company_favorite_institution` (ManyToOne)
- `subscription` → `plan` (ManyToOne)
- `role_permission` → `role` (ManyToOne)
- `role_permission` → `permission` (ManyToOne)

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
  curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{
       "email": "retail@empresa.com",
       "rawPassword": "pass123",
       "username": "retail1",
       "actor": "RETAIL",
       "retailCompanyId": 1
     }'
   ```

   Beneficiario:
   ```bash
  curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{
       "email": "beneficiary@test.com",
       "rawPassword": "pass123",
       "username": "benef1",
       "actor": "BENEFICIARY",
       "beneficiaryInstitutionId": 1
     }'
   ```

2. **Hacer login** (obtener JWT token):
   ```bash
  curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email": "user@test.com", "rawPassword": "pass123"}'
   ```
   
   Respuesta:
   ```json
   {
     "user": {
       "id": 1,
       "email": "user@test.com",
       "actor": "RETAIL",
       "companyId": 1,
       "roleId": 1,
       "roleName": "RETAIL_FULL_ACCESS"
     },
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
       http://localhost:8080/api/auth/profile
   ```

### Seguridad

- **Login form deshabilitado**: `@Configuration(proxyBeanMethods=false)` + `.formLogin(form -> form.disable())`
- **HTTP Basic deshabilitado**: `.httpBasic(basic -> basic.disable())`
- **JWT required**: todos los endpoints excepto `/api/auth/register`, `/api/auth/login`, `/api/retail-companies/**`, `/api/beneficiary-institutions/**`, `/api/beneficiary-institution-headquarters/**`, `/api/retail-company-headquarters/**`, `/api/institution-types/**`, `/api/countries/**`, `/api/addresses/**`, `/api/shrinkages/categories/**`, `/api/shrinkages/reasons/**` y Swagger docs
- **Session stateless**: `SessionCreationPolicy.STATELESS`
- **CORS enabled**: permite `http://localhost:5173` (configurado en `WebConfig`)
- **CompanyId en escrituras**: operaciones de registro/modificacion usan `companyId` del token; algunos GETs aceptan `companyId` pero se valida contra el token.

## Estados y enums

MermaStatus: `REGISTERED`, `DONABLE`, `IN_PROCESS`, `DONATED`, `NOT_DONABLE`
MermaReason: `EXPIRATION`, `DAMAGED_PACKAGING`, `OVERSTOCK`
BeneficiaryStatus: `ACTIVE`, `INACTIVE`
BeneficiaryType: `SCHOOL`, `SHELTER`, `NGO`
DonationStatus: `ASSIGNED`, `DELIVERED`, `CONFIRMED`
DonationRequestStatus: `PENDING`, `ACCEPTED`, `REJECTED`, `CANCELLED`, `COMPLETED`
UserRole: `MANAGER` (retail), `BENEFICIARY`
UserStatus: `ACTIVE`, `INACTIVE`

## Endpoints (mapa rapido)

Merma:
- `POST /api/shrinkages`
- `PATCH /api/shrinkages/{shrinkageId}/donable`
- `PATCH /api/shrinkages/{shrinkageId}/not-donable`
- `PATCH /api/shrinkages/{shrinkageId}/donated`
- `GET /api/shrinkages/{shrinkageId}`
- `GET /api/shrinkages?status=REGISTERED|DONABLE|IN_PROCESS|DONATED|NOT_DONABLE&companyId=X`
- `GET /api/shrinkages/donable?companyId=X`
- `GET /api/shrinkages/company`
- Beneficiarios: solo pueden leer mermas en estado `DONABLE`.

Catálogos de merma:
- `POST /api/shrinkages/categories`
- `PATCH /api/shrinkages/categories/{categoryId}`
- `DELETE /api/shrinkages/categories/{categoryId}`
- `GET /api/shrinkages/categories`
- `GET /api/shrinkages/categories/{categoryId}`
- `POST /api/shrinkages/reasons`
- `PATCH /api/shrinkages/reasons/{shrinkageReasonId}`

Ubicaciones:
- `POST /api/countries`
- `GET /api/countries`
- `GET /api/countries/{countryId}`
- `POST /api/addresses`
- `GET /api/addresses`
- `GET /api/addresses/{addressId}`

Sedes:
- `POST /api/retail-company-headquarters`
- `GET /api/retail-company-headquarters`
- `GET /api/retail-company-headquarters/{headquarterId}`
- `POST /api/beneficiary-institution-headquarters`
- `GET /api/beneficiary-institution-headquarters`
- `GET /api/beneficiary-institution-headquarters/{headquarterId}`

Donaciones:
- `POST /api/donations/create`
- `PATCH /api/donations/{donationId}/delivered`
- `PATCH /api/donations/{donationId}/confirm`
- `GET /api/donations/{donationId}`
- `GET /api/donations?status=ASSIGNED|DELIVERED|CONFIRMED`
- `GET /api/donations/by-beneficiary/{beneficiaryId}`
- `GET /api/donations/statistics` (devuelve cantidad de donaciones por beneficiario - manager only)

Donation Requests:
- `POST /api/requests` (beneficiary creates a request for a donable merma; `beneficiaryId` se obtiene del token)
- `GET /api/requests/{requestId}` (gets a request by id)
- `GET /api/requests?beneficiaryId=X` (lists a beneficiary's requests)
- `GET /api/requests/merma/{mermaId}` (lists requests for a merma - manager only)
- `PATCH /api/requests/{requestId}/accept` (manager accepts a request)
- `PATCH /api/requests/{requestId}/reject` (manager rejects a request)
- `PATCH /api/requests/{requestId}/cancel` (beneficiary cancels a request)
- `GET /api/requests/company` (lists requests for the manager's company - manager only)
- `GET /api/requests/product/{productName}` (lists requests by merma product name - manager only)

Companies (public, no JWT):
- `POST /api/retail-companies`
- `GET /api/retail-companies`
- `GET /api/retail-companies/{companyId}`

IAM:
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/profile` (devuelve `companyId`, `email`, `role` del usuario autenticado)

Seguridad:
- Las rutas protegidas requieren `Authorization: Bearer <jwt>`.
- El JWT contiene `userId`, `email`, `companyId`, `beneficiaryInstitutionId`, `actor`, `roleId` y `roleName`.
- Las rutas `/api/retail-companies/**`, `/api/beneficiary-institutions/**`, `/api/beneficiary-institution-headquarters/**`, `/api/retail-company-headquarters/**`, `/api/institution-types/**`, `/api/countries/**`, `/api/addresses/**`, `/api/shrinkages/categories/**` y `/api/shrinkages/reasons/**` son publicas y no requieren token.

## Ejemplos rapidos por contexto

Merma (registrar y marcar donable):
```http
POST /api/shrinkages
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
PATCH /api/shrinkages/1/donable
```

Beneficiarios (registrar y desactivar):
```http
POST /api/institution-types
Content-Type: application/json

{
  "name": "HOSPITAL"
}
```
```http
POST /api/beneficiary-institutions
Content-Type: application/json

{
  "name": "Colegio San Juan",
  "institutionTypeId": 1
}
```
```http
PUT /api/beneficiary-institutions/1
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
POST /api/retail-companies
Content-Type: application/json

{
  "name": "Retail Norte SAC"
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
POST /api/auth/register
Content-Type: application/json

{
  "email": "manager@retail.com",
  "rawPassword": "admin123",
  "username": "retail1",
  "actor": "RETAIL",
  "retailCompanyId": 1
}
```

```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "beneficiary@ngo.org",
  "rawPassword": "benef123",
  "username": "benef1",
  "actor": "BENEFICIARY",
  "beneficiaryInstitutionId": 1
}
```

```http
POST /api/auth/login
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
    "roleId": 1,
    "roleName": "RETAIL_FULL_ACCESS"
  },
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Endpoint de perfil (usuario autenticado)
------------------------------------

Obtén la información del usuario autenticado (`companyId`, `email`, `role`).

Request:

```http
GET /api/auth/profile
Authorization: Bearer <token>
```

Response (200):

```json
{
  "companyId": 1,
  "email": "manager@retail.com",
  "roleName": "RETAIL_FULL_ACCESS"
}
```

Ejemplo cURL:

```bash
curl -X GET http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```
## Endpoints de usuario (IAM)

Se agregaron endpoints para consultar cuentas de usuario y listas filtradas por rol. Estos endpoints requieren un token JWT válido en el header `Authorization: Bearer <token>`, pero no validan *quién* realiza la consulta — cualquier usuario autenticado puede leerlos.

- `GET /api/auth/users/{userId}` — Devuelve la cuenta solicitada. Respuesta segura (`UserAccountDto`) con campos: `id`, `email`, `companyId` (nullable), `role`, `status`. Bajo ningún concepto se devuelve `passwordHash`.
- `GET /api/auth/users?role=MANAGER|BENEFICIARY` — Lista usuarios filtrados por rol. Si no se especifica `role` devuelve todos los usuarios (respuesta: array de `UserAccountDto`).

Ejemplos:

```bash
curl -X GET http://localhost:8080/api/auth/users/1 \
  -H "Authorization: Bearer <token>"
```

```bash
curl -X GET "http://localhost:8080/api/auth/users?role=BENEFICIARY" \
  -H "Authorization: Bearer <token>"
```

Nota: la implementación usa DTOs para evitar exponer `passwordHash` accidentalmente desde la entidad JPA.

Notas:
- El endpoint requiere autenticación; usa el token devuelto por `/api/auth/login`.
- Si el usuario no tiene `companyId` (por ejemplo, un beneficiario), el valor puede ser `null`.

## Ejemplos cURL (flujo completo)

1) Registrar merma:
```bash
curl -X POST http://localhost:8080/api/shrinkages \
  -H "Content-Type: application/json" \
  -d '{"productName":"Yogurt Natural","categoryName":"Lacteos","quantity":12,"expirationDate":"2026-05-10","reason":"EXPIRATION"}'
```

2) Marcar merma como donable:
```bash
curl -X PATCH http://localhost:8080/api/shrinkages/1/donable
```

3) Registrar beneficiario (tipo + institucion):
```bash
curl -X POST http://localhost:8080/api/institution-types \
  -H "Content-Type: application/json" \
  -d '{"name":"HOSPITAL"}'
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
- Register (`POST /api/shrinkages`):
```json
{
  "productName": "Yogurt Natural",
  "categoryName": "Lacteos",
  "quantity": 12,
  "expirationDate": "2026-05-10",
  "reason": "EXPIRATION"
}
```
- Donable (`PATCH /api/shrinkages/{shrinkageId}/donable`): sin body.
- Not Donable (`PATCH /api/shrinkages/{shrinkageId}/not-donable`): sin body.
- Donated (`PATCH /api/shrinkages/{shrinkageId}/donated`): sin body.

Catálogos de merma:
- Create category (`POST /api/shrinkages/categories`):
```json
{
  "name": "Lacteos"
}
```
- Patch category (`PATCH /api/shrinkages/categories/{categoryId}`):
```json
{
  "name": "Congelados"
}
```
- Create reason (`POST /api/shrinkages/reasons`):
```json
{
  "name": "Damaged packaging"
}
```
- Patch reason (`PATCH /api/shrinkages/reasons/{shrinkageReasonId}`):
```json
{
  "name": "Expired"
}
```

Beneficiarios:
- Register type (`POST /api/institution-types`):
```json
{
  "name": "HOSPITAL"
 }
```
- Register (`POST /api/beneficiary-institutions`):
```json
{
  "name": "Colegio San Juan",
  "institutionTypeId": 1
}
```
- Update (`PUT /api/beneficiary-institutions/{beneficiaryId}`):
```json
{
  "name": "Colegio San Juan",
  "institutionTypeId": 1
 }
```
- List (`GET /api/beneficiary-institutions`): sin body.

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
- Register (`POST /api/auth/register`):
  - `RETAIL`: `retailCompanyId` obligatorio.
  - `BENEFICIARY`: `beneficiaryInstitutionId` obligatorio.
```json
{
  "email": "admin@retail.com",
  "rawPassword": "admin123",
  "username": "retail1",
  "actor": "RETAIL",
  "retailCompanyId": 1
}
```

```json
{
  "email": "beneficiary@ngo.org",
  "rawPassword": "benef123",
  "username": "benef1",
  "actor": "BENEFICIARY",
  "beneficiaryInstitutionId": 1
}
```
- Login (`POST /api/auth/login`):
```json
{
  "email": "admin@retail.com",
  "rawPassword": "admin123"
}
```

Companies (public, no JWT):
- Create (`POST /api/retail-companies`):
```json
{
  "name": "Retail Norte SAC"
}
```
- List (`GET /api/retail-companies`): sin body.
- Get by id (`GET /api/retail-companies/{companyId}`): sin body.

## Supuestos del proyecto

- La merma registrada ya no pertenece al inventario general.
- La decision donable/no donable es responsabilidad del usuario retail.
- Beneficiarios desactivados no deben recibir nuevas donaciones.
- La confirmacion de recepcion cierra el ciclo de la donacion.

## Como ejecutar

Si no tienes Maven instalado, usa el wrapper:

```powershell
.\mvnw.cmd clean package -DskipTests
java -jar target\FluxusBackend-0.0.1-SNAPSHOT.jar
```
