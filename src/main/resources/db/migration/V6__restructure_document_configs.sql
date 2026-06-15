-- Drop the old enum-keyed table and replace with a country-aware registry

DROP TABLE IF EXISTS document_configs;

CREATE TABLE document_configs (
    id           VARCHAR PRIMARY KEY,
    country_code VARCHAR NOT NULL,
    type_code    VARCHAR NOT NULL,
    display_name VARCHAR NOT NULL,
    UNIQUE (country_code, type_code)
);

INSERT INTO document_configs (id, country_code, type_code, display_name) VALUES
    ('co-antecedentes',  'CO', 'ANTECEDENTES',        'Certificado de antecedentes'),
    ('co-sanciones',     'CO', 'SANCIONES',            'Certificado de sanciones'),
    ('co-inhabilidades', 'CO', 'INHABILIDADES',        'Certificado de inhabilidades'),
    ('co-historial',     'CO', 'HISTORIAL_CREDITICIO', 'Historial crediticio');
