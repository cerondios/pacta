# PACTA — Session Context

> Long-term rental marketplace built with Java 21 + Spring Boot 4.0.6  
> This file captures all architectural decisions, patterns and progress from the development session.

---

## 1. Project Overview

PACTA is a two-sided marketplace:
- **Landlords** list apartments for long-term lease
- **Tenants** browse, apply, get verified, and sign contracts virtually

Key differentiator from Airbnb: contracts are legal instruments with duration, monthly payments, deposit and compliance checks (antecedentes, sanciones, inhabilidades, historial crediticio).

---

## 2. Infrastructure

| Component | Choice | Notes |
|---|---|---|
| Language | Java 21 | Virtual threads, records, pattern matching |
| Framework | Spring Boot 4.0.6 | Latest stable |
| Build | Gradle multi-module | Fast incremental builds |
| Database | PostgreSQL | Hosted on Render |
| Migrations | Flyway | Never modify applied; always add new V{n} files |
| Deployment | Docker + docker-compose | With Nginx reverse proxy |
| Observability | Grafana Cloud (OTLP) | Via Java Agent — no code changes needed |
| User auth | Passwordless OTP (email) | JWT HS256, 1h TTL |
| Operator auth | Email + bcrypt password | Separate JWT with `type=operator` claim |

### Local development
```bash
docker compose up -d   # starts postgres
./gradlew bootRun      # with SPRING_PROFILES_ACTIVE=local
```

### Environments
| Profile | Purpose | DB |
|---|---|---|
| `local` | Local dev | localhost:5432 hardcoded |
| `dev` | Render dev service | Env vars |
| `prod` | Render prod service | Env vars |

---

## 3. Module Structure

```
src/main/java/com/pacta/pacta_app/
│
├── auth/                   ← Passwordless OTP auth + JWT issuance (users)
├── admin/                  ← Internal operators — CRUD, auth, seeder
├── user/                   ← Platform end-users (LANDLORD, TENANT)
├── kyc/                    ← KYC document management
├── compliance/             ← Compliance docs (ANTECEDENTES etc.) + config
├── banking/                ← Bank accounts
├── profile/                ← Self-service: logged-in user manages own data
├── reviewer/               ← Reviewer queue + approve/reject actions
├── landlord/               ← Landlord views
├── tenant/                 ← Tenant views + score
└── shared/                 ← DateUtil, IdGenerator, MetricRecorder, DocumentStatus,
                               filters (PactaTokenFilter, OperatorTokenFilter),
                               GlobalExceptionHandler
```

### Per-module structure (all follow the same pattern)
```
{module}/
  domain/           ← Entities, value objects, repository interfaces (I{Name}Repository)
  application/      ← Services, DTOs
  infrastructure/
    persistence/    ← JPA entities, Spring Data repos, Postgres adapters
    controller/     ← REST controllers
```

---

## 4. Key Separation of Concerns

| Module | Audience | Endpoints |
|---|---|---|
| `profile/` | Logged-in user (self) | `GET/PUT /api/profile`, `/api/profile/kyc`, `/api/profile/documents`, `/api/profile/bank-accounts` |
| `user/` | Admin operations on users | `GET/PATCH/DELETE /api/users` |
| `landlord/` | Anyone viewing landlords | `GET /api/landlords` |
| `tenant/` | Anyone viewing tenants | `GET /api/tenants`, `/api/tenants/{id}/score` |
| `reviewer/` | REVIEWER / ADMIN / SUPER_ADMIN | `GET /api/reviewer/pending`, `PATCH /api/reviewer/kyc/{id}/approve` |
| `admin/` | SUPER_ADMIN managing operators | `POST/GET /api/admins` |
| `auth/` | User registration/login/verify | `POST /api/auth/register`, `/api/auth/login`, `/api/auth/verify` |
| `admin/auth` | Operator login | `POST /api/auth/admin/login` |
| `kyc/` | KYC submit + admin review | `POST /api/kyc`, `PATCH /api/kyc/{userId}/approve` |
| `compliance/` | Compliance docs + configs | `POST /api/compliance`, `GET /api/compliance/configs` |
| `banking/` | Bank accounts | `POST/DELETE /api/bank-accounts` |

