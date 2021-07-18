DO $$
DECLARE
    c_schema_name VARCHAR(32) := 'hams_data';
    c_source_table_name VARCHAR(32) := 'user_login';
    c_source_column_name VARCHAR(32) := 'account_seq';
    c_target_table_name VARCHAR(32) := 'account';
    c_target_column_name VARCHAR(32) := 'account_seq';
    c_constraint_name VARCHAR(32) := 'fk_user_login_account';

    v_source_table_exists BOOLEAN;
    v_target_table_exists BOOLEAN;
    v_source_column_exists BOOLEAN;
    v_target_column_exists BOOLEAN;
    v_constraint_exists BOOLEAN;
BEGIN
    SELECT EXISTS(
        SELECT
          FROM INFORMATION_SCHEMA.TABLES
         WHERE TABLE_SCHEMA = c_schema_name
           AND TABLE_NAME = c_source_table_name
    ) INTO v_source_table_exists;

    SELECT EXISTS(
        SELECT
          FROM INFORMATION_SCHEMA.COLUMNS
         WHERE TABLE_SCHEMA = c_schema_name
           AND TABLE_NAME = c_source_table_name
           AND COLUMN_NAME = c_source_column_name
    ) INTO v_source_column_exists;

    SELECT EXISTS(
        SELECT
          FROM INFORMATION_SCHEMA.TABLES
         WHERE TABLE_SCHEMA = c_schema_name
           AND TABLE_NAME = c_target_table_name
    ) INTO v_target_table_exists;

    SELECT EXISTS(
        SELECT
          FROM INFORMATION_SCHEMA.COLUMNS
         WHERE TABLE_SCHEMA = c_schema_name
           AND TABLE_NAME = c_target_table_name
           AND COLUMN_NAME = c_target_column_name
    ) INTO v_target_column_exists;

    SELECT EXISTS(
        SELECT
          FROM PG_CONSTRAINT
         WHERE CONNAME = c_constraint_name
    ) INTO v_constraint_exists;

    IF v_source_table_exists AND v_source_column_exists AND v_target_table_exists AND v_target_column_exists AND NOT v_constraint_exists THEN
        EXECUTE 'ALTER TABLE ' || c_source_table_name || ' ADD CONSTRAINT ' || c_constraint_name || ' FOREIGN KEY (' || c_source_column_name ||') REFERENCES ' || c_target_table_name || '(' || c_target_column_name || ')';
    END IF;
END $$ LANGUAGE plpgsql;
