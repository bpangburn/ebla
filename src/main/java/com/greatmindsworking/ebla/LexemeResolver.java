/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2002-2004, Brian E. Pangburn
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



package com.greatmindsworking.ebla;



import java.util.*;
import java.sql.*;

import com.greatmindsworking.utils.DBConnector;

import java.io.*;


/**
 * LexemeResolver.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Resolves lexemes describing an experience to the entities in that experience.
 *<p>
 *<pre>
 * 1. Loop through all lexemes and determine if they already exist in lexeme_data
 *		a. if there is an existing record, bump its occurance_count
 *		b. if there is not an existing record, add one with occurance_count = 1
 *		c. in either case, add a record to experience_lexeme_data with
 *         resolution_code = 0
 *
 * 2. Lookup lexemes for current experience and see if any have already been mapped
 *    to entities (i.e. lexeme has already been encountered and mapped to an entity).
 *    If the mapped entity matches an unresolved entity in the current experience:
 * 		a. remove lexeme from current unmapped word list
 * 		b. bump occurance_count in entity_lexeme_data
 * 		c. set resolution_code in experience_lexeme_data to 1
 * 		d. set resolution_code in experience_entity_data to 1
 *
 * 3. If a lexemes for the current experience have already been mapped, but no
 *    unresolved matching entity is found, compare mapped entities to current
 *    entities and declare a "match" based on some similarity measure or threshold
 *    number of matching attributes.  If a match is found:
 *		a. updated/drop the relevant attributes for the entity
 * 		b. remove lexeme from current unmapped word list
 * 		c. bump occurance_count in entity_lexeme_data
 * 		d. set resolution_code in experience_lexeme_data to 1
 * 		e. set resolution_code in experience_entity_data to 1
 *    This is essentially the model merging logic and will be added later.
 *
 * 4. After #1, #2, & #3, if there is only one unmapped word and entity for
 *    the current experience, declare a "match" and:
 *		a. add the lexeme-entity mapping to entity_lexeme_data with
 *         occurance_count = 1
 * 		b. set resolution_code in experience_lexeme_data to 1
 * 		c. set resolution_code in experience_entity_data to 1
 *		d. search for other instaces of that resolution
 *		e. search for single unmatched pairs created by the new resolution
 *
 * 5. Attempt to resolve any remaining unmatched lexemes by comparing to prior
 *    experiences & using process of elimination (cross-situitational learning):
 *		a. dealing only with unresolved entities/lexemes from this point forward
 *		b. need to examine set intersection and difference between all pairs of
 *         experiences (current & prior) with unresolved entities/lexemes
 *
 *		   e.g. examine all: 	EXP_A INT EXP_B
 *								EXP_A DIF EXP_B
 *								EXP_B DIF EXP_A
 *
 *		   	if |EXP_A INT EXP_B| = 1 for entities and lexemes,
 *            then intersection -> resolution
 *		   	if |EXP_A DIF EXP_B| = 1 for entities and lexemes,
 *            then difference -> resolution
 *		   	if |EXP_B DIF EXP_A| = 1 for entities and lexemes,
 *            then difference -> resolution
 *
 *		c. add the lexeme-entity mapping to entity_lexeme_data with
 *         occurance_count = 1
 * 		d. set resolution_code in experience_lexeme_data to 1
 * 		e. set resolution_code in experience_entity_data to 1
 *		f. search for other instaces of that resolution
 *		g. search for single unmatched pairs created by the new resolution
 *
 *	  This technique will solve "C" for cases such as:
 *
 *                           exp_id          entity
 *                           ======          ======
 *				1		A
 *				1		B
 *				1		C
 *				1		D
 *				1		E
 *
 *				2		D
 *				2		E
 *				2		F
 *				2		G
 *
 *				3		A
 *				3		F
 *				3		H
 *
 *				4		B
 *				4		G
 *				4		I
 *
 *	 Processing experiences sequentially...
 *		#3-1 solves A, #3-2 solves F, #3-3 solves H
 *		#4-1 solves B, #4-2 solves G, #4-4 solves I
 *		#1-2 solves C
 *</pre>
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class LexemeResolver {
	/**
	 * database connection info
	 */
	private DBConnector dbc = null;

	/**
	 * long containing the database record ID for the current experience
	 */
	private long expID = -1;

	/**
	 * long containing the database record ID for the current calculation run
	 */
	private long runID = -1;

	/**
	 * long containing the order that the current experience is being processed
	 */
	private long expIndex = 1;

	/**
	 * ArrayList of all lexemes for the current experience
	 */
	private ArrayList<String> lexemeAL = null;

	/**
	 * String of lexemes from experience_data table in ebla_data
	 */
	private String lexemes = "";



	/**
	 * Class constructor that tokenizes description associated with each experience
	 * and sets the necessary parameters.
	 *
	 * @param _lexemes		comma separated list of lexemes describing an experience
	 * @param _expID		unique id of experience for which entities are being extracted
	 * @param _runID		unique id of current calculation run
	 * @param _expIndex 	order that experience is being processed
	 * @param _dbc			connection to EBLA database
	 */
    public LexemeResolver(String _lexemes, long _expID, long _runID, long _expIndex,
    	DBConnector _dbc) {

		try {

			lexemes = _lexemes;

			// BREAK OUT LEXICAL ITEMS FROM LANGUAGE ASSOCIATED WITH EACH EXPERIENCE
				// BUILD STRING TOKENIZER FOR EXPERIENCE DESCRIPTION IN DATABASE
					StringTokenizer st = new StringTokenizer(_lexemes);

				// INITIALIZE LEXEME ARRAYLIST
					lexemeAL = new ArrayList<String>();

				// LOOP THROUGH TOKENS AND ADD TO VECTOR
					while (st.hasMoreTokens()) {
						lexemeAL.add(st.nextToken());
					}

			// SET EXPERIENCE ID
				expID = _expID;

			// SET RUN ID
				runID = _runID;

			// SET EXPERIENCE INDEX
				expIndex = _expIndex;

			// SET DATABASE CONNECTION
				dbc = _dbc;

		} catch (Exception e) {
			System.out.println("\n--- LexemeResolver Constructor() Exception ---\n");
			e.printStackTrace();
		}

	} // end LexemeResolver()



	/**
	 * Attempts to generate protolanguage descriptions for the current experience based on
	 * existing entity-lexeme mappings.
	 */
	 public void generateDescriptions(FileWriter fw, int loopCount, int stdDev) {

		// DECLARATIONS
			ResultSet mapRS = null;			// ENTITY-LEXEME DATA MAPPINGS
			String sql = "";				// USED TO BUILD QUERIES AGAINST THE ebla_data DATABASE

			long entityID = 0;				// ID OF CURRENT ENTITY

			ResultSet eedRS = null;			// EXPERIENCE-ENTITY DATA RESULTSET

			boolean mappingFound = false;	// INDICATE IF A MAPPING EXISTS FOR THE CURRENT ENTITY
			boolean firstLexeme = true;		// USED FOR COMMA PLACEMENT IN DESCRIPTIONS - INDICATES FIRST LEXEME
			                                // IN DESCRIPTION
			String description = "";		// PROTOLANGUAGE DESCRIPTION OF EXPERIENCE
			String tmpString = "";			// USED FOR BUILDING DESCRIPTIONS

			double unknownCount = 0;		// FOR DESCRIPTIONS, NUMBER OF ENTITIES WHERE LEXEME IS UNKNOWN
			double correctCount = 0;		// FOR DESCRIPTIONS, NUMBER OF ENTITIES WHERE LEXEME IS CORRECT
			double incorrectCount = 0;		// FOR DESCRIPTIONS, NUMBER OF ENTITIES WHERE LEXEME IS INCORRECT



		try {

			// FILL CURRENT ENTITY SET
				sql = "SELECT entity_id FROM experience_entity_data WHERE experience_id = " + expID
					+ " AND run_id = " + runID + ";";

			// EXECUTE QUERY
				eedRS = dbc.getStatement().executeQuery(sql);

			// LOOP THROUGH RESULTSET AND CHECK EACH ENTITY FOR A MAPPING
				while (eedRS.next()) {
					// EXTRACT ID
						entityID = eedRS.getLong("entity_id");

// SHOULD UPDATE THIS TO USE SOME SORT OF PROBABILITY ???

					// QUERY FOR MAPPING (IF MULTIPLE MAPPINGS EXIST, USE MOST COMMON)
						sql = "SELECT * FROM entity_lexeme_data, lexeme_data "
							+ " WHERE entity_lexeme_data.entity_id=" + entityID
							+ " AND entity_lexeme_data.lexeme_id = lexeme_data.lexeme_id"
							+ " ORDER BY entity_lexeme_data.occurance_count;";
						mapRS = dbc.getStatement().executeQuery(sql);

					// RESET MAPPING FLAG
						mappingFound = false;

					// RESET TEMP CORRECT & INCORRECT COUNTS
						double tmpCorrectCount = 0;
						double tmpIncorrectCount = 0;

					// GET LEXEME IF MAPPING EXISTS
						while (mapRS.next()) {
							// EXTRACT MAPPING ID
							//	entLexID = mapRS.getLong("entity_lexeme_id");

							// EXTRACT OCCURANCE COUNT
							//	occuranceCount = mapRS.getInt("occurance_count");

							// EXTRACT LEXEME
								tmpString = mapRS.getString("lexeme");

							// ADD LEXEME TO DESCRIPTION
								if (firstLexeme) {
									description = tmpString;
									firstLexeme = false;
								} else {
									if (!mappingFound) {
										description = description + "," + tmpString;
									} else {
										description = description + " OR " + tmpString;
									}
								}

							// UPDATE MAPPING FLAG
								mappingFound = true;

							// DETERMINE IF MAPPING IS CORRECT
								if (lexemeAL.contains(tmpString)) {
									tmpCorrectCount++;
									lexemeAL.remove(tmpString);
								} else {
									tmpIncorrectCount++;
								}

						} // end while

					// UPDATE DESCRIPTION
						if (!mappingFound) {
							// ADD UNKNOWN TO DESCRIPTION
								if (firstLexeme) {
									description = "[unknown]";
									firstLexeme = false;
								} else {
									description += ", [unknown]";
								}

							// BUMP UNKNOWN COUNTER
								unknownCount++;
						} else {
							// UPDATE CORRECT / INCORRECT COUNTS
								correctCount += (tmpCorrectCount / (tmpCorrectCount + tmpIncorrectCount));
								incorrectCount += (tmpIncorrectCount / (tmpCorrectCount + tmpIncorrectCount));
						}

					// CLOSE RESULTSET
						mapRS.close();

					// UPDATE INDICATORS IF A MAPPING WAS FOUND
						if (mappingFound) {
							// UPDATE occurance_count IN entity_lexeme_data
							//	occuranceCount++;
							//	sql = "UPDATE entity_lexeme_data SET occurance_count = " + occuranceCount
							//		+ " WHERE entity_lexeme_id = " + entLexID + ";";
							//	tmpState2.executeUpdate(sql);

							// SET resolution_code TO 1 IN experience_entity_data AND experience_lexeme_data
							// UPDATE resolution_index IN experience_lexeme_data
								sql = "UPDATE experience_entity_data SET resolution_code = 1"
									+ " WHERE experience_id = " + expID
									+ " AND run_id = " + runID
									+ " AND entity_id = " + entityID + ";";
								dbc.getStatement().executeUpdate(sql);

						}

				} // end while (eedRS.next())

			// CLOSE RESULTSET
				eedRS.close();

			// ADD COUNTS TO DESCRIPTION
				description = description + ";" + correctCount + ";" + incorrectCount+ ";" + unknownCount;

			// WRITE DESCRIPTION TO TEXTFILE
				fw.write(loopCount + ";" + stdDev + ";" + expIndex + ";" + description + ";" + lexemes + "\n");

			// WRITE DESCRIPTION TO DATABASE
				sql = "UPDATE experience_run_data SET experience_description = '" + description + "'"
					+ " WHERE experience_id = " + expID
					+ " AND run_id = " + runID + ";";
				dbc.getStatement().executeUpdate(sql);

		} catch (Exception e) {
			System.out.println("\n--- LexemeResolver.generateDescriptions() Exception ---\n");
			System.out.println("	Current Query: " + sql +"\n");
			e.printStackTrace();
			
		} finally {
		// CLOSE OPEN FILES/RESULTSETS
			try {
				if (eedRS!=null) eedRS.close();
				if (mapRS!=null) mapRS.close();

			} catch (Exception misc) {
				System.out.println("\n--- LexemeResolver.generateDescriptions() Exception ---\n");
				misc.printStackTrace();
			}
		}

	} // end generateDescriptions()



	/**
	 * Resolves the lexemes for the current experience to the entities for the
	 * current experience and attempts to back-fill lexeme-entity relations
	 * for prior experiences.
	 */
	public void resolveLexemes() {

		// DECLARATIONS
			ArrayList<Long> curLex = null;	// UNRESOLVED LEXEMES FROM THE CURRENT EXPERIENCE
			ArrayList<Long> curEnt = null;	// UNRESOLVED ENTITIES FROM THE CURRENT EXPERIENCE

			ResultSet eedRS = null;			// EXPERIENCE-ENTITY DATA RESULTSET
			ResultSet entityRS = null;		// ENTITY RESULTSET
			ResultSet lexemeRS = null;		// LEXEME DATA RESULTSET
			ResultSet mapRS = null;			// ENTITY-LEXEME DATA MAPPINGS RESULTSET
			ResultSet expRS = null;			// USED FOR CROSS-SITUATIONAL LEARNING QUERIES		

			String sql = "";				// USED TO BUILD QUERIES AGAINST THE ebla_data DATABASE
			String entityString = "";		// USED FOR BUILDING LISTS OF ENTITIES CONTAINED IN QUERIES
			String lexemeString = "";		// USED FOR BUILDING LISTS OF LEXEMES CONTAINED IN QUERIES

			boolean lexemeExists;			// INDICATES THAT AN ENTITY-LEXEME MAPPING EXISTS FOR THE CURRENT ENTITY
			int occuranceCount = 0;			// NUMBER OF TIMES A GIVEN ENTITY-LEXEME MAPPING HAS BEEN ENCOUNTERED

			long lexemeID = 0;				// DATABASE ID FOR CURRENT LEXEME
			long entityID = 0;				// DATABASE ID FOR CURRENT ENTITY
			long entLexID = 0;				// DATABASE ID FOR CURRENT ENTITY-LEXEME MAPPING


			String eedSQL = "";				// TEXT OF QUERY AGAINST experience_entity_data
			String eldSQL = "";				// TEXT OF QUERY AGAINST experience_lexeme_data

			boolean tech1 = true;			// RESULTS OF CROSS-SITUITATIONAL LEARNING TECHNIQUE #1
			boolean tech2 = true;			// RESULTS OF CROSS-SITUITATIONAL LEARNING TECHNIQUE #2
			boolean tech3 = true;			// RESULTS OF CROSS-SITUITATIONAL LEARNING TECHNIQUE #3

			long exp1 = 0;					// FIRST EXPERIENCE FOR CROSS-SITUITATIONAL LEARNING
			long exp2 = 0;					// SECOND EXPERIENCE FOR CROSS-SITUITATIONAL LEARNING

			boolean tmpFirst = true;		// INDICATES WHETHER FIRST LEXEME/ENTITY IS BEING PROCESS WHEN
											// BUILDING LISTS IN SQL STATEMENTS (DETERMINES LOCATION OF
											// PARANTHESIS & COMMAS)

			Long tmpID = null;				// USED FOR STORING ENTITY ID'S IN ARRAYLIST


		try {

			// INITIALIZE CURRENT UNRESOLVED LEXEME & ENTITY SETS
				curLex = new ArrayList<Long>();
				curEnt = new ArrayList<Long>();

			// FILL CURRENT ENTITY SET
				sql = "SELECT entity_id FROM experience_entity_data WHERE experience_id = " + expID
					+ " AND run_id = " + runID + ";";

			// EXECUTE QUERY
				eedRS = dbc.getStatement().executeQuery(sql);

			// LOOP THROUGH RESULTSET AND ADD ENTITIES TO ARRAYLIST
				while (eedRS.next()) {
					tmpID = new Long(eedRS.getLong("entity_ID"));
					if (curEnt.contains(tmpID)) {
						System.out.println("THE SAME ENTITY EXISTS TWICE FOR EXPERIENCE ID# " + expID
							+ " -- RUN ID# " + runID + "!!!");
					} else {
						curEnt.add(tmpID);
					}
				} // end while

			// CLOSE RESULTSET
				eedRS.close();

			// CREATE lexeme_data RECORDS FOR LEXEMES IF THEY DON'T ALREADY EXIST
			//	- IF IN EXISTANCE,
			//		1. BUMP OCCURANCE COUNT
			//		2. GET ID AND ADD TO curLex
			//		3. ADD RECORD TO experience_lexeme_data WITH resolution_code = 0
			//  - IF NOT IN EXISTANCE
			//		1. ADD RECORD TO lexeme_data WITH occurance_count = 1
			//		2. GET ID AND ADD TO curLex
			//		3. ADD RECORD TO experience_lexeme_data WITH resolution_code = 0
				Iterator<String> lexemeIT = lexemeAL.iterator();
				while (lexemeIT.hasNext()) {
					// INITIALIZE FLAG INDICATING IF LEXEME ALREADY EXISTS IN DATABASE
						lexemeExists = false;

					// EXTRACT CURRENT LEXEME
						String tmpLex = lexemeIT.next();

					// QUERY lexeme_data FOR CURRENT LEXEME
						sql = "SELECT * FROM lexeme_data"
							+ " WHERE run_id = " + runID
							+ " AND lexeme = '" + tmpLex + "';";

					// EXECUTE QUERY
						lexemeRS = dbc.getStatement().executeQuery(sql);

					// IF RECORDS EXIST, GRAB LEXEME ID
						if (lexemeRS.next()) {
							// RESET FLAG
								lexemeExists = true;

							// EXTRACT LEXEME ID
								lexemeID = lexemeRS.getLong("lexeme_id");

							// EXTRACT OCCURANCE COUNT
								occuranceCount = lexemeRS.getInt("occurance_count");

							// INCREMENT OCCURANCE COUNT
								occuranceCount++;
						}

					// CLOSE LEXEME RESULTSET
						lexemeRS.close();

					// IF LEXEME EXISTS, UPDATE VALUES ACCORDINGLY - OTHERWISE ADD NEW LEXEME
						if (lexemeExists) {
						// UPDATE OCCURANCE COUNT IN LEXEME DATA IF NECESSARY
							// BUILD UPDATE QUERY
								sql = "UPDATE lexeme_data SET occurance_count = " + occuranceCount
									+ " WHERE lexeme_id = " + lexemeID + ";";

							// EXECUTE QUERY
								dbc.getStatement().executeUpdate(sql);

						} else {
						// ADD NEW LEXEME
							// GET NEXT lexeme_id
								sql = "SELECT nextval('lexeme_data_seq') AS next_index;";
								lexemeRS = dbc.getStatement().executeQuery(sql);
								lexemeRS.next();
								lexemeID = lexemeRS.getLong("next_index");
								lexemeRS.close();

							// ADD LEXEME DATA RECORD
								sql = "INSERT INTO lexeme_data (lexeme_id, run_id, lexeme, occurance_count) VALUES ("
									+ lexemeID + "," + runID + ", '" + tmpLex + "', 1);";
								dbc.getStatement().executeUpdate(sql);

						} // end if (lexemeExists)...

					// ADD EXPERIENCE LEXEME RECORD
						sql = "INSERT INTO experience_lexeme_data (experience_id, run_id, lexeme_id, resolution_code) VALUES ("
							+ expID + "," + runID + "," + lexemeID + ", 0);";
						dbc.getStatement().executeUpdate(sql);

					// ADD LEXEME ID TO ARRAYLIST
						curLex.add(new Long(lexemeID));

				} // end while

			// QUIT NOW IF THERE ARE NO ENTITIES FOR THE CURRENT EXPERIENCE
				if (curEnt.isEmpty()) {
					System.out.println("\n\nNo entities detected for experience #" + expID + ".\n\n");
					return;
				}

			// CHECK FOR EXISTING LEXEMES THAT MAP TO AN ENTITY
				// BUILD ENTITY STRING FOR QUERY
					Iterator<Long> entityIT = curEnt.iterator();
					tmpFirst = true;
					while (entityIT.hasNext()) {
					// EXTRACT CURRENT ENTITY ID
						long tmpEnt = entityIT.next();

					// ADD TO STRING
						if (tmpFirst) {
							tmpFirst = false;
							entityString = "(" + tmpEnt;
						} else {
							entityString += ", " + tmpEnt;
						}
					} // end while
					entityString += ")";

				// BUILD LEXEME STRING FOR QUERY
					Iterator<Long>lexemeIDIT = curLex.iterator();
					tmpFirst = true;
					while (lexemeIDIT.hasNext()) {
					// EXTRACT CURRENT ENTITY ID
						long tmpLex = lexemeIDIT.next();

					// ADD TO STRING
						if (tmpFirst) {
							tmpFirst = false;
							lexemeString = "(" + tmpLex;
						} else {
							lexemeString += ", " + tmpLex;
						}
					} // end while
					lexemeString += ")";

				// BUILD QUERY
					sql = "SELECT * FROM entity_lexeme_data WHERE entity_id IN " + entityString
						+ " AND lexeme_id IN " + lexemeString + ";";

				// EXECUTE QUERY
					mapRS = dbc.getStatement().executeQuery(sql);

				// LOOP THROUGH RESULTS
				// TO EXECUTE QUERIES INSIDE THE LOOP, NEED TO USE A 2ND STATEMENT OR mapRS WILL GET CORRUPTED
					while (mapRS.next()) {
						// EXTRACT VALUES
							entLexID = mapRS.getLong("entity_lexeme_id");
							entityID = mapRS.getLong("entity_id");
							lexemeID = mapRS.getLong("lexeme_id");
							occuranceCount = mapRS.getInt("occurance_count");

// POSSIBLE TO CREATE UNMATCHED PAIR IF ENTITY HAS BEEN RESOLVED TO MULTIPLE LEXEMES IN CURRENT EXPERIENCE
// OR LEXEME HAS BEEN RESOLVED TO MULTIPLE ENTITIES IN CURRENT EXPERIENCE
// OR ENTITY EXISTS TWICE IN CURRENT EXPERIENCE
						// REMOVE UNRESOLVED ENTITY FROM HASH SET
							curEnt.remove(new Long(entityID));

						// REMOVE UNRESOLVED LEXEME FROM HASH SET
							curLex.remove(new Long(lexemeID));

						// UPDATE resolution_code IN experience_entity_data AND experience_lexeme_data
							occuranceCount += updateExpResolutions(expID, entityID, lexemeID, expIndex);

						// UPDATE occurance_count IN entity_lexeme_data
							sql = "UPDATE entity_lexeme_data SET occurance_count = " + occuranceCount
								+ " WHERE entity_lexeme_id = " + entLexID + ";";
							dbc.getStatement().executeUpdate(sql);

					} // end while

				// CLOSE RESULTSET
					mapRS.close();

			// CHECK FOR SINGLE UNMATCHED ENTITY-LEXEME PAIR REMAINING IN ARRAYLISTS
				if ((curEnt.size() == 1) && (curLex.size() == 1)) {
					// GET ENTITY & LEXEME ID'S
						entityID = curEnt.get(0);
						lexemeID = curLex.get(0);

					// UPDATE resolution_code IN experience_entity_data AND experience_lexeme_data
						occuranceCount = updateExpResolutions(expID, entityID, lexemeID, expIndex);

					// ADD ENTITY-LEXEME MAPPING RECORD
						sql = "INSERT INTO entity_lexeme_data (entity_id, lexeme_id, occurance_count)"
							+ " VALUES (" + entityID + ", " + lexemeID + ", " + occuranceCount + ");";
						dbc.getStatement().executeUpdate(sql);

					// DELETE CONTENTS OF ENTITY & LEXEME ARRAY LISTS
						curEnt.clear();
						curLex.clear();

					// SEARCH FOR OTHER UNRESOLVED INSTANCES OF THIS NEW MAPPING AND RESOLVE
						 if (resolveOtherInstances(entityID, lexemeID, 1)) {
							 resolveSinglePairs();
						 }
				}


			// NOW USE PROCESS OF ELIMINATION (CROSS-SITUITATIONAL LEARNING) TO COMPARE UNRESOLVED LEXEMES/ENTITIES
			// ACROSS MULTIPLE EXPERIENCES
			//
			// THREE TECHNIQUES ARE USED TO FIND RESOLUTIONS
			// 	1. find all unmatched sets with single overlap & solve -> ||EXP1 INT EXP2|| = 1
			//	2. find all unmatched sets with single difference & solve -> ||EXP1 DIF EXP2|| = 1 (or # matches = ||EXP1|| - 1)
			//	3. find all unmatched sets with single difference & solve -> ||EXP2 DIF EXP1|| = 1 (or # matches = ||EXP2|| - 1)
			//
			// CONTINUE UNTIL ALL THREE TECHNIQUES RETURN NO RESULTS
				while (tech1 || tech2 || tech3) {
				// RESET TECHNIQUE RESULTS
					tech1 = false;
					tech2 = false;
					tech3 = false;

				// TECHNIQUE #1 - FIND ALL UNMATCHED SETS WITH SINGLE OVERLAP & SOLVE
				// RETURN ONLY FIRST RESULT
				// ID1 < ID2 (DOESN'T REALLY MATTER FOR THIS CASE) AND MATCH COUNT = 1

					// BUILD QUERIES
						eedSQL = "(SELECT eed1.experience_id AS id1, eed2.experience_id AS id2, COUNT(*)"
							   + " FROM experience_entity_data eed1, experience_entity_data eed2"
							   + " WHERE (eed1.run_id = " + runID + ")"
							   + " AND (eed1.resolution_code = 0)"
							   + " AND (eed2.run_id = " + runID + ")"
							   + " AND (eed2.resolution_code = 0)"
							   + " AND (eed1.entity_id = eed2.entity_id)"
							   + " AND (eed1.experience_id < eed2.experience_id)"
							   + " GROUP BY eed1.experience_id, eed2.experience_id"
							   + " HAVING COUNT(*) = 1)";

						eldSQL = "(SELECT eld1.experience_id AS id1, eld2.experience_id AS id2, COUNT(*)"
							   + " FROM experience_lexeme_data eld1, experience_lexeme_data eld2"
							   + " WHERE (eld1.run_id = " + runID + ")"
							   + " AND (eld1.resolution_code = 0)"
							   + " AND (eld2.run_id = " + runID + ")"
							   + " AND (eld2.resolution_code = 0)"
							   + " AND (eld1.lexeme_id = eld2.lexeme_id)"
							   + " AND (eld1.experience_id < eld2.experience_id)"
							   + " GROUP BY eld1.experience_id, eld2.experience_id"
							   + " HAVING COUNT(*) = 1)";

						sql = "SELECT * FROM " + eedSQL + " e, " + eldSQL + " l"
							+ " WHERE e.id1 = l.id1"
							+ " AND e.id2 = l.id2"
							+ " LIMIT 1;";

					// EXECUTE QUERY
						expRS = dbc.getStatement().executeQuery(sql);

					// EXTRACT EXPERIENCE ID'S
						if (expRS.next()) {
							// INDICATE THAT RESULTS WERE FOUND
								tech1 = true;

							// EXTRACT TWO EXPERIENCES INVOLVED
								exp1 = expRS.getLong("id1");
								exp2 = expRS.getLong("id2");
						}

					// CLOSE RESULTSET
						expRS.close();

					// CONTINUE TECHNIQUE #1 ONLY IF A RESOLUTION WAS DISCOVERED
						if (tech1) {
						// GET ENTITY ID INVOLVED
							// BUILD QUERY
								eedSQL = "SELECT entity_id FROM experience_entity_data"
									   + " WHERE experience_id = " + exp1
									   + " AND run_id = " + runID
									   + " AND resolution_code = 0"
									   + " AND entity_id IN ("
									   + " 		SELECT entity_id FROM experience_entity_data"
									   + " 		WHERE experience_id = " + exp2
									   + " 		AND run_id = " + runID
									   + "		AND resolution_code = 0"
									   + "		);";

							// OPEN RESULTSET & MOVE TO FIRST (ONLY) RECORD
								entityRS = dbc.getStatement().executeQuery(eedSQL);
								entityRS.next();

							// EXTRACT ID
								entityID = entityRS.getLong("entity_id");

							// CHECK FOR MULTIPLE ENTITIES - SHOULD BE IMPOSSIBLE
								if (entityRS.next()) {
									System.out.println("ERROR - LEX QUERY #1 - MULTIPLE ENTITIES!!!!");
									System.exit(0);
								}

							// CLOSE RESULTSET
								entityRS.close();

						// GET LEXEME ID INVOLVED
							// BUILD QUERY
								eldSQL = "SELECT lexeme_id FROM experience_lexeme_data"
									   + " WHERE experience_id = " + exp1
									   + " AND run_id = " + runID
									   + " AND resolution_code = 0"
									   + " AND lexeme_id IN ("
									   + " 		SELECT lexeme_id FROM experience_lexeme_data"
									   + " 		WHERE experience_id = " + exp2
									   + " 		AND run_id = " + runID
									   + "		AND resolution_code = 0"
									   + "		);";

							// OPEN RESULTSET & MOVE TO FIRST (ONLY) RECORD
								lexemeRS = dbc.getStatement().executeQuery(eldSQL);
								lexemeRS.next();

							// EXTRACT ID
								lexemeID = lexemeRS.getLong("lexeme_id");

							// CHECK FOR MULTIPLE LEXEMES - SHOULD BE IMPOSSIBLE
								if (lexemeRS.next()) {
									System.out.println("ERROR - LEX QUERY #1 - MULTIPLE LEXEMES!!!!");
									System.exit(0);
								}

							// CLOSE RESULTSET
								lexemeRS.close();

							// UPDATE resolution_code IN experience_entity_data AND experience_lexeme_data
							// FIRST EXPERIENCE
								occuranceCount = updateExpResolutions(exp1, entityID, lexemeID, expIndex);

							// UPDATE resolution_code IN experience_entity_data AND experience_lexeme_data
							// SECOND EXPERIENCE
								occuranceCount += updateExpResolutions(exp2, entityID, lexemeID, expIndex);

							// ADD ENTITY-LEXEME MAPPING RECORD (SET OCCURANCE_COUNT TO 2 BECAUSE TWO MAPPINGS WERE INVOLVED)
								sql = "INSERT INTO entity_lexeme_data (entity_id, lexeme_id, occurance_count)"
									+ " VALUES (" + entityID + ", " + lexemeID + ", " + occuranceCount + ");";
								dbc.getStatement().executeUpdate(sql);

						// SEARCH FOR OTHER UNRESOLVED INSTANCES OF THIS NEW MAPPING AND RESOLVE
							 if (resolveOtherInstances(entityID, lexemeID, 2)) {
								 resolveSinglePairs();
							 }
						} // end if


				// TECHNIQUE #2 - FIND ALL MATCHED SETS WHERE
				// #MATCHES = (#UNRESOLVED INSTANCES IN FIRST EXPERIENCE-1) AND (EXP_ID_1 < EXP_ID_2)
				// RETURN ONLY FIRST RESULT
				// ID1 < ID2 AND MATCH COUNT = # UNRESOLVED ENTITIES/LEXEMES IN FIRST EXPERIENCE - 1
				// TO PROPERLY HANDLE THE SITUATION WHERE AN ENTITY EXISTS TWICE IN AN EXPERIENCE
				// (DUE TO IT REALLY EXISTING TWICE OR OVERGENERALIZATION (E.G. BALL AND HAND MAP
				// TO SAME ENTITY)), ONLY DISTINCT PAIRINGS ARE TAKEN FROM THE "B" SET
					// BUILD QUERIES
						eedSQL = "(SELECT eed1.experience_id AS id1, eed2.experience_id AS id2, COUNT(*) as ecnt"
							   + " FROM experience_entity_data eed1, "
							   + " (SELECT DISTINCT experience_id, entity_id"
							   + "		FROM experience_entity_data WHERE resolution_code=0"
							   + " 		AND run_id = " + runID + ") eed2"
							   + " WHERE (eed1.resolution_code = 0)"
							   + " AND (eed1.run_id = " + runID + ")"
							   + " AND (eed1.entity_id = eed2.entity_id)"
							   + " AND (eed1.experience_id < eed2.experience_id)"
							   + " GROUP BY eed1.experience_id, eed2.experience_id"
							   + " HAVING COUNT(*) = ((SELECT COUNT(*) FROM experience_entity_data"
							   + " 		WHERE experience_id = eed1.experience_id"
							   + " 		AND run_id = " + runID + " AND resolution_code=0) - 1))";

						eldSQL = "(SELECT eld1.experience_id AS id1, eld2.experience_id AS id2, COUNT(*) as lcnt"
							   + " FROM experience_lexeme_data eld1, "
							   + " (SELECT DISTINCT experience_id, lexeme_id"
							   + "		FROM experience_lexeme_data WHERE resolution_code=0"
							   + " 		AND run_id = " + runID + ") eld2"
							   + " WHERE (eld1.resolution_code = 0)"
							   + " AND (eld1.run_id = " + runID + ")"
							   + " AND (eld1.lexeme_id = eld2.lexeme_id)"
							   + " AND (eld1.experience_id < eld2.experience_id)"
							   + " GROUP BY eld1.experience_id, eld2.experience_id"
							   + " HAVING COUNT(*) = ((SELECT COUNT(*) FROM experience_lexeme_data"
							   + " 		WHERE experience_id = eld1.experience_id"
							   + " 		AND run_id = " + runID + " AND resolution_code=0) - 1))";

						sql = "SELECT * FROM " + eedSQL + " e, " + eldSQL + " l"
							+ " WHERE e.id1 = l.id1"
							+ " AND e.id2 = l.id2"
							+ " LIMIT 1;";

					// EXECUTE QUERY
						expRS = dbc.getStatement().executeQuery(sql);

					// EXTRACT EXPERIENCE ID'S
						if (expRS.next()) {
							// INDICATE THAT RESULTS WERE FOUND
								tech2 = true;

							// EXTRACT TWO EXPERIENCES INVOLVED
								exp1 = expRS.getLong("id1");
								exp2 = expRS.getLong("id2");
						}

					// CLOSE RESULTSET
						expRS.close();

					// CONTINUE TECHNIQUE #2 ONLY IF A RESOLUTION WAS DISCOVERED
						if (tech2) {
						// GET ENTITY ID INVOLVED - USING AN EXCEPT CLAUSE
							// BUILD QUERY
								eedSQL = "SELECT entity_id FROM experience_entity_data"
									   + " WHERE experience_id = " + exp1
									   + " AND run_id = " + runID
									   + " AND resolution_code = 0"
									   + " EXCEPT"
									   + " 		SELECT entity_id FROM experience_entity_data"
									   + " 		WHERE experience_id = " + exp2
									   + " 		AND run_id = " + runID
									   + " 		AND resolution_code = 0;";

							// OPEN RESULTSET & MOVE TO FIRST (ONLY) RECORD
								entityRS = dbc.getStatement().executeQuery(eedSQL);
								entityRS.next();

							// EXTRACT ID
								entityID = entityRS.getLong("entity_id");

							// CHECK FOR MULTIPLE ENTITIES - SHOULD BE IMPOSSIBLE
								if (entityRS.next()) {
									System.out.println("ERROR - LEX QUERY #2 - MULTIPLE ENTITIES (" + exp1 + "-" + exp2 + ")");
									System.exit(0);
								}

							// CLOSE RESULTSET
								entityRS.close();

						// GET LEXEME ID INVOLVED
							// BUILD QUERY
								eldSQL = "SELECT lexeme_id FROM experience_lexeme_data"
									   + " WHERE experience_id = " + exp1
									   + " AND run_id = " + runID
									   + " AND resolution_code = 0"
									   + " EXCEPT"
									   + " 		SELECT lexeme_id FROM experience_lexeme_data"
									   + " 		WHERE experience_id = " + exp2
									   + " 		AND run_id = " + runID
									   + " 		AND resolution_code = 0;";

							// OPEN RESULTSET & MOVE TO FIRST (ONLY) RECORD
								lexemeRS = dbc.getStatement().executeQuery(eldSQL);
								lexemeRS.next();

							// EXTRACT ID
								lexemeID = lexemeRS.getLong("lexeme_id");

							// CHECK FOR MULTIPLE LEXEMES - SHOULD BE IMPOSSIBLE
								if (lexemeRS.next()) {
									System.out.println("ERROR - LEX QUERY #2 - MULTIPLE LEXEMES (" + exp1 + "-" + exp2 + ")");
									System.exit(0);
								}

							// CLOSE RESULTSET
								lexemeRS.close();

							// UPDATE resolution_code IN experience_entity_data AND experience_lexeme_data
								occuranceCount = updateExpResolutions(exp1, entityID, lexemeID, expIndex);

							// ADD ENTITY-LEXEME MAPPING RECORD
								sql = "INSERT INTO entity_lexeme_data (entity_id, lexeme_id, occurance_count)"
									+ " VALUES (" + entityID + ", " + lexemeID + ", " + occuranceCount + ");";
								dbc.getStatement().executeUpdate(sql);

						// SEARCH FOR OTHER UNRESOLVED INSTANCES OF THIS NEW MAPPING AND RESOLVE
							 if (resolveOtherInstances(entityID, lexemeID, 1)) {
								 resolveSinglePairs();
							 }
						} // end if (tech2)


				// TECHNIQUE #3 - FIND ALL MATCHED SETS WHERE
				// #MATCHES = (#UNRESOLVED INSTANCES IN FIRST EXPERIENCE-1) AND (EXP_ID_1 > EXP_ID_2)
				// RETURN ONLY FIRST RESULT
				// ID1 > ID2 AND MATCH COUNT = # UNRESOLVED ENTITIES/LEXEMES IN FIRST EXPERIENCE - 1
				// TO PROPERLY HANDLE THE SITUATION WHERE AN ENTITY EXISTS TWICE IN AN EXPERIENCE
				// (DUE TO IT REALLY EXISTING TWICE OR OVERGENERALIZATION (E.G. BALL AND HAND MAP
				// TO SAME ENTITY)), ONLY DISTINCT PAIRINGS ARE TAKEN FROM THE "B" SET
					// BUILD QUERIES
						eedSQL = "(SELECT eed1.experience_id AS id1, eed2.experience_id AS id2, COUNT(*)"
							   + " FROM experience_entity_data eed1, "
							   + " (SELECT DISTINCT experience_id, entity_id"
							   + "		FROM experience_entity_data WHERE resolution_code=0"
							   + " 		AND run_id = " + runID + ") eed2"
							   + " WHERE (eed1.resolution_code = 0)"
							   + " AND (eed1.run_id = " + runID + ")"
							   + " AND (eed1.entity_id = eed2.entity_id)"
							   + " AND (eed1.experience_id > eed2.experience_id)"
							   + " GROUP BY eed1.experience_id, eed2.experience_id"
							   + " HAVING COUNT(*) = ((SELECT COUNT(*) FROM experience_entity_data"
							   + "		WHERE experience_id = eed1.experience_id"
							   + " 		AND run_id = " + runID + " AND resolution_code=0) - 1))";

						eldSQL = "(SELECT eld1.experience_id AS id1, eld2.experience_id AS id2, COUNT(*)"
							   + " FROM experience_lexeme_data eld1, "
							   + " (SELECT DISTINCT experience_id, lexeme_id"
							   + "		FROM experience_lexeme_data WHERE resolution_code=0"
							   + " 		AND run_id = " + runID + ") eld2"
							   + " WHERE (eld1.resolution_code = 0)"
							   + " AND (eld1.run_id = " + runID + ")"
							   + " AND (eld1.lexeme_id = eld2.lexeme_id)"
							   + " AND (eld1.experience_id > eld2.experience_id)"
							   + " GROUP BY eld1.experience_id, eld2.experience_id"
							   + " HAVING COUNT(*) = ((SELECT COUNT(*) FROM experience_lexeme_data"
							   + " 		WHERE experience_id = eld1.experience_id"
							   + " 		AND run_id = " + runID + " AND resolution_code=0) - 1))";

						sql = "SELECT * FROM " + eedSQL + " e, " + eldSQL + " l"
							+ " WHERE e.id1 = l.id1"
							+ " AND e.id2 = l.id2"
							+ " LIMIT 1;";

					// EXECUTE QUERY
						expRS = dbc.getStatement().executeQuery(sql);

					// EXTRACT EXPERIENCE ID'S
						if (expRS.next()) {
							// INDICATE THAT RESULTS WERE FOUND
								tech3 = true;

							// EXTRACT TWO EXPERIENCES INVOLVED
								exp1 = expRS.getLong("id1");
								exp2 = expRS.getLong("id2");
						}

					// CLOSE RESULTSET
						expRS.close();

					// CONTINUE TECHNIQUE #3 ONLY IF A RESOLUTION WAS DISCOVERED
						if (tech3) {
						// GET ENTITY ID INVOLVED - USING AN EXCEPT CLAUSE
							// BUILD QUERY
								eedSQL = "SELECT entity_id FROM experience_entity_data"
									   + " WHERE experience_id = " + exp1
									   + " AND run_id = " + runID
									   + " AND resolution_code = 0"
									   + " EXCEPT"
									   + " 		SELECT entity_id FROM experience_entity_data"
									   + " 		WHERE experience_id = " + exp2
									   + " 		AND run_id = " + runID
									   + " 		AND resolution_code = 0;";

							// OPEN RESULTSET & MOVE TO FIRST (ONLY) RECORD
								entityRS = dbc.getStatement().executeQuery(eedSQL);
								entityRS.next();

							// EXTRACT ID
								entityID = entityRS.getLong("entity_id");

							// CHECK FOR MULTIPLE ENTITIES - SHOULD BE IMPOSSIBLE
								if (entityRS.next()) {
									System.out.println("ERROR - LEX QUERY #3 - MULTIPLE ENTITIES (" + exp1 + "-" + exp2 + ")");
									System.exit(0);
								}

							// CLOSE RESULTSET
								entityRS.close();

						// GET LEXEME ID INVOLVED
							// BUILD QUERY
								eldSQL = "SELECT lexeme_id FROM experience_lexeme_data"
									   + " WHERE experience_id = " + exp1
									   + " AND run_id = " + runID
									   + " AND resolution_code = 0"
									   + " EXCEPT"
									   + " 		SELECT lexeme_id FROM experience_lexeme_data"
									   + " 		WHERE experience_id = " + exp2
									   + " 		AND run_id = " + runID
									   + " 		AND resolution_code = 0;";

							// OPEN RESULTSET & MOVE TO FIRST (ONLY) RECORD
								lexemeRS = dbc.getStatement().executeQuery(eldSQL);
								lexemeRS.next();

							// EXTRACT ID
								lexemeID = lexemeRS.getLong("lexeme_id");

							// CHECK FOR MULTIPLE LEXEMES - SHOULD BE IMPOSSIBLE
								if (lexemeRS.next()) {
									System.out.println("ERROR - LEX QUERY #3 - MULTIPLE LEXEMES (" + exp1 + "-" + exp2 + ")");
									System.exit(0);
								}

							// CLOSE RESULTSET
								lexemeRS.close();

							// UPDATE resolution_code IN experience_entity_data AND experience_lexeme_data
								occuranceCount = updateExpResolutions(exp1, entityID, lexemeID, expIndex);

							// ADD ENTITY-LEXEME MAPPING RECORD
								sql = "INSERT INTO entity_lexeme_data (entity_id, lexeme_id, occurance_count)"
									+ " VALUES (" + entityID + ", " + lexemeID + ", " + occuranceCount + ");";
								dbc.getStatement().executeUpdate(sql);

						// SEARCH FOR OTHER UNRESOLVED INSTANCES OF THIS NEW MAPPING AND RESOLVE
							 if (resolveOtherInstances(entityID, lexemeID, 1)) {
								 resolveSinglePairs();
							 }
						} // end if (tech3)

				} // end while (tech1 || tech2 || tech3)

		} catch (Exception e) {
			System.out.println("\n--- LexemeResolver.resolveLexemes() Exception ---\n");
			System.out.println("	Current Query: " + sql +"\n");
			e.printStackTrace();
		} finally {
		// CLOSE OPEN FILES/RESULTSETS
			try {
				if (eedRS!=null) eedRS.close();
				if (entityRS!=null) entityRS.close();
				if (lexemeRS!=null) lexemeRS.close();
				if (mapRS!=null) mapRS.close();
				if (expRS!=null) expRS.close();
			} catch (Exception misc) {
				System.out.println("\n--- LexemeResolver.resolveLexemes() Exception ---\n");
				misc.printStackTrace();
			}
		}


	} // end resolveLexemes()



	/**
	 * Updates the resolution_code in both experience_entity_data and experience_lexeme_data
	 * for new entity-lexeme resolutions.
	 *<p>
	 * Since it is possible that an entity (or lexeme) can exist more than once in an
	 * experience (e.g. min SD is too high, or an object disappears and reappears), not ALL
	 * instances of each entity and lexeme for an experience are marked as resolved.  For
	 * example, if the entity occurs twice, but the lexeme only occurs once, then only one
	 * of the entities will be marked as resolved/mapped.
	 *
	 * @param _expID	unique id of experience for which entities are being extracted
	 * @param _entityID	ID of entity for which a new mapping was discovered
	 * @param _lexemeID ID of entity for which a new mapping was discovered
	 * @param _expIndex number of experience processed thus far for current run
	 *
	 * @return number of entities/lexemes updated
	 */
	private int updateExpResolutions(long _expID, long _entityID,
		long _lexemeID, long _expIndex) {

		// DECLARATIONS
			String sql = "";				// STRING USED FOR QUERY CONSTRUCTION
			ResultSet tmpRS = null;			// USED TO QUERY # OF TIMES A GIVEN ENTITY/LEXEME OCCURS IN AN EXPERIENCE
			int maxCount = 0;				// MAXIMUM NUMBER OF TIMES TO UPDATE A NEW MAPPING FOR A GIVEN EXPERIENCE
			String tmpEEDString = "";		// USED FOR BUILDING LISTS OF experience_entity_data RECORD ID'S
			String tmpELDString = "";		// USED FOR BUILDING LISTS OF experience_lexeme_data RECORD ID'S

		try {

			// QUERY # OF TIMES THAT ENTITY AND LEXEME EXIST FOR CURRENT QUERY AND USE SMALLER COUNT
			// UPDATE OCCURANCE COUNT ACCORDINGLY
				ArrayList<Long> tmpEED = new ArrayList<Long>();
				ArrayList<Long> tmpELD = new ArrayList<Long>();

				sql = "SELECT * FROM experience_entity_data"
					+ " WHERE experience_id = " + _expID
					+ " AND run_id = " + runID
					+ " AND entity_id = " + _entityID
					+ " AND resolution_code = 0;";

				tmpRS = dbc.getStatement().executeQuery(sql);

				while (tmpRS.next()) {
					tmpEED.add(new Long(tmpRS.getLong("experience_entity_id")));
				}
				tmpRS.close();

				sql = "SELECT * FROM experience_lexeme_data"
					+ " WHERE experience_id = " + _expID
					+ " AND run_id = " + runID
					+ " AND lexeme_id = " + _lexemeID
					+ " AND resolution_code = 0;";

				tmpRS = dbc.getStatement().executeQuery(sql);

				while (tmpRS.next()) {
					tmpELD.add(new Long(tmpRS.getLong("experience_lexeme_id")));
				}
				tmpRS.close();

				maxCount = tmpEED.size();
				if (tmpELD.size() < maxCount) {
					maxCount = tmpELD.size();
				}

			// BUILD LISTS OF experience_entity_data AND experience_lexeme_data RECORD
			// ID'S TO BE UPDATED
				for (int i=0; i<maxCount; i++) {
					if (i==0) {
						tmpEEDString = tmpEED.get(i).toString();
						tmpELDString = tmpELD.get(i).toString();
					} else {
						tmpEEDString += ", " + tmpEED.get(i).toString();
						tmpELDString += ", " + tmpELD.get(i).toString();
					}
				} // end for

			// SET resolution_code TO 1 IN experience_entity_data AND experience_lexeme_data
			// UPDATE resolution_index IN experience_lexeme_data
				if (maxCount > 0) {
					sql = "UPDATE experience_entity_data SET resolution_code = 1"
						+ " WHERE experience_entity_id IN (" + tmpEEDString + ");";
					dbc.getStatement().executeUpdate(sql);

					sql = "UPDATE experience_lexeme_data SET resolution_code = 1, resolution_index = " + _expIndex
						+ " WHERE experience_lexeme_id IN (" + tmpELDString + ");";
					dbc.getStatement().executeUpdate(sql);
				}

		} catch (Exception e) {
			System.out.println("\n--- LexemeResolver.updateExpResolutions() Exception ---\n");
			e.printStackTrace();
			System.out.println("	Current Query: " + sql +"\n");
		} finally {
		// CLOSE OPEN FILES/RESULTSETS
			try {
				if (tmpRS!=null) tmpRS.close();

			} catch (Exception misc) {
				System.out.println("\n--- LexemeResolver.updateExpResolutions() Exception ---\n");
				misc.printStackTrace();
			}
		}

		// RETURN RESULT
			return maxCount;

	} // end updateExpResolutions()



	/**
	 * Takes a new entity-lexeme resolution and searches for all other experiences where the resolution applies.
	 *
	 * If the resolution applies to other experiences, search those experience to see if only a single
	 * entity-lexeme pair remains unresolved following the new resolution.  If so, resolve that pair
	 * and call this routine recursively.
	 *
	 * @param _entityID	database ID of entity in new entity-lexeme resolution
	 * @param _lexemeID	database ID of lexeme in new entity-lexeme resolution
	 * @param _occCount	number of times that entity-lexeme mapping appears in the database
	 *
	 * @return Boolean indicating if new resolutions were made
	 */
	private boolean resolveOtherInstances(long _entityID, long _lexemeID, int _occCount) {

		// DECLARATIONS
			String sql = "";				// STRING USED FOR QUERY CONSTRUCTION
			ResultSet matchRS = null;		// RESULTSET OF EXPERIENCES CONTAINING NEWLY DISCOVERED MAPPING
			int newOccurances = 0;			// NUMBER OF TIMES A NEW MAPPING OCCURS
			long expID = 0;					// EXPERIENCE ID


		try {
			// QUERY experience_entity_data AND experience_lexeme_data FOR UNRESOLVED EXPERIENCES WITH THE NEWLY
			// RESOLVED entity-lexeme PAIR
				sql = "SELECT eed.experience_id FROM experience_entity_data eed, experience_lexeme_data eld"
					+ " WHERE eed.entity_id = " + _entityID
					+ " AND eld.lexeme_id = " + _lexemeID
					+ " AND eed.experience_id = eld.experience_id"
					+ " AND eed.resolution_code = 0"
					+ " AND eld.resolution_code = 0;";

			// BUILD RESULTSET
				matchRS = dbc.getStatement().executeQuery(sql);

			// INITIALIZE OCCURANCE COUNTER
				newOccurances = 0;

			// RESOLVE RESULTS
			// TO EXECUTE QUERIES INSIDE THE LOOP, NEED TO USE A 2ND STATEMENT OR matchRS WILL GET CORRUPTED
				while (matchRS.next()) {
					// EXTRACT EXPERIENCE ID
						expID = matchRS.getLong("experience_id");

					// UPDATE resolution_code IN experience_entity_data AND experience_lexeme_data
						newOccurances += updateExpResolutions(expID, _entityID, _lexemeID, expIndex);

				} // end while

			// CLOSE RESULTSET
				matchRS.close();

			// UPDATE occurance_count IN entity_lexeme_data
				if (newOccurances > 0) {
					sql = "UPDATE entity_lexeme_data SET occurance_count = " + (newOccurances + _occCount)
						+ " WHERE entity_id = " + _entityID + " AND lexeme_id = " + _lexemeID + ";";
					dbc.getStatement().executeUpdate(sql);
				}

		} catch (Exception e) {
			System.out.println("\n--- LexemeResolver.resolveOtherInstances() Exception ---\n");
			e.printStackTrace();
		} finally {
		// CLOSE OPEN FILES/RESULTSETS
			try {
				if (matchRS!=null) matchRS.close();

			} catch (Exception misc) {
				System.out.println("\n--- LexemeResolver.resolveOtherInstances() Exception ---\n");
				misc.printStackTrace();
			}
		}

		// RETURN RESULT
			if (newOccurances > 0) {
				return true;
			}
			return false;

	} // end resolveOtherInstances()



	/**
	 * Searches experiences for single entity-lexemes pairs (e.g. all but one
	 * entity and lexeme for an experience have been resolved) and resolves them.
	 *
	 * If new resolutions are discovered, it calls resolveOtherMapping for each.
	 *
	 * @return Boolean indicating if any new single unmapped entity-lexeme pairings were discovered
	 */
	private boolean resolveSinglePairs() {

		// DECLARATIONS
			String sql = "";				// STRING USED FOR QUERY CONSTRUCTION

			ResultSet expRS = null;			// RESULTSET USED TO QUERY SINGLE UNMAPPED ENTITY-LEXEME PAIRINGS

			long expID = 0;					// EXPERIENCE ID FOR NEW MAPPING
			long entityID = 0;				// ENTITY ID FOR NEW MAPPING
			long lexemeID = 0;				// LEXEME ID FOR NEW MAPPING

			boolean foundPair = false;		// INDICATES THAT A SINGLE UNMAPPED ENTITY-LEXEME PAIRING EXIST FOR AN EXPERIENCE

			boolean stopLoop = false;		// USED TO LOOP AND RESOLVE SINGLE PAIRS UNTIL NO MORE REMAIN


		try {

			// LOOP AND RESOLVE SINGLE PAIRS UNTIL NO MORE REMAIN
				while (!stopLoop) {
					// RESET foundPair
						foundPair = false;


// COULD THIS RETURN RECORDS WHERE THERE WERE TWO UNMAPPED LEXEMES AND A SINGLE UNMAPPED ENTITY???


					// QUERY experience_entity_data AND experience_lexeme_data FOR EXPERIENCES WITH ONLY A SINGLE
					// UNRESOLVED entity-lexeme PAIR -- ONLY RETURN FIRST RESULT
						sql = "SELECT eed.experience_id, COUNT(*) FROM experience_entity_data eed, experience_lexeme_data eld"
							+ " WHERE eed.run_id = " + runID
							+ " AND eld.run_id = " + runID
							+ " AND eed.experience_id = eld.experience_id"
							+ " AND eed.resolution_code = 0"
							+ " AND eld.resolution_code = 0"
							+ " GROUP BY eed.experience_id"
							+ " HAVING COUNT(*) = 1"
							+ " LIMIT 1;";

					// BUILD RESULTSET
						expRS = dbc.getStatement().executeQuery(sql);

					// RESOLVE RESULTS
						if (expRS.next()) {
							// INDICATE THAT A PAIR HAS BEEN FOUND
								foundPair = true;

							// EXTRACT EXPERIENCE ID
								expID = expRS.getLong("experience_id");
						}

					// CLOSE RESULTSET
						expRS.close();

					// CONTINUE ONLY IF RECORDS WERE RETURNED
					// OTHERWISE STOP LOOP
						if (foundPair) {
							// QUERY PAIR FROM experience_entity_data AND experience_lexeme_data
								sql = "SELECT entity_id, lexeme_id FROM experience_entity_data eed, experience_lexeme_data eld"
									+ " WHERE eed.run_id = " + runID
									+ " AND eld.run_id = " + runID
									+ " AND eed.experience_id = " + expID
									+ " AND eld.experience_id = " + expID
									+ " AND eed.resolution_code = 0"
									+ " AND eld.resolution_code = 0;";

							// BUILD RESULTSET
								expRS = dbc.getStatement().executeQuery(sql);
								expRS.next();

							// EXTRACT ID'S
								entityID = expRS.getLong("entity_id");
								lexemeID = expRS.getLong("lexeme_id");

							// CLOSE RESULTSET
								expRS.close();

							// ADD ENTITY-LEXEME MAPPING RECORD
								sql = "INSERT INTO entity_lexeme_data (entity_id, lexeme_id, occurance_count)"
									+ " VALUES (" + entityID + ", " + lexemeID + ", 1);";
								dbc.getStatement().executeUpdate(sql);

							// UPDATE resolution_code IN experience_entity_data
								sql = "UPDATE experience_entity_data SET resolution_code = 1"
									+ " WHERE experience_id = " + expID
									+ " AND run_id = " + runID
									+ " AND entity_id = " + entityID + ";";
								dbc.getStatement().executeUpdate(sql);

							// UPDATE resolution_code AND resolution_index IN experience_lexeme_data
								sql = "UPDATE experience_lexeme_data SET resolution_code = 1, resolution_index = " + expIndex
									+ " WHERE experience_id = " + expID
									+ " AND run_id = " + runID
									+ " AND lexeme_id = " + lexemeID + ";";
								dbc.getStatement().executeUpdate(sql);

							// RESOLVE OTHER INSTANCES OF NEW MAPPING
								resolveOtherInstances(entityID, lexemeID, 1);

						} else {
							// SET FLAG TO STOP LOOP
								stopLoop = true;
						} // end if (foundPair)

				} // end while(!stopLoop)

		} catch (Exception e) {
			System.out.println("\n--- LexemeResolver.resolveSinglePairs() Exception ---\n");
			e.printStackTrace();
		} finally {
		// CLOSE OPEN FILES/RESULTSETS
			try {
				if (expRS!=null) expRS.close();

			} catch (Exception misc) {
				System.out.println("\n--- LexemeResolver.resolveSinglePairs() Exception ---\n");
				misc.printStackTrace();
			}
		}

		// RETURN RESULT
			return foundPair;

	} // end resolveSinglePairs()

} // end LexemeResolver class



