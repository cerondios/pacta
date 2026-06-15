-- ═══════════════════════════════════════════════════════════════════════════════
-- PACTA — Consolidated Schema (final state after V1 → V21)
-- Generated from migration files; no ALTER TABLE statements included.
-- ═══════════════════════════════════════════════════════════════════════════════


-- ── AUTH / USERS ──────────────────────────────────────────────────────────────

-- V1 + V5 (added city)
CREATE TABLE users (
    id               VARCHAR PRIMARY KEY,
    full_name        VARCHAR,
    email            VARCHAR UNIQUE,
    phone_indicative VARCHAR,
    phone_number     VARCHAR,
    country          VARCHAR,
    status           VARCHAR,
    score            INTEGER,
    created_at       VARCHAR,
    updated_at       VARCHAR,
    updated_by       VARCHAR,
    removal_reason   VARCHAR,
    removed_at       VARCHAR,
    removed_by       VARCHAR,
    city             VARCHAR
);

-- V1 (unchanged)
CREATE TABLE user_roles (
    user_id VARCHAR NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role    VARCHAR NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- V1 created as admin_users, renamed to operators (V4) + password_hash added (V4)
CREATE TABLE operators (
    id            VARCHAR PRIMARY KEY,
    full_name     VARCHAR NOT NULL,
    email         VARCHAR NOT NULL UNIQUE,
    role          VARCHAR NOT NULL,
    status        VARCHAR NOT NULL,
    created_at    VARCHAR NOT NULL,
    updated_at    VARCHAR,
    password_hash VARCHAR
);

-- V1 (unchanged)
CREATE TABLE permissions (
    id VARCHAR PRIMARY KEY
);

-- V1 (unchanged)
CREATE TABLE role_permissions (
    role       VARCHAR,
    permission VARCHAR,
    PRIMARY KEY (role, permission)
);

-- V1 (unchanged)
CREATE TABLE verification_codes (
    email      VARCHAR PRIMARY KEY,
    code_hash  VARCHAR,
    expires_at VARCHAR
);


-- ── KYC / COMPLIANCE DOCUMENTS ───────────────────────────────────────────────

-- V1 + V2 (added reviewed_by, reviewed_at)
CREATE TABLE kyc_documents (
    id           VARCHAR PRIMARY KEY,
    user_id      VARCHAR UNIQUE,
    front_key    VARCHAR,
    rear_key     VARCHAR,
    selfie_key   VARCHAR,
    status       VARCHAR,
    submitted_at VARCHAR,
    reviewed_by  VARCHAR,
    reviewed_at  VARCHAR
);

-- V1 dropped and replaced by V6 (country-aware registry)
CREATE TABLE document_configs (
    id           VARCHAR PRIMARY KEY,
    country_code VARCHAR NOT NULL,
    type_code    VARCHAR NOT NULL,
    display_name VARCHAR NOT NULL,
    UNIQUE (country_code, type_code)
);

-- V1 + V3 (added reviewed_by, reviewed_at)
CREATE TABLE compliance_documents (
    id            VARCHAR PRIMARY KEY,
    user_id       VARCHAR,
    document_type VARCHAR,
    key           VARCHAR,
    issued_at     VARCHAR,
    expires_at    VARCHAR,
    status        VARCHAR,
    expired       BOOLEAN,
    submitted_at  VARCHAR,
    reviewed_by   VARCHAR,
    reviewed_at   VARCHAR
);


-- ── BANK ACCOUNTS ─────────────────────────────────────────────────────────────

-- V1 (unchanged)
CREATE TABLE bank_accounts (
    id             VARCHAR PRIMARY KEY,
    user_id        VARCHAR,
    bank_name      VARCHAR,
    account_number VARCHAR,
    account_type   VARCHAR,
    holder_name    VARCHAR,
    created_at     VARCHAR
);


-- ── PROPERTIES ───────────────────────────────────────────────────────────────

-- V7 base
-- V8:  area_m2 → area (DOUBLE PRECISION), monthly_rent_cents → monthly_rent,
--      admin_fee_cents → admin_fee; added name, neighborhood, area_unit, currency
-- V10: name → title, added address, dropped bank_account_id, dropped stratum
-- V11: added purpose
-- V12: added country
-- V14: photo_urls → photo_keys
-- V15: floor → floors, dropped stratum (IF EXISTS), dropped previous_status
CREATE TABLE properties (
    id                  VARCHAR PRIMARY KEY,
    landlord_id         VARCHAR NOT NULL,
    city                VARCHAR,
    type                VARCHAR,
    area                DOUBLE PRECISION,
    bedrooms            INTEGER,
    bathrooms           INTEGER,
    floors              INTEGER,
    parking_spots       INTEGER,
    amenities           TEXT,
    photo_keys          TEXT,
    description         TEXT,
    monthly_rent        BIGINT,
    admin_fee           BIGINT,
    min_contract_months INTEGER,
    allows_pets         BOOLEAN NOT NULL,
    allows_smokers      BOOLEAN NOT NULL,
    allows_children     BOOLEAN NOT NULL,
    status              VARCHAR NOT NULL,
    created_at          VARCHAR NOT NULL,
    updated_at          VARCHAR,
    updated_by          VARCHAR,
    title               VARCHAR,
    neighborhood        VARCHAR,
    area_unit           VARCHAR,
    currency            VARCHAR,
    address             VARCHAR,
    purpose             VARCHAR NOT NULL,
    country             VARCHAR(100)
);

-- V9 (unchanged; V13 and V17 only affect data)
CREATE TABLE property_attribute_configs (
    id            VARCHAR PRIMARY KEY,
    property_type VARCHAR NOT NULL,
    category      VARCHAR NOT NULL,
    display_name  VARCHAR NOT NULL,
    enabled       BOOLEAN NOT NULL,
    created_by    VARCHAR NOT NULL,
    created_at    VARCHAR NOT NULL
);

-- V16 (unchanged)
CREATE TABLE property_requests (
    id          VARCHAR PRIMARY KEY,
    tenant_id   VARCHAR NOT NULL,
    property_id VARCHAR NOT NULL,
    status      VARCHAR NOT NULL,
    applied_at  VARCHAR NOT NULL,
    reviewed_at VARCHAR,
    reviewed_by VARCHAR,
    UNIQUE (tenant_id, property_id)
);


-- ── PRICING ───────────────────────────────────────────────────────────────────

-- V18 (unchanged)
CREATE TABLE pricing_configs (
    id                    VARCHAR PRIMARY KEY,
    country_code          VARCHAR NOT NULL UNIQUE,
    commission_percentage VARCHAR NOT NULL,
    created_at            VARCHAR NOT NULL,
    updated_at            VARCHAR
);


-- ── PAYMENTS ─────────────────────────────────────────────────────────────────

-- V19 + V20 (wompi_transaction_id → gateway_transaction_id)
CREATE TABLE payments (
    id                     VARCHAR PRIMARY KEY,
    tenant_id              VARCHAR NOT NULL,
    property_request_id    VARCHAR NOT NULL,
    amount_in_cents        BIGINT NOT NULL,
    currency               VARCHAR NOT NULL,
    status                 VARCHAR NOT NULL,
    reference              VARCHAR NOT NULL UNIQUE,
    gateway_transaction_id VARCHAR,
    redirect_url           VARCHAR,
    created_at             VARCHAR NOT NULL,
    updated_at             VARCHAR
);


-- ── DEALS ─────────────────────────────────────────────────────────────────────

-- V21 (unchanged)
CREATE TABLE deals (
    id                      VARCHAR PRIMARY KEY,
    property_request_id     VARCHAR NOT NULL UNIQUE,
    property_id             VARCHAR NOT NULL,
    landlord_id             VARCHAR NOT NULL,
    tenant_id               VARCHAR NOT NULL,
    status                  VARCHAR NOT NULL,
    contract_file_key       VARCHAR NOT NULL,
    landlord_signed_at      VARCHAR,
    landlord_signature_name VARCHAR,
    tenant_signed_at        VARCHAR,
    tenant_signature_name   VARCHAR,
    created_at              VARCHAR NOT NULL,
    updated_at              VARCHAR
);
