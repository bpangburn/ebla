/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2002-2003, Brian E. Pangburn
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


/* This SQL script is used to generate the database for the ebla_data
   database in Postgres */


/* 1st-level Parameter Data table */

/* This table is used to initialize the run-time parameters for EBLA */

CREATE SEQUENCE parameter_data_seq;
CREATE TABLE parameter_data (
    /* UNIQUE ID FOR EACH PARAMETER RECORD */
    parameter_id            INTEGER     	PRIMARY KEY
                                        	DEFAULT nextval('parameter_data_seq'),
    /* DESCRIPTION OF PARAMETER SET */
    description             VARCHAR(100),
    /* DIRECTORY PATH FOR STORAGE OF TEMPORARY FILES DURING PROCESSING */
    tmp_path                VARCHAR(50),
    /* COLOR RADIUS FOR MEAN-SHIFT ANALYSIS COLOR IMAGE SEGMENTATION */
    seg_color_radius        FLOAT       	DEFAULT 6.5
                                        	NOT NULL,
    /* SPATIAL RADIUS FOR MEAN-SHIFT ANALYSIS COLOR IMAGE SEGMENTATION */
    seg_spatial_radius      INTEGER     	DEFAULT 7
                                        	NOT NULL,
    /* MINIMUM PIXEL REGION FOR MEAN-SHIFT ANALYSIS COLOR IMAGE SEGMENTATION */
    seg_min_region          INTEGER     	DEFAULT 20
                                        	NOT NULL,
    /* 0=NO SPEEDUP; 1=MEDIUM SPEEDUP; 2=HIGH SPEEDUP (SPEEDUP FOR 
       MEAN-SHIFT ANALYSIS COLOR IMAGE SEGMENTATION */
    seg_speed_up_code       INT2        	DEFAULT 0
                                        	NOT NULL,
    /* SPEEDUP FACTOR FOR HIGH SPEEDUP OPTION */
    seg_speed_up_factor     FLOAT       	DEFAULT 0.5
                                        	NOT NULL,
    /* FILE PREFIX FOR TEMP FRAMES EXTRACTED FROM EACH MOVIE/EXPERIENCE */
    frame_prefix            VARCHAR(50),
    /* FILE PREFIX FOR TEMP SEGMENTED IMAGES CREATED FOR EACH FRAME */
    seg_prefix              VARCHAR(50),
    /* FILE PREFIX FOR TEMP POLYGON IMAGES CREATED FOR EACH FRAME */    
    poly_prefix             VARCHAR(50),
    /* PERCENTAGE OF TOTAL PIXELS THAT AN OBJECT MUST CONTAIN TO BE CONSIDERED
       PART OF THE BACKGROUND RATHER THAN A SIGNIFICANT OBJECT (0 - 100) */
    background_pixels       FLOAT       	DEFAULT 20.0
                                        	NOT NULL,
    /* MINIMUM NUMBER OF PIXELS THAT CONSTITUTE A "SIGNIFICANT" OBJECT */
    min_pixel_count         INTEGER     	DEFAULT 500
                                        	NOT NULL,
    /* MINIMUM NUMBER OF CONSECUTIVE FRAMES THAT AN OBJECT MUST APPEAR IN TO
       BE CONSIDERED A SIGNIFICANT OBJECT (HELPS TO ELIMINATE NOISE /
       SHADOWS). */
    min_frame_count         INTEGER     	DEFAULT 7
                                        	NOT NULL,
    /* 0=NO; 1=YES (REDUCE COLOR DEPTH OF SEGMENTED REGIONS) */
    reduce_color_code       INT2        	DEFAULT 0
                                        	NOT NULL,
    /* NOTES ABOUT THE PARAMETERS */
    notes                   VARCHAR(255)
    );



/* 1st-level Experience Data table */

/* This table contains information about the multimedia file representing an
   EBLA perceptual experience along with a description of the experience */

