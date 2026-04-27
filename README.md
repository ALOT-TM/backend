# FluxusBackend

Implementacion DDD con cuatro bounded contexts: Merma Management, Donations Management, Beneficiaries Management e Identity & Access Management (IAM).

## Endpoints base

- `GET /api-docs` OpenAPI JSON
- `GET /swagger-ui.html` Swagger UI

### Merma Management
- `POST /api/mermas/register`
- `PATCH /api/mermas/{mermaId}/donable`
- `PATCH /api/mermas/{mermaId}/not-donable`
- `PATCH /api/mermas/{mermaId}/donated`
- `GET /api/mermas/{mermaId}`
- `GET /api/mermas?status=REGISTERED|DONABLE|DONATED|NOT_DONABLE`

### Beneficiaries Management
- `POST /api/beneficiaries/register`
- `PUT /api/beneficiaries/{beneficiaryId}`
- `PATCH /api/beneficiaries/{beneficiaryId}/activate`
- `PATCH /api/beneficiaries/{beneficiaryId}/deactivate`
- `GET /api/beneficiaries/{beneficiaryId}`
- `GET /api/beneficiaries?status=ACTIVE|INACTIVE`

### Donations Management
- `POST /api/donations/create`
- `PATCH /api/donations/{donationId}/delivered`
- `PATCH /api/donations/{donationId}/confirm`
- `GET /api/donations/{donationId}`
- `GET /api/donations?status=ASSIGNED|DELIVERED|CONFIRMED`
- `GET /api/donations/by-beneficiary/{beneficiaryId}`

### Identity & Access Management (IAM)
- `POST /api/iam/register`
- `POST /api/iam/login`

