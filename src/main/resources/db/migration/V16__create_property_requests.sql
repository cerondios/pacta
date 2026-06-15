CREATE TABLE property_requests (
    id          VARCHAR PRIMARY KEY,
    tenant_id   VARCHAR NOT NULL,
    property_id VARCHAR NOT NULL,
    status      VARCHAR NOT NULL DEFAULT 'PENDING',
    applied_at  VARCHAR NOT NULL,
    reviewed_at VARCHAR,
    reviewed_by VARCHAR,
    UNIQUE (tenant_id, property_id)
);