CREATE SEQUENCE experience_data_seq;
CREATE TABLE experience_data (
    /* UNIQUE ID FOR EACH EXPERIENCE DATA RECORD */
    experience_id           INTEGER     	PRIMARY KEY
                                        	DEFAULT nextval('experience_data_seq'),
    /* DESCRIPTION OF EXPERIENCE */
    description             VARCHAR(50)     	NOT NULL,

    /* COMPLETE PATH AND FILENAME OF SOURCE MOVIE (AVI OR MOV) THAT CONTAINS
       EXPERIENCE   */
    video_path              VARCHAR(100)    	NOT NULL,
    /* SUBDIRECTORY FOR STORAGE OF TEMPORARY FILES DURING PROCESSING
       ADDED TO tmp_path FROM parameter_data */
    tmp_path                VARCHAR(50),
    /* (PROTO) LANGUAGE DESCRIPTION FOR EXPERIENCE */
    experience_lexemes      VARCHAR(100),
    /* NOTES ABOUT THE EXPERIENCE */
    notes                   VARCHAR(255)
    );
    
    
    
/* 1st-level Attribute List Data table */

/* This table contains a list of the attributes that can be detected by EBLA */

CREATE SEQUENCE attribute_list_data_seq;
CREATE TABLE attribute_list_data (
    /* UNIQUE ID FOR EACH ATTRIBUTE LIST DATA RECORD */
    attribute_list_id       INTEGER     	PRIMARY KEY
                                        	DEFAULT 
                                        	nextval('attribute_list_data_seq'),
    /* ATTRIBUTE DESCRIPTION */
    description             VARCHAR(50) 	NOT NULL,
    /* 0=NO; 1=YES (INCLUDE ATTRIBUTE WHEN ANALYZING EXPERIENCES) */
    include_code            INT2        	DEFAULT 1
                                        	NOT NULL,
    /* 0=OBJECT; 1=RELATION (INDICATES WHETHER ATTRIBUTE APPLIES TO AN OBJECT
       OR THE RELATION BETWEEN TWO OBJECTS) */
    type_code               INT2        	DEFAULT 0
                                        	NOT NULL,
    /* NAME OF JAVA CLASS THAT SHOULD BE INVOKED TO ANALYZE ATTRIBUTE 
       (CURRENTLY NOT IMPLEMENTED) */   
    class_name              VARCHAR(50),
    /* NOTES ABOUT THE EXPERIENCE */
    notes                   VARCHAR(255)
    );
CREATE INDEX att_list_description_idx ON attribute_list_data (description);
    
    
    
/* 2nd-level Session Data table */

/* This table is used to store information about each session of EBLA calculation runs */

