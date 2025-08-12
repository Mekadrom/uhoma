DO $$
DECLARE
    c_schema_name VARCHAR(32) := 'hams_data';
    c_table_name VARCHAR(32) := 'node';
    c_sequence_name VARCHAR(32) := 'sq_node';

    v_table_exists BOOLEAN;
    v_sequence_exists BOOLEAN;
BEGIN
    SELECT EXISTS(
        SELECT
          FROM INFORMATION_SCHEMA.TABLES
         WHERE TABLE_SCHEMA = c_schema_name
           AND TABLE_NAME = c_table_name
    ) INTO v_table_exists;

    SELECT EXISTS(
        SELECT
          FROM INFORMATION_SCHEMA.SEQUENCES
         WHERE SEQUENCE_SCHEMA = c_schema_name
           AND SEQUENCE_NAME = c_sequence_name
    ) INTO v_sequence_exists;

    IF NOT v_sequence_exists THEN
        EXECUTE 'CREATE SEQUENCE ' || c_sequence_name || ' START WITH 1 INCREMENT BY 50;';
    END IF;

    IF NOT v_table_exists THEN
        EXECUTE 'CREATE TABLE ' || c_table_name || ' (
            node_seq BIGINT NOT NULL DEFAULT nextval(''' || c_schema_name || '.' || c_sequence_name || '''),
            room_seq BIGINT NOT NULL,
            name VARCHAR(256),
            UNIQUE (room_seq, name),
            PRIMARY KEY (node_seq),
            FOREIGN KEY (room_seq) REFERENCES room (room_seq)
        );';
    END IF;
END $$ LANGUAGE plpgsql;
