-- ── properties table ──────────────────────────────────────────────────────────
ALTER TABLE properties RENAME COLUMN floor TO floors;
ALTER TABLE properties DROP COLUMN IF EXISTS stratum;
ALTER TABLE properties DROP COLUMN IF EXISTS previous_status;
