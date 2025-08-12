DO $$
DECLARE
    c_schema_name VARCHAR(32) := 'hams_data';

    v_row_exists BOOLEAN;
BEGIN
    SELECT EXISTS(
        SELECT
          FROM user_login
         WHERE UPPER(username) = UPPER('test')
    ) INTO v_row_exists;

    IF NOT v_row_exists THEN
        INSERT INTO user_login (
            username,
            "password",
            roles,
            last_login,
            created
        ) VALUES (
            'admin',
            '$2y$12$8.WZVLPGTsg42OjSd.zjkecWxjZRYjDLOTsSRyao.LQzHJ0aeJ24q',
            'ROLE_ADMIN',
            NULL,
            NOW()
        );
    END IF;
END $$ LANGUAGE plpgsql;
