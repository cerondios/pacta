CREATE TABLE property_attribute_configs (
    id            VARCHAR PRIMARY KEY,
    property_type VARCHAR NOT NULL,   -- 'APARTMENT' | 'HOUSE' | 'PARKING' | 'ALL'
    category      VARCHAR NOT NULL,   -- 'AMENITY' | 'SERVICE' | 'FEATURE'
    display_name  VARCHAR NOT NULL,
    enabled       BOOLEAN NOT NULL DEFAULT TRUE,
    created_by    VARCHAR NOT NULL,
    created_at    VARCHAR NOT NULL
);

-- Seed: amenities previously hardcoded in the Amenity enum
INSERT INTO property_attribute_configs (id, property_type, category, display_name, enabled, created_by, created_at) VALUES
  (gen_random_uuid(), 'ALL',       'AMENITY', 'Gimnasio',             true, 'system', NOW()::text),
  (gen_random_uuid(), 'ALL',       'AMENITY', 'Piscina',              true, 'system', NOW()::text),
  (gen_random_uuid(), 'ALL',       'AMENITY', 'Ascensor',             true, 'system', NOW()::text),
  (gen_random_uuid(), 'ALL',       'AMENITY', 'Seguridad 24h',        true, 'system', NOW()::text),
  (gen_random_uuid(), 'ALL',       'AMENITY', 'Lavandería',           true, 'system', NOW()::text),
  (gen_random_uuid(), 'ALL',       'AMENITY', 'Terraza / Rooftop',    true, 'system', NOW()::text),
  (gen_random_uuid(), 'ALL',       'AMENITY', 'Coworking',            true, 'system', NOW()::text),
  (gen_random_uuid(), 'ALL',       'AMENITY', 'Zona para mascotas',   true, 'system', NOW()::text),
  (gen_random_uuid(), 'ALL',       'AMENITY', 'Zona de juegos',       true, 'system', NOW()::text),
  (gen_random_uuid(), 'ALL',       'AMENITY', 'Parqueadero cubierto', true, 'system', NOW()::text),
  (gen_random_uuid(), 'ALL',       'AMENITY', 'Amoblado',             true, 'system', NOW()::text);
