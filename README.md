# User Domain Design

---

## 1. Roles & Authorization

### 1.1 Roles

| Role | Type | Description |
|------|------|-------------|
| `SUPER_ADMIN` | Admin | Full access, manages roles and permissions |
| `ADMIN` | Admin | Manages users, documents, configs, properties |
| `REVIEWER` | Admin | Reviews and approves/rejects documents |
| `LANDLORD` | End user | Lists properties for rent |
| `TENANT` | End user | Searches and rents properties |

- A user can hold multiple roles (e.g. `TENANT` + `LANDLORD`)
- `TENANT` and `LANDLORD` go through the same onboarding flow
- Public property listing requires no authentication

### 1.2 Permissions

| Permission | Description |
|-----------|-------------|
| `user:read` | View any user's full profile |
| `user:block` | Block / unblock a user |
| `user:remove` | Remove a user |
| `user:restore` | Restore a removed user within 30 days |
| `document:approve` | Approve a KYC or compliance document |
| `document:reject` | Reject a KYC or compliance document |
| `config:read` | View document expiry configs |
| `config:write` | Create / update document expiry configs |
| `bank-account:read` | View any user's bank accounts |
| `role:read` | View roles and their permissions |
| `role:write` | Assign / remove permissions from roles |
| `property:write` | Create / update properties (own for LANDLORD, any for ADMIN) |
| `property:delete` | Delete properties (own for LANDLORD, any for ADMIN) |
| `property:moderate` | Hide, feature or flag any property |

### 1.3 Role → Permission Mapping

| Permission | SUPER_ADMIN | ADMIN | REVIEWER | LANDLORD | TENANT |
|-----------|:-----------:|:-----:|:--------:|:--------:|:------:|
| `user:read` | ✓ | ✓ | ✓ | — | — |
| `user:block` | ✓ | ✓ | — | — | — |
| `user:remove` | ✓ | ✓ | — | — | — |
| `user:restore` | ✓ | ✓ | — | — | — |
| `document:approve` | ✓ | ✓ | ✓ | — | — |
| `document:reject` | ✓ | ✓ | ✓ | — | — |
| `config:read` | ✓ | ✓ | — | — | — |
| `config:write` | ✓ | ✓ | — | — | — |
| `bank-account:read` | ✓ | ✓ | ✓ | — | — |
| `role:read` | ✓ | — | — | — | — |
| `role:write` | ✓ | — | — | — | — |
| `property:write` | ✓ | ✓ | — | ✓ | — |
| `property:delete` | ✓ | ✓ | — | ✓ | — |
| `property:moderate` | ✓ | ✓ | — | — | — |

### 1.4 How Permissions Are Loaded

Permissions are stored in DB (`roles`, `permissions`, `role_permissions` tables) and embedded in the JWT at login time. No DB query on every request.

JWT payload:
```json
{
  "sub": "user-uuid",
  "roles": ["TENANT", "LANDLORD"],
  "permissions": ["property:write", "property:delete"],
  "status": "ACTIVE",
  "iat": 1748736000,
  "exp": 1748739600
}
```

- If an admin changes permissions, the user gets the new ones on their next login
- Staleness is controlled by `JWT_TTL_SECONDS` (keep short, e.g. 1 hour)
- User blocking is enforced separately via `status` check on every request

---

## 2. User

### 2.1 Status Lifecycle

```
PENDING_VERIFICATION → PENDING_KYC → [admin approves KYC] → ACTIVE ⇄ COMPLETED
                                             ↑                    ↓
                                        REJECTED             BLOCKED (admin only)
                                        [re-upload]               ↓
                                                        PENDING_VERIFICATION (on unblock)

ANY state → REMOVED (soft delete, restorable within 30 days)
```

### 2.2 Transition Rules

| From | To | Trigger |
|------|----|---------|
| PENDING_VERIFICATION | PENDING_KYC | Email code verified |
| PENDING_KYC | ACTIVE | Admin approves KYC |
| PENDING_KYC | PENDING_KYC | Admin rejects KYC → user re-uploads |
| ACTIVE | COMPLETED | All compliance docs APPROVED + at least one bank account |
| COMPLETED | ACTIVE | Any compliance document expires |
| ACTIVE / COMPLETED | BLOCKED | Admin action only (reversible) |
| BLOCKED | PENDING_VERIFICATION | Admin unblocks (user restarts full onboarding) |
| ACTIVE / COMPLETED | REMOVED | Admin action OR user self-deletion |
| REMOVED | last state | Restored by admin within 30 days only |

- Users can only self-delete from `ACTIVE` or `COMPLETED`
- Mid-onboarding abandonment: account auto-expires, no self-delete needed

### 2.3 User Fields

```json
{
  "id": "uuid",
  "fullName": "John Doe",
  "email": "john@example.com",
  "phone": {
    "indicative": "+57",
    "number": "3001234567"
  },
  "country": "CO",
  "roles": ["TENANT", "LANDLORD"],
  "status": "ACTIVE",
  "score": 50,
  "createdAt": "2026-06-01T10:00:00Z",
  "updatedAt": "2026-06-05T10:00:00Z",
  "updatedBy": "uuid",
  "removalReason": null,
  "removedAt": null,
  "removedBy": null
}
```

