DO $$
DECLARE
    c_schema_name VARCHAR(32) := 'hams_data';

    v_row_exists BOOLEAN;
BEGIN
    SELECT EXISTS(
        SELECT
          FROM user_login
         WHERE UPPER(username) = UPPER('admin')
    ) INTO v_row_exists;

    IF NOT v_row_exists THEN
        INSERT INTO account (
            account_seq,
            created,
            type
        ) VALUES (
            nextval('sq_account'),
            NOW(),
            'corporate'
        );
    END IF;
END $$ LANGUAGE plpgsql;
