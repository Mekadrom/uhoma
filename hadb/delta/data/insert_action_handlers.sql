INSERT INTO action_handler (action_handler_seq, name, handler_def, account_seq)
SELECT action_handler_seq, name, handler_def, account_seq
  FROM (VALUES(NEXTVAL('sq_action_handler'), 'RESPONSE_NOT_REQUIRED', '{"respond":"NOT_REQUIRED"}', 1)) AS S(action_handler_seq, name, handler_def, account_seq)
 WHERE NOT EXISTS (
        SELECT *
          FROM action_handler
         WHERE UPPER(name) LIKE UPPER('%RESPONSE_NOT_REQUIRED%')
       );

INSERT INTO action_handler (action_handler_seq, name, handler_def, account_seq)
SELECT action_handler_seq, name, handler_def, account_seq
  FROM (VALUES(NEXTVAL('sq_action_handler'), 'RESPONSE_REQUIRED', '{"respond":"REQUIRED"}', 1)) AS S(action_handler_seq, name, handler_def, account_seq)
 WHERE NOT EXISTS (
        SELECT *
          FROM action_handler
         WHERE UPPER(name) LIKE UPPER('%RESPONSE_REQUIRED%')
       );

INSERT INTO action_handler (action_handler_seq, name, handler_def, account_seq)
SELECT action_handler_seq, name, handler_def, account_seq
  FROM (VALUES(NEXTVAL('sq_action_handler'), 'RESPONSE_REQUESTED', '{"respond":"REQUESTED"}', 1)) AS S(action_handler_seq, name, handler_def, account_seq)
 WHERE NOT EXISTS (
        SELECT *
          FROM action_handler
         WHERE UPPER(name) LIKE UPPER('%RESPONSE_REQUESTED%')
       );
