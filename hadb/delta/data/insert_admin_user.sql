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
        INSERT INTO user_login (
            user_login_seq,
            username,
            "password",
            auths,
            node_seq,
            last_login,
            created
        ) VALUES (
            nextval('sq_user_login'),
            'admin',
            '$2y$12$8.WZVLPGTsg42OjSd.zjkecWxjZRYjDLOTsSRyao.LQzHJ0aeJ24q',
            'ROLE_ADMIN',
            null,
            null,
            NOW()
        );
    END IF;
END $$ LANGUAGE plpgsql;