---

## 5. User & Admin Roles

### Platform users (`users` table) — `Role` enum
- `LANDLORD` — lists properties
- `TENANT` — rents properties (needs KYC + compliance docs + bank account)

### Internal operators (`operators` table) — `AdminRole` enum
- `SUPER_ADMIN` — full access, manages other operators
- `ADMIN` — manages users, configs, properties
- `REVIEWER` — approves/rejects KYC and compliance documents

Key design: **operators and users are separate tables with separate auth flows**. They must never be confused.

Permission checks live on the `Admin` domain object:
```java
admin.canReview()        // REVIEWER | ADMIN | SUPER_ADMIN
admin.canManageUsers()   // ADMIN | SUPER_ADMIN
admin.canManageConfig()  // ADMIN | SUPER_ADMIN
admin.canManageAdmins()  // SUPER_ADMIN only
```

---

## 6. User Status Lifecycle

```
PENDING_VERIFICATION
    ↓ (verifyEmail — OTP verified)
PENDING_KYC
    ↓ (approveKyc — reviewer action)
ACTIVE
    ↓ (complete — all 4 compliance docs approved + bank account)
COMPLETED
    ↓ (downgradeToActive — any compliance doc expires)
ACTIVE
```

Admin-only transitions:
- `ACTIVE / COMPLETED → BLOCKED` via `block(adminId)`
- `BLOCKED → PENDING_VERIFICATION` via `unblock(adminId)`
- `ACTIVE / COMPLETED → REMOVED` via `remove(reason, removedBy)`

---

## 7. Operator Status Lifecycle

Operators have their own status managed by `AdminStatus` enum:

```
ACTIVE
  ↓ (suspend — by SUPER_ADMIN)
SUSPENDED
  ↓ (reactivate — by SUPER_ADMIN)
ACTIVE
```

Domain methods (on `Admin`):
```java
admin.suspend(suspendedBy)      // ACTIVE → SUSPENDED; throws if already suspended
admin.reactivate(reactivatedBy) // SUSPENDED → ACTIVE; throws if not suspended
admin.changePassword(newHash)   // always allowed; returns new instance
```

`Admin.isActive()` checks `status == AdminStatus.ACTIVE`.

---

## 8. Database Schema

```sql
-- Core
users              (id, full_name, email, phone_*, country, status, score, roles, created_at, updated_at, ...)
user_roles         (user_id FK, role)
operators          (id, full_name, email, role, status, password_hash, created_at, updated_at)
                   -- was: admin_users — renamed in V4 migration

-- Permissions (seeded)
permissions        (id)
role_permissions   (role, permission)

-- Auth
verification_codes (email PK, code_hash, expires_at)

-- KYC
kyc_documents      (id, user_id UNIQUE, front_key, rear_key, selfie_key,
                    status, submitted_at, reviewed_by, reviewed_at)

-- Compliance
document_configs   (document_type PK, expiry_days, warning_days)  ← seeded with defaults
compliance_documents (id, user_id, document_type, key, issued_at, expires_at,
                      status, expired, submitted_at, reviewed_by, reviewed_at)

-- Banking
bank_accounts      (id, user_id, bank_name, account_number, account_type, holder_name, created_at)
```

### Migration history
| Version | Description |
|---|---|
| `V1__create_auth_tables.sql` | Full initial schema + seeds |
| `V2__kyc_documents_add_review_fields.sql` | `ALTER TABLE kyc_documents ADD COLUMN reviewed_by, reviewed_at` |
| `V3__compliance_documents_add_review_fields.sql` | `ALTER TABLE compliance_documents ADD COLUMN reviewed_by, reviewed_at` |
| `V4__rename_admin_users_to_operators.sql` | `ALTER TABLE admin_users RENAME TO operators; ALTER TABLE operators ADD COLUMN password_hash` |

