 -- ── Rename admin_users → operators ───────────────────────────────────────────

ALTER TABLE admin_users RENAME TO operators;

-- ── Add password credential ───────────────────────────────────────────────────
-- Nullable initially; the SuperAdminSeeder sets the first one at boot time.
-- All subsequent operators are created via the API which requires a password.

ALTER TABLE operators
    ADD COLUMN IF NOT EXISTS password_hash VARCHAR;
