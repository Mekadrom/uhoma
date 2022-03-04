DO $$
DECLARE
    c_schema_name VARCHAR(32) := 'hams_data';

    v_row_exists BOOLEAN;
    v_admin_user_login_seq INTEGER;
BEGIN
    SELECT EXISTS(
        SELECT
          FROM home
         WHERE UPPER(type) = UPPER('test')
    ) INTO v_row_exists;

    SELECT user_login_seq
      FROM user_login
     WHERE UPPER(username) = UPPER('admin')
      INTO v_admin_user_login_seq;

    IF NOT v_row_exists THEN
        INSERT INTO home (
            home_seq,
            created,
            type,
            name,
            owner_user_login_seq
        ) VALUES (
            NEXTVAL('sq_home'),
            NOW(),
            'test',
            'test_home',
            v_admin_user_login_seq
        );
    END IF;
END $$ LANGUAGE plpgsql;
