-- V9 and V13 both seeded ('ALL', 'AMENITY', 'Ascensor') and
-- ('ALL', 'AMENITY', 'Parqueadero cubierto'), creating duplicate rows with
-- different ids. This generically dedupes by (property_type, category,
-- display_name), keeping the oldest row per group as canonical.
--
-- properties.amenities stores config ids (pipe-separated text), so any
-- property already referencing a duplicate's id is repointed to the
-- canonical id before the duplicate row is deleted.

DO $$
DECLARE
    dup RECORD;
BEGIN
    FOR dup IN
        SELECT r.id AS dup_id, c.canonical_id
        FROM (
            SELECT id, property_type, category, display_name,
                   ROW_NUMBER() OVER (
                       PARTITION BY property_type, category, display_name
                       ORDER BY created_at ASC, id ASC
                   ) AS rn
            FROM property_attribute_configs
        ) r
        JOIN (
            SELECT id AS canonical_id, property_type, category, display_name
            FROM (
                SELECT id, property_type, category, display_name,
                       ROW_NUMBER() OVER (
                           PARTITION BY property_type, category, display_name
                           ORDER BY created_at ASC, id ASC
                       ) AS rn
                FROM property_attribute_configs
            ) ranked
            WHERE rn = 1
        ) c
        ON r.property_type = c.property_type
       AND r.category      = c.category
       AND r.display_name  = c.display_name
        WHERE r.rn > 1
    LOOP
        UPDATE properties
        SET amenities = REPLACE(amenities, dup.dup_id, dup.canonical_id)
        WHERE amenities LIKE '%' || dup.dup_id || '%';

        DELETE FROM property_attribute_configs WHERE id = dup.dup_id;
    END LOOP;
END $$;
