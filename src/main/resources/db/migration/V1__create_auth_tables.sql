-- ─────────────────────────────────────────────────────────────────────────────
-- PACTA — Initial Schema
-- ─────────────────────────────────────────────────────────────────────────────

-- ── Users ────────────────────────────────────────────────────────────────────

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
    removed_by       VARCHAR
);

CREATE TABLE user_roles (
    user_id VARCHAR NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role    VARCHAR NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- ── Admin users (internal operators: ADMIN, SUPER_ADMIN, REVIEWER) ──────────

CREATE TABLE admin_users (
    id         VARCHAR PRIMARY KEY,
    full_name  VARCHAR NOT NULL,
    email      VARCHAR NOT NULL UNIQUE,
    role       VARCHAR NOT NULL,
    status     VARCHAR NOT NULL DEFAULT 'ACTIVE',
    created_at VARCHAR NOT NULL,
    updated_at VARCHAR
);

-- ── Permissions & RBAC ───────────────────────────────────────────────────────

CREATE TABLE permissions (
    id VARCHAR PRIMARY KEY
);

CREATE TABLE role_permissions (
    role       VARCHAR,
    permission VARCHAR,
    PRIMARY KEY (role, permission)
);

-- Seed permissions
INSERT INTO permissions (id) VALUES
    ('user:read'),
    ('user:block'),
    ('user:remove'),
    ('user:restore'),
    ('document:approve'),
    ('document:reject'),
    ('config:read'),
    ('config:write'),
    ('bank-account:read'),
    ('role:read'),
    ('role:write'),
    ('property:write'),
    ('property:delete'),
    ('property:moderate');

-- Seed role → permission mapping
INSERT INTO role_permissions (role, permission) VALUES
    -- SUPER_ADMIN: all permissions
    ('SUPER_ADMIN', 'user:read'),
    ('SUPER_ADMIN', 'user:block'),
    ('SUPER_ADMIN', 'user:remove'),
    ('SUPER_ADMIN', 'user:restore'),
    ('SUPER_ADMIN', 'document:approve'),
    ('SUPER_ADMIN', 'document:reject'),
    ('SUPER_ADMIN', 'config:read'),
    ('SUPER_ADMIN', 'config:write'),
    ('SUPER_ADMIN', 'bank-account:read'),
    ('SUPER_ADMIN', 'role:read'),
    ('SUPER_ADMIN', 'role:write'),
    ('SUPER_ADMIN', 'property:write'),
    ('SUPER_ADMIN', 'property:delete'),
    ('SUPER_ADMIN', 'property:moderate'),
    -- ADMIN
    ('ADMIN', 'user:read'),
    ('ADMIN', 'user:block'),
    ('ADMIN', 'user:remove'),
    ('ADMIN', 'user:restore'),
    ('ADMIN', 'document:approve'),
    ('ADMIN', 'document:reject'),
    ('ADMIN', 'config:read'),
    ('ADMIN', 'config:write'),
    ('ADMIN', 'bank-account:read'),
    ('ADMIN', 'property:write'),
    ('ADMIN', 'property:delete'),
    ('ADMIN', 'property:moderate'),
    -- REVIEWER
    ('REVIEWER', 'user:read'),
    ('REVIEWER', 'document:approve'),
    ('REVIEWER', 'document:reject'),
    ('REVIEWER', 'bank-account:read'),
    -- LANDLORD
    ('LANDLORD', 'property:write'),
    ('LANDLORD', 'property:delete');

-- ── Verification codes (OTP) ─────────────────────────────────────────────────

CREATE TABLE verification_codes (
    email      VARCHAR PRIMARY KEY,
    code_hash  VARCHAR,
    expires_at VARCHAR
);

-- ── KYC Documents ────────────────────────────────────────────────────────────

CREATE TABLE kyc_documents (
    id           VARCHAR PRIMARY KEY,
    user_id      VARCHAR UNIQUE,
    front_key    VARCHAR,
    rear_key     VARCHAR,
    selfie_key   VARCHAR,
    status       VARCHAR,
    submitted_at VARCHAR
);

-- ── Document Configs (admin-configurable per type) ───────────────────────────

CREATE TABLE document_configs (
    document_type VARCHAR PRIMARY KEY,
    expiry_days   INTEGER,
    warning_days  INTEGER
);

-- Default config for each compliance document type
INSERT INTO document_configs (document_type, expiry_days, warning_days) VALUES
    ('ANTECEDENTES',         90,  15),
    ('SANCIONES',            90,  15),
    ('INHABILIDADES',        90,  15),
    ('HISTORIAL_CREDITICIO', 180, 30);

-- ── Compliance Documents ─────────────────────────────────────────────────────

CREATE TABLE compliance_documents (
    id            VARCHAR PRIMARY KEY,
    user_id       VARCHAR,
    document_type VARCHAR,
    key           VARCHAR,
    issued_at     VARCHAR,
    expires_at    VARCHAR,
    status        VARCHAR,
    expired       BOOLEAN,
    submitted_at  VARCHAR
);

-- ── Bank Accounts ─────────────────────────────────────────────────────────────

CREATE TABLE bank_accounts (
    id             VARCHAR PRIMARY KEY,
    user_id        VARCHAR,
    bank_name      VARCHAR,
    account_number VARCHAR,
    account_type   VARCHAR,
    holder_name    VARCHAR,
    created_at     VARCHAR
);
