CREATE TABLE pricing_configs (
    id                    VARCHAR PRIMARY KEY,
    country_code          VARCHAR NOT NULL UNIQUE,
    commission_percentage VARCHAR NOT NULL,
    created_at            VARCHAR NOT NULL,
    updated_at            VARCHAR
);
