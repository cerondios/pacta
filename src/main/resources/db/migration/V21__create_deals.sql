CREATE TABLE deals (
    id                      VARCHAR PRIMARY KEY,
    property_request_id    VARCHAR NOT NULL UNIQUE,
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
