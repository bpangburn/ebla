/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2002-2005, Brian E. Pangburn
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  The names of its contributors may not be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */


/* This SQL script is used to add the eblauser/guest account and set lower
   permissions for ebla_data */
   
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
