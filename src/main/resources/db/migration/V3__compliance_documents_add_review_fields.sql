-- ── Compliance Documents: add reviewer audit fields ──────────────────────────

ALTER TABLE compliance_documents
    ADD COLUMN IF NOT EXISTS reviewed_by VARCHAR,
    ADD COLUMN IF NOT EXISTS reviewed_at VARCHAR;