CREATE SEQUENCE session_data_seq;
CREATE TABLE session_data (
    /* UNIQUE ID FOR EACH SESSION RECORD */
    session_id              INTEGER     	PRIMARY KEY
                                        	DEFAULT nextval('session_data_seq'),
    /* ID OF PARENT PARAMETER DATA RECORD */
    parameter_id            INTEGER     	REFERENCES parameter_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* DESCRIPTION OF SESSION SET */
    description             VARCHAR(100),
    /* START DATE/TIME FOR CURRENT SESSION */
    session_start           TIMESTAMP           DEFAULT now()
						NOT NULL,
    /* STOP DATE/TIME FOR CURRENT SESSION */
    session_stop            TIMESTAMP,
    /* IP ADDRESS OF CLIENT MACHINE */
    session_ip		    VARCHAR(100),
    /* 0=NO; 1=YES (REGENERATE ALL INTEREMEDIATE IMAGES) */
    regen_int_images_code   INT2        	DEFAULT 0
                                        	NOT NULL,
    /* 0=NO; 1=YES (REDIRECT SCREEN OUTPUT TO LOG FILE) */
    log_to_file_code        INT2        	DEFAULT 0
                                        	NOT NULL,
    /* 0=NO; 1=YES (RANDOMIZE EXPERIENCES WHEN QUERYING FROM DATABASE) */
    randomize_exp_code      INT2        	DEFAULT 0
                                        	NOT NULL,
    /* NUMBER OF EXPERIENCES TO GENERATE DESCRIPTIONS FOR */
    desc_to_generate        INTEGER     	DEFAULT 0
                                        	NOT NULL,
    /* STARTING MINIMUM STANDARD DEVIATION */
    min_sd_start            INTEGER     	DEFAULT 5
                                        	NOT NULL,
    /* STOPPING MINIMUM STANDARD DEVIATION */
    min_sd_stop             INTEGER     	DEFAULT 5
                                        	NOT NULL,
    /* MINIMUM STANDARD DEVIATION STEP SIZE*/
    min_sd_step             INTEGER     	DEFAULT 5
                                        	NOT NULL,                       
    /* # OF TIMES TO PROCESS EXPERIENCES FOR EACH MIN STANDARD DEVIATION */
    loop_count              INTEGER     	DEFAULT 1
                                        	NOT NULL,
    /* 0=NO; 1=YES (LIMIT STANDARD DEVIATION FOR ENTITY MATCHING TO SPECIFIED
       VALUE - IF NO THEN UTILIZE CALCULATED SD FOR CURRENT ENTITY
       ATTRIBUTES) */	
    fixed_sd_code           INT2        	DEFAULT 0
                                        	NOT NULL,
    /* 0=NO; 1=YES (DISPLAY MOVIE DURING FRAME EXTRACTION) */
    display_movie_code      INT2        	DEFAULT 0
                                        	NOT NULL,
    /* 0=NO; 1=YES (DISPLAY DETAILED DATA DURING FRAME PROCESSING) */
    display_text_code       INT2        	DEFAULT 0
                                        	NOT NULL,
    /* 0=NO; 1=YES (LEXEMES ARE CASE-SENSITIVE) */
    case_sensitive_code     INT2        	DEFAULT 0
                                        	NOT NULL,
    /* NOTES ABOUT THE SESSION */
    notes                   VARCHAR(255)
    ); 
CREATE INDEX session_parameter_id_idx ON session_data (parameter_id);

    
    
/* 2nd-level Parameter-Experience Data table */

/* This table determines which experiences should be included with each set
   of parameters */

CREATE SEQUENCE parameter_experience_data_seq;
CREATE TABLE parameter_experience_data (
    /* UNIQUE ID FOR EACH PARAMETER-EXPERIENCE RECORD */
    parameter_experience_id INTEGER     	PRIMARY KEY
                                        	DEFAULT 
                                        	nextval('parameter_experience_data_seq'),
    /* ID OF PARENT PARAMETER DATA RECORD */
    parameter_id            INTEGER     	REFERENCES parameter_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* ID OF PARENT EXPERIENCE DATA RECORD */
    experience_id           INTEGER     	REFERENCES experience_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* STATUS OF frame_analysis_data CALCULATIONS FOR PARAMETER-EXPERIENCE COMBO:
    	0 = NOT CALCULATED
    	1 = CALCULATIONS IN PROGRESS
    	2 = CALCULATIONS COMPLETED */
    calc_status_code        INT2        	DEFAULT 0
                                        	NOT NULL,                       
    calc_timestamp          TIMESTAMP           DEFAULT now()
						NOT NULL
    );
CREATE INDEX para_exp_parameter_id_idx ON parameter_experience_data (parameter_id);
CREATE INDEX para_exp_experience_id_idx ON parameter_experience_data (experience_id);    
    
    
        
/* 3rd-level Frame Analysis Data table */

/* This table contains the preliminary information about the "significant"
   objects encountered in an EBLA experience */