/*
 * $Log$
 * Revision 1.29  2011/04/28 14:55:07  yoda2
 * Addressing Java 1.6 -Xlint warnings.
 *
 * Revision 1.28  2011/04/25 03:52:10  yoda2
 * Fixing compiler warnings for Generics, etc.
 *
 * Revision 1.27  2011/04/25 02:34:51  yoda2
 * Coding for Java Generics.
 *
 * Revision 1.26  2005/02/17 23:33:54  yoda2
 * JavaDoc fixes & retooling for SwingSet 1.0RC compatibility.
 *
 * Revision 1.25  2004/08/02 21:28:03  yoda2
 * Remove non-standard characters from comments for Linux compatability.
 *
 * Revision 1.24  2004/06/26 05:01:28  yoda2
 * Fixed various typos in comments.
 *
 * Revision 1.23  2004/06/24 17:24:49  yoda2
 * Fixed bug in generateDescriptions where candidate entities were being filtered by runID, but not by experienceID.
 *
 * Revision 1.22  2004/02/25 21:58:10  yoda2
 * Updated copyright notice.
 *
 * Revision 1.21  2003/08/08 13:32:53  yoda2
 * Rewritten for use with new database structure (e.g. run_data).
 *
 * Revision 1.20  2002/12/11 22:54:34  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.19  2002/10/27 23:04:50  bpangburn
 * Finished JavaDoc.
 *
 * Revision 1.18  2002/10/21 15:23:08  bpangburn
 * Parameter cleanup and documentation.
 *
 * Revision 1.17  2002/10/02 20:51:33  bpangburn
 * Added BSD style license and cleaned up docs.
 *
 * Revision 1.16  2002/09/27 23:12:07  bpangburn
 * Added license information and moved log to end of file.
 *
 * Revision 1.15  2002/09/17 21:43:20  bpangburn
 * Improved code to handle resolutions where an entity is detected multiple times in a single experience.
 *
 * Revision 1.14  2002/09/08 17:19:58  bpangburn
 * Improved logging for descriptions generated by EBLA.
 *
 * Revision 1.13  2002/09/05 18:13:05  bpangburn
 * Added code for testing EBLA using multiple variances over multiple runs, writing results to semi-colon separated files.
 *
 * Revision 1.12  2002/08/23 21:39:25  bpangburn
 * Added code to return statistical information about the accuracy of protolanguage descriptions generated.
 *
 * Revision 1.11  2002/08/23 16:31:15  bpangburn
 * Bug fixed to protolanguage description generation code.  SQL Join was missing.
 *
 * Revision 1.10  2002/08/21 03:13:18  bpangburn
 * Added setting of resolution_index and protolanguage generation.
 *
 * Revision 1.9  2002/08/20 22:15:41  bpangburn
 * Added code to stop resolution following lexeme parse if no entities are detected for current experience.
 *
 * Revision 1.8  2002/08/07 13:38:32  bpangburn
 * Added dump of current SQL query to error reporting 8-2-2002.
 *
 * Revision 1.7  2002/06/26 13:28:39  bpangburn
 * Debugging.
 *
 * Revision 1.6  2002/05/15 22:30:47  bpangburn
 * Finished debugging and started cleanup.
 *
 * Revision 1.5  2002/05/14 22:39:52  bpangburn
 * Debugging lexical resolution code.
 *
 * Revision 1.4  2002/05/14 02:00:38  bpangburn
 * Added rest of cross-situitational learning code.
 *
 * Revision 1.3  2002/05/13 22:52:50  bpangburn
 * Added code to resolve single unresolved entity-lexeme pairs for an experience.  Added code to back-fill new resolutions to other experiences.  Started cross-situitational learning code.
 *
 * Revision 1.2  2002/05/10 22:06:45  bpangburn
 * Added all of the code for lexical resolution, except for the cross-situational learning portion.
 *
 * Revision 1.1  2002/05/08 22:49:57  bpangburn
 * Created LexemeResolver class to handle lexical resolution process and updated EBLA class accordingly.
 *
 */