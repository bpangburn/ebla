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


/* This SQL script is used to generate the database for the ebla_data
   database in Postgres and prime the parameter_data, experience_data,
   and parameter_experience_data tables */
   
--
-- PostgreSQL database dump
--

SET SESSION AUTHORIZATION 'postgres';

--
-- TOC entry 3 (OID 2200)
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;


SET search_path = public, pg_catalog;

--
-- TOC entry 4 (OID 8222503)
-- Name: parameter_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE parameter_data_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 6 (OID 8222503)
-- Name: parameter_data_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE parameter_data_seq FROM PUBLIC;


--
-- TOC entry 46 (OID 8222505)
-- Name: parameter_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE parameter_data (
    parameter_id integer DEFAULT nextval('parameter_data_seq'::text) NOT NULL,
    description character varying(100),
    tmp_path character varying(50),
    seg_color_radius double precision DEFAULT 6.5 NOT NULL,
    seg_spatial_radius integer DEFAULT 7 NOT NULL,
    seg_min_region integer DEFAULT 20 NOT NULL,
    seg_speed_up_code smallint DEFAULT 0 NOT NULL,
    frame_prefix character varying(50),
    seg_prefix character varying(50),
    poly_prefix character varying(50),
    background_pixels double precision DEFAULT 20.0 NOT NULL,
    min_pixel_count integer DEFAULT 500 NOT NULL,
    min_frame_count integer DEFAULT 7 NOT NULL,
    reduce_color_code smallint DEFAULT 0 NOT NULL,
    notes character varying(255),
    seg_speed_up_factor double precision DEFAULT 0.5 NOT NULL,
    edison_port_version smallint DEFAULT 0 NOT NULL
);


--
-- TOC entry 47 (OID 8222505)
-- Name: parameter_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE parameter_data FROM PUBLIC;


--
-- TOC entry 7 (OID 8222517)
-- Name: experience_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE experience_data_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 9 (OID 8222517)
-- Name: experience_data_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE experience_data_seq FROM PUBLIC;


--
-- TOC entry 48 (OID 8222519)
-- Name: experience_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE experience_data (
    experience_id integer DEFAULT nextval('experience_data_seq'::text) NOT NULL,
    description character varying(50) NOT NULL,
    video_path character varying(100) NOT NULL,
    tmp_path character varying(50),
    experience_lexemes character varying(100),
    notes character varying(255)
);


--
-- TOC entry 49 (OID 8222519)
-- Name: experience_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE experience_data FROM PUBLIC;


--
-- TOC entry 10 (OID 8222522)
-- Name: attribute_list_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE attribute_list_data_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 12 (OID 8222522)
-- Name: attribute_list_data_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE attribute_list_data_seq FROM PUBLIC;


--
-- TOC entry 50 (OID 8222524)
-- Name: attribute_list_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE attribute_list_data (
    attribute_list_id integer DEFAULT nextval('attribute_list_data_seq'::text) NOT NULL,
    description character varying(50) NOT NULL,
    include_code smallint DEFAULT 1 NOT NULL,
    type_code smallint DEFAULT 0 NOT NULL,
    class_name character varying(50),
    notes character varying(255)
);


--
-- TOC entry 51 (OID 8222524)
-- Name: attribute_list_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE attribute_list_data FROM PUBLIC;


--
-- TOC entry 13 (OID 8222529)
-- Name: session_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE session_data_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 15 (OID 8222529)
-- Name: session_data_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE session_data_seq FROM PUBLIC;


--
-- TOC entry 52 (OID 8222531)
-- Name: session_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE session_data (
    session_id integer DEFAULT nextval('session_data_seq'::text) NOT NULL,
    parameter_id integer,
    description character varying(100),
    session_start timestamp without time zone DEFAULT now() NOT NULL,
    session_stop timestamp without time zone,
    regen_int_images_code smallint DEFAULT 0 NOT NULL,
    log_to_file_code smallint DEFAULT 0 NOT NULL,
    randomize_exp_code smallint DEFAULT 0 NOT NULL,
    desc_to_generate integer DEFAULT 0 NOT NULL,
    min_sd_start integer DEFAULT 5 NOT NULL,
    min_sd_stop integer DEFAULT 5 NOT NULL,
    min_sd_step integer DEFAULT 5 NOT NULL,
    loop_count integer DEFAULT 1 NOT NULL,
    fixed_sd_code smallint DEFAULT 0 NOT NULL,
    display_movie_code smallint DEFAULT 0 NOT NULL,
    display_text_code smallint DEFAULT 0 NOT NULL,
    case_sensitive_code smallint DEFAULT 0 NOT NULL,
    notes character varying(255),
    session_ip character varying(100)
);


--
-- TOC entry 53 (OID 8222531)
-- Name: session_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE session_data FROM PUBLIC;


--
-- TOC entry 16 (OID 8222547)
-- Name: parameter_experience_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE parameter_experience_data_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 18 (OID 8222547)
-- Name: parameter_experience_data_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE parameter_experience_data_seq FROM PUBLIC;


