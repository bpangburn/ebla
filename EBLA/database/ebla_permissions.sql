CREATE USER eblauser with PASSWORD 'guest';

GRANT SELECT ON attribute_list_data_seq TO eblauser;
GRANT SELECT, INSERT, UPDATE, DELETE ON attribute_value_data TO eblauser;
GRANT SELECT, INSERT, UPDATE, DELETE ON entity_data TO eblauser;
GRANT SELECT, INSERT, UPDATE, DELETE ON entity_lexeme_data TO eblauser;
GRANT SELECT ON experience_data TO eblauser;
GRANT SELECT, INSERT, UPDATE, DELETE ON experience_entity_data TO eblauser;
GRANT SELECT, INSERT, UPDATE, DELETE ON experience_lexeme_data TO eblauser;
GRANT SELECT, INSERT, UPDATE, DELETE ON experience_run_data TO eblauser;
GRANT SELECT, INSERT, UPDATE, DELETE ON frame_analysis_data TO eblauser;
GRANT SELECT, INSERT, UPDATE, DELETE ON lexeme_data TO eblauser;
GRANT SELECT ON parameter_data TO eblauser;
GRANT SELECT, UPDATE ON parameter_experience_data TO eblauser;
GRANT SELECT, INSERT, UPDATE, DELETE ON run_data TO eblauser;
GRANT SELECT, INSERT, UPDATE, DELETE ON session_data TO eblauser;

GRANT SELECT ON attribute_list_data_seq TO eblauser;
GRANT SELECT, UPDATE ON attribute_value_data_seq TO eblauser;
GRANT SELECT, UPDATE ON entity_data_seq TO eblauser;
GRANT SELECT, UPDATE ON entity_lexeme_data_seq TO eblauser;
GRANT SELECT ON experience_data_seq TO eblauser;
GRANT SELECT, UPDATE ON experience_entity_data_seq TO eblauser;
GRANT SELECT, UPDATE ON experience_lexeme_data_seq TO eblauser;
GRANT SELECT, UPDATE ON experience_run_data_seq TO eblauser;
GRANT SELECT, UPDATE ON frame_analysis_data_seq TO eblauser;
GRANT SELECT, UPDATE ON lexeme_data_seq TO eblauser;
GRANT SELECT ON parameter_data_seq TO eblauser;
GRANT SELECT ON parameter_experience_data_seq TO eblauser;
GRANT SELECT, UPDATE ON run_data_seq TO eblauser;
GRANT SELECT, UPDATE ON session_data_seq TO eblauser;

REVOKE ALL ON DATABASE ebla_data FROM PUBLIC,eblauser;
