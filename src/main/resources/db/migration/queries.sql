-- ─────────────────────────────────────────────────────────────────────────────
-- 1. All existing tables
-- ─────────────────────────────────────────────────────────────────────────────
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_type = 'BASE TABLE'
ORDER BY table_name;


-- ─────────────────────────────────────────────────────────────────────────────
-- 2. Structure of each table
-- ─────────────────────────────────────────────────────────────────────────────
SELECT column_name, data_type, character_maximum_length, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'users'
ORDER BY ordinal_position;

SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'user_roles'
ORDER BY ordinal_position;

SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'permissions'
ORDER BY ordinal_position;

SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'role_permissions'
ORDER BY ordinal_position;

SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'verification_codes'
ORDER BY ordinal_position;

SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'kyc_documents'
ORDER BY ordinal_position;

SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'document_configs'
ORDER BY ordinal_position;

SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'compliance_documents'
ORDER BY ordinal_position;

SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'bank_accounts'
ORDER BY ordinal_position;


-- ─────────────────────────────────────────────────────────────────────────────
-- 3. All items per table
-- ─────────────────────────────────────────────────────────────────────────────
SELECT * FROM users;
SELECT * FROM user_roles;
SELECT * FROM permissions;
SELECT * FROM role_permissions;
SELECT * FROM verification_codes;
SELECT * FROM kyc_documents;
SELECT * FROM document_configs;
SELECT * FROM compliance_documents;
SELECT * FROM bank_accounts;
SELECT * FROM properties;
SELECT * FROM operators;

SELECT * FROM property_attribute_configs;
DELETE FROM property_attribute_configs where property_type = 'ALL';
SELECT * FROM users
LEFT JOIN user_roles ON users.id = user_roles.user_id;

DELETE FROM users;
DELETE FROM operators;
DELETE FROM user_roles;
DELETE FROM properties;
DELETE FROM kyc_documents;
DELETE FROM compliance_documents;
DELETE FROM verification_codes;
DELETE FROM bank_accounts;
DELETE FROM document_configs;
DELETE FROM permissions;
DELETE FROM property_requests;
DELETE FROM role_permissions;
DELETE FROM property_attribute_configs;
DELETE FROM payments;
DELETE FROM deals;


UPDATE users
SET status = 'COMPLETED', updated_at = NOW()
WHERE id = 'dd526aae-1f72-43e5-b4ce-030d75cc9f39';


DROP TABLE IF EXISTS properties;
DROP TABLE IF EXISTS compliance_documents;
DROP TABLE IF EXISTS kyc_documents;
DROP TABLE IF EXISTS bank_accounts;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS role_permissions;
DROP TABLE IF EXISTS verification_codes;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS permissions;
DROP TABLE IF EXISTS document_configs CASCADE;