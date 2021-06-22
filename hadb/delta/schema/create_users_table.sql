DO $$
DECLARE
    c_schema_name VARCHAR(32) := 'hams_data';
    c_table_name VARCHAR(32) := 'user_login';
    c_sequence_name VARCHAR(32) := 'sq_user_login';

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
            user_login_seq BIGINT NOT NULL,
            username VARCHAR(256),
            password VARCHAR(1024),
            is_locked BOOLEAN DEFAULT FALSE,
            is_enabled BOOLEAN DEFAULT TRUE,
            is_expired BOOLEAN DEFAULT FALSE,
            is_credentials_expired BOOLEAN DEFAULT FALSE,
            auths VARCHAR(1024) NOT NULL,
            node_seq BIGINT,
            last_login TIMESTAMP WITH TIME ZONE,
            created TIMESTAMP WITH TIME ZONE NOT NULL,
            PRIMARY KEY (user_login_seq),
            UNIQUE (username),
            UNIQUE (node_seq),
            FOREIGN KEY (node_seq) REFERENCES node (node_seq)
        );';
    END IF;
END $$ LANGUAGE plpgsql;