--
-- TOC entry 54 (OID 8222549)
-- Name: parameter_experience_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE parameter_experience_data (
    parameter_experience_id integer DEFAULT nextval('parameter_experience_data_seq'::text) NOT NULL,
    parameter_id integer,
    experience_id integer,
    calc_status_code smallint DEFAULT 0 NOT NULL,
    calc_timestamp timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 55 (OID 8222549)
-- Name: parameter_experience_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE parameter_experience_data FROM PUBLIC;


--
-- TOC entry 19 (OID 8222554)
-- Name: frame_analysis_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE frame_analysis_data_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 21 (OID 8222554)
-- Name: frame_analysis_data_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE frame_analysis_data_seq FROM PUBLIC;


--
-- TOC entry 56 (OID 8222556)
-- Name: frame_analysis_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE frame_analysis_data (
    frame_analysis_id integer DEFAULT nextval('frame_analysis_data_seq'::text) NOT NULL,
    parameter_experience_id integer,
    frame_number integer DEFAULT 0 NOT NULL,
    object_number integer DEFAULT 0 NOT NULL,
    polygon_point_count integer DEFAULT 0 NOT NULL,
    polygon_point_list text NOT NULL,
    rgb_color integer DEFAULT 0 NOT NULL,
    bound_rect_points character varying(50) NOT NULL,
    centroid_x integer DEFAULT 0 NOT NULL,
    centroid_y integer DEFAULT 0 NOT NULL,
    area double precision DEFAULT 0 NOT NULL
);


--
-- TOC entry 57 (OID 8222556)
-- Name: frame_analysis_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE frame_analysis_data FROM PUBLIC;


--
-- TOC entry 22 (OID 8222569)
-- Name: run_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE run_data_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 24 (OID 8222569)
-- Name: run_data_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE run_data_seq FROM PUBLIC;


--
-- TOC entry 58 (OID 8222571)
-- Name: run_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE run_data (
    run_id integer DEFAULT nextval('run_data_seq'::text) NOT NULL,
    session_id integer,
    run_index integer DEFAULT 0 NOT NULL,
    run_start timestamp without time zone DEFAULT now() NOT NULL,
    run_stop timestamp without time zone,
    min_sd integer DEFAULT 0 NOT NULL
);


--
-- TOC entry 59 (OID 8222571)
-- Name: run_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE run_data FROM PUBLIC;


--
-- TOC entry 25 (OID 8222577)
-- Name: entity_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE entity_data_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 27 (OID 8222577)
-- Name: entity_data_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE entity_data_seq FROM PUBLIC;


--
-- TOC entry 60 (OID 8222579)
-- Name: entity_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE entity_data (
    entity_id integer DEFAULT nextval('entity_data_seq'::text) NOT NULL,
    run_id integer,
    occurance_count integer DEFAULT 1 NOT NULL
);


--
-- TOC entry 61 (OID 8222579)
-- Name: entity_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE entity_data FROM PUBLIC;


--
-- TOC entry 28 (OID 8222583)
-- Name: lexeme_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE lexeme_data_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 30 (OID 8222583)
-- Name: lexeme_data_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE lexeme_data_seq FROM PUBLIC;


--
-- TOC entry 62 (OID 8222585)
-- Name: lexeme_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE lexeme_data (
    lexeme_id integer DEFAULT nextval('lexeme_data_seq'::text) NOT NULL,
    run_id integer,
    lexeme character varying(50) NOT NULL,
    occurance_count integer DEFAULT 1 NOT NULL
);


--
-- TOC entry 63 (OID 8222585)
-- Name: lexeme_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE lexeme_data FROM PUBLIC;


--
-- TOC entry 31 (OID 8222589)
-- Name: experience_run_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE experience_run_data_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 33 (OID 8222589)
-- Name: experience_run_data_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE experience_run_data_seq FROM PUBLIC;


--
-- TOC entry 64 (OID 8222591)
-- Name: experience_run_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE experience_run_data (
    experience_run_id integer DEFAULT nextval('experience_run_data_seq'::text) NOT NULL,
    experience_id integer,
    run_id integer,
    experience_index integer DEFAULT 0 NOT NULL,
    experience_description character varying(100)
);


--
-- TOC entry 65 (OID 8222591)
-- Name: experience_run_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE experience_run_data FROM PUBLIC;


--
-- TOC entry 34 (OID 8222595)
-- Name: entity_lexeme_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE entity_lexeme_data_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 36 (OID 8222595)
-- Name: entity_lexeme_data_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE entity_lexeme_data_seq FROM PUBLIC;


--
-- TOC entry 66 (OID 8222597)
-- Name: entity_lexeme_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE entity_lexeme_data (
    entity_lexeme_id integer DEFAULT nextval('entity_lexeme_data_seq'::text) NOT NULL,
    entity_id integer,
    lexeme_id integer,
    occurance_count integer DEFAULT 1 NOT NULL
);


--
-- TOC entry 67 (OID 8222597)
-- Name: entity_lexeme_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE entity_lexeme_data FROM PUBLIC;


--
-- TOC entry 37 (OID 8222601)
-- Name: experience_entity_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE experience_entity_data_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 39 (OID 8222601)
-- Name: experience_entity_data_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE experience_entity_data_seq FROM PUBLIC;


--
-- TOC entry 68 (OID 8222603)
-- Name: experience_entity_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE experience_entity_data (
    experience_entity_id integer DEFAULT nextval('experience_entity_data_seq'::text) NOT NULL,
    experience_id integer,
    run_id integer,
    entity_id integer,
    resolution_code smallint DEFAULT 0 NOT NULL
);


--
-- TOC entry 69 (OID 8222603)
-- Name: experience_entity_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE experience_entity_data FROM PUBLIC;


--
-- TOC entry 40 (OID 8222607)
-- Name: experience_lexeme_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE experience_lexeme_data_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 42 (OID 8222607)
-- Name: experience_lexeme_data_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE experience_lexeme_data_seq FROM PUBLIC;


--
-- TOC entry 70 (OID 8222609)
-- Name: experience_lexeme_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE experience_lexeme_data (
    experience_lexeme_id integer DEFAULT nextval('experience_lexeme_data_seq'::text) NOT NULL,
    experience_id integer,
    run_id integer,
    lexeme_id integer,
    resolution_code smallint DEFAULT 0 NOT NULL,
    resolution_index integer DEFAULT 0 NOT NULL
);


--
-- TOC entry 71 (OID 8222609)
-- Name: experience_lexeme_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE experience_lexeme_data FROM PUBLIC;


--
-- TOC entry 43 (OID 8222614)
-- Name: attribute_value_data_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE attribute_value_data_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 45 (OID 8222614)
-- Name: attribute_value_data_seq; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE attribute_value_data_seq FROM PUBLIC;


--
-- TOC entry 72 (OID 8222616)
-- Name: attribute_value_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE attribute_value_data (
    attribute_value_id integer DEFAULT nextval('attribute_value_data_seq'::text) NOT NULL,
    attribute_list_id integer,
    run_id integer,
    entity_id integer,
    avg_value double precision DEFAULT 0 NOT NULL,
    std_deviation double precision DEFAULT 0 NOT NULL
);


--
-- TOC entry 73 (OID 8222616)
-- Name: attribute_value_data; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE attribute_value_data FROM PUBLIC;


--
-- Data for TOC entry 111 (OID 8222505)
-- Name: parameter_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY parameter_data (parameter_id, description, tmp_path, seg_color_radius, seg_spatial_radius, seg_min_region, seg_speed_up_code, frame_prefix, seg_prefix, poly_prefix, background_pixels, min_pixel_count, min_frame_count, reduce_color_code, notes, seg_speed_up_factor, edison_port_version) FROM stdin;
9	Animations	./images/animations/	6.5	7	20	1	/frame	/seg	/poly	20	500	7	0	All eight animations.	1	0
13	Videos-Best (Used for Descriptions)	./images/videos-best/	13	7	500	1	/frame	/seg	/poly	20	500	7	0	167 of 319 videos used to evaluate description performance.	0.5	0
11	Videos-OK (Used for Acquisition)	./images/videos-ok/	13	7	500	1	/frame	/seg	/poly	20	500	7	0	226 of 319 videos used to evaluate acquisition performance.	0.5	0
\.


--
-- Data for TOC entry 112 (OID 8222519)
-- Name: experience_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY experience_data (experience_id, description, video_path, tmp_path, experience_lexemes, notes) FROM stdin;
312	vase_20020809_down1L	./experiences/vase_20020809_down1L.avi	vase_20020809_down1L	hand putdown vase	3;split
313	vase_20020809_down1R	./experiences/vase_20020809_down1R.avi	vase_20020809_down1R	hand putdown vase	2;
314	vase_20020809_down2L	./experiences/vase_20020809_down2L.avi	vase_20020809_down2L	hand putdown vase	4;
315	vase_20020809_down2R	./experiences/vase_20020809_down2R.avi	vase_20020809_down2R	hand putdown vase	2;
316	vase_20020809_push1L	./experiences/vase_20020809_push1L.avi	vase_20020809_push1L	hand push vase	2;
317	vase_20020809_push1R	./experiences/vase_20020809_push1R.avi	vase_20020809_push1R	hand push vase	3;split
325	vase_20020809_up1R	./experiences/vase_20020809_up1R.avi	vase_20020809_up1R	hand pickup vase	2;
326	vase_20020809_up2L	./experiences/vase_20020809_up2L.avi	vase_20020809_up2L	hand pickup vase	2;
327	vase_20020809_up2R	./experiences/vase_20020809_up2R.avi	vase_20020809_up2R	hand pickup vase	2;
4	ani_ball_touch1	./experiences/ball_touch1.avi	ball_touch1	hand touch ball	5;animation
6	ani_cube_down1	./experiences/cube_down1.avi	cube_down1	hand putdown cube	5;animation
5	ani_cube_up1	./experiences/cube_up1.avi	cube_up1	hand pickup cube	5;animation
58	bluebowl_20020809_up1L	./experiences/bluebowl_20020809_up1L.avi	bluebowl_20020809_up1L	hand pickup bowl	3;drop
59	bluebowl_20020809_up1R	./experiences/bluebowl_20020809_up1R.avi	bluebowl_20020809_up1R	hand pickup bowl	2;
1	ani_ball_up1	./experiences/ball_up1.avi	ball_up1	hand pickup ball	5;animation
8	ani_cube_touch1	./experiences/cube_touch1.avi	cube_touch1	hand touch cube	5;animation
60	bluebowl_20020809_up2L	./experiences/bluebowl_20020809_up2L.avi	bluebowl_20020809_up2L	hand pickup bowl	3;drop
266	greenring_20020809_down2L	./experiences/greenring_20020809_down2L.avi	greenring_20020809_down2L	hand putdown ring	4;drop
267	greenring_20020809_down2R	./experiences/greenring_20020809_down2R.avi	greenring_20020809_down2R	hand putdown ring	3;drop
268	greenring_20020809_drop1L	./experiences/greenring_20020809_drop1L.avi	greenring_20020809_drop1L	hand drop ring	4;drop
269	greenring_20020809_drop1R	./experiences/greenring_20020809_drop1R.avi	greenring_20020809_drop1R	hand drop ring	4;drop
270	greenring_20020809_drop2L	./experiences/greenring_20020809_drop2L.avi	greenring_20020809_drop2L	hand drop ring	4;drop
271	greenring_20020809_drop2R	./experiences/greenring_20020809_drop2R.avi	greenring_20020809_drop2R	hand drop ring	3;drop
272	greenring_20020809_drop3L	./experiences/greenring_20020809_drop3L.avi	greenring_20020809_drop3L	hand drop ring	4;drop
273	greenring_20020809_roll1L	./experiences/greenring_20020809_roll1L.avi	greenring_20020809_roll1L	hand roll ring	4;drop
274	greenring_20020809_roll1R	./experiences/greenring_20020809_roll1R.avi	greenring_20020809_roll1R	hand roll ring	2;
275	greenring_20020809_roll2L	./experiences/greenring_20020809_roll2L.avi	greenring_20020809_roll2L	hand roll ring	4;
276	greenring_20020809_roll2R	./experiences/greenring_20020809_roll2R.avi	greenring_20020809_roll2R	hand roll ring	3;drop
277	greenring_20020809_touch1L	./experiences/greenring_20020809_touch1L.avi	greenring_20020809_touch1L	hand touch ring	4;drop
278	greenring_20020809_touch1R	./experiences/greenring_20020809_touch1R.avi	greenring_20020809_touch1R	hand touch ring	4;drop
279	greenring_20020809_touch2L	./experiences/greenring_20020809_touch2L.avi	greenring_20020809_touch2L	hand touch ring	4;drop
280	greenring_20020809_touch2R	./experiences/greenring_20020809_touch2R.avi	greenring_20020809_touch2R	hand touch ring	3;drop
281	greenring_20020809_up1L	./experiences/greenring_20020809_up1L.avi	greenring_20020809_up1L	hand pickup ring	3;drop
282	greenring_20020809_up1R	./experiences/greenring_20020809_up1R.avi	greenring_20020809_up1R	hand pickup ring	3;drop
283	greenring_20020809_up2L	./experiences/greenring_20020809_up2L.avi	greenring_20020809_up2L	hand pickup ring	4;drop
284	greenring_20020809_up2R	./experiences/greenring_20020809_up2R.avi	greenring_20020809_up2R	hand pickup ring	4;drop
285	orangecup_20020809_down1L	./experiences/orangecup_20020809_down1L.avi	orangecup_20020809_down1L	hand putdown cup	2;
286	orangecup_20020809_down1R	./experiences/orangecup_20020809_down1R.avi	orangecup_20020809_down1R	hand putdown cup	3;shrunk
287	orangecup_20020809_down2R	./experiences/orangecup_20020809_down2R.avi	orangecup_20020809_down2R	hand putdown cup	4;shrunk
288	orangecup_20020809_pull1L	./experiences/orangecup_20020809_pull1L.avi	orangecup_20020809_pull1L	hand pull cup	3;drop
289	orangecup_20020809_pull1R	./experiences/orangecup_20020809_pull1R.avi	orangecup_20020809_pull1R	hand pull cup	3;drop
290	orangecup_20020809_pull2L	./experiences/orangecup_20020809_pull2L.avi	orangecup_20020809_pull2L	hand pull cup	4;drop
291	orangecup_20020809_pull2R	./experiences/orangecup_20020809_pull2R.avi	orangecup_20020809_pull2R	hand pull cup	3;drop
292	orangecup_20020809_push1L	./experiences/orangecup_20020809_push1L.avi	orangecup_20020809_push1L	hand push cup	3;drop
293	orangecup_20020809_push1R	./experiences/orangecup_20020809_push1R.avi	orangecup_20020809_push1R	hand push cup	4;drop
294	orangecup_20020809_push2L	./experiences/orangecup_20020809_push2L.avi	orangecup_20020809_push2L	hand push cup	4;drop
295	orangecup_20020809_push2R	./experiences/orangecup_20020809_push2R.avi	orangecup_20020809_push2R	hand push cup	4;drop
296	orangecup_20020809_tip1L	./experiences/orangecup_20020809_tip1L.avi	orangecup_20020809_tip1L	hand tipover cup	3;split
297	orangecup_20020809_tip1R	./experiences/orangecup_20020809_tip1R.avi	orangecup_20020809_tip1R	hand tipover cup	2;
298	orangecup_20020809_tip2L	./experiences/orangecup_20020809_tip2L.avi	orangecup_20020809_tip2L	hand tipover cup	3;split
299	orangecup_20020809_tip2R	./experiences/orangecup_20020809_tip2R.avi	orangecup_20020809_tip2R	hand tipover cup	3;split
300	orangecup_20020809_touch1L	./experiences/orangecup_20020809_touch1L.avi	orangecup_20020809_touch1L	hand touch cup	2;
301	orangecup_20020809_touch1R	./experiences/orangecup_20020809_touch1R.avi	orangecup_20020809_touch1R	hand touch cup	2;
302	orangecup_20020809_touch2L	./experiences/orangecup_20020809_touch2L.avi	orangecup_20020809_touch2L	hand touch cup	2;
303	orangecup_20020809_touch2R	./experiences/orangecup_20020809_touch2R.avi	orangecup_20020809_touch2R	hand touch cup	2;
304	orangecup_20020809_up1L	./experiences/orangecup_20020809_up1L.avi	orangecup_20020809_up1L	hand pickup cup	2;
305	orangecup_20020809_up1R	./experiences/orangecup_20020809_up1R.avi	orangecup_20020809_up1R	hand pickup cup	2;
306	orangecup_20020809_up2L	./experiences/orangecup_20020809_up2L.avi	orangecup_20020809_up2L	hand pickup cup	2;
307	orangecup_20020809_up2R	./experiences/orangecup_20020809_up2R.avi	orangecup_20020809_up2R	hand pickup cup	2;
308	rings_20020619_stack1R	./experiences/rings_20020619_stack1R.avi	rings_20020619_stack1R	hand stack rings	4;drop
309	rings_20020619_unstack1L	./experiences/rings_20020619_unstack1L.avi	rings_20020619_unstack1L	hand unstack rings	4;drop
310	rings_20020619_unstack1R	./experiences/rings_20020619_unstack1R.avi	rings_20020619_unstack1R	hand unstack rings	4;drop
311	rings_20020619_unstack2R	./experiences/rings_20020619_unstack2R.avi	rings_20020619_unstack2R	hand unstack rings	4;drop
220	greenbowl_20020809_drop2R	./experiences/greenbowl_20020809_drop2R.avi	greenbowl_20020809_drop2R	hand drop bowl	4;
221	greenbowl_20020809_pull1L	./experiences/greenbowl_20020809_pull1L.avi	greenbowl_20020809_pull1L	hand pull bowl	2;
222	greenbowl_20020809_pull1R	./experiences/greenbowl_20020809_pull1R.avi	greenbowl_20020809_pull1R	hand pull bowl	2;
223	greenbowl_20020809_pull2L	./experiences/greenbowl_20020809_pull2L.avi	greenbowl_20020809_pull2L	hand pull bowl	2;
224	greenbowl_20020809_pull2R	./experiences/greenbowl_20020809_pull2R.avi	greenbowl_20020809_pull2R	hand pull bowl	2;
225	greenbowl_20020809_push1L	./experiences/greenbowl_20020809_push1L.avi	greenbowl_20020809_push1L	hand push bowl	2;
226	greenbowl_20020809_push1R	./experiences/greenbowl_20020809_push1R.avi	greenbowl_20020809_push1R	hand push bowl	4;drop
227	greenbowl_20020809_push2L	./experiences/greenbowl_20020809_push2L.avi	greenbowl_20020809_push2L	hand push bowl	4;drop
228	greenbowl_20020809_push2R	./experiences/greenbowl_20020809_push2R.avi	greenbowl_20020809_push2R	hand push bowl	3;drop
229	greenbowl_20020809_touch1L	./experiences/greenbowl_20020809_touch1L.avi	greenbowl_20020809_touch1L	hand touch ring	2;
230	greenbowl_20020809_touch1R	./experiences/greenbowl_20020809_touch1R.avi	greenbowl_20020809_touch1R	hand touch ring	2;
231	greenbowl_20020809_touch2L	./experiences/greenbowl_20020809_touch2L.avi	greenbowl_20020809_touch2L	hand touch ring	2;
232	greenbowl_20020809_touch2R	./experiences/greenbowl_20020809_touch2R.avi	greenbowl_20020809_touch2R	hand touch ring	2;
233	greenbowl_20020809_up1L	./experiences/greenbowl_20020809_up1L.avi	greenbowl_20020809_up1L	hand pickup bowl	3;drop
234	greenbowl_20020809_up1R	./experiences/greenbowl_20020809_up1R.avi	greenbowl_20020809_up1R	hand pickup bowl	2;
235	greenring_20020619_down1L	./experiences/greenring_20020619_down1L.avi	greenring_20020619_down1L	hand putdown ring	4;
236	greenring_20020619_down1R	./experiences/greenring_20020619_down1R.avi	greenring_20020619_down1R	hand putdown ring	4;drop
237	greenring_20020619_down2L	./experiences/greenring_20020619_down2L.avi	greenring_20020619_down2L	hand putdown ring	4;
238	greenring_20020619_down2R	./experiences/greenring_20020619_down2R.avi	greenring_20020619_down2R	hand putdown ring	4;drop
239	greenring_20020619_drop1R	./experiences/greenring_20020619_drop1R.avi	greenring_20020619_drop1R	hand drop ring	3;drop
240	greenring_20020619_drop2R	./experiences/greenring_20020619_drop2R.avi	greenring_20020619_drop2R	hand drop ring	4;drop
241	greenring_20020619_drop3R	./experiences/greenring_20020619_drop3R.avi	greenring_20020619_drop3R	hand drop ring	4;drop
242	greenring_20020619_drop4R	./experiences/greenring_20020619_drop4R.avi	greenring_20020619_drop4R	hand drop ring	4;
243	greenring_20020619_pull1L	./experiences/greenring_20020619_pull1L.avi	greenring_20020619_pull1L	hand pull ring	2;
244	greenring_20020619_pull1R	./experiences/greenring_20020619_pull1R.avi	greenring_20020619_pull1R	hand pull ring	3;drop
245	greenring_20020619_slide1L	./experiences/greenring_20020619_slide1L.avi	greenring_20020619_slide1L	hand slide ring	2;
246	greenring_20020619_slide1R	./experiences/greenring_20020619_slide1R.avi	greenring_20020619_slide1R	hand slide ring	3;drop
247	greenring_20020619_tilt1L	./experiences/greenring_20020619_tilt1L.avi	greenring_20020619_tilt1L	hand tilt ring	2;
248	greenring_20020619_tilt1R	./experiences/greenring_20020619_tilt1R.avi	greenring_20020619_tilt1R	hand tilt ring	2;
249	greenring_20020619_tilt2L	./experiences/greenring_20020619_tilt2L.avi	greenring_20020619_tilt2L	hand tilt ring	2;
250	greenring_20020619_tilt2R	./experiences/greenring_20020619_tilt2R.avi	greenring_20020619_tilt2R	hand tilt ring	4;drop
251	greenring_20020619_tipover1L	./experiences/greenring_20020619_tipover1L.avi	greenring_20020619_tipover1L	hand tipover ring	2;
252	greenring_20020619_tipover1R	./experiences/greenring_20020619_tipover1R.avi	greenring_20020619_tipover1R	hand tipover ring	4;drop
253	greenring_20020619_tipover2L	./experiences/greenring_20020619_tipover2L.avi	greenring_20020619_tipover2L	hand tipover ring	2;
254	greenring_20020619_tipover2R	./experiences/greenring_20020619_tipover2R.avi	greenring_20020619_tipover2R	hand tipover ring	4;drop
255	greenring_20020619_touch1L	./experiences/greenring_20020619_touch1L.avi	greenring_20020619_touch1L	hand touch ring	2;
256	greenring_20020619_touch1R	./experiences/greenring_20020619_touch1R.avi	greenring_20020619_touch1R	hand touch ring	2;
257	greenring_20020619_touch2L	./experiences/greenring_20020619_touch2L.avi	greenring_20020619_touch2L	hand touch ring	2;
258	greenring_20020619_touch2R	./experiences/greenring_20020619_touch2R.avi	greenring_20020619_touch2R	hand touch ring	2;
259	greenring_20020619_up1L	./experiences/greenring_20020619_up1L.avi	greenring_20020619_up1L	hand pickup ring	2;
260	greenring_20020619_up1R	./experiences/greenring_20020619_up1R.avi	greenring_20020619_up1R	hand pickup ring	4;drop
261	greenring_20020619_up2L	./experiences/greenring_20020619_up2L.avi	greenring_20020619_up2L	hand pickup ring	4;
262	greenring_20020619_up2R	./experiences/greenring_20020619_up2R.avi	greenring_20020619_up2R	hand pickup ring	4;drop
263	greenring_20020619_up3L	./experiences/greenring_20020619_up3L.avi	greenring_20020619_up3L	hand pickup ring	4;
264	greenring_20020809_down1L	./experiences/greenring_20020809_down1L.avi	greenring_20020809_down1L	hand putdown ring	4;drop
265	greenring_20020809_down1R	./experiences/greenring_20020809_down1R.avi	greenring_20020809_down1R	hand putdown ring	3;drop
172	box_20020809_up1L	./experiences/box_20020809_up1L.avi	box_20020809_up1L	hand pickup box	2;
173	box_20020809_up1R	./experiences/box_20020809_up1R.avi	box_20020809_up1R	hand pickup box	2;
174	box_20020809_up2L	./experiences/box_20020809_up2L.avi	box_20020809_up2L	hand pickup box	2;
175	box_20020809_up2R	./experiences/box_20020809_up2R.avi	box_20020809_up2R	hand pickup box	2;
176	cup_20020619_down1L	./experiences/cup_20020619_down1L.avi	cup_20020619_down1L	hand putdown cup	2;
177	cup_20020619_down1R	./experiences/cup_20020619_down1R.avi	cup_20020619_down1R	hand putdown cup	4;drop
178	cup_20020619_down2L	./experiences/cup_20020619_down2L.avi	cup_20020619_down2L	hand putdown cup	2;
179	cup_20020619_down2R	./experiences/cup_20020619_down2R.avi	cup_20020619_down2R	hand putdown cup	4;drop
180	cup_20020619_pull1L	./experiences/cup_20020619_pull1L.avi	cup_20020619_pull1L	hand pull cup	2;
181	cup_20020619_pull1R	./experiences/cup_20020619_pull1R.avi	cup_20020619_pull1R	hand pull cup	4;drop
182	cup_20020619_slide1L	./experiences/cup_20020619_slide1L.avi	cup_20020619_slide1L	hand slide cup	3;drop
183	cup_20020619_slide1R	./experiences/cup_20020619_slide1R.avi	cup_20020619_slide1R	hand slide cup	3;drop
184	cup_20020619_slide2L	./experiences/cup_20020619_slide2L.avi	cup_20020619_slide2L	hand slide cup	2;
185	cup_20020619_tipover1L	./experiences/cup_20020619_tipover1L.avi	cup_20020619_tipover1L	hand tipover cup	2;
186	cup_20020619_tipover1R	./experiences/cup_20020619_tipover1R.avi	cup_20020619_tipover1R	hand tipover cup	2;
187	cup_20020619_tipover2L	./experiences/cup_20020619_tipover2L.avi	cup_20020619_tipover2L	hand tipover cup	2;
188	cup_20020619_tipover2R	./experiences/cup_20020619_tipover2R.avi	cup_20020619_tipover2R	hand tipover cup	2;
189	cup_20020619_touch1L	./experiences/cup_20020619_touch1L.avi	cup_20020619_touch1L	hand touch cup	2;
190	cup_20020619_touch1R	./experiences/cup_20020619_touch1R.avi	cup_20020619_touch1R	hand touch cup	2;
191	cup_20020619_touch2L	./experiences/cup_20020619_touch2L.avi	cup_20020619_touch2L	hand touch cup	2;
192	cup_20020619_touch2R	./experiences/cup_20020619_touch2R.avi	cup_20020619_touch2R	hand touch cup	2;
193	cup_20020619_up1L	./experiences/cup_20020619_up1L.avi	cup_20020619_up1L	hand pickup cup	2;
194	cup_20020619_up1R	./experiences/cup_20020619_up1R.avi	cup_20020619_up1R	hand pickup cup	2;
195	cup_20020619_up2L	./experiences/cup_20020619_up2L.avi	cup_20020619_up2L	hand pickup cup	2;
196	cup_20020619_up2R	./experiences/cup_20020619_up2R.avi	cup_20020619_up2R	hand pickup cup	3;drop
197	garfield_20020809_up1L	./experiences/garfield_20020809_up1L.avi	garfield_20020809_up1L	hand pickup garfield	3;split
198	garfield_20020809_down1L	./experiences/garfield_20020809_down1L.avi	garfield_20020809_down1L	hand putdown garfield	4;split
199	garfield_20020809_down1R	./experiences/garfield_20020809_down1R.avi	garfield_20020809_down1R	hand putdown garfield	4;split
200	garfield_20020809_down2L	./experiences/garfield_20020809_down2L.avi	garfield_20020809_down2L	hand putdown garfield	3;drop and split
201	garfield_20020809_down2R	./experiences/garfield_20020809_down2R.avi	garfield_20020809_down2R	hand putdown garfield	3;split
202	garfield_20020809_drop1L	./experiences/garfield_20020809_drop1L.avi	garfield_20020809_drop1L	hand drop garfield	3;split
203	garfield_20020809_drop1R	./experiences/garfield_20020809_drop1R.avi	garfield_20020809_drop1R	hand drop garfield	3;split
204	garfield_20020809_drop2L	./experiences/garfield_20020809_drop2L.avi	garfield_20020809_drop2L	hand drop garfield	3;split
205	garfield_20020809_drop2R	./experiences/garfield_20020809_drop2R.avi	garfield_20020809_drop2R	hand drop garfield	4;split
206	garfield_20020809_touch1L	./experiences/garfield_20020809_touch1L.avi	garfield_20020809_touch1L	hand touch garfield	3;split
207	garfield_20020809_touch1R	./experiences/garfield_20020809_touch1R.avi	garfield_20020809_touch1R	hand touch garfield	3;drop and split
208	garfield_20020809_touch2L	./experiences/garfield_20020809_touch2L.avi	garfield_20020809_touch2L	hand touch garfield	3;split
209	garfield_20020809_touch2R	./experiences/garfield_20020809_touch2R.avi	garfield_20020809_touch2R	hand touch garfield	3;split
210	garfield_20020809_up1R	./experiences/garfield_20020809_up1R.avi	garfield_20020809_up1R	hand pickup garfield	3;split
211	garfield_20020809_up2L	./experiences/garfield_20020809_up2L.avi	garfield_20020809_up2L	hand pickup garfield	3;drop and split
212	garfield_20020809_up2R	./experiences/garfield_20020809_up2R.avi	garfield_20020809_up2R	hand pickup garfield	3;split
213	greenbowl_20020809_down1L	./experiences/greenbowl_20020809_down1L.avi	greenbowl_20020809_down1L	hand putdown bowl	4;drop
214	greenbowl_20020809_down1R	./experiences/greenbowl_20020809_down1R.avi	greenbowl_20020809_down1R	hand putdown bowl	2;
215	greenbowl_20020809_down2L	./experiences/greenbowl_20020809_down2L.avi	greenbowl_20020809_down2L	hand putdown bowl	3;drop
216	greenbowl_20020809_down2R	./experiences/greenbowl_20020809_down2R.avi	greenbowl_20020809_down2R	hand putdown bowl	2;
217	greenbowl_20020809_drop1L	./experiences/greenbowl_20020809_drop1L.avi	greenbowl_20020809_drop1L	hand drop bowl	2;
218	greenbowl_20020809_drop1R	./experiences/greenbowl_20020809_drop1R.avi	greenbowl_20020809_drop1R	hand drop bowl	2;
219	greenbowl_20020809_drop2L	./experiences/greenbowl_20020809_drop2L.avi	greenbowl_20020809_drop2L	hand drop bowl	2;
121	book_20020809_slide1R	./experiences/book_20020809_slide1R.avi	book_20020809_slide1R	hand slide book	4;split
122	book_20020809_slide2L	./experiences/book_20020809_slide2L.avi	book_20020809_slide2L	hand slide book	3;split
123	book_20020809_slide2R	./experiences/book_20020809_slide2R.avi	book_20020809_slide2R	hand slide book	3;split
124	book_20020809_tilt1L	./experiences/book_20020809_tilt1L.avi	book_20020809_tilt1L	hand tipover book	4;split
125	book_20020809_tilt1R	./experiences/book_20020809_tilt1R.avi	book_20020809_tilt1R	hand tipover book	4;split
126	book_20020809_tilt2L	./experiences/book_20020809_tilt2L.avi	book_20020809_tilt2L	hand tipover book	4;split
127	book_20020809_tilt2R	./experiences/book_20020809_tilt2R.avi	book_20020809_tilt2R	hand tipover book	4;split
128	book_20020809_touch1L	./experiences/book_20020809_touch1L.avi	book_20020809_touch1L	hand touch book	4;
129	book_20020809_touch1R	./experiences/book_20020809_touch1R.avi	book_20020809_touch1R	hand touch book	3;split
130	book_20020809_touch2L	./experiences/book_20020809_touch2L.avi	book_20020809_touch2L	hand touch book	4;
131	book_20020809_touch2R	./experiences/book_20020809_touch2R.avi	book_20020809_touch2R	hand touch book	2;
132	book_20020809_up1L	./experiences/book_20020809_up1L.avi	book_20020809_up1L	hand pickup book	2;
133	book_20020809_up1R	./experiences/book_20020809_up1R.avi	book_20020809_up1R	hand pickup book	2;
134	book_20020809_up2L	./experiences/book_20020809_up2L.avi	book_20020809_up2L	hand pickup book	2;
135	book_20020809_up2R	./experiences/book_20020809_up2R.avi	book_20020809_up2R	hand pickup book	2;
136	box_20020619_down1L	./experiences/box_20020619_down1L.avi	box_20020619_down1L	hand putdown box	2;
137	box_20020619_down1R	./experiences/box_20020619_down1R.avi	box_20020619_down1R	hand putdown box	3;merge
138	box_20020619_down2L	./experiences/box_20020619_down2L.avi	box_20020619_down2L	hand putdown box	4;merge
139	box_20020619_down2R	./experiences/box_20020619_down2R.avi	box_20020619_down2R	hand putdown box	3;merge
140	box_20020619_pull1L	./experiences/box_20020619_pull1L.avi	box_20020619_pull1L	hand pull box	2;
141	box_20020619_pull1R	./experiences/box_20020619_pull1R.avi	box_20020619_pull1R	hand pull box	3;drop
142	box_20020619_slide1L	./experiences/box_20020619_slide1L.avi	box_20020619_slide1L	hand slide box	2;
143	box_20020619_slide1R	./experiences/box_20020619_slide1R.avi	box_20020619_slide1R	hand slide box	3;drop
144	box_20020619_tipover1L	./experiences/box_20020619_tipover1L.avi	box_20020619_tipover1L	hand tipover box	2;
145	box_20020619_tipover1R	./experiences/box_20020619_tipover1R.avi	box_20020619_tipover1R	hand tipover box	2;
146	box_20020619_tipover2L	./experiences/box_20020619_tipover2L.avi	box_20020619_tipover2L	hand tipover box	2;
147	box_20020619_tipover2R	./experiences/box_20020619_tipover2R.avi	box_20020619_tipover2R	hand tipover box	2;
148	box_20020619_touch1L	./experiences/box_20020619_touch1L.avi	box_20020619_touch1L	hand touch box	2;
149	box_20020619_touch1R	./experiences/box_20020619_touch1R.avi	box_20020619_touch1R	hand touch box	2;
150	box_20020619_touch2L	./experiences/box_20020619_touch2L.avi	box_20020619_touch2L	hand touch box	2;
151	box_20020619_touch2R	./experiences/box_20020619_touch2R.avi	box_20020619_touch2R	hand touch box	2;
152	box_20020619_up1L	./experiences/box_20020619_up1L.avi	box_20020619_up1L	hand pickup box	2;
153	box_20020619_up1R	./experiences/box_20020619_up1R.avi	box_20020619_up1R	hand pickup box	3;drop
154	box_20020619_up2L	./experiences/box_20020619_up2L.avi	box_20020619_up2L	hand pickup box	4;merge
155	box_20020619_up3L	./experiences/box_20020619_up3L.avi	box_20020619_up3L	hand pickup box	3;merge
156	box_20020809_down1L	./experiences/box_20020809_down1L.avi	box_20020809_down1L	hand putdown box	2;
157	box_20020809_down1R	./experiences/box_20020809_down1R.avi	box_20020809_down1R	hand putdown box	2;
158	box_20020809_down2L	./experiences/box_20020809_down2L.avi	box_20020809_down2L	hand putdown box	2;
159	box_20020809_down2R	./experiences/box_20020809_down2R.avi	box_20020809_down2R	hand putdown box	2;
160	box_20020809_drop1L	./experiences/box_20020809_drop1L.avi	box_20020809_drop1L	hand drop box	2;
161	box_20020809_drop1R	./experiences/box_20020809_drop1R.avi	box_20020809_drop1R	hand drop box	4;
162	box_20020809_drop2L	./experiences/box_20020809_drop2L.avi	box_20020809_drop2L	hand drop box	2;
163	box_20020809_drop2R	./experiences/box_20020809_drop2R.avi	box_20020809_drop2R	hand drop box	2;
164	box_20020809_push1L	./experiences/box_20020809_push1L.avi	box_20020809_push1L	hand push box	2;
165	box_20020809_push1R	./experiences/box_20020809_push1R.avi	box_20020809_push1R	hand push box	2;
166	box_20020809_push2L	./experiences/box_20020809_push2L.avi	box_20020809_push2L	hand push box	4;
167	box_20020809_push2R	./experiences/box_20020809_push2R.avi	box_20020809_push2R	hand push box	2;
168	box_20020809_touch1L	./experiences/box_20020809_touch1L.avi	box_20020809_touch1L	hand touch box	2;
169	box_20020809_touch1R	./experiences/box_20020809_touch1R.avi	box_20020809_touch1R	hand touch box	2;
170	box_20020809_touch2L	./experiences/box_20020809_touch2L.avi	box_20020809_touch2L	hand touch box	2;
171	box_20020809_touch2R	./experiences/box_20020809_touch2R.avi	box_20020809_touch2R	hand touch box	2;
74	bluering_20020619_tilt1L	./experiences/bluering_20020619_tilt1L.avi	bluering_20020619_tilt1L	hand tilt ring	2;
75	bluering_20020619_tilt1R	./experiences/bluering_20020619_tilt1R.avi	bluering_20020619_tilt1R	hand tilt ring	2;
76	bluering_20020619_tilt2L	./experiences/bluering_20020619_tilt2L.avi	bluering_20020619_tilt2L	hand tilt ring	2;
77	bluering_20020619_tilt2R	./experiences/bluering_20020619_tilt2R.avi	bluering_20020619_tilt2R	hand tilt ring	2;
78	bluering_20020619_tilt3R	./experiences/bluering_20020619_tilt3R.avi	bluering_20020619_tilt3R	hand tilt ring	4;
79	bluering_20020619_tipover1L	./experiences/bluering_20020619_tipover1L.avi	bluering_20020619_tipover1L	hand tipover ring	2;
80	bluering_20020619_tipover1R	./experiences/bluering_20020619_tipover1R.avi	bluering_20020619_tipover1R	hand tipover ring	2;
81	bluering_20020619_tipover2L	./experiences/bluering_20020619_tipover2L.avi	bluering_20020619_tipover2L	hand tipover ring	2;
82	bluering_20020619_tipover2R	./experiences/bluering_20020619_tipover2R.avi	bluering_20020619_tipover2R	hand tipover ring	2;
83	bluering_20020619_touch1L	./experiences/bluering_20020619_touch1L.avi	bluering_20020619_touch1L	hand touch ring	2;
84	bluering_20020619_touch1R	./experiences/bluering_20020619_touch1R.avi	bluering_20020619_touch1R	hand touch ring	2;
85	bluering_20020619_touch2L	./experiences/bluering_20020619_touch2L.avi	bluering_20020619_touch2L	hand touch ring	2;
86	bluering_20020619_touch3L	./experiences/bluering_20020619_touch3L.avi	bluering_20020619_touch3L	hand touch ring	2;
87	bluering_20020619_up1L	./experiences/bluering_20020619_up1L.avi	bluering_20020619_up1L	hand pickup ring	4;
88	bluering_20020619_up1R	./experiences/bluering_20020619_up1R.avi	bluering_20020619_up1R	hand pickup ring	4;
89	bluering_20020619_up2L	./experiences/bluering_20020619_up2L.avi	bluering_20020619_up2L	hand pickup ring	4;
90	bluering_20020619_up3L	./experiences/bluering_20020619_up3L.avi	bluering_20020619_up3L	hand pickup ring	2;
91	bluering_20020809_down1L	./experiences/bluering_20020809_down1L.avi	bluering_20020809_down1L	hand putdown ring	4;drop
92	bluering_20020809_down1R	./experiences/bluering_20020809_down1R.avi	bluering_20020809_down1R	hand putdown ring	4;drop
93	bluering_20020809_down2L	./experiences/bluering_20020809_down2L.avi	bluering_20020809_down2L	hand putdown ring	4;drop
94	bluering_20020809_down2R	./experiences/bluering_20020809_down2R.avi	bluering_20020809_down2R	hand putdown ring	4;drop
95	bluering_20020809_drop1L	./experiences/bluering_20020809_drop1L.avi	bluering_20020809_drop1L	hand drop ring	4;drop
96	bluering_20020809_drop1R	./experiences/bluering_20020809_drop1R.avi	bluering_20020809_drop1R	hand drop ring	4;drop
97	bluering_20020809_drop2L	./experiences/bluering_20020809_drop2L.avi	bluering_20020809_drop2L	hand drop ring	4;drop
98	bluering_20020809_drop2R	./experiences/bluering_20020809_drop2R.avi	bluering_20020809_drop2R	hand drop ring	4;drop
99	bluering_20020809_roll1L	./experiences/bluering_20020809_roll1L.avi	bluering_20020809_roll1L	hand roll ring	4;
100	bluering_20020809_roll1R	./experiences/bluering_20020809_roll1R.avi	bluering_20020809_roll1R	hand roll ring	4;drop
101	bluering_20020809_roll2L	./experiences/bluering_20020809_roll2L.avi	bluering_20020809_roll2L	hand roll ring	4;
102	bluering_20020809_roll2R	./experiences/bluering_20020809_roll2R.avi	bluering_20020809_roll2R	hand roll ring	3;drop
103	bluering_20020809_rollchaseL	./experiences/bluering_20020809_rollchaseL.avi	bluering_20020809_rollchaseL	hand roll ring	2;
104	bluering_20020809_touch1L	./experiences/bluering_20020809_touch1L.avi	bluering_20020809_touch1L	hand touch ring	4;drop
105	bluering_20020809_touch1R	./experiences/bluering_20020809_touch1R.avi	bluering_20020809_touch1R	hand touch ring	3;drop
106	bluering_20020809_touch2L	./experiences/bluering_20020809_touch2L.avi	bluering_20020809_touch2L	hand touch ring	4;drop
107	bluering_20020809_touch2R	./experiences/bluering_20020809_touch2R.avi	bluering_20020809_touch2R	hand touch ring	3;drop
108	bluering_20020809_up1L	./experiences/bluering_20020809_up1L.avi	bluering_20020809_up1L	hand pickup ring	3;drop
109	bluering_20020809_up1R	./experiences/bluering_20020809_up1R.avi	bluering_20020809_up1R	hand pickup ring	3;drop
110	bluering_20020809_up2L	./experiences/bluering_20020809_up2L.avi	bluering_20020809_up2L	hand pickup ring	4;drop
111	bluering_20020809_up2R	./experiences/bluering_20020809_up2R.avi	bluering_20020809_up2R	hand pickup ring	4;drop
112	book_20020809_down1L	./experiences/book_20020809_down1L.avi	book_20020809_down1L	hand putdown book	2;
113	book_20020809_down1R	./experiences/book_20020809_down1R.avi	book_20020809_down1R	hand putdown book	2;
114	book_20020809_down2L	./experiences/book_20020809_down2L.avi	book_20020809_down2L	hand putdown book	2;
115	book_20020809_down2R	./experiences/book_20020809_down2R.avi	book_20020809_down2R	hand putdown book	2;
116	book_20020809_pull1L	./experiences/book_20020809_pull1L.avi	book_20020809_pull1L	hand pull book	3;split
117	book_20020809_pull1R	./experiences/book_20020809_pull1R.avi	book_20020809_pull1R	hand pull book	4;split
118	book_20020809_pull2L	./experiences/book_20020809_pull2L.avi	book_20020809_pull2L	hand pull book	4;split
119	book_20020809_pull2R	./experiences/book_20020809_pull2R.avi	book_20020809_pull2R	hand pull book	3;split
120	book_20020809_slide1L	./experiences/book_20020809_slide1L.avi	book_20020809_slide1L	hand slide book	3;split
25	ball_20020809_drop2L	./experiences/ball_20020809_drop2L.avi	ball_20020809_drop2L	hand drop ball	2;
26	ball_20020809_drop2R	./experiences/ball_20020809_drop2R.avi	ball_20020809_drop2R	hand drop ball	2;
27	ball_20020809_roll1L	./experiences/ball_20020809_roll1L.avi	ball_20020809_roll1L	hand roll ball	2;
28	ball_20020809_roll1R	./experiences/ball_20020809_roll1R.avi	ball_20020809_roll1R	hand roll ball	2;
29	ball_20020809_roll2L	./experiences/ball_20020809_roll2L.avi	ball_20020809_roll2L	hand roll ball	2;
30	ball_20020809_roll2R	./experiences/ball_20020809_roll2R.avi	ball_20020809_roll2R	hand roll ball	4;
31	ball_20020809_touch1L	./experiences/ball_20020809_touch1L.avi	ball_20020809_touch1L	hand touch ball	2;
32	ball_20020809_touch1R	./experiences/ball_20020809_touch1R.avi	ball_20020809_touch1R	hand touch ball	2;
33	ball_20020809_touch2L	./experiences/ball_20020809_touch2L.avi	ball_20020809_touch2L	hand touch ball	2;
34	ball_20020809_touch2R	./experiences/ball_20020809_touch2R.avi	ball_20020809_touch2R	hand touch ball	2;
35	ball_20020809_up1L	./experiences/ball_20020809_up1L.avi	ball_20020809_up1L	hand pickup ball	2;
36	ball_20020809_up1R	./experiences/ball_20020809_up1R.avi	ball_20020809_up1R	hand pickup ball	2;
37	ball_20020809_up2L	./experiences/ball_20020809_up2L.avi	ball_20020809_up2L	hand pickup ball	2;
38	ball_20020809_up2R	./experiences/ball_20020809_up2R.avi	ball_20020809_up2R	hand pickup ball	2;
39	bluebowl_20020809_down1L	./experiences/bluebowl_20020809_down1L.avi	bluebowl_20020809_down1L	hand putdown bowl	3;drop
40	bluebowl_20020809_down1R	./experiences/bluebowl_20020809_down1R.avi	bluebowl_20020809_down1R	hand putdown bowl	2;
41	bluebowl_20020809_down2R	./experiences/bluebowl_20020809_down2R.avi	bluebowl_20020809_down2R	hand putdown bowl	2;
42	bluebowl_20020809_drop1L	./experiences/bluebowl_20020809_drop1L.avi	bluebowl_20020809_drop1L	hand drop bowl	2;
43	bluebowl_20020809_drop1R	./experiences/bluebowl_20020809_drop1R.avi	bluebowl_20020809_drop1R	hand drop bowl	4;drop
44	bluebowl_20020809_drop2L	./experiences/bluebowl_20020809_drop2L.avi	bluebowl_20020809_drop2L	hand drop bowl	2;
45	bluebowl_20020809_drop2R	./experiences/bluebowl_20020809_drop2R.avi	bluebowl_20020809_drop2R	hand drop bowl	4;drop
46	bluebowl_20020809_pull1L	./experiences/bluebowl_20020809_pull1L.avi	bluebowl_20020809_pull1L	hand pull bowl	2;
47	bluebowl_20020809_pull1R	./experiences/bluebowl_20020809_pull1R.avi	bluebowl_20020809_pull1R	hand pull bowl	2;
48	bluebowl_20020809_pull2L	./experiences/bluebowl_20020809_pull2L.avi	bluebowl_20020809_pull2L	hand pull bowl	2;
49	bluebowl_20020809_pull2R	./experiences/bluebowl_20020809_pull2R.avi	bluebowl_20020809_pull2R	hand pull bowl	2;
50	bluebowl_20020809_push1L	./experiences/bluebowl_20020809_push1L.avi	bluebowl_20020809_push1L	hand push bowl	2;
51	bluebowl_20020809_push1R	./experiences/bluebowl_20020809_push1R.avi	bluebowl_20020809_push1R	hand push bowl	2;
52	bluebowl_20020809_push2L	./experiences/bluebowl_20020809_push2L.avi	bluebowl_20020809_push2L	hand push bowl	2;
53	bluebowl_20020809_push2R	./experiences/bluebowl_20020809_push2R.avi	bluebowl_20020809_push2R	hand push bowl	2;
54	bluebowl_20020809_touch1L	./experiences/bluebowl_20020809_touch1L.avi	bluebowl_20020809_touch1L	hand touch bowl	2;
55	bluebowl_20020809_touch1R	./experiences/bluebowl_20020809_touch1R.avi	bluebowl_20020809_touch1R	hand touch bowl	2;
56	bluebowl_20020809_touch2L	./experiences/bluebowl_20020809_touch2L.avi	bluebowl_20020809_touch2L	hand touch bowl	2;
57	bluebowl_20020809_touch2R	./experiences/bluebowl_20020809_touch2R.avi	bluebowl_20020809_touch2R	hand touch bowl	2;
61	bluebowl_20020809_up2R	./experiences/bluebowl_20020809_up2R.avi	bluebowl_20020809_up2R	hand pickup bowl	2;
62	bluering_20020619_down1L	./experiences/bluering_20020619_down1L.avi	bluering_20020619_down1L	hand putdown ring	4;
63	bluering_20020619_down1R	./experiences/bluering_20020619_down1R.avi	bluering_20020619_down1R	hand putdown ring	4;
64	bluering_20020619_down2L	./experiences/bluering_20020619_down2L.avi	bluering_20020619_down2L	hand putdown ring	4;
65	bluering_20020619_down2R	./experiences/bluering_20020619_down2R.avi	bluering_20020619_down2R	hand putdown ring	4;
66	bluering_20020619_drop1R	./experiences/bluering_20020619_drop1R.avi	bluering_20020619_drop1R	hand drop ring	4;
67	bluering_20020619_drop2R	./experiences/bluering_20020619_drop2R.avi	bluering_20020619_drop2R	hand drop ring	4;
68	bluering_20020619_drop3R	./experiences/bluering_20020619_drop3R.avi	bluering_20020619_drop3R	hand drop ring	4;
69	bluering_20020619_drop4R	./experiences/bluering_20020619_drop4R.avi	bluering_20020619_drop4R	hand drop ring	4;
70	bluering_20020619_pull1L	./experiences/bluering_20020619_pull1L.avi	bluering_20020619_pull1L	hand pull ring	2;
71	bluering_20020619_pull1R	./experiences/bluering_20020619_pull1R.avi	bluering_20020619_pull1R	hand pull ring	2;
72	bluering_20020619_slidel1L	./experiences/bluering_20020619_slidel1L.avi	bluering_20020619_slidel1L	hand slide ring	2;
73	bluering_20020619_slidel1R	./experiences/bluering_20020619_slidel1R.avi	bluering_20020619_slidel1R	hand slide ring	2;
318	vase_20020809_push2L	./experiences/vase_20020809_push2L.avi	vase_20020809_push2L	hand push vase	2;
319	vase_20020809_push2R	./experiences/vase_20020809_push2R.avi	vase_20020809_push2R	hand push vase	2;
320	vase_20020809_touch1L	./experiences/vase_20020809_touch1L.avi	vase_20020809_touch1L	hand touch vase	2;
321	vase_20020809_touch1R	./experiences/vase_20020809_touch1R.avi	vase_20020809_touch1R	hand touch vase	2;
322	vase_20020809_touch2L	./experiences/vase_20020809_touch2L.avi	vase_20020809_touch2L	hand touch vase	2;
323	vase_20020809_touch2R	./experiences/vase_20020809_touch2R.avi	vase_20020809_touch2R	hand touch vase	2;
324	vase_20020809_up1L	./experiences/vase_20020809_up1L.avi	vase_20020809_up1L	hand pickup vase	2;
9	ball_20020619_down1L	./experiences/ball_20020619_down1L.avi	ball_20020619_down1L	hand putdown ball	2;
10	ball_20020619_down1R	./experiences/ball_20020619_down1R.avi	ball_20020619_down1R	hand putdown ball	4;
11	ball_20020619_down2R	./experiences/ball_20020619_down2R.avi	ball_20020619_down2R	hand putdown ball	4;
12	ball_20020619_down3R	./experiences/ball_20020619_down3R.avi	ball_20020619_down3R	hand putdown ball	4;
13	ball_20020619_touch1L	./experiences/ball_20020619_touch1L.avi	ball_20020619_touch1L	hand touch ball	2;
14	ball_20020619_touch1R	./experiences/ball_20020619_touch1R.avi	ball_20020619_touch1R	hand touch ball	2;
15	ball_20020619_touch2L	./experiences/ball_20020619_touch2L.avi	ball_20020619_touch2L	hand touch ball	2;
16	ball_20020619_touch3L	./experiences/ball_20020619_touch3L.avi	ball_20020619_touch3L	hand touch ball	2;
17	ball_20020619_up1L	./experiences/ball_20020619_up1L.avi	ball_20020619_up1L	hand pickup ball	2;
18	ball_20020619_up1R	./experiences/ball_20020619_up1R.avi	ball_20020619_up1R	hand pickup ball	2;
19	ball_20020619_up2L	./experiences/ball_20020619_up2L.avi	ball_20020619_up2L	hand pickup ball	2;
20	ball_20020619_up3L	./experiences/ball_20020619_up3L.avi	ball_20020619_up3L	hand pickup ball	2;
21	ball_20020809_down1R	./experiences/ball_20020809_down1R.avi	ball_20020809_down1R	hand putdown ball	2;
22	ball_20020809_down2R	./experiences/ball_20020809_down2R.avi	ball_20020809_down2R	hand putdown ball	2;
23	ball_20020809_drop1L	./experiences/ball_20020809_drop1L.avi	ball_20020809_drop1L	hand drop ball	2;
24	ball_20020809_drop1R	./experiences/ball_20020809_drop1R.avi	ball_20020809_drop1R	hand drop ball	2;
2	ani_ball_down1	./experiences/ball_down1.avi	ball_down1	hand putdown ball	5;animation
7	ani_cube_slide1	./experiences/cube_slide1.avi	cube_slide1	hand slide cube	5;animation
3	ani_ball_slide1	./experiences/ball_slide1.avi	ball_slide1	hand slide ball	5;animation
\.


--
-- Data for TOC entry 113 (OID 8222524)
-- Name: attribute_list_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY attribute_list_data (attribute_list_id, description, include_code, type_code, class_name, notes) FROM stdin;
2	grayscale	1	0	\N	grayscale value (0-255)
3	edges	1	0	\N	# edges in polygon
4	relativeCGX	1	0	\N	x coordinate of centroid relative to bounding rectangle, normalized by rectangle width
5	relativeCGY	1	0	\N	y coordinate of centroid relative to bounding rectangle, normalized by rectangle height
6	contact	1	0	\N	0 if bounding rectangles of polygons overlap, 1 otherwise
7	x relation	1	0	\N	-1=o1 left (west) of o2, 0=o1 has same x-coord as o2, 1=o1 right (east) of o2
8	y relation	1	0	\N	-1=o1 over (north) o2, 0=o1 has same y-coord as o2, 1=o1 below (south) o2
9	delta x	1	0	\N	-1=delta x is decreasing, 0=delta x is unchanged, 1=delta x is increasing
10	delta y	1	0	\N	-1=delta y is decreasing, 0=delta y is unchanged, 1=delta y is increasing
11	x travel	1	0	\N	add 1 for each object moving right and subtract 1 for each object moving left
12	y travel	1	0	\N	add 1 for each object moving down and subtract 1 for each object moving up
1	area	1	0	\N	# pixels in polygon
\.


--
-- Data for TOC entry 114 (OID 8222531)
-- Name: session_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY session_data (session_id, parameter_id, description, session_start, session_stop, regen_int_images_code, log_to_file_code, randomize_exp_code, desc_to_generate, min_sd_start, min_sd_stop, min_sd_step, loop_count, fixed_sd_code, display_movie_code, display_text_code, case_sensitive_code, notes, session_ip) FROM stdin;
\.


--
-- Data for TOC entry 115 (OID 8222549)
-- Name: parameter_experience_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY parameter_experience_data (parameter_experience_id, parameter_id, experience_id, calc_status_code, calc_timestamp) FROM stdin;
870	11	217	0	2004-02-09 09:40:52.833999
871	11	218	0	2004-02-09 09:42:09.342802
872	11	219	0	2004-02-09 09:43:16.259358
505	13	171	0	2003-05-22 11:52:19.326917
506	13	172	0	2003-05-22 11:54:55.884013
507	13	173	0	2003-05-22 11:57:14.948059
508	13	174	0	2003-05-22 11:59:56.51666
509	13	175	0	2003-05-22 12:03:03.313505
510	13	176	0	2003-05-22 12:06:29.417333
512	13	178	0	2003-05-22 12:08:16.511324
514	13	180	0	2003-05-22 12:10:19.952849
518	13	184	0	2003-05-22 12:12:36.764111
519	13	185	0	2003-05-22 12:15:04.639133
520	13	186	0	2003-05-22 12:16:26.662578
521	13	187	0	2003-05-22 12:18:23.279896
522	13	188	0	2003-05-22 12:20:24.051002
523	13	189	0	2003-05-22 12:22:14.318867
524	13	190	0	2003-05-22 12:23:57.675053
525	13	191	0	2003-05-22 12:26:22.456694
526	13	192	0	2003-05-22 12:28:17.975232
527	13	193	0	2003-05-22 12:30:37.16548
528	13	194	0	2003-05-22 12:32:18.866583
529	13	195	0	2003-05-22 12:34:00.385242
548	13	214	0	2003-05-22 12:36:03.85887
550	13	216	0	2003-05-22 12:38:14.460362
551	13	217	0	2003-05-22 12:40:39.954256
874	11	221	0	2004-02-09 09:44:55.1628
775	11	122	0	2004-02-09 09:47:29.476829
776	11	123	0	2004-02-09 09:50:32.206029
782	11	129	0	2004-02-09 09:53:34.240101
784	11	131	0	2004-02-09 09:55:45.318003
785	11	132	0	2004-02-09 09:58:05.189632
786	11	133	0	2004-02-09 10:01:23.413963
787	11	134	0	2004-02-09 10:04:38.273844
788	11	135	0	2004-02-09 10:07:30.429628
789	11	136	0	2004-02-09 10:10:29.544755
790	11	137	0	2004-02-09 10:12:18.666034
792	11	139	0	2004-02-09 10:15:20.567615
793	11	140	0	2004-02-09 10:17:37.157858
794	11	141	0	2004-02-09 10:20:49.023688
795	11	142	0	2004-02-09 10:23:23.905199
796	11	143	0	2004-02-09 10:24:33.218571
797	11	144	0	2004-02-09 10:27:00.39695
579	13	245	0	2003-05-22 13:11:17.898608
581	13	247	0	2003-05-22 13:13:30.696773
582	13	248	0	2003-05-22 13:16:08.221286
583	13	249	0	2003-05-22 13:19:23.211275
585	13	251	0	2003-05-22 13:21:40.334278
587	13	253	0	2003-05-22 13:22:57.920218
589	13	255	0	2003-05-22 13:24:04.900732
590	13	256	0	2003-05-22 13:25:37.860808
591	13	257	0	2003-05-22 13:27:49.514551
592	13	258	0	2003-05-22 13:29:20.501498
593	13	259	0	2003-05-22 13:31:03.747981
608	13	274	0	2003-05-22 13:33:51.474269
619	13	285	0	2003-05-22 13:34:47.100566
335	9	2	0	2003-05-21 15:58:46.199726
336	9	3	0	2003-05-21 16:00:06.806016
337	9	4	0	2003-05-21 16:01:18.299242
338	9	1	0	2003-05-21 16:02:04.571508
339	9	6	0	2003-05-21 16:03:20.777476
340	9	7	0	2003-05-21 16:04:33.977629
341	9	8	0	2003-05-21 16:05:43.971162
342	9	5	0	2003-05-21 16:06:29.446579
343	13	9	0	2003-05-21 16:42:23.568389
347	13	13	0	2003-05-21 16:45:11.898912
348	13	14	0	2003-05-21 16:47:32.246881
349	13	15	0	2003-05-21 16:49:18.896251
350	13	16	0	2003-05-21 16:51:28.317891
351	13	17	0	2003-05-21 16:53:53.198804
352	13	18	0	2003-05-21 16:56:27.125494
353	13	19	0	2003-05-21 16:58:50.35801
354	13	20	0	2003-05-21 17:01:16.188567
355	13	21	0	2003-05-21 17:03:49.037663
356	13	22	0	2003-05-21 17:06:06.713454
357	13	23	0	2003-05-21 17:08:47.783106
358	13	24	0	2003-05-21 17:10:02.598138
359	13	25	0	2003-05-21 17:11:03.008276
360	13	26	0	2003-05-21 17:12:03.10461
361	13	27	0	2003-05-21 17:13:07.81205
362	13	28	0	2003-05-21 17:14:18.002893
363	13	29	0	2003-05-21 17:15:10.365464
365	13	31	0	2003-05-21 17:16:03.804876
366	13	32	0	2003-05-21 17:18:10.433057
367	13	33	0	2003-05-21 17:20:04.143634
368	13	34	0	2003-05-21 17:22:35.528121
369	13	35	0	2003-05-21 17:24:41.610803
370	13	36	0	2003-05-21 17:27:33.814362
371	13	37	0	2003-05-21 17:29:22.731552
372	13	38	0	2003-05-21 17:31:27.563857
631	13	297	0	2003-05-22 13:37:44.104642
634	13	300	0	2003-05-22 13:40:08.249629
798	11	145	0	2004-02-09 10:29:18.021414
799	11	146	0	2004-02-09 10:31:43.106144
800	11	147	0	2004-02-09 10:33:51.391268
801	11	148	0	2004-02-09 10:36:13.801757
802	11	149	0	2004-02-09 10:38:22.290045
635	13	301	0	2003-05-22 13:43:13.168328
636	13	302	0	2003-05-22 13:46:16.963136
637	13	303	0	2003-05-22 13:49:30.547358
638	13	304	0	2003-05-22 13:52:52.515872
639	13	305	0	2003-05-22 13:55:52.412153
640	13	306	0	2003-05-22 13:57:57.813693
641	13	307	0	2003-05-22 14:00:41.15067
647	13	313	0	2003-05-22 14:03:15.526628
649	13	315	0	2003-05-22 14:05:41.169243
650	13	316	0	2003-05-22 14:08:30.820666
652	13	318	0	2003-05-22 14:10:58.581651
653	13	319	0	2003-05-22 14:13:53.72818
654	13	320	0	2003-05-22 14:16:39.192414
655	13	321	0	2003-05-22 14:19:13.600472
656	13	322	0	2003-05-22 14:21:35.730195
657	13	323	0	2003-05-22 14:25:02.963961
658	13	324	0	2003-05-22 14:27:13.319206
659	13	325	0	2003-05-22 14:32:27.747157
660	13	326	0	2003-05-22 14:34:16.32496
661	13	327	0	2003-05-22 14:36:31.842599
374	13	40	0	2003-05-22 14:39:19.040131
375	13	41	0	2003-05-22 14:41:57.932679
376	13	42	0	2003-05-22 14:44:16.365478
378	13	44	0	2003-05-22 14:45:41.013898
380	13	46	0	2003-05-22 14:47:20.56215
381	13	47	0	2003-05-22 14:50:18.674886
382	13	48	0	2003-05-22 14:52:38.786667
383	13	49	0	2003-05-22 14:55:47.32977
384	13	50	0	2003-05-22 15:02:01.785871
385	13	51	0	2003-05-22 15:04:57.129753
386	13	52	0	2003-05-22 15:07:54.44005
387	13	53	0	2003-05-22 15:10:05.992483
388	13	54	0	2003-05-22 15:12:12.776249
389	13	55	0	2003-05-22 15:14:28.413069
390	13	56	0	2003-05-22 15:16:43.247735
391	13	57	0	2003-05-22 15:19:21.514992
393	13	59	0	2003-05-22 15:21:49.582974
395	13	61	0	2003-05-22 15:24:13.523668
404	13	70	0	2003-05-22 15:26:43.259025
405	13	71	0	2003-05-22 15:28:44.982492
406	13	72	0	2003-05-22 15:31:37.627363
407	13	73	0	2003-05-22 15:33:34.361861
408	13	74	0	2003-05-22 15:35:36.07217
409	13	75	0	2003-05-22 15:37:34.537815
803	11	150	0	2004-02-09 10:40:31.479959
804	11	151	0	2004-02-09 10:41:31.18474
805	11	152	0	2004-02-09 10:43:57.917252
806	11	153	0	2004-02-09 10:45:24.965719
808	11	155	0	2004-02-09 10:47:46.786923
809	11	156	0	2004-02-09 10:49:50.242005
810	11	157	0	2004-02-09 10:52:31.340293
811	11	158	0	2004-02-09 10:55:29.840056
812	11	159	0	2004-02-09 10:58:09.472808
813	11	160	0	2004-02-09 11:00:37.39138
815	11	162	0	2004-02-09 11:02:19.20537
816	11	163	0	2004-02-09 11:03:21.768049
817	11	164	0	2004-02-09 11:04:31.1789
818	11	165	0	2004-02-09 11:07:29.286661
820	11	167	0	2004-02-09 11:10:05.230139
821	11	168	0	2004-02-09 11:13:59.952215
822	11	169	0	2004-02-09 11:16:16.679154
823	11	170	0	2004-02-09 11:18:52.393678
824	11	171	0	2004-02-09 11:21:04.756341
825	11	172	0	2004-02-09 11:23:21.541807
826	11	173	0	2004-02-09 11:25:24.758483
827	11	174	0	2004-02-09 11:27:49.191042
758	11	105	0	2004-02-09 14:00:54.668713
760	11	107	0	2004-02-09 14:03:06.49244
761	11	108	0	2004-02-09 14:05:13.401374
762	11	109	0	2004-02-09 14:07:52.559844
765	11	112	0	2004-02-09 14:10:39.069375
766	11	113	0	2004-02-09 14:13:59.69978
767	11	114	0	2004-02-09 14:17:01.743441
768	11	115	0	2004-02-09 14:19:55.106887
769	11	116	0	2004-02-09 14:22:53.4882
772	11	119	0	2004-02-09 14:26:48.639499
773	11	120	0	2004-02-09 14:28:58.734537
864	11	211	0	2004-02-09 14:32:22.2787
865	11	212	0	2004-02-09 14:35:04.446172
867	11	214	0	2004-02-09 14:37:28.823508
898	11	245	0	2004-02-09 14:39:32.925868
899	11	246	0	2004-02-09 14:41:46.601522
900	11	247	0	2004-02-09 14:43:56.293455
662	11	9	0	2004-02-09 14:46:27.931372
666	11	13	0	2004-02-09 14:49:12.249358
667	11	14	0	2004-02-09 14:51:31.000479
668	11	15	0	2004-02-09 14:53:18.499844
669	11	16	0	2004-02-09 14:55:27.817834
901	11	248	0	2004-02-09 14:57:51.846137
902	11	249	0	2004-02-09 15:01:04.238529
904	11	251	0	2004-02-09 15:03:15.032769
906	11	253	0	2004-02-09 15:04:27.800237
908	11	255	0	2004-02-09 15:05:36.706619
909	11	256	0	2004-02-09 15:07:10.538795
910	11	257	0	2004-02-09 15:09:18.419772
911	11	258	0	2004-02-09 15:10:50.361907
912	11	259	0	2004-02-09 15:12:32.550384
918	11	265	0	2004-02-09 15:15:10.772149
920	11	267	0	2004-02-09 15:17:43.536207
924	11	271	0	2004-02-09 15:20:32.592024
927	11	274	0	2004-02-09 15:24:52.869596
929	11	276	0	2004-02-09 15:25:44.086823
933	11	280	0	2004-02-09 15:27:07.53467
934	11	281	0	2004-02-09 15:29:30.712691
935	11	282	0	2004-02-09 15:32:03.959466
938	11	285	0	2004-02-09 15:34:40.898559
939	11	286	0	2004-02-09 15:37:21.131658
941	11	288	0	2004-02-09 15:39:43.278855
942	11	289	0	2004-02-09 15:44:21.936512
944	11	291	0	2004-02-09 15:48:05.760263
945	11	292	0	2004-02-09 15:50:30.252048
949	11	296	0	2004-02-09 15:53:53.021536
950	11	297	0	2004-02-09 15:56:14.492336
951	11	298	0	2004-02-09 15:58:25.618861
952	11	299	0	2004-02-09 16:00:56.350527
953	11	300	0	2004-02-09 16:03:47.820765
954	11	301	0	2004-02-09 16:06:31.8269
955	11	302	0	2004-02-09 16:09:23.391083
956	11	303	0	2004-02-09 16:12:16.496806
957	11	304	0	2004-02-09 16:15:20.787814
958	11	305	0	2004-02-09 16:18:20.046834
959	11	306	0	2004-02-09 16:20:20.950038
960	11	307	0	2004-02-09 16:22:50.672253
965	11	312	0	2004-02-09 16:25:18.07406
966	11	313	0	2004-02-09 16:28:00.94697
968	11	315	0	2004-02-09 16:30:20.373025
969	11	316	0	2004-02-09 16:33:02.811609
970	11	317	0	2004-02-09 16:35:23.147377
971	11	318	0	2004-02-09 16:37:56.778983
972	11	319	0	2004-02-09 16:40:40.033886
973	11	320	0	2004-02-09 16:43:12.662916
974	11	321	0	2004-02-09 16:45:34.147504
975	11	322	0	2004-02-09 16:47:45.454319
976	11	323	0	2004-02-09 16:50:50.63179
977	11	324	0	2004-02-09 16:52:52.714814
846	11	193	0	2004-02-09 16:54:57.568986
847	11	194	0	2004-02-09 16:56:47.875032
828	11	175	0	2004-02-09 11:30:34.654201
829	11	176	0	2004-02-09 11:33:38.572494
831	11	178	0	2004-02-09 11:35:22.290873
833	11	180	0	2004-02-09 11:37:19.853291
835	11	182	0	2004-02-09 11:39:29.337591
836	11	183	0	2004-02-09 11:41:21.255726
837	11	184	0	2004-02-09 11:43:14.614426
838	11	185	0	2004-02-09 11:45:34.798689
839	11	186	0	2004-02-09 11:46:51.181589
840	11	187	0	2004-02-09 11:48:39.484278
841	11	188	0	2004-02-09 11:50:32.483915
842	11	189	0	2004-02-09 11:52:15.941581
843	11	190	0	2004-02-09 11:53:56.499113
844	11	191	0	2004-02-09 11:56:15.313432
845	11	192	0	2004-02-09 11:58:09.981097
672	11	19	0	2004-02-09 12:00:25.707177
673	11	20	0	2004-02-09 12:02:55.067522
674	11	21	0	2004-02-09 12:05:33.32339
675	11	22	0	2004-02-09 12:07:47.061243
676	11	23	0	2004-02-09 12:10:23.395897
677	11	24	0	2004-02-09 12:11:34.573497
678	11	25	0	2004-02-09 12:12:33.292717
679	11	26	0	2004-02-09 12:13:31.519218
680	11	27	0	2004-02-09 12:14:34.197419
681	11	28	0	2004-02-09 12:15:43.036275
682	11	29	0	2004-02-09 12:16:35.332384
684	11	31	0	2004-02-09 12:17:30.873876
685	11	32	0	2004-02-09 12:19:33.415406
686	11	33	0	2004-02-09 12:21:22.486453
687	11	34	0	2004-02-09 12:23:46.399608
688	11	35	0	2004-02-09 12:25:47.356335
689	11	36	0	2004-02-09 12:28:26.633182
690	11	37	0	2004-02-09 12:30:05.990347
691	11	38	0	2004-02-09 12:31:50.873434
692	11	39	0	2004-02-09 12:33:52.490425
693	11	40	0	2004-02-09 12:36:29.684083
694	11	41	0	2004-02-09 12:39:01.507042
695	11	42	0	2004-02-09 12:41:13.986618
697	11	44	0	2004-02-09 12:42:34.697983
699	11	46	0	2004-02-09 12:44:10.317563
700	11	47	0	2004-02-09 12:47:04.106637
411	13	77	0	2003-05-22 15:41:31.232905
413	13	79	0	2003-05-22 15:44:15.097482
414	13	80	0	2003-05-22 15:46:29.124705
415	13	81	0	2003-05-22 15:47:50.921418
416	13	82	0	2003-05-22 15:49:54.630907
417	13	83	0	2003-05-22 15:51:18.695894
418	13	84	0	2003-05-22 15:52:36.018967
419	13	85	0	2003-05-22 15:54:50.256458
420	13	86	0	2003-05-22 15:56:21.302451
424	13	90	0	2003-05-22 15:58:38.799379
437	13	103	0	2003-05-22 16:00:15.094323
446	13	112	0	2003-05-22 16:01:42.995132
447	13	113	0	2003-05-22 16:05:28.403323
448	13	114	0	2003-05-22 16:08:49.171158
449	13	115	0	2003-05-22 16:12:07.714246
465	13	131	0	2003-05-22 16:15:27.844136
466	13	132	0	2003-05-22 16:18:04.993602
467	13	133	0	2003-05-22 16:21:41.66509
468	13	134	0	2003-05-22 16:25:20.381974
469	13	135	0	2003-05-22 16:28:23.708066
470	13	136	0	2003-05-22 16:31:35.843192
474	13	140	0	2003-05-22 16:33:27.86447
476	13	142	0	2003-05-22 16:36:44.263812
478	13	144	0	2003-05-22 16:38:05.763042
479	13	145	0	2003-05-22 16:40:36.051849
480	13	146	0	2003-05-22 16:43:09.529038
481	13	147	0	2003-05-22 16:45:21.950047
482	13	148	0	2003-05-22 16:48:26.281904
483	13	149	0	2003-05-22 16:50:47.299402
484	13	150	0	2003-05-22 16:53:04.709922
485	13	151	0	2003-05-22 16:54:10.491025
486	13	152	0	2003-05-22 16:56:57.062044
490	13	156	0	2003-05-22 16:58:35.781689
491	13	157	0	2003-05-22 17:01:35.774902
492	13	158	0	2003-05-22 17:05:09.202212
493	13	159	0	2003-05-22 17:08:15.05856
494	13	160	0	2003-05-22 17:11:25.911946
496	13	162	0	2003-05-22 17:13:13.647134
497	13	163	0	2003-05-22 17:14:25.047599
498	13	164	0	2003-05-22 17:15:43.067585
499	13	165	0	2003-05-22 17:18:51.203865
501	13	167	0	2003-05-22 17:21:37.334925
502	13	168	0	2003-05-22 17:26:01.119583
503	13	169	0	2003-05-22 17:28:39.370265
504	13	170	0	2003-05-22 17:31:46.160227
552	13	218	0	2003-05-22 12:42:01.583875
553	13	219	0	2003-05-22 12:43:12.224688
555	13	221	0	2003-05-22 12:44:56.15481
556	13	222	0	2003-05-22 12:47:43.503636
557	13	223	0	2003-05-22 12:50:14.192112
558	13	224	0	2003-05-22 12:52:59.31598
559	13	225	0	2003-05-22 12:55:36.719222
563	13	229	0	2003-05-22 12:58:16.574529
564	13	230	0	2003-05-22 13:00:42.464146
565	13	231	0	2003-05-22 13:02:49.419068
566	13	232	0	2003-05-22 13:05:01.182712
568	13	234	0	2003-05-22 13:07:11.072343
577	13	243	0	2003-05-22 13:09:32.227721
701	11	48	0	2004-02-09 12:49:22.208503
702	11	49	0	2004-02-09 12:52:21.647532
703	11	50	0	2004-02-09 12:55:26.213179
704	11	51	0	2004-02-09 12:58:14.611545
705	11	52	0	2004-02-09 13:01:03.637506
706	11	53	0	2004-02-09 13:03:12.062468
707	11	54	0	2004-02-09 13:05:10.85854
708	11	55	0	2004-02-09 13:07:21.837825
709	11	56	0	2004-02-09 13:09:37.453164
410	13	76	0	2003-05-22 15:40:03.19076
710	11	57	0	2004-02-09 13:12:16.931395
711	11	58	0	2004-02-09 13:14:46.223083
712	11	59	0	2004-02-09 13:17:59.170206
713	11	60	0	2004-02-09 13:20:16.445094
714	11	61	0	2004-02-09 13:22:54.51015
723	11	70	0	2004-02-09 13:25:21.639673
724	11	71	0	2004-02-09 13:27:24.632634
725	11	72	0	2004-02-09 13:30:21.447454
726	11	73	0	2004-02-09 13:32:23.840465
727	11	74	0	2004-02-09 13:34:30.876805
728	11	75	0	2004-02-09 13:36:30.457242
729	11	76	0	2004-02-09 13:39:00.961026
730	11	77	0	2004-02-09 13:40:27.763646
732	11	79	0	2004-02-09 13:43:07.445213
733	11	80	0	2004-02-09 13:45:05.515075
734	11	81	0	2004-02-09 13:46:25.923833
735	11	82	0	2004-02-09 13:48:20.139571
736	11	83	0	2004-02-09 13:49:41.390488
737	11	84	0	2004-02-09 13:50:55.126561
738	11	85	0	2004-02-09 13:53:03.348685
739	11	86	0	2004-02-09 13:54:31.977397
743	11	90	0	2004-02-09 13:56:46.106428
868	11	215	0	2004-02-09 09:35:19.359917
869	11	216	0	2004-02-09 09:38:36.361762
848	11	195	0	2004-02-09 16:58:33.494966
849	11	196	0	2004-02-09 17:00:43.474277
852	11	200	0	2004-02-09 17:02:42.840986
853	11	201	0	2004-02-09 17:05:40.82152
854	11	202	0	2004-02-09 17:08:15.148458
855	11	203	0	2004-02-09 17:09:18.054119
856	11	204	0	2004-02-09 17:10:56.480655
858	11	206	0	2004-02-09 17:12:45.28127
859	11	207	0	2004-02-09 17:15:11.574138
860	11	208	0	2004-02-09 17:17:19.481095
861	11	209	0	2004-02-09 17:19:43.769361
862	11	197	0	2004-02-09 17:21:58.116883
863	11	210	0	2004-02-09 17:24:12.728799
670	11	17	0	2004-02-09 17:25:55.467759
671	11	18	0	2004-02-09 17:28:31.743841
978	11	325	0	2004-02-09 17:30:59.181653
979	11	326	0	2004-02-09 17:32:42.776722
980	11	327	0	2004-02-09 17:34:53.786305
875	11	222	0	2004-02-09 17:37:31.009934
876	11	223	0	2004-02-09 17:39:52.077966
877	11	224	0	2004-02-09 17:42:26.355894
878	11	225	0	2004-02-09 17:44:52.134485
881	11	228	0	2004-02-09 17:47:23.222038
882	11	229	0	2004-02-09 17:49:26.087296
883	11	230	0	2004-02-09 17:51:42.87906
884	11	231	0	2004-02-09 17:53:40.780188
885	11	232	0	2004-02-09 17:55:43.618307
886	11	233	0	2004-02-09 17:57:46.107099
887	11	234	0	2004-02-09 18:01:25.61867
892	11	239	0	2004-02-09 18:03:39.010034
896	11	243	0	2004-02-09 18:06:06.06468
897	11	244	0	2004-02-09 18:07:48.461958
755	11	102	0	2004-02-09 13:58:19.277246
756	11	103	0	2004-02-09 13:59:36.292043
\.


--
-- Data for TOC entry 116 (OID 8222556)
-- Name: frame_analysis_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY frame_analysis_data (frame_analysis_id, parameter_experience_id, frame_number, object_number, polygon_point_count, polygon_point_list, rgb_color, bound_rect_points, centroid_x, centroid_y, area) FROM stdin;
\.


--
-- Data for TOC entry 117 (OID 8222571)
-- Name: run_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY run_data (run_id, session_id, run_index, run_start, run_stop, min_sd) FROM stdin;
\.


--
-- Data for TOC entry 118 (OID 8222579)
-- Name: entity_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY entity_data (entity_id, run_id, occurance_count) FROM stdin;
\.


--
-- Data for TOC entry 119 (OID 8222585)
-- Name: lexeme_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY lexeme_data (lexeme_id, run_id, lexeme, occurance_count) FROM stdin;
\.


--
-- Data for TOC entry 120 (OID 8222591)
-- Name: experience_run_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY experience_run_data (experience_run_id, experience_id, run_id, experience_index, experience_description) FROM stdin;
\.


--
-- Data for TOC entry 121 (OID 8222597)
-- Name: entity_lexeme_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY entity_lexeme_data (entity_lexeme_id, entity_id, lexeme_id, occurance_count) FROM stdin;
\.


--
-- Data for TOC entry 122 (OID 8222603)
-- Name: experience_entity_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY experience_entity_data (experience_entity_id, experience_id, run_id, entity_id, resolution_code) FROM stdin;
\.


--
-- Data for TOC entry 123 (OID 8222609)
-- Name: experience_lexeme_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY experience_lexeme_data (experience_lexeme_id, experience_id, run_id, lexeme_id, resolution_code, resolution_index) FROM stdin;
\.


--
-- Data for TOC entry 124 (OID 8222616)
-- Name: attribute_value_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY attribute_value_data (attribute_value_id, attribute_list_id, run_id, entity_id, avg_value, std_deviation) FROM stdin;
\.


--
-- TOC entry 76 (OID 8223364)
-- Name: att_list_description_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX att_list_description_idx ON attribute_list_data USING btree (description);


--
-- TOC entry 79 (OID 8223365)
-- Name: session_parameter_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX session_parameter_id_idx ON session_data USING btree (parameter_id);


--
-- TOC entry 81 (OID 8223366)
-- Name: para_exp_parameter_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX para_exp_parameter_id_idx ON parameter_experience_data USING btree (parameter_id);


--
-- TOC entry 80 (OID 8223367)
-- Name: para_exp_experience_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX para_exp_experience_id_idx ON parameter_experience_data USING btree (experience_id);


--
-- TOC entry 85 (OID 8223368)
-- Name: fra_ana_para_exp_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fra_ana_para_exp_id_idx ON frame_analysis_data USING btree (parameter_experience_id);


--
-- TOC entry 83 (OID 8223369)
-- Name: fra_ana_frame_number_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fra_ana_frame_number_idx ON frame_analysis_data USING btree (frame_number);


--
-- TOC entry 84 (OID 8223370)
-- Name: fra_ana_object_number_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fra_ana_object_number_idx ON frame_analysis_data USING btree (object_number);


--
-- TOC entry 89 (OID 8223371)
-- Name: entity_run_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX entity_run_id_idx ON entity_data USING btree (run_id);


--
-- TOC entry 92 (OID 8223372)
-- Name: lexeme_run_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX lexeme_run_id_idx ON lexeme_data USING btree (run_id);


--
-- TOC entry 91 (OID 8223373)
-- Name: lexeme_lexeme_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX lexeme_lexeme_idx ON lexeme_data USING btree (lexeme);


--
-- TOC entry 93 (OID 8223374)
-- Name: exp_run_experience_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX exp_run_experience_id_idx ON experience_run_data USING btree (experience_id);


--
-- TOC entry 94 (OID 8223375)
-- Name: exp_run_run_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX exp_run_run_id_idx ON experience_run_data USING btree (run_id);


--
-- TOC entry 96 (OID 8223376)
-- Name: ent_lex_entity_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ent_lex_entity_id_idx ON entity_lexeme_data USING btree (entity_id);


--
-- TOC entry 97 (OID 8223377)
-- Name: ent_lex_lexeme_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX ent_lex_lexeme_id_idx ON entity_lexeme_data USING btree (lexeme_id);


--
-- TOC entry 100 (OID 8223378)
-- Name: exp_ent_exp_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX exp_ent_exp_id_idx ON experience_entity_data USING btree (experience_id);


--
-- TOC entry 101 (OID 8223379)
-- Name: exp_ent_run_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX exp_ent_run_id_idx ON experience_entity_data USING btree (run_id);


--
-- TOC entry 99 (OID 8223380)
-- Name: exp_ent_entity_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX exp_ent_entity_id_idx ON experience_entity_data USING btree (entity_id);


--
-- TOC entry 103 (OID 8223381)
-- Name: exp_lex_exp_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX exp_lex_exp_id_idx ON experience_lexeme_data USING btree (experience_id);


--
-- TOC entry 105 (OID 8223382)
-- Name: exp_lex_run_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX exp_lex_run_id_idx ON experience_lexeme_data USING btree (run_id);


--
-- TOC entry 104 (OID 8223383)
-- Name: exp_lex_lexeme_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX exp_lex_lexeme_id_idx ON experience_lexeme_data USING btree (lexeme_id);


--
-- TOC entry 107 (OID 8223384)
-- Name: att_val_attribute_list_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX att_val_attribute_list_id_idx ON attribute_value_data USING btree (attribute_list_id);


--
-- TOC entry 109 (OID 8223385)
-- Name: att_val_run_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX att_val_run_id_idx ON attribute_value_data USING btree (run_id);


--
-- TOC entry 108 (OID 8223386)
-- Name: att_val_entity_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX att_val_entity_id_idx ON attribute_value_data USING btree (entity_id);


--
-- TOC entry 74 (OID 8223387)
-- Name: parameter_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY parameter_data
    ADD CONSTRAINT parameter_data_pkey PRIMARY KEY (parameter_id);


--
-- TOC entry 75 (OID 8223389)
-- Name: experience_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY experience_data
    ADD CONSTRAINT experience_data_pkey PRIMARY KEY (experience_id);


--
-- TOC entry 77 (OID 8223391)
-- Name: attribute_list_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY attribute_list_data
    ADD CONSTRAINT attribute_list_data_pkey PRIMARY KEY (attribute_list_id);


--
-- TOC entry 78 (OID 8223393)
-- Name: session_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY session_data
    ADD CONSTRAINT session_data_pkey PRIMARY KEY (session_id);


--
-- TOC entry 82 (OID 8223399)
-- Name: parameter_experience_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY parameter_experience_data
    ADD CONSTRAINT parameter_experience_data_pkey PRIMARY KEY (parameter_experience_id);


--
-- TOC entry 86 (OID 8223409)
-- Name: frame_analysis_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY frame_analysis_data
    ADD CONSTRAINT frame_analysis_data_pkey PRIMARY KEY (frame_analysis_id);


--
-- TOC entry 87 (OID 8223415)
-- Name: run_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY run_data
    ADD CONSTRAINT run_data_pkey PRIMARY KEY (run_id);


--
-- TOC entry 88 (OID 8223421)
-- Name: entity_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY entity_data
    ADD CONSTRAINT entity_data_pkey PRIMARY KEY (entity_id);


--
-- TOC entry 90 (OID 8223427)
-- Name: lexeme_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY lexeme_data
    ADD CONSTRAINT lexeme_data_pkey PRIMARY KEY (lexeme_id);


--
-- TOC entry 95 (OID 8223433)
-- Name: experience_run_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY experience_run_data
    ADD CONSTRAINT experience_run_data_pkey PRIMARY KEY (experience_run_id);


--
-- TOC entry 98 (OID 8223443)
-- Name: entity_lexeme_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY entity_lexeme_data
    ADD CONSTRAINT entity_lexeme_data_pkey PRIMARY KEY (entity_lexeme_id);


--
-- TOC entry 102 (OID 8223453)
-- Name: experience_entity_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY experience_entity_data
    ADD CONSTRAINT experience_entity_data_pkey PRIMARY KEY (experience_entity_id);


--
-- TOC entry 106 (OID 8223467)
-- Name: experience_lexeme_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY experience_lexeme_data
    ADD CONSTRAINT experience_lexeme_data_pkey PRIMARY KEY (experience_lexeme_id);


--
-- TOC entry 110 (OID 8223481)
-- Name: attribute_value_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY attribute_value_data
    ADD CONSTRAINT attribute_value_data_pkey PRIMARY KEY (attribute_value_id);


--
-- TOC entry 125 (OID 8223395)
-- Name: $1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY session_data
    ADD CONSTRAINT "$1" FOREIGN KEY (parameter_id) REFERENCES parameter_data(parameter_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 126 (OID 8223401)
-- Name: $1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY parameter_experience_data
    ADD CONSTRAINT "$1" FOREIGN KEY (parameter_id) REFERENCES parameter_data(parameter_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 127 (OID 8223405)
-- Name: $2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY parameter_experience_data
    ADD CONSTRAINT "$2" FOREIGN KEY (experience_id) REFERENCES experience_data(experience_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 128 (OID 8223411)
-- Name: $1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY frame_analysis_data
    ADD CONSTRAINT "$1" FOREIGN KEY (parameter_experience_id) REFERENCES parameter_experience_data(parameter_experience_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 129 (OID 8223417)
-- Name: $1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY run_data
    ADD CONSTRAINT "$1" FOREIGN KEY (session_id) REFERENCES session_data(session_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 130 (OID 8223423)
-- Name: $1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY entity_data
    ADD CONSTRAINT "$1" FOREIGN KEY (run_id) REFERENCES run_data(run_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 131 (OID 8223429)
-- Name: $1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY lexeme_data
    ADD CONSTRAINT "$1" FOREIGN KEY (run_id) REFERENCES run_data(run_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 132 (OID 8223435)
-- Name: $1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY experience_run_data
    ADD CONSTRAINT "$1" FOREIGN KEY (experience_id) REFERENCES experience_data(experience_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 133 (OID 8223439)
-- Name: $2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY experience_run_data
    ADD CONSTRAINT "$2" FOREIGN KEY (run_id) REFERENCES run_data(run_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 134 (OID 8223445)
-- Name: $1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY entity_lexeme_data
    ADD CONSTRAINT "$1" FOREIGN KEY (entity_id) REFERENCES entity_data(entity_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 135 (OID 8223449)
-- Name: $2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY entity_lexeme_data
    ADD CONSTRAINT "$2" FOREIGN KEY (lexeme_id) REFERENCES lexeme_data(lexeme_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 136 (OID 8223455)
-- Name: $1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY experience_entity_data
    ADD CONSTRAINT "$1" FOREIGN KEY (experience_id) REFERENCES experience_data(experience_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 137 (OID 8223459)
-- Name: $2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY experience_entity_data
    ADD CONSTRAINT "$2" FOREIGN KEY (run_id) REFERENCES run_data(run_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 138 (OID 8223463)
-- Name: $3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY experience_entity_data
    ADD CONSTRAINT "$3" FOREIGN KEY (entity_id) REFERENCES entity_data(entity_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 139 (OID 8223469)
-- Name: $1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY experience_lexeme_data
    ADD CONSTRAINT "$1" FOREIGN KEY (experience_id) REFERENCES experience_data(experience_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 140 (OID 8223473)
-- Name: $2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY experience_lexeme_data
    ADD CONSTRAINT "$2" FOREIGN KEY (run_id) REFERENCES run_data(run_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 141 (OID 8223477)
-- Name: $3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY experience_lexeme_data
    ADD CONSTRAINT "$3" FOREIGN KEY (lexeme_id) REFERENCES lexeme_data(lexeme_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 142 (OID 8223483)
-- Name: $1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY attribute_value_data
    ADD CONSTRAINT "$1" FOREIGN KEY (attribute_list_id) REFERENCES attribute_list_data(attribute_list_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 143 (OID 8223487)
-- Name: $2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY attribute_value_data
    ADD CONSTRAINT "$2" FOREIGN KEY (run_id) REFERENCES run_data(run_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 144 (OID 8223491)
-- Name: $3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY attribute_value_data
    ADD CONSTRAINT "$3" FOREIGN KEY (entity_id) REFERENCES entity_data(entity_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 5 (OID 8222503)
-- Name: parameter_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('parameter_data_seq', 13, true);


--
-- TOC entry 8 (OID 8222517)
-- Name: experience_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('experience_data_seq', 327, true);


--
-- TOC entry 11 (OID 8222522)
-- Name: attribute_list_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('attribute_list_data_seq', 12, true);


--
-- TOC entry 14 (OID 8222529)
-- Name: session_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('session_data_seq', 118, true);


--
-- TOC entry 17 (OID 8222547)
-- Name: parameter_experience_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('parameter_experience_data_seq', 980, true);


--
-- TOC entry 20 (OID 8222554)
-- Name: frame_analysis_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('frame_analysis_data_seq', 53657, true);


--
-- TOC entry 23 (OID 8222569)
-- Name: run_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('run_data_seq', 835, true);


--
-- TOC entry 26 (OID 8222577)
-- Name: entity_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('entity_data_seq', 69938, true);


--
-- TOC entry 29 (OID 8222583)
-- Name: lexeme_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('lexeme_data_seq', 14120, true);


--
-- TOC entry 32 (OID 8222589)
-- Name: experience_run_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('experience_run_data_seq', 157124, true);


--
-- TOC entry 35 (OID 8222595)
-- Name: entity_lexeme_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('entity_lexeme_data_seq', 101274, true);


--
-- TOC entry 38 (OID 8222601)
-- Name: experience_entity_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('experience_entity_data_seq', 479453, true);


--
-- TOC entry 41 (OID 8222607)
-- Name: experience_lexeme_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('experience_lexeme_data_seq', 471282, true);


--
-- TOC entry 44 (OID 8222614)
-- Name: attribute_value_data_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('attribute_value_data_seq', 418312, true);


--
-- TOC entry 2 (OID 2200)
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