---

## 9. Repository Pattern

Every table has:
- An **interface** `I{Name}Repository` in the domain layer
- An **in-memory** implementation (active on `local`/`default` profiles)
- A **Postgres** implementation (active on `dev`/`prod` profiles)

```
IUserRepository
  ├── InMemoryUserRepository     @Profile(local, default)
  └── PostgresUserRepository     @Profile(dev, prod)    @Primary
```

All repos have `save()` (create) and `update()` (modify existing) as separate methods.

---

## 10. DDD Patterns Applied

### Rich domain (not anemic)
Domain classes own their behavior. Services ask *"can you do this?"*, objects decide.

```java
// ❌ Anemic — service decides
user.setStatus(BLOCKED);

// ✅ Rich — domain decides + enforces rules
user.block(adminId);     // throws IllegalStateException if already blocked or removed
```

### Immutability via `@Builder(toBuilder = true)`
No `@With` anywhere. State changes only happen through named domain methods which call `toBuilder()` internally. External code cannot set arbitrary fields.

### `IllegalStateException` for domain preconditions
Domain methods throw `IllegalStateException` — no Spring types in the domain layer.  
`GlobalExceptionHandler` maps it to HTTP 409 Conflict:
```java
@ExceptionHandler(IllegalStateException.class)
@ResponseStatus(HttpStatus.CONFLICT)
ErrorResponse handleDomainViolation(IllegalStateException ex) {
    return new ErrorResponse(ex.getMessage());
}
```

### `save()` vs `update()`
- `save()` → creates a new entity
- `update()` → modifies an existing entity

All repositories implement both.

### Domain methods on `User`
```java
user.verifyEmail()           // PENDING_VERIFICATION → PENDING_KYC
user.approveKyc(by)          // PENDING_KYC → ACTIVE
user.rejectKyc(by)
user.complete(by)            // ACTIVE → COMPLETED
user.downgradeToActive()     // COMPLETED → ACTIVE (doc expired)
user.block(adminId)          // → BLOCKED
user.unblock(adminId)        // BLOCKED → PENDING_VERIFICATION
user.remove(reason, by)      // → REMOVED
user.updateProfile(...)
user.addScore(points)
```

### Domain methods on `KycDocument`
```java
KycDocument.create(ids, userId, frontKey, rearKey, selfieKey)  // factory
doc.approve(reviewedBy)    // PENDING_REVIEW → APPROVED; throws if not PENDING_REVIEW
doc.reject(reviewedBy)     // PENDING_REVIEW → REJECTED; throws if not PENDING_REVIEW
doc.resubmit(frontKey, rearKey, selfieKey)  // REJECTED → PENDING_REVIEW; throws if not REJECTED
```

### Domain methods on `ComplianceDocument`
```java
ComplianceDocument.create(...)          // factory
doc.approve(reviewedBy)                 // PENDING_REVIEW → APPROVED
doc.reject(reviewedBy)                  // PENDING_REVIEW → REJECTED
doc.markExpired()                       // sets expired=true
doc.isApprovedAndValid()                // status == APPROVED && !expired
doc.isPendingReview()
```

### Domain methods on `Admin`
```java
Admin.create(ids, fullName, email, role, passwordHash)  // factory
admin.suspend(suspendedBy)
admin.reactivate(reactivatedBy)
admin.changePassword(newHash)
admin.isActive()
admin.canReview()
admin.canManageUsers()
admin.canManageConfig()
admin.canManageAdmins()
```

---

## 11. User Auth Flow (Passwordless OTP)

```
POST /api/auth/register  { email, fullName, roles, phone, country }
  → creates User (PENDING_VERIFICATION) + issues OTP

POST /api/auth/login     { email }
  → re-issues OTP if account exists

POST /api/auth/verify    { email, code }
  → validates OTP
  → if PENDING_VERIFICATION: transitions to PENDING_KYC
  → returns JWT Bearer token
```

