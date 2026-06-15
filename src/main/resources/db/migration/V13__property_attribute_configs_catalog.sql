
-- ─── ALL types ────────────────────────────────────────────────────────────────
INSERT INTO property_attribute_configs (id, property_type, category, display_name, enabled, created_by, created_at) VALUES
  (gen_random_uuid(), 'ALL', 'SERVICE', 'Internet incluido',      true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ALL', 'SERVICE', 'Agua incluida',          true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ALL', 'SERVICE', 'Luz incluida',           true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ALL', 'SERVICE', 'Gas incluido',           true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ALL', 'SERVICE', 'Vigilancia 24h',         true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ALL', 'AMENITY', 'Ascensor',               true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ALL', 'AMENITY', 'Parqueadero cubierto',   true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ALL', 'AMENITY', 'Parqueadero bicicletas', true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000');

-- ─── APARTMENT ────────────────────────────────────────────────────────────────
INSERT INTO property_attribute_configs (id, property_type, category, display_name, enabled, created_by, created_at) VALUES
  (gen_random_uuid(), 'APARTMENT', 'FEATURE', 'Balcón',             true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'FEATURE', 'Cocina integral',    true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'FEATURE', 'Calentador',         true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'FEATURE', 'Depósito',           true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'FEATURE', 'Cuarto de servicio', true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'FEATURE', 'Vista panorámica',   true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'FEATURE', 'Amoblado',           true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'AMENITY', 'Piscina',            true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'AMENITY', 'Gimnasio',           true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'AMENITY', 'Salón comunal',      true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'AMENITY', 'Terraza / Rooftop',  true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'AMENITY', 'Zona BBQ',           true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'AMENITY', 'Zona de juegos',     true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'AMENITY', 'Zona para mascotas', true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'AMENITY', 'Coworking',          true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'AMENITY', 'Portería',           true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'APARTMENT', 'SERVICE', 'Servicio de limpieza', true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000');

-- ─── HOUSE ────────────────────────────────────────────────────────────────────
INSERT INTO property_attribute_configs (id, property_type, category, display_name, enabled, created_by, created_at) VALUES
  (gen_random_uuid(), 'HOUSE', 'FEATURE', 'Jardín',             true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'HOUSE', 'FEATURE', 'Garaje',             true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'HOUSE', 'FEATURE', 'Balcón',             true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'HOUSE', 'FEATURE', 'Cocina integral',    true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'HOUSE', 'FEATURE', 'Calentador',         true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'HOUSE', 'FEATURE', 'Depósito',           true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'HOUSE', 'FEATURE', 'Cuarto de servicio', true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'HOUSE', 'FEATURE', 'Amoblada',           true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'HOUSE', 'AMENITY', 'Piscina',            true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'HOUSE', 'AMENITY', 'Zona de lavado',     true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'HOUSE', 'AMENITY', 'Zona BBQ',           true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'HOUSE', 'AMENITY', 'Terraza',            true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'HOUSE', 'AMENITY', 'Zona para mascotas', true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'HOUSE', 'SERVICE', 'Servicio de limpieza', true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000');

-- ─── ROOM ─────────────────────────────────────────────────────────────────────
INSERT INTO property_attribute_configs (id, property_type, category, display_name, enabled, created_by, created_at) VALUES
  (gen_random_uuid(), 'ROOM', 'FEATURE', 'Baño privado',               true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'FEATURE', 'Baño compartido',            true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'FEATURE', 'Cama doble',                 true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'FEATURE', 'Cama sencilla',              true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'FEATURE', 'Closet',                     true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'FEATURE', 'Escritorio',                 true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'FEATURE', 'Amoblada',                   true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'FEATURE', 'Balcón',                     true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'FEATURE', 'Ventana exterior',           true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'FEATURE', 'Aire acondicionado',         true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'FEATURE', 'Ventilador',                 true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'AMENITY', 'Cocina compartida',          true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'AMENITY', 'Sala compartida',            true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'AMENITY', 'Zona de estudio',            true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'AMENITY', 'Lavandería',                 true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'AMENITY', 'Terraza',                    true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'SERVICE', 'Servicios públicos incluidos', true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000'),
  (gen_random_uuid(), 'ROOM', 'SERVICE', 'Servicio de limpieza',        true, 'ba2abacc-0e18-4207-9b7c-d36a4f5aad73', '2026-06-17T14:17:30.948+0000');
