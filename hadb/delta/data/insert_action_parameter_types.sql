INSERT INTO action_parameter_type (name, type_def, home_seq)
SELECT 'string', '{"type":"string"}', (SELECT last_value FROM sq_home)
 WHERE NOT EXISTS (
        SELECT *
          FROM action_parameter_type
         WHERE UPPER(name) LIKE UPPER('%string%')
       );

INSERT INTO action_parameter_type (name, type_def, home_seq)
SELECT 'number', '{"type":"number"}', (SELECT last_value FROM sq_home)
 WHERE NOT EXISTS (
        SELECT *
          FROM action_parameter_type
         WHERE UPPER(name) LIKE UPPER('%number%')
       );

INSERT INTO action_parameter_type (name, type_def, home_seq)
SELECT 'boolean', '{"type":"list","values":["true","false"],"config":{"allowEmptyValues":false,"defaultWhenEmpty":"false"}}', (SELECT last_value FROM sq_home)
 WHERE NOT EXISTS (
        SELECT *
          FROM action_parameter_type
         WHERE UPPER(name) LIKE UPPER('%boolean%')
       );