JWT payload:
```json
{ "sub": "email", "uid": "user-id", "roles": ["TENANT"], "status": "PENDING_KYC" }
```

All subsequent user requests use `X-User-Id` header (injected by `PactaTokenFilter` from JWT).

`PactaTokenFilter` skips: `/api/auth/**`, `/api/admins/**`, `/api/reviewer/**`

---

## 12. Operator Auth Flow (Email + Password)

```
POST /api/auth/admin/login  { email, password }
  → AdminAuthService validates bcrypt hash
  → checks admin.isActive() — SUSPENDED operators cannot login
  → same generic error "Invalid credentials" for wrong email, wrong password,
    or suspended account (no enumeration)
  → returns AdminAuthResponse { accessToken, tokenType, expiresIn }
```

Operator JWT payload:
```json
{ "sub": "email", "id": "operator-id", "adminRole": "REVIEWER" }
```

`OperatorTokenFilter` protects `/api/admins/**` and `/api/reviewer/**`:
- Reads `X-Operator-Token` header
- Rejects tokens without `adminRole` claim with 403 (user JWTs never carry this claim)
- Injects `X-Operator-Id` (from `id` claim) and `X-Operator-Role` (from `adminRole` claim)

Controllers read reviewer identity from `X-Operator-Id`:
```java
@RequestHeader(OperatorTokenFilter.HEADER_OPERATOR_ID) String reviewerId
```

### Operator Bootstrap (Seeder)
`SuperAdminSeeder` (implements `ApplicationRunner`) runs on startup:
- Reads env vars `PACTA_SUPER_ADMIN_EMAIL` and `PACTA_SUPER_ADMIN_PASSWORD`
- If no SUPER_ADMIN exists → creates one (idempotent)
- If env vars are missing → logs a warning and skips gracefully

After the seed, additional operators are created via `POST /api/admins` authenticated as SUPER_ADMIN.

### Operator Creation Guard
`AdminService.create()` loads the calling operator by `X-Operator-Id` and checks `caller.canManageAdmins()` (SUPER_ADMIN only). Non-SUPER_ADMIN callers receive 403.

Password is hashed with BCrypt before storing; minimum 8 characters enforced at DTO level.

---

## 13. KYC Flow

### First submission
```
POST /api/kyc  (user auth)
  → KycService.submit() — no existing document found
  → KycDocument.create() + kycDocs.save()
  → user.addScore(+20)
```

### Review by operator
```
PATCH /api/reviewer/kyc/{userId}/approve  (operator auth)
  → doc.approve(reviewedBy)   → status APPROVED; reviewedBy + reviewedAt stamped
  → kycDocs.update(doc)
  → user.approveKyc(reviewedBy) — throws if user not found (triggers @Transactional rollback)
  → users.update(user)

PATCH /api/reviewer/kyc/{userId}/reject   (operator auth)
  → doc.reject(reviewedBy)    → status REJECTED; reviewedBy + reviewedAt stamped
  → kycDocs.update(doc)
```

### Resubmission after rejection (Option A — in-place override)
```
POST /api/kyc  (user auth)
  → KycService.submit() — existing document found with status REJECTED
  → doc.resubmit(frontKey, rearKey, selfieKey)
      → status PENDING_REVIEW; submittedAt reset; reviewedBy/reviewedAt cleared
  → kycDocs.update(doc)
  → NO additional score (prevents farming)

Guard: if existing status is PENDING_REVIEW or APPROVED → HTTP 409
```

KYC state machine:
```
(none) ──────────────────────→ PENDING_REVIEW  [first submit, +20 score]
PENDING_REVIEW ───approve()──→ APPROVED
PENDING_REVIEW ───reject()───→ REJECTED
REJECTED ────────resubmit()──→ PENDING_REVIEW  [no extra score]
```

---

## 14. Compliance Document Flow

