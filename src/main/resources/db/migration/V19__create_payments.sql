CREATE TABLE payments (
    id                  VARCHAR PRIMARY KEY,
    tenant_id           VARCHAR NOT NULL,
    property_request_id VARCHAR NOT NULL,
    amount_in_cents     BIGINT NOT NULL,
    currency            VARCHAR NOT NULL,
    status              VARCHAR NOT NULL,
    reference           VARCHAR NOT NULL UNIQUE,
    wompi_transaction_id VARCHAR,
    redirect_url        VARCHAR,
    created_at          VARCHAR NOT NULL,
    updated_at          VARCHAR
);
