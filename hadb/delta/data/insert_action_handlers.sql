INSERT INTO action_handler (action_handler_seq, name, handler_def, account_seq)
SELECT action_handler_seq, name, handler_def, account_seq
  FROM (VALUES(NEXTVAL('sq_action_handler'), 'HTTP_WITH_RESPONSE', '{"metadata":{"is_builtin":true,"builtin_type":"http_handler","type":"template","response":true},"def":{"method":"string","connect_type":"string","url":"string","port":"string","endpoint":"string","query_params":"object","headers":"object","body":"string"}}', 1)) AS S(action_handler_seq, name, handler_def, account_seq)
 WHERE NOT EXISTS (
        SELECT *
          FROM action_handler
         WHERE name = 'HTTP_WITH_RESPONSE'
       );