CREATE SEQUENCE frame_analysis_data_seq;
CREATE TABLE frame_analysis_data (
    /* UNIQUE ID FOR EACH FRAME ANALYSIS RECORD */
    frame_analysis_id       INTEGER     	PRIMARY KEY
                                        	DEFAULT 
                                        	nextval('frame_analysis_data_seq'),
    /* ID OF PARENT EXPERIENCE RECORD */
    parameter_experience_id INTEGER     	REFERENCES parameter_experience_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* NUMBER OF CURRENT FRAME */
    frame_number            INTEGER     	DEFAULT 0
                                        	NOT NULL,
    /* OBJECT INDEX (AS OBJECTS ARE DETECTED, THEY ARE INDEXED FROM THE TOP
       DOWN AND CORRELATED FROM FRAME TO FRAME) */
    object_number           INTEGER     	DEFAULT 0
                                        	NOT NULL,
    /* NUMBER OF POINTS IN POLYGON */
    polygon_point_count     INTEGER     	DEFAULT 0
                                        	NOT NULL,
    /* COMMA-SEPARATED LIST OF POLYGON POINTS */
    polygon_point_list      TEXT        	NOT NULL,
    /* RGB COLOR OF OBJECT (27 POSSIBLE VALUES - EACH RGB COMPONENT IS ROUNDED
       TO 0, 128, OR 255) */
    rgb_color               INTEGER     	DEFAULT 0
                                        	NOT NULL,
    /* COMMA-SEPARTED LIST OF BOUNDING RECTANGLE POINTS */
    bound_rect_points       VARCHAR(50) 	NOT NULL,
    /* X COORDINATE OF CENTER OF GRAVITY */
    centroid_x              INTEGER     	DEFAULT 0
                                        	NOT NULL,
    /* Y COORDINATE OF CENTER OF GRAVITY */
    centroid_y              INTEGER     	DEFAULT 0
                                        	NOT NULL,
    /* AREA OF OBJECT */
    area                    FLOAT       	DEFAULT 0
                                        	NOT NULL
    );
CREATE INDEX fra_ana_para_exp_id_idx ON frame_analysis_data (parameter_experience_id);
CREATE INDEX fra_ana_frame_number_idx ON frame_analysis_data (frame_number);
CREATE INDEX fra_ana_object_number_idx ON frame_analysis_data (object_number);    
    
    
    
/* 3rd-level Run Data table */

/* This table tracks each EBLA calculation run */

CREATE SEQUENCE run_data_seq;
CREATE TABLE run_data (
    /* UNIQUE ID FOR EACH RUN DATA RECORD */
    run_id                  INTEGER     	PRIMARY KEY
                                        	DEFAULT nextval('run_data_seq'),
    /* ID OF PARENT SESSION DATA RECORD */
    session_id              INTEGER     	REFERENCES session_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* ORDER THAT RUN OCCURS IN CURRENT SESSION */
    run_index               INTEGER         	DEFAULT 0
                                        	NOT NULL,
    /* START DATE/TIME FOR CURRENT RUN */
    run_start               TIMESTAMP           DEFAULT now()
						NOT NULL,
    /* STOP DATE/TIME FOR CURRENT RUN */
    run_stop                TIMESTAMP,
    /* MINIMUM STANDARD DEVIATION FOR RUN */
    min_sd                  INTEGER     	DEFAULT 0
                                        	NOT NULL
    );



/* 4th-level Entity Data table */

/* This table contains all of the entities that have been detected by EBLA */

CREATE SEQUENCE entity_data_seq;
CREATE TABLE entity_data (
    /* UNIQUE ID FOR EACH ENTITY DATA RECORD */
    entity_id               INTEGER     	PRIMARY KEY
                                        	DEFAULT nextval('entity_data_seq'),
    /* ID OF PARENT RUN DATA RECORD */
    run_id                  INTEGER     	REFERENCES run_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* NUMBER OF TIMES THAT ENTITY HAS BEEN RECOGNIZED WHEN PROCESSING EBLA
       EXPERIENCES */
    occurance_count         INTEGER     	DEFAULT 1
                                        	NOT NULL
    );
CREATE INDEX entity_run_id_idx ON entity_data (run_id); 



/* 4th-level Lexeme Data table */

/* This table contains all of the lexical items that have been detected by
   EBLA. A lexical item can occur in the table multiple times if multiple
   senses of the word are encountered */