---

## 3. Documents

### 3.1 Document State Machine

Applies to both KYC and compliance documents:

```
PENDING_REVIEW → APPROVED
     ↑               ↓ (compliance only)
REJECTED          EXPIRED
```

### 3.2 KYC Document

```json
{
  "frontKey": "s3-key/front.jpg",
  "rearKey": "s3-key/rear.jpg",
  "selfieKey": "s3-key/selfie.jpg",
  "status": "PENDING_REVIEW",
  "submittedAt": "2026-06-01T10:05:00Z"
}
```

- No expiry
- Admin approves/rejects
- Approval moves user `PENDING_KYC → ACTIVE`

### 3.3 Compliance Documents

Array — each document has its own state and type:

```json
[
  {
    "id": "uuid",
    "type": "ANTECEDENTES",
    "key": "s3-key/antecedentes.pdf",
    "issuedAt": "2026-01-01T00:00:00Z",
    "expiresAt": "2026-04-01T00:00:00Z",
    "status": "APPROVED",
    "expired": false
  }
]
```

**Document types:** `ANTECEDENTES`, `SANCIONES`, `INHABILIDADES`, `HISTORIAL_CREDITICIO`

- `expiresAt` = `issuedAt` + `expiryDays` (admin-configured per type)
- When expired: `status → EXPIRED`, user drops `COMPLETED → ACTIVE`, email sent
- Frontend alert shown `warningDays` before expiry (admin-configured per type)
- All 4 documents must be `APPROVED` + at least one bank account to reach `COMPLETED`

---

## 4. Bank Accounts

Array per user — no primary account. User can add and remove.

```json
{
  "id": "uuid",
  "bankName": "Bancolombia",
  "accountNumber": "****1234",
  "accountType": "SAVINGS",
  "holderName": "John Doe"
}
```

- `accountType`: `SAVINGS` or `CHECKING`
- `accountNumber` masked in responses — last 4 digits only

---

## 5. Score Milestones

| Milestone | Points |
|-----------|--------|
| Email verified | +10 |
| KYC submitted | +20 |
| Bank info added | +20 |
| Antecedentes approved | +10 |
| Sanciones approved | +10 |
| Inhabilidades approved | +10 |
| Historial crediticio approved | +10 |
| Profile completed | +10 |
| **Total** | **100** |

---

## 6. Notifications

| Event | Email | Frontend alert |
|-------|-------|----------------|
| Document rejected | ✓ | ✓ |
| Document expiring soon | — | ✓ (`warningDays` before, admin config) |
| Document expired | ✓ | ✓ |

---

## 7. Admin Configurable Settings

Per document type:

```json
{
  "documentType": "ANTECEDENTES",
  "expiryDays": 90,
  "warningDays": 15
}
```

---

## 8. Full User Profile Response

```json
{
  "id": "uuid",
  "fullName": "John Doe",
  "email": "john@example.com",
  "phone": {
    "indicative": "+57",
    "number": "3001234567"
  },
  "country": "CO",
  "roles": ["TENANT", "LANDLORD"],
  "status": "COMPLETED",
  "score": 100,
  "createdAt": "2026-06-01T10:00:00Z",
  "updatedAt": "2026-06-05T10:00:00Z",
  "updatedBy": "uuid",
  "removalReason": null,
  "removedAt": null,
  "removedBy": null,
  "kyc": {
    "frontKey": "s3-key/front.jpg",
    "rearKey": "s3-key/rear.jpg",
    "selfieKey": "s3-key/selfie.jpg",
    "status": "APPROVED",
    "submittedAt": "2026-06-01T10:05:00Z"
  },
  "compliance": [
    {
      "id": "uuid",
      "type": "ANTECEDENTES",
      "key": "s3-key/antecedentes.pdf",
      "issuedAt": "2026-01-01T00:00:00Z",
      "expiresAt": "2026-04-01T00:00:00Z",
      "status": "APPROVED",
      "expired": false
    },
    {
      "id": "uuid",
      "type": "SANCIONES",
      "key": "s3-key/sanciones.pdf",
      "issuedAt": "2026-01-01T00:00:00Z",
      "expiresAt": "2026-04-01T00:00:00Z",
      "status": "APPROVED",
      "expired": false
    },
    {
      "id": "uuid",
      "type": "INHABILIDADES",
      "key": "s3-key/inhabilidades.pdf",
      "issuedAt": "2026-01-01T00:00:00Z",
      "expiresAt": "2026-04-01T00:00:00Z",
      "status": "APPROVED",
      "expired": false
    },
    {
      "id": "uuid",
      "type": "HISTORIAL_CREDITICIO",
      "key": "s3-key/historial.pdf",
      "issuedAt": "2026-01-01T00:00:00Z",
      "expiresAt": "2026-07-01T00:00:00Z",
      "status": "APPROVED",
      "expired": false
    }
  ],
  "bankAccounts": [
    {
      "id": "uuid",
      "bankName": "Bancolombia",
      "accountNumber": "****1234",
      "accountType": "SAVINGS",
      "holderName": "John Doe"
    }
  ]
}
```
