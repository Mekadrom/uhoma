DO $$
DECLARE
    c_schema_name VARCHAR(32) := 'hams_data';
    c_table_name VARCHAR(32) := 'home_user_link';
    c_sequence_name VARCHAR(32) := 'sq_home_user_link';

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
            home_user_link_seq BIGINT NOT NULL DEFAULT nextval(''' || c_schema_name || '.' || c_sequence_name || '''),
            home_seq BIGINT NOT NULL,
            user_login_seq BIGINT NOT NULL,
            user_added TIMESTAMP WITH TIME ZONE NOT NULL,
            user_removed TIMESTAMP WITH TIME ZONE,
            UNIQUE (home_seq, user_login_seq),
            PRIMARY KEY (home_user_link_seq),
            FOREIGN KEY (home_seq) REFERENCES home (home_seq),
            FOREIGN KEY (user_login_seq) REFERENCES user_login (user_login_seq)
        );';
    END IF;
END $$ LANGUAGE plpgsql;
