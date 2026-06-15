-- Rename unit-contaminated columns to unit-agnostic names
ALTER TABLE properties RENAME COLUMN area_m2             TO area;
ALTER TABLE properties RENAME COLUMN monthly_rent_cents  TO monthly_rent;
ALTER TABLE properties RENAME COLUMN admin_fee_cents     TO admin_fee;

-- Change area to DOUBLE PRECISION to support ft² → m² conversions
ALTER TABLE properties ALTER COLUMN area TYPE DOUBLE PRECISION;

-- Add missing columns
ALTER TABLE properties ADD COLUMN IF NOT EXISTS name         VARCHAR;
ALTER TABLE properties ADD COLUMN IF NOT EXISTS neighborhood VARCHAR;
ALTER TABLE properties ADD COLUMN IF NOT EXISTS area_unit    VARCHAR;
ALTER TABLE properties ADD COLUMN IF NOT EXISTS currency     VARCHAR;
