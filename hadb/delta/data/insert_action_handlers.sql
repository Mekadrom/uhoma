INSERT INTO action_handler (action_handler_seq, name, handler_def, home_seq)
SELECT NEXTVAL('sq_action_handler'), 'HTTP_WITH_RESPONSE', '{"metadata":{"is_builtin":true,"builtin_type":"http_handler","type":"template","response":true},"def":{"method":"string","connect_type":"string","url":"string","port":"string","endpoint":"string","query_params":"object","headers":"object","body":"string"}}', (SELECT last_value FROM sq_home)
 WHERE NOT EXISTS (
        SELECT *
          FROM action_handler
         WHERE UPPER(name) = UPPER('HTTP_WITH_RESPONSE')
       );