CREATE SEQUENCE lexeme_data_seq;
CREATE TABLE lexeme_data (
    /* UNIQUE ID FOR EACH LEXICAL ITEM RECORD */
    lexeme_id               INTEGER     	PRIMARY KEY
                                        	DEFAULT nextval('lexeme_data_seq'),
    /* ID OF PARENT RUN DATA RECORD */
    run_id                  INTEGER     	REFERENCES run_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* LEXICAL ITEM / WORD */
    lexeme                  VARCHAR(50) 	NOT NULL,
    /* NUMBER OF TIMES THAT LEXICAL ITEM HAS BEEN RECOGNIZED WHEN PROCESSING
       EBLA EXPERIENCES */
    occurance_count         INTEGER     	DEFAULT 1
                                        	NOT NULL
    );
CREATE INDEX lexeme_run_id_idx ON lexeme_data (run_id); 
CREATE INDEX lexeme_lexeme_idx ON lexeme_data (lexeme);



/* 4th-level Experience-Run Data table */

/* This table stores run results for each experience */

CREATE SEQUENCE experience_run_data_seq;
CREATE TABLE experience_run_data (
    /* UNIQUE ID FOR EACH EXPERIENCE-RUN RECORD */
    experience_run_id       INTEGER     	PRIMARY KEY
                                        	DEFAULT 
                                        	nextval('experience_run_data_seq'),
    /* ID OF PARENT EXPERIENCE DATA RECORD */
    experience_id           INTEGER     	REFERENCES experience_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* ID OF PARENT RUN DATA RECORD */
    run_id                  INTEGER     	REFERENCES run_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* ORDER THAT EXPERIENCE IS PROCESSED DURING CURRENT RUN
       (USED FOR DETERMINING HOW LONG IT TAKES TO RESOLVE EACH LEXEME) */
    experience_index        INTEGER         	DEFAULT 0
                                        	NOT NULL,
    /* PROTOTLANGUAGE GENERATED BY EBLA TO DESCRIBE AN EXPERIENCE BASED ON
       PRIOR EXPERIENCES */
    experience_description  VARCHAR(100)
    );
CREATE INDEX exp_run_experience_id_idx ON experience_run_data (experience_id);
CREATE INDEX exp_run_run_id_idx ON experience_run_data (run_id); 



/* 5th-level Entity-Lexeme Data table */

/* This table contains a record with the entity-lexeme mappings */

CREATE SEQUENCE entity_lexeme_data_seq;
CREATE TABLE entity_lexeme_data (
    /* UNIQUE ID FOR EACH ENTITY-LEXEME RECORD */
    entity_lexeme_id        INTEGER     	PRIMARY KEY
                                        	DEFAULT 
                                        	nextval('entity_lexeme_data_seq'),
    /* ID OF PARENT ENTITY DATA RECORD */
    entity_id               INTEGER     	REFERENCES entity_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* ID OF PARENT LEXEME DATA RECORD */
    lexeme_id               INTEGER     	REFERENCES lexeme_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* NUMBER OF TIMES THAT LEXICAL ITEM HAS BEEN RECOGNIZED WHEN PROCESSING
       EBLA EXPERIENCES */
    occurance_count         INTEGER     	DEFAULT 1
                                        	NOT NULL
    );
CREATE INDEX ent_lex_entity_id_idx ON entity_lexeme_data (entity_id);
CREATE INDEX ent_lex_lexeme_id_idx ON entity_lexeme_data (lexeme_id);    



/* 5th-level Experience-Entity Data table */

/* This table contains a record with the ID of each entity record in an EBLA
   experience for each run */

