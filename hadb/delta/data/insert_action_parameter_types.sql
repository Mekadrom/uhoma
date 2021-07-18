INSERT INTO action_parameter_type (action_parameter_type_seq, name, type_def, account_seq)
SELECT action_parameter_type_seq, name, type, account_seq
  FROM (VALUES(NEXTVAL('sq_action_parameter_type'), 'string', '{"type":"string"}', 1)) AS S(action_parameter_type_seq, name, type, account_seq)
 WHERE NOT EXISTS (
        SELECT *
          FROM action_parameter_type
         WHERE UPPER(name) LIKE UPPER('%string%')
       );

INSERT INTO action_parameter_type (action_parameter_type_seq, name, type_def, account_seq)
SELECT action_parameter_type_seq, name, type, account_seq
  FROM (VALUES(NEXTVAL('sq_action_parameter_type'), 'number', '{"type":"number"}', 1)) AS S(action_parameter_type_seq, name, type, account_seq)
 WHERE NOT EXISTS (
        SELECT *
          FROM action_parameter_type
         WHERE UPPER(name) LIKE UPPER('%number%')
       );

INSERT INTO action_parameter_type (action_parameter_type_seq, name, type_def, account_seq)
SELECT action_parameter_type_seq, name, type, account_seq
  FROM (VALUES(NEXTVAL('sq_action_parameter_type'), 'boolean', '{"type":"list","values":["true","false"],"config":{"allowEmptyValues":false}}', 1)) AS S(action_parameter_type_seq, name, type, account_seq)
 WHERE NOT EXISTS (
        SELECT *
          FROM action_parameter_type
         WHERE UPPER(name) LIKE UPPER('%boolean%')
       );