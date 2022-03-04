INSERT INTO action_parameter_type (action_parameter_type_seq, name, type_def, home_seq)
SELECT NEXTVAL('sq_action_parameter_type'), 'string', '{"type":"string"}', (SELECT last_value FROM sq_home)
 WHERE NOT EXISTS (
        SELECT *
          FROM action_parameter_type
         WHERE UPPER(name) LIKE UPPER('%string%')
       );

INSERT INTO action_parameter_type (action_parameter_type_seq, name, type_def, home_seq)
SELECT NEXTVAL('sq_action_parameter_type'), 'number', '{"type":"number"}', (SELECT last_value FROM sq_home)
 WHERE NOT EXISTS (
        SELECT *
          FROM action_parameter_type
         WHERE UPPER(name) LIKE UPPER('%number%')
       );

INSERT INTO action_parameter_type (action_parameter_type_seq, name, type_def, home_seq)
SELECT NEXTVAL('sq_action_parameter_type'), 'boolean', '{"type":"list","values":["true","false"],"config":{"allowEmptyValues":false,"defaultWhenEmpty":"false"}}', (SELECT last_value FROM sq_home)
 WHERE NOT EXISTS (
        SELECT *
          FROM action_parameter_type
         WHERE UPPER(name) LIKE UPPER('%boolean%')
       );