CREATE SEQUENCE experience_entity_data_seq;
CREATE TABLE experience_entity_data (
    /* UNIQUE ID FOR EACH EXPERIENCE-ENTITY RECORD */
    experience_entity_id    INTEGER     	PRIMARY KEY
                                        	DEFAULT 
                                        	nextval('experience_entity_data_seq'),
    /* ID OF PARENT EXPERIENCE DATA RECORD */
    experience_id           INTEGER     	REFERENCES experience_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* ID OF PARENT RUN DATA RECORD */
    run_id                  INTEGER     	REFERENCES run_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* ID OF PARENT ENTITY DATA RECORD */
    entity_id               INTEGER     	REFERENCES entity_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* 0=NO; 1=YES (CODE INDICATING IF ENTITY HAS BEEN RESOLVED TO A LEXICAL
       ITEM) */
    resolution_code         INT2        	DEFAULT 0
                                        	NOT NULL                        
    );
CREATE INDEX exp_ent_exp_id_idx ON experience_entity_data (experience_id);
CREATE INDEX exp_ent_run_id_idx ON experience_entity_data (run_id);
CREATE INDEX exp_ent_entity_id_idx ON experience_entity_data (entity_id);



/* 5th-level Experience-Lexeme Data table */

/* This table contains a record with the ID of each lexeme record in an EBLA
   experience for each run */

CREATE SEQUENCE experience_lexeme_data_seq;
CREATE TABLE experience_lexeme_data (
    /* UNIQUE ID FOR EACH EXPERIENCE-LEXEME RECORD */
    experience_lexeme_id    INTEGER     	PRIMARY KEY
                                        	DEFAULT 
                                        	nextval('experience_lexeme_data_seq'),
    /* ID OF PARENT EXPERIENCE DATA RECORD */
    experience_id           INTEGER     	REFERENCES experience_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* ID OF PARENT RUN DATA RECORD */
    run_id                  INTEGER     	REFERENCES run_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* ID OF PARENT LEXEME DATA RECORD */
    lexeme_id               INTEGER     	REFERENCES lexeme_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* 0=NO; 1=YES (CODE INDICATING IF ENTITY HAS BEEN RESOLVED TO A LEXICAL
       ITEM) */
    resolution_code         INT2        	DEFAULT 0
                                        	NOT NULL,
    /* NUMBER OF EXPERIENCES PROCESSED BEFORE RESOLUTION OCCURS (ZERO IF NOT
       RESOLVED) */
    resolution_index        INTEGER     	DEFAULT 0
                                        	NOT NULL
    );
CREATE INDEX exp_lex_exp_id_idx ON experience_lexeme_data (experience_id);
CREATE INDEX exp_lex_run_id_idx ON experience_lexeme_data (run_id);
CREATE INDEX exp_lex_lexeme_id_idx ON experience_lexeme_data (lexeme_id);



/* 5th-level Attribute-Value Data table */

/* This table contains the attribute values for each entity encountered 
   in an EBLA experience */

CREATE SEQUENCE attribute_value_data_seq;
CREATE TABLE attribute_value_data (
    /* UNIQUE ID FOR EACH ATTRIBUTE-VALUE RECORD */
    attribute_value_id      INTEGER     	PRIMARY KEY
                                        	DEFAULT 
                                    		nextval('attribute_value_data_seq'),
    /* ID OF PARENT ATTRIBUTE LIST DATA RECORD */
    attribute_list_id       INTEGER     	REFERENCES attribute_list_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* ID OF PARENT RUN DATA RECORD */
    run_id                  INTEGER     	REFERENCES run_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* ID OF PARENT ENTITY DATA RECORD */
    entity_id               INTEGER     	REFERENCES entity_data
                                        	ON UPDATE CASCADE
                                        	ON DELETE CASCADE,
    /* AVERAGE VALUE OF ATTRIBUTE */
    avg_value               FLOAT       	DEFAULT 0
                                        	NOT NULL,
    /* STANDARD DEVIATION OF ATTRIBUTE */
    std_deviation           FLOAT       	DEFAULT 0
                                        	NOT NULL
    );
CREATE INDEX att_val_attribute_list_id_idx ON attribute_value_data (attribute_list_id);
CREATE INDEX att_val_run_id_idx ON attribute_value_data (run_id);
CREATE INDEX att_val_entity_id_idx ON attribute_value_data (entity_id);