```
POST /api/compliance  (user auth)
  → creates ComplianceDocument (PENDING_REVIEW)

PATCH /api/reviewer/documents/{docId}/approve  (operator auth)
  → doc.approve(reviewedBy)  → APPROVED; reviewedBy + reviewedAt stamped
  → complianceDocs.update(doc)
  → checks if user now has all 4 approved docs + bank account → triggers user.complete()

PATCH /api/reviewer/documents/{docId}/reject   (operator auth)
  → doc.reject(reviewedBy)   → REJECTED; reviewedBy + reviewedAt stamped
  → complianceDocs.update(doc)
```

---

## 15. Reviewer Workflow

```
GET /api/reviewer/pending  (operator auth)
→ Returns users with pending items, grouped by user:
[
  {
    "user_id": "...",
    "full_name": "...",
    "pending_count": 3,
    "kyc": { "status": "PENDING_REVIEW", ... },
    "documents": [ { "type": "ANTECEDENTES", ... } ]
  }
]
```

`validateReviewer(reviewerId)` loads the operator and delegates to `admin.canReview()`.

---

## 16. Score System

| Milestone | Points |
|---|---|
| KYC submitted (first time only) | +20 |
| Bank account added | +20 |
| Each compliance doc approved | +10 (×4 = +40) |
| Profile completed (all docs + bank) | +10 |
| **Total** | **100** |

Score is NOT awarded on KYC resubmission — prevents score farming.

---

## 17. Filter Chain

| Filter | Header | Protects | Validates | Injects |
|---|---|---|---|---|
| `PactaTokenFilter` | `X-Pacta-Token` | All routes except `/api/auth/**`, `/api/admins/**`, `/api/reviewer/**` | User JWT | `X-User-Id` |
| `OperatorTokenFilter` | `X-Operator-Token` | `/api/admins/**`, `/api/reviewer/**` | Operator JWT (`adminRole` claim present) | `X-Operator-Id`, `X-Operator-Role` |

User tokens on operator routes → 403. Each filter reads a different header — no overlap.

---

## 18. Observability

OTel Java Agent in Dockerfile intercepts everything automatically:
- Logs → Grafana Loki
- Metrics → Grafana Mimir
- Traces disabled (removed per request)

Custom metrics via `MetricRecorder`:
```java
metrics.incrementCounter("kyc.approved");
metrics.incrementCounter("user.blocked");
metrics.incrementCounter("admin.created", "role", role.name());
```

Metric name template in `application.yaml`:
```yaml
pacta.metrics.template: "pacta.%s"
```

---

## 19. Key Files Reference

| File | Purpose |
|---|---|
| `V1__create_auth_tables.sql` | Full DB schema + seeds |
| `V2__kyc_documents_add_review_fields.sql` | KYC review audit columns |
| `V3__compliance_documents_add_review_fields.sql` | Compliance review audit columns |
| `V4__rename_admin_users_to_operators.sql` | Table rename + password_hash column |
| `docker-compose.yml` | Local: postgres + app + nginx |
| `ENV_VARIABLES.txt` | All env vars for deployment |
| `PACTA.postman_collection.json` | All API endpoints |
| `CURLS.sh` | curl reference for all endpoints |
| `DDD.md` | DDD mental model explanation |
| `USER_DOMAIN_DESIGN.md` / `.html` | Full user domain specification |
| `PACTA.html` | Architecture domain overview |

---

## 20. Pending / Next Steps

- [x] ~~Admin auth flow (separate from user OTP flow)~~ — **DONE** (operator email+password, JWT, filter chain, seeder)
- [ ] `property` module — landlord lists properties
- [ ] `application` module — tenant applies to property
- [ ] `visit` module — tenant schedules a visit
- [ ] `lease` module — contract creation and virtual signing
- [ ] `payment` module — monthly rent schedules
- [ ] `review` module — post-lease ratings
- [ ] Document expiry scheduled job (mark expired + notify)
- [ ] Compliance doc re-upload after rejection (same in-place override pattern as KYC resubmit)
- [ ] Score milestone notifications
- [ ] Tighten `SecurityConfig.anyRequest().permitAll()` once route ownership is clear
