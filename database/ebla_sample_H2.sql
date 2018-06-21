/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2011, Brian E. Pangburn
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


/* This SQL script is used to load a sample dataset into the database for the ebla_data
   database for the H2 embedded database based on the original PostgreSQL SQL script */
   
/* SET POSTGRES MODE FOR H2 */
SET MODE postgresql;

/* LOAD parameter_data */
DELETE FROM parameter_data;
INSERT INTO parameter_data (parameter_id, description, tmp_path, seg_color_radius, seg_spatial_radius, seg_min_region, seg_speed_up_code, frame_prefix, seg_prefix, poly_prefix, background_pixels, min_pixel_count, min_frame_count, reduce_color_code, notes, seg_speed_up_factor, edison_port_version)
	SELECT * FROM CSVREAD('./database/csv_data/parameter_data.csv');
	--SELECT * FROM CSVREAD('c:\local_code\ebla\database\csv_data\parameter_data.csv');
ALTER SEQUENCE parameter_data_seq RESTART WITH 1000;
SELECT * FROM parameter_data;


/* LOAD experience_data */
DELETE FROM experience_data;
INSERT INTO experience_data (experience_id, description, video_path, tmp_path, experience_lexemes, notes)
	SELECT * FROM CSVREAD('./database/csv_data/experience_data.csv');
	--SELECT * FROM CSVREAD('c:\local_code\ebla\database\csv_data\experience_data.csv');
ALTER SEQUENCE experience_data_seq RESTART WITH 1000;	
SELECT * FROM experience_data;

/* LOAD attribute_list_data */
DELETE FROM attribute_list_data;
INSERT INTO attribute_list_data (attribute_list_id, description, include_code, type_code, class_name, notes)
	SELECT * FROM CSVREAD('./database/csv_data/attribute_list_data.csv');
	--SELECT * FROM CSVREAD('c:\local_code\ebla\database\csv_data\attribute_list_data.csv');
ALTER SEQUENCE attribute_list_data_seq RESTART WITH 1000;	
SELECT * FROM attribute_list_data;


/* LOAD parameter_experience_data */
DELETE FROM parameter_experience_data;
INSERT INTO parameter_experience_data (parameter_experience_id, parameter_id, experience_id, calc_status_code, calc_timestamp)
	SELECT * FROM CSVREAD('./database/csv_data/parameter_experience_data.csv');
	--SELECT * FROM CSVREAD('c:\local_code\ebla\database\csv_data\parameter_experience_data.csv');
ALTER SEQUENCE parameter_experience_data_seq RESTART WITH 1000;	
SELECT * FROM parameter_experience_data;